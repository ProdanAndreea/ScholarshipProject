package com.siemens.controller;

import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;
import com.siemens.view.ClientStart;
import com.siemens.xml.XMLMapper;
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


    public FormsController(){}

    private void getSuperiors(){
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, xmlLabel.getText()).getSuperiors();

        List <Superior> directLeaders = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DIRECT)).collect(Collectors.toList());
        List<Superior> departmentLeaders = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList());

        String[] sefDirectChoices;
        String[] sefDepartamentChoices;
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
                    xmlLabel.setText(xmlFile.getAbsolutePath().replace("\\", "/"));
                    xmlLabel.setOpacity(100);
                    departmentLeader.setDisable(false);
                    directLeader.setDisable(false);
                    getSuperiors();
                }
            }
        });
        //ACTION FOR CREATING THE ENCRYPTED DOCUMENTS
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (
                        directLeader.getValue() == null ||
                                departmentLeader.getValue() == null ||
                                nameText.getCharacters().toString().isEmpty() ||
                                mailText.getCharacters().toString().isEmpty() ||
                                passwordText.getCharacters().toString().isEmpty()
                        ){
                    warningLabel.setOpacity(100);
                }
                else {
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
                            properties.setProperty(encodeMessage("appUser"), encodeMessage(nameText.getCharacters().toString()));
                            properties.setProperty(encodeMessage("userOccupiedPosition"), encodeMessage("User"));
                            properties.setProperty(encodeMessage("superiorName"), encodeMessage(directLeader.getValue().toString()));
                            properties.setProperty(encodeMessage("departmentSuperiorName"), encodeMessage(departmentLeader.getValue().toString()));
                            properties.setProperty(encodeMessage("pathToXML"), encodeMessage(xmlLabel.getText()));
                            //WARNING! THIS IS ONLY A TEMPORARY SOLUTION UNTIL WE DECIDE WHAT THE ACTUAL PATH SHOULD BE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            properties.setProperty(encodeMessage("pathToDocuments"), encodeMessage(jarDir + "\\Bilete Invoire"));

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
                        System.exit(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void initialize(){
        xmlLabel.setOpacity(0);
        warningLabel.setOpacity(0);
        departmentLeader.setDisable(true);
        directLeader.setDisable(true);
        setHandlers();
    }

}
