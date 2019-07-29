package com.siemens.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;


public class ConfirmareController {

    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;


    public void initialize(ClientController clientController) {

        btnOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientController.generatePDF();
                closeWindow();
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        // get a handle to the stage
        Stage stage = (Stage) btnOk.getScene().getWindow();
        // close the stage
        stage.close();
    }

}
