package com.siemens.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.signatures.SignatureUtil;
import com.siemens.configuration.MailConfiguration;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;
import com.siemens.model.Client;
import com.siemens.model.Request;
import com.siemens.view.ClientStart;
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
import java.time.format.DateTimeFormatter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    private void populateRequests(){

        String pathToFiles = ClientStart.fileDirectoryPath + "\\prodan.a.andreea\\adrian";
        File newFolder = new File(pathToFiles);


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
        requestListView.setItems(null);
        requestListView.setItems(requestObservableList);
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
                                Desktop.getDesktop().open(request.getFile());
                                Thread.sleep(500);
                                File file = new File(request.getFile().getAbsolutePath());
                                while(!file.renameTo(file)){
                                    Thread.sleep(100);
                                }

                                PdfDocument pdf = new PdfDocument(
                                        new PdfReader(ClientStart.fileDirectoryPath + "\\" +selectedRequest.getFile().getName())
                                );
                                SignatureUtil signUtil = new SignatureUtil(pdf);
                                if(signUtil.getSignatureNames().size() != 0){
                                    request.setSigned(true);
                                    ArrayList<Request> listToSort = requestObservableList
                                            .stream()
                                            .collect(Collectors.toCollection(ArrayList::new));
                                    requestObservableList = mergeSort(listToSort).stream()
                                            .collect(
                                                    Collectors.collectingAndThen(toList(),
                                                            l -> FXCollections.observableArrayList(l))
                                            );
                                }

                                pdf.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }

                    });
                    refreshItems();
                }
                if(event.getClickCount() == 1){
                    Request selectedRequest = (Request) requestListView.getSelectionModel().getSelectedItem();
                    String source = ClientStart.fileDirectoryPath + "\\"+selectedRequest.getFile().getName();
                    String temp = ClientStart.fileDirectoryPath + "\\temp"+selectedRequest.getFile().getName();
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
                                        "CONFIRMARE CERERE",
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
                                        "RESPINGERE CERERE",
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
        acceptButton.setDisable(true);
        denyButton.setDisable(true);
        requestListView.setItems(requestObservableList);
        populateRequests();
        setHandlers();
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
}
