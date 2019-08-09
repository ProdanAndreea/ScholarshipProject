package com.siemens.controller;

import com.siemens.view.ClientStart;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.security.CodeSource;
import java.util.Properties;

import static com.siemens.view.ClientStart.encodeMessage;
import static com.siemens.view.ClientStart.primaryStage;


public class FormsController {
    @FXML
    private TextField nameText;
    @FXML
    private TextField mailText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private Button browseXML;
    @FXML
    private Button browseRootFolder;
    @FXML
    private ComboBox directLeader;
    @FXML
    private ComboBox departmentLeader;
    @FXML
    private Button finishButton;
    @FXML
    private Label xmlLabel;
    @FXML
    private Label directoryLabel;

    public FormsController(){}

    private void setHandlers(){

        finishButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("ENTERED");
                if (
                        directLeader.getValue() == null ||
                                departmentLeader.getValue() == null ||
                                nameText.getCharacters().toString().isEmpty() ||
                                mailText.getCharacters().toString().isEmpty() ||
                                passwordText.getCharacters().toString().isEmpty()
                        )
                    finishButton.setDisable(true);
            }
        });
//        finishButton.setOnMouseExited(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if(event.getEventType() == MouseEvent.MOUSE_EXITED){
//                    System.out.println("EXIT REGISTERED");
//                    finishButton.setDisable(false);
//                }
//
//            }
//        });

        browseXML.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open XML With Superiors Information");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("XML files","*.xml")
                );
                File xmlFile = fileChooser.showOpenDialog(primaryStage);
                if(xmlFile != null){
                    xmlLabel.setText(xmlFile.getAbsolutePath());
                    xmlLabel.setOpacity(100);
                }
            }
        });

        browseRootFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser rootDirectory = new DirectoryChooser();
                rootDirectory.setTitle("Open Root Folder For Requests");
                File chosenDirectory = rootDirectory.showDialog(primaryStage);
                if(chosenDirectory != null){
                    directoryLabel.setText(chosenDirectory.getAbsolutePath());
                    directoryLabel.setOpacity(100);
                }
            }
        });
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
                    File jarFile = new File(codeSource.getLocation().toURI().getPath());
                    String jarDir = jarFile.getParentFile().getPath();
                    //set absolute paths according to the location of source code after the installation
                    String pathToUserProp = jarDir + "\\user.properties";
                    String pathToMail = jarDir + "\\mail.properties";
                    File userFile = new File(pathToUserProp);
                    File mailFile = new File(pathToMail);
                    //if files created successfully
                    if(userFile.createNewFile()){
                        //insert properties into the user configuration file
                        Writer writer = new FileWriter(userFile);
                        Properties properties = new Properties();
                        //store the properties encoded with the established alg.
                        properties.setProperty(encodeMessage("appUser"), encodeMessage(nameText.toString()));
                        properties.setProperty(encodeMessage("superiorName"), encodeMessage(directLeader.getValue().toString()));
                        properties.setProperty(encodeMessage("departmentSuperiorName"), encodeMessage(departmentLeader.getValue().toString()));
                        //toDo CHOSE A PATH BASED ON FILE BROWSER FRO THE XML
                        properties.setProperty(encodeMessage("pathToXML"), encodeMessage(""));
                        //toDO CHOSE A PATH BASED ON A FILE BROWSER FOR THE ROOT OF ALL THE DOCUMENTS
                        properties.setProperty(encodeMessage("pathToDocuments"), encodeMessage(""));
                        properties.store(writer, "User Information");
                        writer.close();
                    }
                    if(mailFile.createNewFile()){
                        //insert properties into the mail configuration file
                        Writer writer = new FileWriter(mailFile);
                        Properties properties = new Properties();
                        properties.setProperty(encodeMessage("username"), encodeMessage(mailText.toString()));
                        properties.setProperty(encodeMessage("password"), encodeMessage(passwordText.toString()));

                        //store the properties in the file
                        properties.store(writer, "E-mail information");
                        writer.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void initialize(){
        xmlLabel.setOpacity(0);
        directoryLabel.setOpacity(0);
        departmentLeader.setValue("ALA");
        directLeader.setValue("BALA");

        setHandlers();
    }

}
