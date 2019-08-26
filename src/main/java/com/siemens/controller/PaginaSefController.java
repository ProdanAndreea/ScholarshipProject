package com.siemens.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.signatures.SignatureUtil;
import com.siemens.configuration.MailConfiguration;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;
import com.siemens.model.*;
import com.siemens.view.ClientStart;
import com.siemens.xml.XMLMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.siemens.view.ClientStart.superiorsFilePath;
import static java.util.stream.Collectors.toList;

public class PaginaSefController {
    private ObservableList<Request> requestObservableList = FXCollections.observableArrayList();

    private final File folder = new File(ClientStart.fileDirectoryPath);
    @FXML
    private Button acceptButton;

    @FXML
    private Button denyButton;

    @FXML
    private ListView requestListView;

    @FXML
    private ToggleButton resolvedToggleButton;

    @FXML
    private ToggleButton waitingToggleButton;

    @FXML
    private ToggleButton signedToggleButton;

    private List<Superior> sefiDepartment;

    private List<Recovery> listOfRecoveries;

    private Leave desiredLeave;

    private String name;
    private String mailName;
    private String teamLeadName;
    private String departmentLeadName;
    private LocalDate leaveDate;
    private LocalDate creationDate;
    private LocalTime time;

    private String fullPath;


    private void populateRequests(){

        String pathToFiles = ClientStart.fileDirectoryPath;
        if (ClientStart.userPosition.equals("Department Leader")) {
            pathToFiles = pathToFiles + "\\" + ClientStart.senderMail.split("@")[0];
        }

        else {
            String sefDepartmentFolder = this.sefiDepartment.stream()
                    .filter(sup -> sup.getName().equals(ClientStart.departmentSuperior))
                    .findFirst()
                    .get()
                    .getEmail()
                    .split("@")[0];

            pathToFiles = pathToFiles + "\\" + sefDepartmentFolder;
            new File(pathToFiles).mkdir();
            pathToFiles = pathToFiles + "\\" + ClientStart.senderMail.split("@")[0];
        }

        File newFolder = new File(pathToFiles);
        newFolder.mkdir();

        ArrayList<Request> array = new ArrayList<>();
        if (newFolder.listFiles().length == 0) {
            requestObservableList.clear();
        } else {
            for (File file : newFolder.listFiles()) {
                if (file.isDirectory()) {
                    String newPath = file.getPath();
                    for (File fileDirect : file.listFiles()) {
                        array.add(readRequestFromFile(fileDirect, newPath));
                    }
                } else {
                    array.add(readRequestFromFile(file, pathToFiles));
                }
            }

            ArrayList<Request> sorted = mergeSort(array);
            requestObservableList.setAll(sorted);
        }



    }

    private Request readRequestFromFile(File file, String pathToFiles) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat parser = new SimpleDateFormat("HH-mm");

        String[] parts = file.getName().split("_");
        boolean isSigned;
        try{
            PdfDocument pdf = new PdfDocument(new PdfReader(pathToFiles + "\\" +file.getName()));
            PdfAcroForm form  = PdfAcroForm.getAcroForm(pdf, true);
            SignatureUtil signUtil = new SignatureUtil(pdf);
            isSigned = true;
            if (signUtil.getSignatureNames().size() == 0)
                isSigned = false;
            Map<String, PdfFormField> fields = form.getFormFields();
            PdfFormField field1 = fields.get("email");
            PdfFormField sentField = fields.get("hasBeenSent");
            boolean isSent = false;
            if(sentField != null)
                isSent = true;
            String senderMail = field1.getValueAsString();
            pdf.close();
            return new Request(file.getName(), senderMail, file, isSigned, isSent, LocalDate.parse(parts[2], formatter), parser.parse(parts[3]));
        }catch (Exception e){
            e.printStackTrace();
            return new Request();
        }
    }
    private void refreshItems(){
        requestObservableList.removeAll();
        populateRequests();
    }
    private void handleDocument(Request selectedRequest, String source, String temp, String acceptance, Color color){
        try{
            PdfDocument pdf = new PdfDocument(
                    new PdfReader(source),
                    new PdfWriter(temp)
            );
            Document document = new Document(pdf);
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Rectangle rectangle = new Rectangle(0,800,250,50);
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(rectangle);
            widget.makeIndirect(pdf);
            pdf.getFirstPage().addAnnotation(widget);
            PdfFormField field = PdfFormField.createText(pdf);
            field.addKid(widget);
            field.setFieldName("hasBeenSent");
            field.setValue(acceptance)
                    .setFontSize(22)
                    .setColor(color);
            field.setFieldFlag(PdfFormField.FF_READ_ONLY);
            form.addField(field, pdf.getFirstPage());
            document.close();
            File newDocument = new File(temp);
            selectedRequest.getFile().delete();
            newDocument.renameTo(selectedRequest.getFile());
        }catch (Exception e){
            e.printStackTrace();
            ClientStart.logger.info(e.getMessage());
        }
    }

    private void setHandlers(){
        requestListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    Request selectedRequest = (Request) requestListView.getSelectionModel().getSelectedItem();
                    requestObservableList.forEach(request -> {
                        if(request.equals(selectedRequest)){
                            try{
                                String commandPath = selectedRequest.getFile().getAbsolutePath();
                                File file = new File(selectedRequest.getFile().getAbsolutePath());
                                Runtime.getRuntime()
                                        .exec("rundll32 url.dll,FileProtocolHandler "+commandPath);

                                Thread.sleep(2000);
                                while(!selectedRequest.getFile().renameTo(selectedRequest.getFile())){
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(1000);
                                PdfDocument pdf = new PdfDocument(
                                        new PdfReader(file)
                                );
                                SignatureUtil signUtil = new SignatureUtil(pdf);
                                if(signUtil.getSignatureNames().size() != 0){
                                    request.setSigned(true);
                                }
                                pdf.close();
                            }catch (Exception e){
                                e.printStackTrace();
                                ClientStart.logger.severe(e.getMessage());
                            }


                        }

                    });
                    refreshItems();
                }
                else if(event.getClickCount() == 1){
                    Request selectedRequest = (Request) requestListView.getSelectionModel().getSelectedItem();
                    String source = selectedRequest.getFile().getAbsolutePath();
                    String temp = selectedRequest.getFile().getAbsolutePath()
                            .split(".pdf")[0] + "temp.pdf";
                    if(selectedRequest.isSigned() && !selectedRequest.isSent()){
                        acceptButton.setDisable(false);
                        denyButton.setDisable(false);
                        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                handleDocument(
                                        selectedRequest,
                                        source,
                                        temp,
                                        "CERERE ACCEPTATA!",
                                        ColorConstants.GREEN
                                );
                                requestObservableList.forEach(
                                        (r)->{
                                            if(r.equals(selectedRequest))
                                                r.setSent(true);
                                        }
                                );
                                denyButton.setDisable(true);
                                acceptButton.setDisable(true);
                                refreshItems();
                                MailConfiguration.sendMessage(
                                        selectedRequest.getEmailSender(),
                                        "Cerere Confirmata",
                                        "Cererea " + selectedRequest.getFile().getName() + " a fost aprobata!"
                                );

                            }
                        });
                        denyButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                handleDocument(
                                        selectedRequest,
                                        source,
                                        temp,
                                        "CERERE RESPINSA!",
                                        ColorConstants.RED
                                );
                                denyButton.setDisable(true);
                                acceptButton.setDisable(true);
                                requestObservableList.forEach(
                                        (r)->{
                                            if(r.equals(selectedRequest))
                                                r.setSent(true);
                                        }
                                );
                                refreshItems();

                                MailConfiguration.sendMessage(
                                        selectedRequest.getEmailSender(),
                                        "Cerere Respinsa",
                                        "Cererea "+ selectedRequest.getFile().getName() + " a fost respinsa!"
                                );


                            }
                        });

                    }
                    else{
                        acceptButton.setDisable(true);
                        denyButton.setDisable(true);
                    }
                }
            }
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        resolvedToggleButton.setToggleGroup(toggleGroup);
        signedToggleButton.setToggleGroup(toggleGroup);
        waitingToggleButton.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {

                    waitingToggleButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (!waitingToggleButton.isSelected()) {
                                requestListView.setItems(requestObservableList);
                            } else {
                                ObservableList<Request> filteredList = requestObservableList.filtered(request -> !request.isSigned() && !request.isSent());
                                requestListView.setItems(filteredList);
                            }
                        }
                    });


                    signedToggleButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (!signedToggleButton.isSelected()) {
                                requestListView.setItems(requestObservableList);
                            } else {
                                ObservableList<Request> filteredList = requestObservableList.filtered(request -> request.isSigned() && !request.isSent());
                                requestListView.setItems(filteredList);
                            }
                        }
                    });

                    resolvedToggleButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (!resolvedToggleButton.isSelected()) {
                                requestListView.setItems(requestObservableList);
                            } else {
                                ObservableList<Request> filteredList = requestObservableList.filtered(Request::isSent)
                                        .sorted(Comparator.reverseOrder());
                                requestListView.setItems(filteredList);
                            }
                        }
                    });
                }
            }
        });

    }

    public void initialize(){
        if(ClientStart.parameterString.length != 0)
            decodeParameters();
        acceptButton.setDisable(true);
        denyButton.setDisable(true);
        requestListView.setItems(requestObservableList);
        setHandlers();
    }

    public void populateDepartment(List<Superior> sefiDepartment) {

        this.sefiDepartment = sefiDepartment;
        populateRequests();

    }



    private ArrayList<Request> mergeSort(ArrayList<Request> whole) {
        ArrayList<Request> left = new ArrayList<Request>();
        ArrayList<Request> right = new ArrayList<Request>();
        int center;

        if (whole.size() == 1) {
            return whole;
        } else {
            center = whole.size()/2;
            // copy the left half of whole into the left.
            for (int i=0; i<center; i++) {
                left.add(whole.get(i));
            }

            //copy the right half of whole into the new arraylist.
            for (int i=center; i<whole.size(); i++) {
                right.add(whole.get(i));
            }

            // Sort the left and right halves of the arraylist.
            left  = mergeSort(left);
            right = mergeSort(right);

            // Merge the results back together.
            merge(left, right, whole);
        }
        return whole;
    }

    private void merge(ArrayList<Request> left, ArrayList<Request> right, ArrayList<Request> whole) {
        int leftIndex = 0;
        int rightIndex = 0;
        int wholeIndex = 0;

        // As long as neither the left nor the right ArrayList has
        // been used up, keep taking the smaller of left.get(leftIndex)
        // or right.get(rightIndex) and adding it at both.get(bothIndex).
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if ( (left.get(leftIndex).compareTo(right.get(rightIndex))) < 0) {
                whole.set(wholeIndex, left.get(leftIndex));
                leftIndex++;
            } else {
                whole.set(wholeIndex, right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }

        ArrayList<Request> rest;
        int restIndex;
        if (leftIndex >= left.size()) {
            // The left ArrayList has been use up...
            rest = right;
            restIndex = rightIndex;
        } else {
            // The right ArrayList has been used up...
            rest = left;
            restIndex = leftIndex;
        }

        // Copy the rest of whichever ArrayList (left or right) was not used up.
        for (int i=restIndex; i<rest.size(); i++) {
            whole.set(wholeIndex, rest.get(i));
            wholeIndex++;
        }
    }
    private void openPdf(){
        try{
            String commandPath = fullPath;
            File file = new File(fullPath);
            Runtime.getRuntime()
                    .exec("rundll32 url.dll,FileProtocolHandler "+commandPath);

            Thread.sleep(2000);
            while(!file.renameTo(file)){
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
            ClientStart.logger.severe(e.getMessage());
        }
    }

    public void decodeParameters(){
        String[] argsArray = ClientStart.parameterString[0].split(":");
        String[] parameters = argsArray[1].split(",");
        name = parameters[0].replaceAll("%20", " ");
        mailName = parameters[1];
        teamLeadName = parameters[2].replace("%20", " ");
        departmentLeadName = parameters[3].replace("%20", " ");
        leaveDate = LocalDate.parse(parameters[4], ClientController.format);
        time = LocalTime.parse(parameters[5], ClientController.hourFormatter);
        desiredLeave = new Leave(leaveDate, time);
        creationDate = LocalDate.parse(parameters[6], ClientController.format);
        listOfRecoveries = decipherRecoveries(leaveDate, parameters[7]);
        generatePdf();
        openPdf();
    }

    private List<Recovery> decipherRecoveries(LocalDate leaveDate, String recoveriesCode) {
        List<Recovery> recoveries = new ArrayList<Recovery>();
        String[] recoveriesString = recoveriesCode.split("m");
        for (String recovery : recoveriesString) {
            String[] dateAndTime = recovery.split("n");
            LocalDate dateRecovery = LocalDate.parse(dateAndTime[0], ClientController.format);
            LocalTime timeRecovery = LocalTime.parse(dateAndTime[1], ClientController.hourFormatter);
            recoveries.add(new Recovery(leaveDate, dateRecovery, timeRecovery));
        }

        return recoveries;
    }
    private String getFolderForSefDirect(String sefDirect) {
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, superiorsFilePath).getSuperiors();

        String sefDirectMail = sups.stream()
                .filter(superior -> superior.getName().equals(sefDirect))
                .findFirst()
                .get()
                .getEmail();

        return sefDirectMail.split("@")[0];
    }

    public void generatePdf(){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String fullDirectory;

            listOfRecoveries.sort(Comparator.comparing(Recovery::getRecoveryDate));

            String folderNameForDepartment = getFolderForSefDirect(departmentLeadName);
            String directoryForSefDirect = ClientStart.fileDirectoryPath.concat("\\").concat(folderNameForDepartment);
            new File(directoryForSefDirect).mkdir();
            if (ClientStart.userPosition.equals("Team Leader")) {
                fullDirectory = directoryForSefDirect.concat("\\").concat(folderNameForDepartment);
            } else {
                String folderNameForSefDirect = getFolderForSefDirect(teamLeadName);
                fullDirectory = directoryForSefDirect.concat("\\").concat(folderNameForSefDirect);
            }

            new File(fullDirectory).mkdir();

            String pdfFilePath =
                    fullDirectory +"\\Invoire_" + name+
                            "_" + LocalDate.now().format(formatter)  +// desiredLeave.getLeaveDate().toString()
                            "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm")).toString()+".pdf";
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);


            // add hidden email
            PdfPage page = pdf.addNewPage();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfFormField field = PdfFormField.createText(pdf);
            field.setFieldName("email");

            Rectangle rect1 = new Rectangle(240, 800, 150, 20);
            PdfWidgetAnnotation widget1 = new PdfWidgetAnnotation(rect1);
            widget1.makeIndirect(pdf);
            page.addAnnotation(widget1);
            field.addKid(widget1);
            field.setValue(mailName + "@siemens.com");
            field.setVisibility(PdfFormField.HIDDEN); // hide it
            form.addField(field, page);

            // get the value of the field
//            form = PdfAcroForm.getAcroForm(pdf, true);
//            Map<String, PdfFormField> fields = form.getFormFields();
//            PdfFormField field1 = fields.get("email");
//            System.out.println("hidden email field: " + field1.getValueAsString());
            ////////////

            document.add(
                    new Paragraph("SIEMENS SRL")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(22)
                            .setBold()
                            .setFontColor(new DeviceRgb(0, 153, 153))
            );
            document.add(
                    new Paragraph("Bilet Invoire")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20)
                            .setBold()
                    //.setFont(font)
            );


            Text text1 = new Text("Subsemnatul/a ");
            Text text2 = new Text(name).setBold();
            Text text3 = new Text(" doresc a beneficia de o invoire avand durata de ");
            Text text4 = new Text(time.toString()).setBold();
            Text text5 = new Text(" ore, " +
                    "perioada necesara pentru rezolvarea unor probleme cu caracter personal.");

            document.add(
                    new Paragraph().add(text1).add(text2).add(text3).add(text4).add(text5)
                            .setFirstLineIndent(40)
                            .setFontSize(14)
            );

            document.add(
                    new Paragraph("Invoire:")
                            .setBold()
                            .setFontSize(16)
                            .setFirstLineIndent(40)
            );

            Table table = new Table(new float[]{2, 2});
            table.setWidth(UnitValue.createPercentValue(80));

            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data invoirii")
                    )
            );
            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr. ore")
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getNumberOfHours().toString()
                    )
            );
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.add(
                    new Paragraph("Propuneri recuperare:")
                            .setBold()
                            .setFontSize(16)
                            .setFirstLineIndent(40)
            );

            Table recoveryTable = new Table(new float[]{2, 2, 2});
            recoveryTable.setWidth(UnitValue.createPercentValue(80));

            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Recuperare pentru data de")
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data propusa pentru recuperare")
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr. ore recuperate")
                    )
            );
            for (Recovery recovery : listOfRecoveries) {
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getRecoveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getNumberOfHours().toString()
                        )
                );
            }

            recoveryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(recoveryTable);

            Table approvalTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            if(ClientStart.userPosition.equals("User")){
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell(1, 2).add(new Paragraph("  Aprobare"))
                );
                approvalTable.addCell("Sef");
                approvalTable.addCell("Semnatura"); //.setFont(font);
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph("Direct:"))
                                .add(new Paragraph(ClientStart.superiorName)
                                ));
                approvalTable.addCell("");
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph("Departament:"))
                                .add(new Paragraph(ClientStart.departmentSuperior))
                );
                approvalTable.addCell("");

            }else{
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell(1, 2).add(new Paragraph("  Aprobare"))
                );
                approvalTable.addCell("Sef");
                approvalTable.addCell("Semnatura"); //.setFont(font);
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph("Departament:"))
                                .add(new Paragraph(ClientStart.departmentSuperior))
                );
                approvalTable.addCell("");

            }


            document.add(new Paragraph("\n\n"));


            Paragraph dataDeAzi = new Paragraph(
                    "Data de azi: "
            )
                    .setVerticalAlignment(VerticalAlignment.BOTTOM)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setFontSize(12);


            Text text = new Text(creationDate.format(ClientController.format)).setBold();
            dataDeAzi.add(text);

            document.add(dataDeAzi);

            document.add(new Paragraph("\n\n"));

            approvalTable.setWidth(UnitValue.createPercentValue(60));
            approvalTable.setFixedLayout();
            approvalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(approvalTable);


            /* add signature fields */

            // get the number of the table's rows to shift the signature form on y axis based on the table's height
            int noRows = recoveryTable.getNumberOfRows();
            /* sef direct signature */
            // create a signature form field
            if(ClientStart.userPosition.equals("User")){
                PdfSignatureFormField signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)330.4, (float)361.5 - (noRows * (float)22.4), (float)133.9, (float)40.1)); // 329.5
                signatureField.setFieldName("signatureSefDirect");
                // set the widget properties
                signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
                // add the field
                PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);
                /* sef departament signature */
                signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)330.4, (float)321 - (noRows * (float)22.4), (float)133.9, (float)40.1));
                signatureField.setFieldName("signatureSefDepartament");
                signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
                PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);
            }else{
                PdfSignatureFormField signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)330.4, (float)361.5 - (noRows * (float)22.4), (float)133.9, (float)40.1));
                signatureField.setFieldName("signatureSefDepartament");
                signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
                PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);
            }

            document.close();
            fullPath = pdfFilePath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
