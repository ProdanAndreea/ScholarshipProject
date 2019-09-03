package com.siemens.view;
import com.siemens.controller.PaginaSefController;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.*;

import java.nio.file.Files;
import java.security.CodeSource;

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
    public static String acrobatCommand;
    public static Logger logger;
    public static boolean alreadyParsed = false;
    private String jarDir;
    public static  String[] parameterString;
    public ClientStart() {}

    private static void dismissSecurityCertificate(){
        //UNDO the loadSecurityPath method so that the resource security certificate will be deleted from the java home
        String javaHome = System.getProperty("java.home");
        String javaLibPath = javaHome + "/lib/security/cacerts";
        File fileToDismiss = new File(javaLibPath);
        File fileToRecover = new File(javaLibPath + "_old");
        fileToDismiss.delete();
        fileToRecover.renameTo(new File(javaLibPath));
    }
    private void loadSecurityCertificate(){
        try{
            Runtime runtime = Runtime.getRuntime();
            //String command = "java -Djavax.net.ssl.trustStore="+jarDir+"/Security/cacerts";
            String javaHome = System.getProperty("java.home");
//            String javaKeyTool = "\""+javaHome + "/bin/keytool.exe\"";
            String javaLibPath = javaHome + "/lib/security/cacerts";
            String fileToImportPath = jarDir+"/Security/cacerts";
//            String command =  javaKeyTool + " -keystore " + javaLibPath + fileImport + " -storepass changeit";
//            File resourceCertificate = new File(fileToImportPath);
//            File file = new File(javaLibPath);
//            Process process = Runtime.getRuntime().exec("powershell Start-Process powershell -Verb runAs -ArgumentList `" +
//                    "ren \"" + javaLibPath + "\" \"" + javaLibPath + "_old\"`");
            javaLibPath = javaLibPath.replace("/", "\\");
            fileToImportPath = fileToImportPath.replace("/", "\\");
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c",
                    jarDir + "/rename.bat - Shortcut.lnk \"" + javaLibPath + "\" \"" + fileToImportPath + "\"");
            pb.start();


            //File tempCertificate = new File(javaLibPath + "_old");
            //rename the current certificate so it wont be lost
            //file.renameTo(tempCertificate);
            //copy the certificate required to run the application to the corresponding path
//            Files.copy(resourceCertificate.toPath(), file.toPath());
//            runtime.exec(command);
        }catch (Exception e){
            logger.severe(e.getMessage());
            System.exit(0);
        }

    }
    private void initializeLogger(){
        logger = Logger.getLogger("app_logs");
        try {

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
            logger.severe(e.getMessage());
        }

    }

    private void loadUserProperties()throws Exception{

        Properties property = new Properties();
        // get the path of the Jar; parse the file line by line and decode it
        try {
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
            FileInputStream file = new FileInputStream(jarDir + "\\mail.properties");
            //load all the properties from this file
            prop.load(file);
            //we have loaded the properties, so close the file handle
            file.close();
            senderMail = decodeMessage(prop.getProperty(encodeMessage("username")));

        }catch (Exception e)
        {
            logger.severe(e.getMessage());
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        String javaHome = System.getProperty("java.home");

        try{
            CodeSource codeSource = ClientStart.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            jarDir = jarFile.getParentFile().getPath();
        }catch (Exception e){
            e.printStackTrace();
        }
        initializeLogger();
        loadSecurityCertificate();
        getAcrobatExe();
        try{
            loadUserProperties();
            loadMailProperties();
            //The department leader should not have access to the user code.
            primaryStage = stage;
            Parent root = FXMLLoader.load(getClass().getResource("/client_view.fxml"));
            primaryStage.setResizable(false);
            root.setId("pane");
            Scene scene = new Scene(root);

            scene.getStylesheets().add("style/client_view.css");

            primaryStage.setTitle("Invoire");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();


        }catch (IOException e) {
            try{
                Parent root = FXMLLoader.load(ClientStart.class.getResource("/configuratii.fxml"));
                root.setId("pane");
                Scene scene = new Scene(root);

                scene.getStylesheets().add("style/configuratii.css");

                stage.setTitle("Configuratii");
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
                primaryStage = stage;

            }catch (Exception e1){
                e1.printStackTrace();
                logger.info(e1.getMessage());
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.severe(e.getMessage());
        }

    }

    private void getAcrobatExe(){
       try {
           String reg = "HKCR\\Applications\\AcroRD32.exe\\shell\\Read\\command";
           Process process = Runtime.getRuntime().exec("reg query " + reg + " /ve");

           StreamReader reader = new StreamReader(process.getInputStream());
           reader.start();
           process.waitFor();
           reader.join();
           acrobatCommand = reader.getResult().split("\"")[1];
       }
       catch (Exception e) {
           logger.severe("PLEASE INSTALL ACROBAT READER!");
           System.exit(0);
       }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw= new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
            }
        }

        public String getResult() {
            return sw.toString();
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

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("EXECUTES");
                //dismissSecurityCertificate();
            }
        }, "Shutdown-thread"));
        parameterString = args;
        launch(args);

    }
}
