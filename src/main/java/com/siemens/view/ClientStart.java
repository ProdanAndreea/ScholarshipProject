package com.siemens.view;

import com.siemens.configuration.MailConfiguration;
import com.siemens.controller.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *   Starting point for the Client application.
 */

public class ClientStart extends Application {

    public static Stage primaryStage;
    public static String senderMail;
    public static String userName;
    public static String userPosition;
    public static String superiorName;
    public static String departmentSuperior;
    public static String superiorsFilePath;
    public static String fileDirectoryPath ;
    public static Logger logger;
    public ClientStart() {}

    private void initializeLogger(){
        logger = Logger.getLogger("app_logs");


        try {
            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            FileHandler fh;
            // This block configure the logger with handler and formatter
            fh = new FileHandler(jarDir + "/app_logs.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void  restartApplication()
    {
        try{
            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            Process process = new ProcessBuilder(jarDir + "/Bilete Invoire.exe").start();
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void loadUserProperties()throws Exception{

        Properties property = new Properties();
        // get the path of the Jar; parse the file line by line and decode it
        CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
        File jarFile = null;
        try {
            jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            //load the file handle for main.properties
            FileInputStream file = new FileInputStream(jarDir + "\\user.properties");
            //load all the properties from this file
            property.load(file);
            //we have loaded the properties, so close the file handle
            file.close();
            //load the data for the user
            userName = decodeMessage(property.getProperty(encodeMessage("appUser")));
            userPosition = decodeMessage(property.getProperty(encodeMessage("userOccupiedPosition")));
            superiorName = decodeMessage(property.getProperty(encodeMessage("superiorName")));
            departmentSuperior = decodeMessage(property.getProperty(encodeMessage("departmentSuperiorName")));
            superiorsFilePath = decodeMessage(property.getProperty(encodeMessage("pathToXML")));
            fileDirectoryPath = decodeMessage(property.getProperty(encodeMessage("pathToDocuments")));

        }
        catch (Exception e){
            throw e;
        }

    }

    private void loadMailProperties(){
        //file should be parsed line by line and decoded line by line.
        try {
            Properties prop = new Properties();
            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            FileInputStream file = new FileInputStream(jarDir + "\\mail.properties");
            //load all the properties from this file
            prop.load(file);
            //we have loaded the properties, so close the file handle
            file.close();
            senderMail = decodeMessage(prop.getProperty(encodeMessage("username")));

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeLogger();
        try{
            loadUserProperties();
            loadMailProperties();
            //The department leader should not have access to the user code.

                Parent root = FXMLLoader.load(getClass().getResource("/client_view.fxml"));
                root.setId("pane");
                Scene scene = new Scene(root);

                scene.getStylesheets().add("style/client_view.css");

                stage.setTitle("Invoire");
                stage.setScene(scene);
                stage.show();
                primaryStage = stage;
        }catch (IOException e) {
            try{
                Parent root = FXMLLoader.load(ClientStart.class.getResource("/configuratii.fxml"));
                root.setId("pane");
                Scene scene = new Scene(root);

                scene.getStylesheets().add("style/configuratii.css");

                stage.setTitle("Form Proprietati");
                stage.setScene(scene);
                stage.show();
                primaryStage = stage;

            }catch (Exception e1){
                e1.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String decodeMessage(String input){
        StringBuilder decryptedMessage = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (i % 2 == 0) {
                decryptedMessage.append((char)(input.charAt(i) - 4));
            } else {
                decryptedMessage.append((char)(input.charAt(i) - 3));
            }
        }
        return  decryptedMessage.toString();
    }

    public static String encodeMessage(String input){
        StringBuilder encryptedMessage = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (i % 2 == 0) {
                encryptedMessage.append((char)(input.charAt(i) + 4));
            } else {
                encryptedMessage.append((char)(input.charAt(i) + 3));
            }
        }
        return  encryptedMessage.toString();
    }


    public static void main(String[] args) {launch(args);}

}
