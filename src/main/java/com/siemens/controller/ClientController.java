package com.siemens.controller;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.jfoenix.controls.JFXTimePicker;
import com.siemens.model.Leave;
import com.siemens.model.Recovery;
import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;
import com.siemens.view.ClientStart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    @FXML
    private Button btnTrimite;

    private ClientController clientController;

    public ClientController() {
        recoveryList = new ArrayList<>();
    }

    private void setFieldsListeners(){
        //Enable add recovery button only if all necessary fields have been completed
        nume.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if(
                                datePickerInvoire.getValue() != null &&
                                        nrOreInvoire.getValue() != null &&
                                        pozitieAngajat.getCharacters().length() != 0
                                )
                            addRecuperare.setDisable(false);
                        if (nume.getCharacters().length() == 0 || pozitieAngajat.getCharacters().length() == 0)
                           addRecuperare.setDisable(true);



        setDatePickerFormat(datePickerInvoire);

                    }
                }
        );
        pozitieAngajat.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if(
                                datePickerInvoire.getValue() != null &&
                                        nrOreInvoire.getValue() != null &&
                                        nume.getCharacters().length() != 0
                                )
                            addRecuperare.setDisable(false);
                        if (nume.getCharacters().length() == 0 || pozitieAngajat.getCharacters().length() == 0)
                            addRecuperare.setDisable(true);
                    }
                }
        );
        nrOreInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(
                        datePickerInvoire.getValue() != null &&
                                nume.getCharacters().length() != 0 &&
                                pozitieAngajat.getCharacters().length() != 0

                        )
                    addRecuperare.setDisable(false);
                else
                    addRecuperare.setDisable(true);
            }
        });
        datePickerInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(
                        nrOreInvoire.getValue() != null &&
                                nume.getCharacters().length() != 0 &&
                                pozitieAngajat.getCharacters().length() != 0
                        )
                    addRecuperare.setDisable(false);
                else
                    addRecuperare.setDisable(true);
            }
        });
    }
    private void setButtonEvents(ObservableList<Recovery> listOfRecoveries){
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
        btnTrimite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/confirmare.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Confirmare");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    ConfirmareController confirmareController = fxmlLoader.getController();
                    confirmareController.initialize(clientController);

                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // called by the FXML loader after the labels declared above are injected
    public void initialize() {

        setDatePickerFormat(datePickerInvoire);

        clientController = this;

        // disable add button
        addRecuperare.setDisable(true);

        setFieldsListeners();

        // autocomplete
        TextFields.bindAutoCompletion(pozitieAngajat, possibleChoises);


        nrOreInvoire.getItems().addAll(nrOre);
        //MAKE THE LIST OF RECOVERIES
        ObservableList<Recovery> listOfRecoveries = FXCollections.observableArrayList();
        //SET CELL VALUES FOR THE TABLE
        leaveDate.setCellValueFactory(new PropertyValueFactory<Recovery, LocalDate>("recoveryDate"));
        numberOfHours.setCellValueFactory(new PropertyValueFactory<Recovery, LocalTime>("numberOfHours"));

        recoveryTableView.setItems(listOfRecoveries);

        setButtonEvents(listOfRecoveries);

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

    public void generatePdf(){
        try{
            PdfWriter writer = new PdfWriter("test.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20,20,20,20);
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);




            document.add(
                    new Paragraph("SIEMENS SRL")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(22)
                            .setBold()
                            .setFontColor(new DeviceRgb(0,153,153))
            );
            document.add(
                    new Paragraph("Bilet Invoire")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20)
                            .setBold()
            );
            document.add(
                    new Paragraph(
                            "Subsemnatul/a " + nume.getCharacters().toString() + " doresc " +
                                    " a beneficia de o invoire avand durata de "+ nrOreInvoire.getValue().toString() +
                                    " ore, " +
                                    "perioada necesara pentru rezolvarea unor probleme cu caracter personal."

                    )
                            .setFirstLineIndent(40)
                            .setFontSize(14)
            );
            document.add(
                    new Paragraph("Invoire:")
                            .setBold()
                            .setFontSize(16)
                            .setFirstLineIndent(40)
            );

            Table table = new Table(new float[]{2, 2});
            table.setWidth(UnitValue.createPercentValue(80));

            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data invoirii")
                    )
            );
            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr de ore")
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getLeaveDate().toString()
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getNumberOfHours().toString()
                    )
            );
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.add(
                    new Paragraph("Propuneri recuperare:")
                            .setBold()
                            .setFontSize(16)
                            .setFirstLineIndent(40)
            );

            Table recoveryTable = new Table(new float[]{2,2,2});
            recoveryTable.setWidth(UnitValue.createPercentValue(80));

            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Recuperare pentru data de")
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data propusa pentru recuperare")
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr. ore recuperate")
                    )
            );
            for(Recovery recovery : recoveryList){
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getLeaveDate().toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getRecoveryDate().toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getNumberOfHours().toString()
                        )
                );
            }

            recoveryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(recoveryTable);


            Table approvalTable = new Table(new float[]{1,2});
            approvalTable.addCell(
                    new com.itextpdf.layout.element.Cell(1,2 ).add(new Paragraph("  Aprobare"))
            );
            approvalTable.addCell("Sef");
            approvalTable.addCell("Semnatura");
            approvalTable.addCell(
                    new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph("Direct:"))
                            .add(new Paragraph(sefDirect.getValue().toString())
            ));
            approvalTable.addCell("");
            approvalTable.addCell(
                    new Cell()
                            .add(new Paragraph("Departament:"))
                            .add(new Paragraph(sefDepartament.getValue().toString()))
            );
            approvalTable.addCell("");


            document.add(new Paragraph("\n\n"));
            document.add(
                    new Paragraph(
                            "Data de azi: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                            .setVerticalAlignment(VerticalAlignment.BOTTOM)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT)
                            .setFontSize(12)
            );

            document.add(
                    new Paragraph("Semnatura angajat: ")
                            .setVerticalAlignment(VerticalAlignment.BOTTOM)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT)
                            .setFontSize(12)
            );

            document.add(new Paragraph("\n\n"));

            approvalTable.setWidth(UnitValue.createPercentValue(60));
            approvalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(approvalTable);

            document.close();
            //APEL PENTRU TRIMITERE MAIL
            System.exit(0);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
