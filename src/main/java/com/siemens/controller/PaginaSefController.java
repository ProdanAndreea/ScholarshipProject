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
import com.siemens.model.Request;
import com.siemens.view.ClientStart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaginaSefController {
    private ObservableList<Request> requestObservableList = FXCollections.observableArrayList();

    private final File folder = new File(ClientStart.fileDirectoryPath);
    @FXML
    private Button acceptButton;

    @FXML
    private Button denyButton;

    @FXML
    private ListView requestListView;

    private void populateRequests(){
        for(File file : folder.listFiles()){
            try{
                PdfDocument pdf = new PdfDocument(new PdfReader(ClientStart.fileDirectoryPath + "\\" +file.getName()));
                PdfAcroForm form  = PdfAcroForm.getAcroForm(pdf, true);
                SignatureUtil signUtil = new SignatureUtil(pdf);
                boolean isSigned = true;
                if(signUtil.getSignatureNames().size() == 0)
                    isSigned = false;
                Map<String, PdfFormField> fields = form.getFormFields();
                PdfFormField field1 = fields.get("email");
                PdfFormField sentField = fields.get("hasBeenSent");
                boolean isSent = false;
                if(sentField != null)
                    isSent = true;
                String senderMail = field1.getValueAsString();
                pdf.close();
                requestObservableList.add(
                        new Request(file.getName(), senderMail, file, isSigned, isSent)
                );
            }catch (Exception e){
                e.printStackTrace();
            }

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
                                Thread.sleep(2000);
                                File file = new File(request.getFile().getAbsolutePath());
                                while(!file.renameTo(file)){
                                    Thread.sleep(100);
                                }

                                PdfDocument pdf = new PdfDocument(
                                        new PdfReader(ClientStart.fileDirectoryPath + "\\" +selectedRequest.getFile().getName())
                                );
                                SignatureUtil signUtil = new SignatureUtil(pdf);
                                if(signUtil.getSignatureNames().size() != 0)
                                    request.setSigned(true);
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
                                        "CERERE ACCEPTATA",
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
                                        "CONFIRMED",
                                        ClientStart.fileDirectoryPath+"\\"+selectedRequest.getFileName()
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
                                        "REJECTED",
                                        ClientStart.fileDirectoryPath+"\\"+selectedRequest.getFile().getName()
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

    }

    public void initialize(){
        acceptButton.setDisable(true);
        denyButton.setDisable(true);
        requestListView.setItems(requestObservableList);
        populateRequests();
        setHandlers();
    }
}
