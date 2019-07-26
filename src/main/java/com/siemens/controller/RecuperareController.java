package com.siemens.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 26, 2019
 * @Description:
 *  Controller for the interface elements of teh view add_recuperare.
 */
public class RecuperareController {

    @FXML
    private Button okButton;

    public RecuperareController() {}

    public void initialize() {

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Closing dialog");

                // get a handle to the stage
                Stage stage = (Stage) okButton.getScene().getWindow();
                // do what you have to do
                stage.close();
            }
        });
    }
}
