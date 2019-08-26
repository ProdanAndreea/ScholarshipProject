package com.siemens.controller;

import com.siemens.model.Leave;
import com.siemens.model.Recovery;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

public class RezolvareCerereController {

    @FXML
    private Label labelName;
    @FXML
    private Label labelLeave;
    @FXML
    private Label labelRecoveries;
    @FXML
    private Button acceptButton;
    @FXML
    private Button refuseButton;

    ClientController clientController;

    public void initialize(ClientController clientController, String name, Leave leave, List<Recovery> recoveries) {

        this.clientController = clientController;

        labelName.setText(name);
        labelName.setOpacity(1);

        labelLeave.setText(leave.getLeaveDate() + " - " + leave.getHoursToCover());
        labelLeave.setOpacity(1);

        StringBuilder recoveriesText = new StringBuilder();
        for (Recovery recovery: recoveries) {
            recoveriesText.append(recovery.getRecoveryDate() + " - " + recovery.getNumberOfHours() + '\n');
        }
        labelRecoveries.setText(recoveriesText.toString());
        labelRecoveries.setOpacity(1);


        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientController.generatePdf();
                closeWindow();
            }
        });

        refuseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        // get a handle to the stage
        Stage stage = (Stage) refuseButton.getScene().getWindow();
        // close the stage
        stage.close();
    }
}
