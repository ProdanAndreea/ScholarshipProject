package com.siemens.controller;

import com.jfoenix.controls.JFXTimePicker;
import com.siemens.model.Leave;
import com.siemens.model.Recovery;
import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;
import com.siemens.view.ClientStart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.siemens.xml.XMLMapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *  Controller for the interface elements of the view client_view.
 */
public class ClientController {
    private List<Recovery> recoveryList;
    private Leave desiredLeave = null;
    private final String pattern = "dd-MM-yyyy";
    static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
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

    public static final String[] nrOre = {
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
    private javafx.scene.control.TableView<Recovery> recoveryTableView;

    private String[] sefDirectChoises;
    private String[] sefDepartamentChoises;
    private List<Superior> sefiDirecti;
    private List<Superior> sefiDepartament;

    @FXML
    private TableColumn<Recovery, LocalTime> numberOfHours;

    @FXML
    private TableColumn<Recovery, LocalDate> leaveDate;

    @FXML
    private ListView recoveryListView;

    @FXML
    private DatePicker datePickerInvoire;

    @FXML
    private TextField pozitieAngajat;
    @FXML
    private TextField nume;
   // @FXML
   // private JFXTimePicker timePickerInvoire;
    @FXML
    private ComboBox nrOreInvoire;

    @FXML
    private Button addRecuperare;
    //@FXML
    //private Label remainedHours;
    @FXML
    private ComboBox sefDirect;
    @FXML
    private ComboBox sefDepartament;

    public ClientController() {
        recoveryList = new ArrayList<>();
    }

    // called by the FXML loader after the labels declared above are injected
    public void initialize() {

        setDatePickerFormat(datePickerInvoire);

        // disable add button
        addRecuperare.setDisable(true);

        nrOreInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(datePickerInvoire.getValue() != null)
                        addRecuperare.setDisable(false);
            }
        });
        datePickerInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(nrOreInvoire.getValue() != null)
                    addRecuperare.setDisable(false);
            }
        });

        // autocomplete
        TextFields.bindAutoCompletion(pozitieAngajat, possibleChoises);

        nrOreInvoire.getItems().addAll(nrOre);
        //MAKE THE LIST OF RECOVERIES
        ObservableList<Recovery> listOfRecoveries = FXCollections.observableArrayList();
        //SET CELL VALUES FOR THE TABLE
        leaveDate.setCellValueFactory(new PropertyValueFactory<Recovery, LocalDate>("leaveDate"));
        numberOfHours.setCellValueFactory(new PropertyValueFactory<Recovery, LocalTime>("numberOfHours"));

        recoveryTableView.setItems(listOfRecoveries);

        addRecuperare.addEventHandler(
                MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if(recoveryList.size() >= 4)
                            addRecuperare.setDisable(true);
                    }
                }
        );

        addRecuperare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/add_recuperare.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Ore Recuperare");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    RecuperareController recuperareController = fxmlLoader.getController();

                    if(desiredLeave == null)
                        desiredLeave = new Leave(
                                datePickerInvoire.getValue(),
                                LocalTime.parse(nrOreInvoire.getValue().toString(), hourFormatter)
                                );
                    recuperareController.initialize(desiredLeave, recoveryList, listOfRecoveries);

                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




        getSuperiors();
    }


    void getSuperiors() {
        XMLMapper<Superiors> xmlMapperClient = new XMLMapper<>();
        List<Superior> sups = xmlMapperClient.jaxbXMLToObjects( Superiors.class, "superiors.xml").getSuperiors();

        sefiDirecti = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DIRECT)).collect(Collectors.toList());
        sefiDepartament = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList());

        sefDirectChoises = new String[sefiDirecti.size()];
        for (int i = 0; i < sefiDirecti.size(); i++) {
            sefDirectChoises[i] = sefiDirecti.get(i).getName();
        }
        sefDirect.getItems().addAll(sefDirectChoises);

        sefDepartamentChoises = new String[sefiDepartament.size()];
        for (int i = 0; i < sefiDepartament.size(); i++) {
            sefDirectChoises[i] = sefiDepartament.get(i).getName();
        }
        sefDepartament.getItems().addAll(sefDirectChoises);
    }

    public static void setDatePickerFormat(DatePicker datePicker) {
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
