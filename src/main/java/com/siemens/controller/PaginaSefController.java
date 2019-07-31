package com.siemens.controller;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
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
import java.util.ArrayList;
import java.util.List;

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
            requestObservableList.add(
                    new Request(file.getName(), "", file, false)
            );
        }
    }
    private void refreshItems(){
        requestListView.setItems(null);
        requestListView.setItems(requestObservableList);
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
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            request.setSigned(true);
                        }

                    });
                    refreshItems();
                }
                if(event.getClickCount() == 1){
                    Request selectedRequest = (Request) requestListView.getSelectionModel().getSelectedItem();
                    String source = ClientStart.fileDirectoryPath + "\\"+selectedRequest.getFile().getName();
                    String temp = ClientStart.fileDirectoryPath + "\\temp"+selectedRequest.getFile().getName();
                    if(selectedRequest.isSigned()){
                        acceptButton.setDisable(false);
                        denyButton.setDisable(false);
                        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try{
                                    Document document = new Document(
                                            new PdfDocument(
                                                    new PdfReader(source),
                                                    new PdfWriter(temp)
                                            )
                                    );
                                    document.add(
                                            new Paragraph("CERERE APROBATA")
                                                    .setFontSize(24)
                                                    .setFontColor(ColorConstants.GREEN )
                                                    .setTextAlignment(TextAlignment.RIGHT)
                                    );
                                    document.close();
                                    File newDocument = new File(temp);
                                    selectedRequest.getFile().delete();
                                    newDocument.renameTo(selectedRequest.getFile());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
//
//                                MailConfiguration.sendMessage(
//                                        selectedRequest.getEmailSender(),
//                                        "CONFIRMARE CERERE",
//                                        "",
//                                        ClientStart.fileDirectoryPath+"\\"+selectedRequest.getFileName()
//                                );
                                refreshItems();
                            }
                        });
                        denyButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try{
                                    Document document = new Document(
                                            new PdfDocument(
                                                    new PdfReader(source),
                                                    new PdfWriter(temp)
                                            )
                                    );
                                    document.add(
                                            new Paragraph("CERERE RESPINSA")
                                                    .setFontSize(24)
                                                    .setFontColor(ColorConstants.RED)
                                                    .setTextAlignment(TextAlignment.RIGHT)
                                    );
                                    document.close();
                                    File newDocument = new File(temp);
                                    selectedRequest.getFile().delete();
                                    newDocument.renameTo(selectedRequest.getFile());
//                                    File actuallDocument = new File(source);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                refreshItems();
//                                MailConfiguration.sendMessage(
//                                        selectedRequest.getEmailSender(),
//                                        "RESPINGERE CERERE",
//                                        "",
//                                        ClientStart.fileDirectoryPath+"\\"+selectedRequest.getFile().getName()
//                                );

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
