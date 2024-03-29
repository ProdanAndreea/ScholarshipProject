package com.siemens.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.siemens.configuration.MailConfiguration;
import com.siemens.model.Leave;
import com.siemens.model.Recovery;
import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;
import com.siemens.view.ClientStart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import java.io.*;
import java.security.CodeSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description: Controller for the interface elements of the view client_view.
 */
public class ClientController {
    private ObservableList<Recovery> listOfRecoveries;
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

    private String[] sefDirectChoices;
    private String[] sefDepartamentChoices;
    private List<Superior> sefiDirecti;
    private List<Superior> sefiDepartament;
    private String userName;
    private String userPosition;
    private String superiorName;
    private String departmentSuperior;
    private String superiorsFilePath;

    @FXML
    private CheckBox bossAvailability;

    @FXML
    private Button bossButton;

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
    @FXML
    private Button btnDelete;
    @FXML
    private Label labelInvoire;
    private ClientController clientController;

    public ClientController() {
        listOfRecoveries = FXCollections.observableArrayList();
    }

    private void setFieldsListeners() {
        //Enable add recovery button only if all necessary fields have been completed
        setDatePickerFormat(datePickerInvoire);
//        nume.textProperty().addListener(
//                new ChangeListener<String>() {
//                    @Override
//                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                        if (
//                                datePickerInvoire.getValue() != null &&
//                                        nrOreInvoire.getValue() != null
//                        )
//                            addRecuperare.setDisable(false);
//                        if (nume.getCharacters().length() == 0) {
//                            addRecuperare.setDisable(true);
//                            btnTrimite.setDisable(true);
//                        }
//                        else if(nume.getCharacters().length() != 0 && recoveryList.size()!= 0)
//                            btnTrimite.setDisable(false);
//                    }
//                }
//        );
//        pozitieAngajat.textProperty().addListener(
//                new ChangeListener<String>() {
//                    @Override
//                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                        if (
//                                datePickerInvoire.getValue() != null &&
//                                        nrOreInvoire.getValue() != null &&
//                                        nume.getCharacters().length() != 0
//                        )
//                            addRecuperare.setDisable(false);
//                        if (nume.getCharacters().length() == 0 ){
//                            addRecuperare.setDisable(true);
//                            btnTrimite.setDisable(true);
//                        }
//                        else if(nume.getCharacters().length() != 0 && recoveryList.size()!= 0)
//                            btnTrimite.setDisable(false);
//
//                    }
//                }
//        );
        listOfRecoveries.addListener(new ListChangeListener<Recovery>() {
            @Override
            public void onChanged(Change<? extends Recovery> c) {
                if(listOfRecoveries.size() > 0){
                    btnTrimite.setDisable(false);
                    datePickerInvoire.setDisable(true);
                    nrOreInvoire.setDisable(true);
                }
                else {
                    desiredLeave = null;
                    btnTrimite.setDisable(true);
                    datePickerInvoire.setDisable(false);
                    nrOreInvoire.setDisable(false);
                }


            }
        });
        nrOreInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (
                        datePickerInvoire.getValue() != null &&
                                nume.getCharacters().length() != 0
                )
                    addRecuperare.setDisable(false);
                else
                    addRecuperare.setDisable(true);
            }
        });
        datePickerInvoire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (
                        nrOreInvoire.getValue() != null &&
                                nume.getCharacters().length() != 0
                )
                    addRecuperare.setDisable(false);
                else
                    addRecuperare.setDisable(true);
            }
        });
        sefDepartament.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(
                        nume.getCharacters().length() != 0 &&
                                listOfRecoveries.size() != 0 &&
                                sefDirect.getValue() != null
                        )
                    btnTrimite.setDisable(false);
            }
        });
        sefDirect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(
                        nume.getCharacters().length() != 0 &&
                                listOfRecoveries.size() != 0 &&
                                sefDepartament.getValue() != null
                        )
                    btnTrimite.setDisable(false);
            }
        });
    }

    private void setButtonEvents(ObservableList<Recovery> listOfRecoveries) {

        addRecuperare.addEventHandler(
                MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (listOfRecoveries.size() >= 4)
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
                    root.setId("pane");
                    Stage stage = new Stage();
                    stage.setTitle("Ore Recuperare");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    RecuperareController recuperareController = fxmlLoader.getController();

                    if (desiredLeave == null)
                        desiredLeave = new Leave(
                                datePickerInvoire.getValue(),
                                LocalTime.parse(nrOreInvoire.getValue().toString(), hourFormatter)
                        );
                    recuperareController.initialize(desiredLeave, listOfRecoveries, datePickerInvoire);

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("style/add_recuperare.css");
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        bossButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pagina_sef.fxml"));
                    Parent root = fxmlLoader.load();
                    root.setId("pane");
                    Stage stage = new Stage();
                    stage.setTitle("Cereri de învoire");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("style/pagina_sef.css");
                    stage.setScene(scene);
                    stage.show();
                }catch (Exception e){
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
                    root.setId("pane");
                    Stage stage = new Stage();
                    stage.setTitle("Confirmare");

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    ConfirmareController confirmareController = fxmlLoader.getController();
                    confirmareController.initialize(clientController);

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("style/confirmare.css");
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bossAvailability.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                XMLMapper xmlMapper = new XMLMapper();
                xmlMapper.setAvailable(nume.getText(), bossAvailability.isSelected(), superiorsFilePath);
            }
        });

        btnDelete.setOnAction(e -> {
            Recovery selectedItem = recoveryTableView.getSelectionModel().getSelectedItem();
            listOfRecoveries.remove(selectedItem);
            if (this.listOfRecoveries.size() < 4)
                addRecuperare.setDisable(false);


            if (selectedItem != null && listOfRecoveries.size() != 0) { // there is a selected rowl
                desiredLeave.deleteFromCoveredHours(selectedItem.getNumberOfHours());
                btnDelete.setDisable(true);
            }
            else{
                btnDelete.setDisable(true);
            }
        });
    }
    private void loadUserData(){
        Properties property = new Properties();
       // String userProperties = "user.properties";
        // InputStream inputStream = getClass().getClassLoader().getResourceAsStream(userProperties);
        try{
           // if (inputStream != null) {
                /*
                property.load(inputStream);
                userName = property.getProperty("appUser");
                userPosition = property.getProperty("userOccupiedPosition");
                superiorName = property.getProperty("superiorName");
                departmentSuperior = property.getProperty("departmentSuperiorName");
                */

                // get the path of the Jar
                CodeSource codeSource = ClientController.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                String jarDir = jarFile.getParentFile().getPath();
                System.out.println("-------------------");
                System.out.println(jarDir);

                //load the file handle for main.properties
                FileInputStream file = new FileInputStream(jarDir + "\\user.properties");
                //load all the properties from this file
                property.load(file);
                //we have loaded the properties, so close the file handle
                file.close();

                //retrieve the properties
                userName = property.getProperty("appUser");
                userPosition = property.getProperty("userOccupiedPosition");
                superiorName = property.getProperty("superiorName");
                departmentSuperior = property.getProperty("departmentSuperiorName");
                superiorsFilePath = property.getProperty("pathToXML");

                System.out.println(superiorName);
                System.out.println(departmentSuperior);
                System.out.println(superiorsFilePath);
                
                if(userPosition.equals("Team Leader") || userPosition.equals("Department Leader")){
                    labelInvoire.setOpacity(100);

                    bossAvailability.setOpacity(100);
                    bossAvailability.setDisable(false);

                    XMLMapper xmlMapper = new XMLMapper();
                    bossAvailability.setSelected(xmlMapper.isAvailable(userName, superiorsFilePath));

                    bossButton.setOpacity(100);
                    bossButton.setDisable(false);
                }

                nume.setText(userName);

           // } else {
           //     throw new FileNotFoundException("property file '" + userProperties + "' not found in the classpath");
          //  }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    // called by the FXML loader after the labels declared above are injected
    public void initialize() {
        nume.setEditable(false);

        labelInvoire.setOpacity(0);

        bossAvailability.setOpacity(0);
        bossAvailability.setDisable(true);

        bossButton.setOpacity(0);
        bossButton.setDisable(true);

        btnDelete.setDisable(true);

        //Parse the user prop file
        loadUserData();

        sefDirect.setValue(superiorName);
        sefDepartament.setValue(departmentSuperior);

        setDatePickerFormat(datePickerInvoire);

        clientController = this;

        // disable add button
        addRecuperare.setDisable(true);

        //disable send button until all details are completed
        btnTrimite.setDisable(true);
        setFieldsListeners();

        // autocomplete
//        TextFields.bindAutoCompletion(pozitieAngajat, possibleChoises);


        nrOreInvoire.getItems().addAll(nrOre);


        //SET CELL VALUES FOR THE TABLE
        leaveDate.setCellValueFactory(new PropertyValueFactory<Recovery, LocalDate>("recoveryDate"));
        numberOfHours.setCellValueFactory(new PropertyValueFactory<Recovery, LocalTime>("numberOfHours"));

        recoveryTableView.setItems(listOfRecoveries);

        setButtonEvents(listOfRecoveries);

        System.out.println("-------------   " + superiorsFilePath);
        getSuperiors();

/*
        recoveryTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked");
            }
        });
        */

        recoveryTableView.setRowFactory(tv -> {
            TableRow<Recovery> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                   // Recovery rowData = row.getItem();
                    btnDelete.setDisable(false);
                    System.out.println("click");
                }
            });
            return row ;
        });
    }

    void getSuperiors() {
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, superiorsFilePath).getSuperiors();

        sefiDirecti = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DIRECT)).collect(Collectors.toList());
        sefiDepartament = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList());

        sefDirectChoices = new String[sefiDirecti.size()];
        for (int i = 0; i < sefiDirecti.size(); i++) {
            sefDirectChoices[i] = sefiDirecti.get(i).getName();
        }
        sefDirect.getItems().addAll(sefDirectChoices);

        sefDepartamentChoices = new String[sefiDepartament.size()];
        for (int i = 0; i < sefiDepartament.size(); i++) {
            sefDepartamentChoices[i] = sefiDepartament.get(i).getName();
        }
        sefDepartament.getItems().addAll(sefDepartamentChoices);
    }

    public static void setDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd-MM-yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

//        // disable past days
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    }

    public void generatePdf(){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String pdfFilePath =
                    ClientStart.fileDirectoryPath +"\\Invoire_" + nume.getCharacters().toString()+
                    "_" + LocalDate.now().format(formatter)  +// desiredLeave.getLeaveDate().toString()
                    "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm")).toString()+".pdf";
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);


            // add hidden email
            PdfPage page = pdf.addNewPage();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfFormField field = PdfFormField.createText(pdf);
            field.setFieldName("email");

            Rectangle rect1 = new Rectangle(240, 800, 150, 20);
            PdfWidgetAnnotation widget1 = new PdfWidgetAnnotation(rect1);
            widget1.makeIndirect(pdf);
            page.addAnnotation(widget1);
            field.addKid(widget1);
            field.setValue(ClientStart.senderMail);
            field.setVisibility(PdfFormField.HIDDEN); // hide it
            form.addField(field, page);

            // get the value of the field
//            form = PdfAcroForm.getAcroForm(pdf, true);
//            Map<String, PdfFormField> fields = form.getFormFields();
//            PdfFormField field1 = fields.get("email");
//            System.out.println("hidden email field: " + field1.getValueAsString());
            ////////////

            document.add(
                    new Paragraph("SIEMENS SRL")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(22)
                            .setBold()
                            .setFontColor(new DeviceRgb(0, 153, 153))
            );
            document.add(
                    new Paragraph("Bilet Invoire")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20)
                            .setBold()
                    //.setFont(font)
            );


            Text text1 = new Text("Subsemnatul/a ");
            Text text2 = new Text(nume.getCharacters().toString()).setBold();
            Text text3 = new Text(" doresc a beneficia de o invoire avand durata de ");
            Text text4 = new Text(nrOreInvoire.getValue().toString()).setBold();
            Text text5 = new Text(" ore, " +
                    "perioada necesara pentru rezolvarea unor probleme cu caracter personal.");

            document.add(
                    new Paragraph().add(text1).add(text2).add(text3).add(text4).add(text5)
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
                            new Paragraph("Nr. ore")
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
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

            Table recoveryTable = new Table(new float[]{2, 2, 2});
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
            for (Recovery recovery : listOfRecoveries) {
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getRecoveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
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


            Table approvalTable = new Table(new float[]{1, 2});
            approvalTable.addCell(
                    new com.itextpdf.layout.element.Cell(1, 2).add(new Paragraph("  Aprobare"))
            );
            approvalTable.addCell("Sef");
            approvalTable.addCell("Semnatura"); //.setFont(font);
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


            Paragraph dataDeAzi = new Paragraph(
                    "Data de azi: "
            )
                    .setVerticalAlignment(VerticalAlignment.BOTTOM)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setFontSize(12);


            Text text = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setBold();
            dataDeAzi.add(text);

            document.add(dataDeAzi);

            document.add(new Paragraph("\n\n"));

            approvalTable.setWidth(UnitValue.createPercentValue(60));
            approvalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(approvalTable);


            /* add signature fields */

            // get the number of the table's rows to shift the signature form on y axis based on the table's height
            int noRows = recoveryTable.getNumberOfRows();

            /* sef direct signature */
            // create a signature form field
            PdfSignatureFormField signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)314.4, (float)361.5 - (noRows * (float)22.4), (float)149.1, (float)40.1)); // 329.5
            signatureField.setFieldName("signatureSefDirect");
            // set the widget properties
            signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
            // add the field
            PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);
            /* sef departament signature */
            signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)314.4, (float)321 - (noRows * (float)22.4), (float)149.1, (float)40.1));
            signatureField.setFieldName("signatureSefDepartament");
            signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
            PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);


            document.close();
            //APEL PENTRU TRIMITERE MAIL

            generateMailData();

            System.exit(0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void generateMailData(){
        Superior directLeader = sefiDirecti.stream()
                .filter(boss -> boss.getName().equals(sefDirect.getValue().toString())).findFirst().get();
        Superior departmentLeader = sefiDepartament.stream()
                .filter(departLeader ->  departLeader.getName().equals(sefDepartament.getValue().toString()))
                .findFirst().get();
        String message = "ATI PRIMIT O CERERE PENTRU INVOIRE DE LA " + nume.getCharacters().toString().toUpperCase();

        if (directLeader.getAvailable()) {
            MailConfiguration.sendMessage(directLeader.getEmail(), "CERERE INVOIRE", message);
        } else {
            MailConfiguration.sendMessage(departmentLeader.getEmail(), "CERERE INVOIRE", message);
        }

    }


    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while(reads != -1) {
            baos.write(reads);
            reads = is.read();
        }
        return baos.toByteArray();
    }


    public void generatePdfWithDiacritics(){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String pdfFilePath =
                    ClientStart.fileDirectoryPath +"\\Invoire_" + nume.getCharacters().toString()+
                            "_" + LocalDate.now().format(formatter)  +// desiredLeave.getLeaveDate().toString()
                            "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm")).toString()+".pdf";
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);


            System.out.println();
            System.out.println();
            System.out.println();

            InputStream is = this.getClass().getClassLoader().getResourceAsStream("FreeSans.ttf");


            //   PdfFont font = PdfFontFactory.createFont(toByteArray(is), PdfEncodings.IDENTITY_H); // "src/main/resources/FreeSans.ttf" // ByteStreams.toByteArray(is) - guava //toByteArray(is)


            FontProgramFactory.registerFont("C:\\Siemens\\scholarship_project\\build\\libs\\FreeSans.ttf", "garamond bold");
            //  PdfFont font = PdfFontFactory.createFont("C:\\Siemens\\scholarship_project\\build\\libs\\FreeSans.ttf", "Identity-H", true);
            PdfFont font = PdfFontFactory.createRegisteredFont("garamond bold", PdfEncodings.IDENTITY_H, true);


            // add hidden email
            PdfPage page = pdf.addNewPage();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfFormField field = PdfFormField.createText(pdf);
            field.setFieldName("email");

            Rectangle rect1 = new Rectangle(240, 800, 150, 20);
            PdfWidgetAnnotation widget1 = new PdfWidgetAnnotation(rect1);
            widget1.makeIndirect(pdf);
            page.addAnnotation(widget1);
            field.addKid(widget1);
            field.setValue("test@gmail.com");
            field.setVisibility(PdfFormField.HIDDEN); // hide it
            form.addField(field, page);

            // get the value of the field
//            form = PdfAcroForm.getAcroForm(pdf, true);
//            Map<String, PdfFormField> fields = form.getFormFields();
//            PdfFormField field1 = fields.get("email");
//            System.out.println("hidden email field: " + field1.getValueAsString());
            ////////////

            document.add(
                    new Paragraph("SIEMENS SRL")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(22)
                            .setBold()
                            .setFontColor(new DeviceRgb(0, 153, 153))
            );
            document.add(
                    new Paragraph("Bilet Învoire i\u0301 - a\u030c - a\u0303")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20)
                            .setBold()
                    //.setFont(font)
            );


            Text text1 = new Text("Subsemnatul/a ");
            Text text2 = new Text(nume.getCharacters().toString()).setBold();
            Text text3 = new Text(" doresc a beneficia de o învoire având durata de ").setFont(font);
            Text text4 = new Text(nrOreInvoire.getValue().toString()).setBold();
            Text text5 = new Text(" ore, " +
                    "perioada necesară pentru rezolvarea unor probleme cu caracter personal.").setFont(font);

            document.add(
                    new Paragraph().add(text1).add(text2).add(text3).add(text4).add(text5)
                            .setFirstLineIndent(40)
                            .setFontSize(14)
            );

            Text t = new Text("Învoire:").setFont(font).setBold().setFontSize(16);
            document.add(new Paragraph(t).setFirstLineIndent(40));

            document.add(
                    new Paragraph("Învoire:")
                            .setBold()
                            .setFontSize(16)
                            .setFirstLineIndent(40)
                            .setFont(font)
            );

            Table table = new Table(new float[]{2, 2});
            table.setWidth(UnitValue.createPercentValue(80));

            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data învoirii i\u030c - a\u030c - a\u0303")
                    )
            );
            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr. ore")
                    )
            );
            table.addCell(
                    new Paragraph(
                            desiredLeave.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
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

            Table recoveryTable = new Table(new float[]{2, 2, 2});
            recoveryTable.setWidth(UnitValue.createPercentValue(80));

            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Recuperare pentru data de")
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Data propusă pentru recuperare").setFont(font)
                    )
            );
            recoveryTable.addHeaderCell(
                    new com.itextpdf.layout.element.Cell().add(
                            new Paragraph("Nr. ore recuperate")
                    )
            );
            for (Recovery recovery : listOfRecoveries) {
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                        )
                );
                recoveryTable.addCell(
                        new Paragraph(
                                recovery.getRecoveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
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


            Table approvalTable = new Table(new float[]{1, 2});
            approvalTable.addCell(
                    new com.itextpdf.layout.element.Cell(1, 2).add(new Paragraph("  Aprobare"))
            );
            approvalTable.addCell("Șef").setFont(font);
            approvalTable.addCell("Semnătură"); //.setFont(font);
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


            Paragraph dataDeAzi = new Paragraph(
                    "Data de azi: "
            )
                    .setVerticalAlignment(VerticalAlignment.BOTTOM)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setFontSize(12);


            Text text = new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setBold();
            dataDeAzi.add(text);

            document.add(dataDeAzi);

//            document.add(
//                    new Paragraph("Semnătură angajat: ")
//                            .setVerticalAlignment(VerticalAlignment.BOTTOM)
//                            .setHorizontalAlignment(HorizontalAlignment.LEFT)
//                            .setFontSize(12)
//            );

            document.add(new Paragraph("\n\n"));

            approvalTable.setWidth(UnitValue.createPercentValue(60));
            approvalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(approvalTable);


            /* add signature fields */

            // get the number of the table's rows to shift the signature form on y axis based on the table's height
            int noRows = recoveryTable.getNumberOfRows();

            /* sef direct signature */
            // create a signature form field
            PdfSignatureFormField signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)314.4, (float)329.5 - (noRows * (float)22.4), (float)149.1, (float)46.1));
            signatureField.setFieldName("signatureSefDirect");
            // set the widget properties
            signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
            // add the field
            PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);
            /* sef departament signature */
            signatureField = PdfFormField.createSignature(pdf, new Rectangle((float)314.4, (float)283 - (noRows * (float)22.4), (float)149.1, (float)46.1));
            signatureField.setFieldName("signatureSefDepartament");
            signatureField.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE).setFlags(PdfAnnotation.PRINT);
            PdfAcroForm.getAcroForm(pdf, true).addField(signatureField);


            document.close();
            //APEL PENTRU TRIMITERE MAIL

            generateMailData();

            System.exit(0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
