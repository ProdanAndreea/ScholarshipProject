package com.siemens.controller;

import com.siemens.view.ClientStart;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.security.CodeSource;
import java.util.Properties;

public class ChangeConfigController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text wrongPasswordWarning;
    @FXML
    private Button confirmButton;

    public void initialize(){
        wrongPasswordWarning.setOpacity(0);

        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
                    File jarFile = new File(codeSource.getLocation().toURI().getPath());
                    String jarDir = jarFile.getParentFile().getPath();
                    String pathToMail = jarDir + "\\mail.properties";
                    String pathToUserProp = jarDir + "\\user.properties";
                    Properties property = new Properties();
                    FileInputStream file = new FileInputStream(pathToMail);
                    property.load(file);
                    file.close();
                    String existingPassword = property.getProperty(ClientStart.encodeMessage("password"));
                    File mailFile = new File(pathToMail);
                    File userFile = new File(pathToUserProp);
                    if(ClientStart.decodeMessage(existingPassword).equals(passwordField.getCharacters().toString())){
                        mailFile.delete();
                        userFile.delete();
                        ClientStart.restartApplication();
                    }else{
                        wrongPasswordWarning.setOpacity(100);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
}
