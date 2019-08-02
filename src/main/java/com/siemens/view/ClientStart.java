package com.siemens.view;

import com.siemens.configuration.MailConfiguration;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *   Starting point for the Client application.
 */

public class ClientStart extends Application {

    public static Stage primaryStage;
    public static final String fileDirectoryPath = loadPath();
    public static final String senderMail = loadMail();
    public ClientStart() {}

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client_view.fxml"));
        root.setId("pane");
        Scene scene = new Scene(root);

        scene.getStylesheets().add("style/client_view.css");

        stage.setTitle("Invoire");
        stage.setScene(scene);
        stage.show();

        primaryStage = stage;
    }
    private static String loadPath(){
        Properties property = new Properties();
        String userProperties = "user.properties";
        InputStream inputStream = ClientStart.class.getClassLoader().getResourceAsStream(userProperties);
        try{
            if (inputStream != null) {
                property.load(inputStream);
                return property.getProperty("pathToDocuments");
            }
            else{
                throw new FileNotFoundException("property file '" + userProperties + "' not found in the classpath");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ERROR LOADING PATH";
    }
    private static String loadMail(){
        try {
            Properties prop = new Properties();
            String configFile = "mail.properties";

            InputStream inputStream = MailConfiguration.class.getClassLoader().getResourceAsStream(configFile);

            if (inputStream != null) {
                prop.load(inputStream);
                return prop.getProperty("username");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "ERROR LOADING MAIL";
    }

    public static void main(String[] args) {
        launch(args);
    }

    class WindowButtons extends HBox {

        public WindowButtons() {
            Button closeBtn = new Button("X");

            closeBtn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {
                    Platform.exit();
                }
            });

            this.getChildren().add(closeBtn);
        }
    }
}
