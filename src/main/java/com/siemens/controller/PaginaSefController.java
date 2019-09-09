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
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.siemens.view.ClientStart.alreadyParsed;
import static com.siemens.view.ClientStart.senderMail;
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
    private String requestUserPosition;
    private LocalDate leaveDate;
    private LocalDate creationDate;
    private LocalTime creationHour;
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
            PdfFormField firstRecovery = fields.get("firstRecovery");
            PdfFormField secondRecovery = fields.get("secondOptionalRecovery");
            PdfFormField thirdRecovery = fields.get("thirdOptionalRecovery");
            PdfFormField fourthRecovery = fields.get("fourthOptionalRecovery");
            List<Recovery> requestRecoveries = new ArrayList<>();
            boolean isSent = false;
            if(sentField != null)
                isSent = true;
            String senderMail = field1.getValueAsString();
            String fRecovery = firstRecovery.getValueAsString();
            String sRecovery = secondRecovery.getValueAsString();
            String tRecovery = thirdRecovery.getValueAsString();
            String fthRecovery = fourthRecovery.getValueAsString();
            String timeToLeave = LocalTime.parse(fRecovery.split("_")[0], ClientController.hourFormatter).format(ClientController.hourFormatter);
            LocalDate leaveDate = LocalDate.parse(fRecovery.split("_")[1], ClientController.format);
            requestRecoveries.add(
                    new Recovery(
                            leaveDate,
                            LocalDate.parse(fRecovery.split("_")[2], ClientController.format),
                            LocalTime.parse(fRecovery.split("_")[3], ClientController.hourFormatter)
                            )
            );
            //the rest of them do not necesarly have to be there
            if(!sRecovery.equals("")){
                requestRecoveries.add(
                        new Recovery(
                                leaveDate,
                                LocalDate.parse(sRecovery.split("_")[0], ClientController.format),
                                LocalTime.parse(sRecovery.split("_")[1], ClientController.hourFormatter)
                        )
                );
            }

            if(!tRecovery.equals("")){
                requestRecoveries.add(
                        new Recovery(
                                leaveDate,
                                LocalDate.parse(tRecovery.split("_")[0], ClientController.format),
                                LocalTime.parse(tRecovery.split("_")[1], ClientController.hourFormatter)
                        )
                );
            }

            if(!fthRecovery.equals("")){
                requestRecoveries.add(
                        new Recovery(
                                leaveDate,
                                LocalDate.parse(fthRecovery.split("_")[0], ClientController.format),
                                LocalTime.parse(fthRecovery.split("_")[1], ClientController.hourFormatter)
                        )
                );
            }

            pdf.close();
            return new Request(file.getName(), senderMail, file, isSigned, isSent, LocalDate.parse(parts[2], formatter), parser.parse(parts[3]), timeToLeave, requestRecoveries);
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
    private String generateResponseMessage(String status, Request request){
        StringBuilder message = new StringBuilder(status +"</b> pentru data de <b>" +
                request.getRecoveries().get(0).getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</b>, durata: <b>" +
                request.getHoursToRecoverForMail() + "</b> ore.<br><br>" +
                "Datele alese pentru a recupera: <br>"
        );

        for (Recovery recovery: request.getRecoveries()) {
            message.append("<b>" +  recovery.getRecoveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</b> - <b>" +
                    recovery.getNumberOfHours().toString() + "</b> ore<br>"
            );
        }
        return message.toString();
    }
    private void showMailAlert(String statusMessage){
        if(!statusMessage.equals("Respose has been sent succsessfully!")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setContentText(statusMessage);
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification sent");
            alert.setContentText(statusMessage);
            alert.showAndWait();
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

                                Task backgroundSigning = new Task() {
                                    @Override
                                    protected Object call() throws Exception {

                                        if (isCancelled()) {
                                            updateMessage("Cancelled");
                                            return 0;
                                        }
                                        ProcessBuilder processBuilder = new ProcessBuilder("\""+ClientStart.acrobatCommand+"\"", "\""+commandPath+"\"");
                                        processBuilder.start();
                                        Thread.sleep(3000);
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
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException interrupted) {
                                            if (isCancelled()) {
                                                updateMessage("Cancelled");
                                                return 0;
                                            }
                                        }
                                        return 0;
                                    }
                                };
                                backgroundSigning.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                                    @Override
                                    public void handle(WorkerStateEvent event) {
                                        refreshItems();
                                    }
                                });
                                new Thread(backgroundSigning).start();

//                                Process p = Runtime.getRuntime().exec("cmd /C \""+ ClientStart.acrobatCommand + "\"" + " " + "\"" +commandPath + "\"");
//                                p.waitFor();


//                                Runtime.getRuntime()
//                                        .exec("rundll32 url.dll,FileProtocolHandler "+commandPath).waitFor();
//                                processBuilder.start().waitFor();


                            }catch (Exception e){
                                e.printStackTrace();
                                ClientStart.logger.severe(e.getMessage());
                            }
                        }
                    });
//                    refreshItems();
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


                                try{
                                    MailConfiguration.sendMessage(
                                            selectedRequest.getEmailSender(),
                                            "[INVOIRI]Confirmare cerere:",
                                            generateResponseMessage("Se aproba cerearea de invoire: ",selectedRequest)
                                    );
                                    showMailAlert("Respose has been sent succsessfully!");
                                }catch (Exception e){
                                    showMailAlert(e.getMessage());
                                    return;
                                }
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


                            }
                        });
                        denyButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                try{
                                    MailConfiguration.sendMessage(
                                            selectedRequest.getEmailSender(),
                                            "[INVOIRI]Cerere Respinsa",
                                            generateResponseMessage("Se respinge cererea de invoire: ", selectedRequest)
                                    );
                                    showMailAlert("Respose has been sent succsessfully!");
                                }catch (Exception e){
                                    showMailAlert(e.getMessage());
                                    return;
                                }
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

                    acceptButton.setDisable(true);
                    denyButton.setDisable(true);

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
        if(ClientStart.parameterString.length != 0 && alreadyParsed == false){
            decodeParameters();
            alreadyParsed = true;
        }

        initializeTooltips();
        acceptButton.setDisable(true);
        denyButton.setDisable(true);
        requestListView.setItems(requestObservableList);
        setHandlers();
    }

    public void populateDepartment(List<Superior> sefiDepartment) {

        this.sefiDepartment = sefiDepartment;
        populateRequests();

    }

    private void initializeTooltips() {
//        signedToggleButton.getTooltip().setShowDelay(new Duration(500));
//        resolvedToggleButton.getTooltip().setShowDelay(new Duration(500));
//        waitingToggleButton.getTooltip().setShowDelay(new Duration(500));
//        signedToggleButton.getTooltip().setShowDuration(new Duration(9000));
//        resolvedToggleButton.getTooltip().setShowDuration(new Duration(9000));
//        waitingToggleButton.getTooltip().setShowDuration(new Duration(9000));
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

            ProcessBuilder processBuilder = new ProcessBuilder("\""+ClientStart.acrobatCommand+"\"", "\""+commandPath+"\"");
            File file = new File(commandPath);
            Task mailFile = new Task() {
                @Override
                protected Object call() throws Exception {

                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        return 0;
                    }
                    ProcessBuilder processBuilder = new ProcessBuilder("\""+ClientStart.acrobatCommand+"\"", "\""+commandPath+"\"");
                    processBuilder.start();
                    Thread.sleep(3000);
                    while(!file.renameTo(file)){
                        Thread.sleep(1000);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interrupted) {
                        if (isCancelled()) {
                            updateMessage("Cancelled");
                            return 0;
                        }
                    }
                    return 0;
                }
            };
            mailFile.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    refreshItems();
                }
            });
            new Thread(mailFile).start();
        }catch (Exception e){
            e.printStackTrace();
            ClientStart.logger.severe(e.getMessage());
        }
    }

    public void decodeParameters(){
        String[] argsArray = ClientStart.parameterString[0].split(":", 2);
        String[] parameters = argsArray[1].split(",");
        name = parameters[0].replaceAll("%20", " ");
        mailName = parameters[1];

        String[] leaders = parameters[2].split("&");
        if (leaders.length == 2) {
            teamLeadName = leaders[0].replaceAll("%20", " ");
            departmentLeadName = leaders[1].replace("%20", " ");
            this.requestUserPosition = "User";
        } else {
            departmentLeadName = leaders[0].replaceAll("%20", " ");
            this.requestUserPosition = "Team Leader";
        }

        leaveDate = LocalDate.parse(parameters[3], ClientController.format);
        time = LocalTime.parse(parameters[4], ClientController.hourFormatter);
        desiredLeave = new Leave(leaveDate, time);
        creationDate = LocalDate.parse(parameters[5], ClientController.format);
        creationHour = LocalTime.parse(parameters[6], ClientController.hourFormatterWithSeconds);
        listOfRecoveries = decipherRecoveries(leaveDate, parameters[7]);


        fullPath = getPdfFilePath();

        if (new File(fullPath).isFile()) {
            openPdf();
        } else {
            generatePdf(fullPath);
            openPdf();
        }
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, superiorsFilePath).getSuperiors();
        populateDepartment(sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList()));
    }

    private String getPdfFilePath() {

        String fullDirectory;

        String folderNameForDepartment = getFolderForSefDirect(departmentLeadName);
        String directoryForSefDirect = ClientStart.fileDirectoryPath.concat("\\").concat(folderNameForDepartment);
        new File(directoryForSefDirect).mkdir();

        fullDirectory = directoryForSefDirect.concat("\\".concat(senderMail.split("@")[0]));
        new File(fullDirectory).mkdir();

        String pdfFilePath =
                fullDirectory +"\\Invoire_" + name+
                        "_" + DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(creationDate)  +// desiredLeave.getLeaveDate().toString()
                        "_" + creationHour.toString().replaceAll(":", "-")+".pdf";

        return pdfFilePath;
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

    public void generatePdf(String pdfFilePath){
        try{
            listOfRecoveries.sort(Comparator.comparing(Recovery::getRecoveryDate));
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

            //add hidden values for recoveries
            //max 4 recoveries are avaialble
            PdfFormField firstRecovery = PdfFormField.createText(pdf);
            firstRecovery.setFieldName("firstRecovery");
            PdfFormField secondRecovery = PdfFormField.createText(pdf);
            secondRecovery.setFieldName("secondOptionalRecovery");
            PdfFormField thirdRecovery = PdfFormField.createText(pdf);
            thirdRecovery.setFieldName("thirdOptionalRecovery");
            PdfFormField fourthRecovery = PdfFormField.createText(pdf);
            fourthRecovery.setFieldName("fourthOptionalRecovery");
            firstRecovery.addKid(widget1);

            secondRecovery.addKid(widget1);
            secondRecovery.setVisibility(PdfFormField.HIDDEN);
            thirdRecovery.addKid(widget1);
            thirdRecovery.setVisibility(PdfFormField.HIDDEN);
            fourthRecovery.addKid(widget1);
            fourthRecovery.setVisibility(PdfFormField.HIDDEN);

            //only the first recovery proposal is MANDATORY, the rest are not
            firstRecovery.setValue(
                    desiredLeave.getNumberOfHours().format(ClientController.hourFormatter) + "_" +
                    desiredLeave.getLeaveDate().format(ClientController.format) + "_" +
                    listOfRecoveries.get(0).getRecoveryDate().format(ClientController.format) + "_" +
                            listOfRecoveries.get(0).getNumberOfHours().format(ClientController.hourFormatter)
            );
            firstRecovery.setVisibility(PdfFormField.HIDDEN);
            form.addField(firstRecovery, page);

            try{
                secondRecovery.setValue(
                        listOfRecoveries.get(1).getRecoveryDate().format(ClientController.format) + "_" +
                                listOfRecoveries.get(1).getNumberOfHours().format(ClientController.hourFormatter)
                );
                form.addField(secondRecovery, page);
            }catch (Exception e){
                secondRecovery.setValue("");
                form.addField(secondRecovery, page);
            }
            try{
                thirdRecovery.setValue(
                        listOfRecoveries.get(2).getRecoveryDate().format(ClientController.format) + "_" +
                            listOfRecoveries.get(2).getNumberOfHours().format(ClientController.hourFormatter)
                );
                form.addField(thirdRecovery, page);
            }catch (Exception e){
                thirdRecovery.setValue("");
                form.addField(thirdRecovery, page);
            }
            try {
                fourthRecovery.setValue(
                        listOfRecoveries.get(3).getRecoveryDate().format(ClientController.format) + "_" +
                                listOfRecoveries.get(3).getNumberOfHours().format(ClientController.hourFormatter)
                );
                form.addField(fourthRecovery, page);
            }catch (Exception e){
                fourthRecovery.setValue("");
                form.addField(fourthRecovery, page);
            }

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
            if(this.requestUserPosition.equals("User")){
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell(1, 2).add(new Paragraph("  Aprobare"))
                );
                approvalTable.addCell("Sef");
                approvalTable.addCell("Semnatura"); //.setFont(font);
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph("Direct:"))
                                .add(new Paragraph(teamLeadName)
                                ));
                approvalTable.addCell("");
                approvalTable.addCell(
                        new com.itextpdf.layout.element.Cell()
                                .add(new Paragraph("Departament:"))
                                .add(new Paragraph(departmentLeadName))
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
                                .add(new Paragraph(departmentLeadName))
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
            if(this.requestUserPosition.equals("User")){
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
           e.printStackTrace();
        }
    }
}
