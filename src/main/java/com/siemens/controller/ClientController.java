package com.siemens.controller;

import com.jfoenix.controls.JFXTimePicker;
import com.siemens.view.ClientStart;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.controlsfx.control.textfield.TextFields;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *  Controller for the interface elements of the view client_view.
 */
public class ClientController {

    private final String pattern = "dd-MM-yyyy";
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String[] possibleChoises = {
            "Application Developer",
            "Applications Engineer",
            "Associate Developer",
            "Computer Programmer",
            "Developer",
            "Java Developer",
            "Junior Software Engineer",
            ".NET Developer",
            "Programmer",
            "Programmer Analyst",
            "Senior Applications Engineer",
            "Senior Programmer",
            "Senior Programmer Analyst",
            "Senior Software Engineer",
            "Senior System Architect",
            "Senior System Designer",
            "Senior Systems Software Engineer",
            "Software Architect",
            "Software Developer",
            "Software Engineer",
            "Software Quality Assurance Analyst",
            "System Architect",
            "Systems Software Engineer",
            "Front End Developer",
            "Senior Web Administrator",
            "Senior Web Developer",
            "Web Administrator",
            "Web Developer",
            "Webmaster"
    };

    private final String[] nrOre = {
            "00:30",
            "01:00",
            "01:30",
            "02:00",
            "02:30",
            "03:00",
            "03:30",
            "04:00",
    };

    @FXML
    private DatePicker datePickerInvoire;
    @FXML
    private DatePicker datePickerRecuperare;
    @FXML
    private TextField pozitieAngajat;
    @FXML
    private TextField nume;
   // @FXML
   // private JFXTimePicker timePickerInvoire;
    @FXML
    private ComboBox nrOreInvoire;
    @FXML
    private ComboBox nrOreRecuperare;
    @FXML
    private Button addRecuperare;
    //@FXML
    //private Label remainedHours;

    public ClientController() { }

    // called by the FXML loader after the labels declared above are injected
    public void initialize() {

        setDatePickerFormat(datePickerInvoire);
        setDatePickerFormat(datePickerRecuperare);

        // disable add button
        addRecuperare.setDisable(true);
        nrOreInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    addRecuperare.setDisable(false);
            }
        });

        // autocomplete
        TextFields.bindAutoCompletion(pozitieAngajat, possibleChoises);

        nrOreInvoire.getItems().addAll(nrOre);
        nrOreRecuperare.getItems().addAll(nrOre);

        addRecuperare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Opening dialog");

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/add_recuperare.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Ore Recuperare");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MM-yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }
}
