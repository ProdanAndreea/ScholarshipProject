package com.siemens.controller;

import com.siemens.model.Leave;
import com.siemens.model.Recovery;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 26, 2019
 * @Description:
 *  Controller for the interface elements of teh view add_recuperare.
 */
public class RecuperareController {

    @FXML
    private Button okButton;
    @FXML
    private ComboBox nrOreRecuperare;
    @FXML
    private DatePicker datePickerRecuperare;
    @FXML
    private Label timeToRecoverLabel;
    @FXML
    private Text recoveryCompleteText;

    public RecuperareController() {}

    public void initialize(Leave leave, ObservableList<Recovery> recoveryView, DatePicker datePickerInvoire) {

        recoveryCompleteText.setOpacity(0);

        ClientController.setDatePickerFormat(datePickerRecuperare);

        int i = 0;
        while(
                i < ClientController.nrOre.length &&
                ClientController.nrOre[i].compareTo(leave.getHoursToCover().toString()) <= 0){

            nrOreRecuperare.getItems().add(ClientController.nrOre[i]);
            i++;
        }

        if(nrOreRecuperare.getItems().size() == 0){
            recoveryCompleteText.setOpacity(100);
            timeToRecoverLabel.setDisable(true);
        }

        okButton.setDisable(true);

        timeToRecoverLabel.setText(
                "Mai sunt de recuperat " + leave.getHoursToCover().toString() + " ore."
        );

        nrOreRecuperare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(datePickerRecuperare.getValue() != null)
                    okButton.setDisable(false);
            }
        });
        datePickerRecuperare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(nrOreRecuperare.getValue() != null)
                    okButton.setDisable(false);
            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // get a handle to the stage
                Stage stage = (Stage) okButton.getScene().getWindow();
                // do what you have to do
                Recovery recovery = new Recovery(
                        leave.getLeaveDate(),
                        datePickerRecuperare.getValue(),
                        LocalTime.parse(nrOreRecuperare.getValue().toString(), ClientController.hourFormatter)
                );
                leave.setCoveredHours(
                        LocalTime.parse(nrOreRecuperare.getValue().toString(), ClientController.hourFormatter)
                );
                recoveryView.add(recovery);
                stage.close();
            }
        });

        disablePastDates(datePickerRecuperare, LocalDate.parse(datePickerInvoire.getValue().toString()));
    }

    private void disablePastDates(DatePicker datePicker, LocalDate selectedDate) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(selectedDate) < 0 );
            }
        });
    }
}
