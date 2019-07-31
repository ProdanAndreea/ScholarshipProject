package com.siemens.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *   Starting point for the Client application.
 */

public class ClientStart extends Application {

    public static Stage primaryStage;
    public static final String fileDirectoryPath = "C:\\Users\\Public\\Desktop\\Bilete Invoire";
    public ClientStart() {}

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client_view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Invoire");
        stage.setScene(scene);
        stage.show();

        primaryStage = stage;
    }

    public static void main(String[] args) {


        launch(args);
    }
}
