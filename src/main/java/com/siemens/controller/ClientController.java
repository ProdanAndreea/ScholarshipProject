package com.siemens.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *  Controller for the interface elements of the client.
 */
public class ClientController {

    public ClientController() { }


    @FXML
    private TextArea textarea;

    @FXML
    private Button test_button;

    @FXML
    private DatePicker datePicker;

    // called by the FXML loader after the labels declared above are injected
    public void initialize() {

        test_button.setOnAction((event) -> {
            textarea.appendText("Hello!");
        });
    }
}
