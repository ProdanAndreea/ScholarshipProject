package com.siemens.controller;

import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;
import com.siemens.view.ClientStart;
import com.siemens.xml.XMLMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
    private ComboBox directLeader;
    @FXML
    private ComboBox departmentLeader;
    @FXML
    private Button finishButton;
    @FXML
    private Label xmlLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private Button changeRootButton;
    @FXML
    private Label defaultRootDirectory;

    private String[] sefDirectChoices;

    private String[] sefDepartamentChoices;

    private String position;

    public FormsController(){}

    private void parseSuperiors(){
        if(
                Arrays.stream(sefDirectChoices)
                        .filter(superior -> superior.equals(nameText.getCharacters().toString()))
                        .findFirst().isPresent()
                ){
            position = "Team Leader";
            departmentLeader.setDisable(false);
            directLeader.setDisable(true);
        }
        else if(
                Arrays.stream(sefDepartamentChoices)
                        .filter(superior ->  superior.equals(nameText.getCharacters().toString()))
                        .findFirst().isPresent()
                ){
            position = "Department Leader";
            departmentLeader.setDisable(true);
            directLeader.setDisable(true);
        }
        else{
            position = "User";
            departmentLeader.setDisable(false);
            directLeader.setDisable(false);
        }
    }
    private void getSuperiors(){
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, xmlLabel.getText()).getSuperiors();

        List <Superior> directLeaders = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DIRECT)).collect(Collectors.toList());
        List<Superior> departmentLeaders = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList());

        sefDirectChoices = new String[directLeaders.size()];
        for (int i = 0; i < directLeaders.size(); i++) {
            sefDirectChoices[i] = directLeaders.get(i).getName();
        }
        directLeader.getItems().addAll(sefDirectChoices);

        sefDepartamentChoices = new String[departmentLeaders.size()];
        for (int i = 0; i < departmentLeaders.size(); i++) {
            sefDepartamentChoices[i] = departmentLeaders.get(i).getName();
        }
        departmentLeader.getItems().addAll(sefDepartamentChoices);
    }

    private void setHandlers(){
        //BROWSE FOR THE XML FILE AND LOAD OPTIONS INTO DROPDOWNS
        nameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(nameText.getCharacters().length() > 0)
                    browseXML.setDisable(false);
                if(!xmlLabel.getText().equals("")){
                    parseSuperiors();
                }
            }
        });
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
                    directLeader.getItems().clear();
                    departmentLeader.getItems().clear();
                    xmlLabel.setText(xmlFile.getAbsolutePath().replace("\\", "/"));
                    xmlLabel.setOpacity(100);
                    getSuperiors();
                    //automatically determine the position of the person who installs the app
                    parseSuperiors();
                }
            }
        });
        //BROWSE FOR FILE
        changeRootButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Choose where the root folder is");
                File directory = directoryChooser.showDialog(primaryStage);
                if(directory != null){
                    defaultRootDirectory.setText(directory.getAbsolutePath().replace("\\","/"));
                }
            }
        });
        //ACTION FOR CREATING THE ENCRYPTED DOCUMENTS
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (
                        (directLeader.getValue() == null && !(position.equals("Team Leader") || position.equals("Department Leader"))) ||
                                (departmentLeader.getValue() == null && !position.equals("Department Leader")) ||
                                nameText.getCharacters().toString().isEmpty()  ||
                                mailText.getCharacters().toString().isEmpty() ||
                                passwordText.getCharacters().toString().isEmpty()
                        ){
                    warningLabel.setOpacity(100);
                }
                else {
                    try{

                        String directLeaderName, departmentLeaderName;
                        if(position.equals("Team Leader")){
                            directLeaderName = "";
                            departmentLeaderName = departmentLeader.getValue().toString();
                        }
                        else if(position.equals("Department Leader")){
                            directLeaderName = "";
                            departmentLeaderName = "";
                        }
                        else {
                            directLeaderName = directLeader.getValue().toString();
                            departmentLeaderName = departmentLeader.getValue().toString();
                        }


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
                            properties.setProperty(encodeMessage("appUser"), encodeMessage(nameText.getCharacters().toString()));
                            properties.setProperty(encodeMessage("userOccupiedPosition"), encodeMessage(position));
                            properties.setProperty(encodeMessage("superiorName"), encodeMessage(directLeaderName));
                            properties.setProperty(encodeMessage("departmentSuperiorName"), encodeMessage(departmentLeaderName));
                            properties.setProperty(encodeMessage("pathToXML"), encodeMessage(xmlLabel.getText()));
                            //WARNING! THIS IS ONLY A TEMPORARY SOLUTION UNTIL WE DECIDE WHAT THE ACTUAL PATH SHOULD BE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            properties.setProperty(encodeMessage("pathToDocuments"), encodeMessage(defaultRootDirectory.getText()));

                            properties.store(writer, "User Information");
                            writer.close();
                        }
                        if(mailFile.createNewFile()){
                            //insert properties into the mail configuration file
                            Writer writer = new FileWriter(mailFile);
                            Properties properties = new Properties();
                            properties.setProperty(encodeMessage("username"), encodeMessage(mailText.getCharacters().toString()));
                            properties.setProperty(encodeMessage("password"), encodeMessage(passwordText.getCharacters().toString()));

                            //store the properties in the file
                            properties.store(writer, "E-mail information");
                            writer.close();
                        }
                        ClientStart.restartApplication();
                    }catch (Exception e){
                        e.printStackTrace();
                        ClientStart.logger.severe(e.getMessage());
                    }
                }
            }
        });
    }

    public void initialize(){
        try{
            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            defaultRootDirectory.setText(jarDir.replace("\\", "/") + "/Invoiri");
        }catch (Exception e){
            e.printStackTrace();
            ClientStart.logger.severe(e.getMessage());
        }

        browseXML.setDisable(true);
        xmlLabel.setText("");
        xmlLabel.setOpacity(0);
        warningLabel.setOpacity(0);
        departmentLeader.setDisable(true);
        directLeader.setDisable(true);
        setHandlers();
    }

}
