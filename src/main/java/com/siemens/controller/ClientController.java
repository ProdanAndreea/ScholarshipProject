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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.siemens.configuration.MailConfiguration;
import com.siemens.model.*;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.*;
import java.security.CodeSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.siemens.view.ClientStart.alreadyParsed;
import static com.siemens.view.ClientStart.logger;
import static com.siemens.view.ClientStart.superiorsFilePath;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description: Controller for the interface elements of the view client_view.
 */
public class ClientController {
    private ObservableList<Recovery> listOfRecoveries;
    private Leave desiredLeave = null;
    private final String pattern = "dd-MM-yyyy";
    public static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter hourFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");

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


    private List<Superior> sefiDirecti;
    private List<Superior> sefiDepartament;


    @FXML
    private ToggleButton availableButton;

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
    private Button btnTrimite;
    @FXML
    private Button btnDelete;
    @FXML
    private Label labelInvoire;
    @FXML
    private Button changeConfigsButton;
    @FXML
    private Label sefDirectLabel;
    @FXML
    private Label sefDepartamentLabel;
    @FXML
    private Label sefDirectLabelDefault;
    @FXML
    private Label sefDepartamentLabelDefault;


    private ClientController clientController;

    public ClientController() {
        listOfRecoveries = FXCollections.observableArrayList();
    }

    private void setFieldsListeners() {
        setDatePickerFormat(datePickerInvoire);
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

    }

    private void setButtonEvents(ObservableList<Recovery> listOfRecoveries) {
        addRecuperare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/add_recuperare.fxml"));
                    Parent root = fxmlLoader.load();
                    root.setId("pane");
                    Stage stage = new Stage();
                    stage.setTitle("Ore Recuperare");
                    stage.setResizable(false);
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
                    stage.setTitle("Cereri de invoire");
                    stage.setResizable(false);

                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    PaginaSefController paginaSefController = fxmlLoader.getController();
                    paginaSefController.populateDepartment(sefiDepartament);

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
                    stage.setResizable(false);

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

        availableButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                XMLMapper xmlMapper = new XMLMapper();
                xmlMapper.setAvailable(nume.getText(), availableButton.isSelected(), superiorsFilePath);
                setTextForAvailableButton();
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

        changeConfigsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/modify_config_prompt.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Confirmare");
                    stage.setResizable(false);



                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(ClientStart.primaryStage.getScene().getWindow());

                    Scene scene = new Scene(root);

                    scene.getStylesheets().add("style/modify_config_prompt.css");


                    stage.setScene(scene);
                    stage.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTextForAvailableButton() {
        if ((availableButton.isSelected())) {
            availableButton.setText("Disponibil");
        } else {
            availableButton.setText("Indisponibil");
        }
    }

    private void determineFunctionalities(){

        if(ClientStart.userPosition.equals("Team Leader") || ClientStart.userPosition.equals("Department Leader")){
            labelInvoire.setOpacity(100);
            XMLMapper xmlMapper = new XMLMapper();
            availableButton.setVisible(true);
            availableButton.setSelected(xmlMapper.isAvailable(ClientStart.userName, superiorsFilePath));
            setTextForAvailableButton();
            bossButton.setOpacity(100);
            bossButton.setDisable(false);
        }
        nume.setText(ClientStart.userName);
    }
    private void launchPdfForSigning(){
        if(ClientStart.parameterString.length != 0 && alreadyParsed == false){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pagina_sef.fxml"));
                Parent root = fxmlLoader.load();
                root.setId("pane");
                Stage stage = new Stage();
                stage.setTitle("Cereri de invoire");
                stage.setResizable(false);

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
    }

    // called by the FXML loader after the labels declared above are injected
    public void initialize() {
        ClientStart.primaryStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                launchPdfForSigning();
            }
        });
        if(ClientStart.userPosition.equals("Department Leader")){
            datePickerInvoire.setDisable(true);
            nrOreInvoire.setDisable(true);
        }
        nume.setEditable(false);
        labelInvoire.setOpacity(0);
        availableButton.setVisible(false);
        bossButton.setOpacity(0);
        bossButton.setDisable(true);
        btnDelete.setDisable(true);
        determineFunctionalities();
        inializeTooltips();
        sefDirectLabel.setText(ClientStart.superiorName);
        sefDepartamentLabel.setText(ClientStart.departmentSuperior);
        sefDepartamentLabel.setStyle("-fx-font-weight: bold");
        sefDirectLabel.setStyle("-fx-font-weight: bold");
        setDatePickerFormat(datePickerInvoire);
        hideSefLabels();


        clientController = this;

        // disable add button
        addRecuperare.setDisable(true);

        //disable send button until all details are completed
        btnTrimite.setDisable(true);
        setFieldsListeners();

        nrOreInvoire.getItems().addAll(nrOre);

        //SET CELL VALUES FOR THE TABLE
        leaveDate.setCellValueFactory(new PropertyValueFactory<Recovery, LocalDate>("recoveryDate"));
        numberOfHours.setCellValueFactory(new PropertyValueFactory<Recovery, LocalTime>("numberOfHours"));

        recoveryTableView.setItems(listOfRecoveries);

        setButtonEvents(listOfRecoveries);

        getSuperiors();

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

        listOfRecoveries.addListener((ListChangeListener.Change<? extends Recovery> change) -> {
            if (listOfRecoveries.size() == 4) {
                addRecuperare.setDisable(true);
            }
        });


    }

    private void inializeTooltips() {
//        availableButton.getTooltip().setShowDelay(new Duration(500));
//        availableButton.getTooltip().setShowDuration(new Duration(9000));
//        changeConfigsButton.getTooltip().setShowDelay(new Duration(500));
//        changeConfigsButton.getTooltip().setShowDuration(new Duration(9000));
//        bossButton.getTooltip().setShowDelay(new Duration(500));
//        bossButton.getTooltip().setShowDuration(new Duration(9000));
    }

    void getSuperiors() {
        List<Superior> sups = XMLMapper.jaxbXMLToObjects(Superiors.class, superiorsFilePath).getSuperiors();

        sefiDirecti = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DIRECT)).collect(Collectors.toList());
        sefiDepartament = sups.stream().filter(superior -> superior.getPositionEnum().equals(PositionEnum.DEPARTAMENT)).collect(Collectors.toList());

    }

    private void hideSefLabels() {
        if (ClientStart.userPosition.equals("Team Leader"))
            sefDirectLabelDefault.setVisible(false);
        else if (ClientStart.userPosition.equals("Department Leader")) {
            sefDirectLabelDefault.setVisible(false);
            sefDepartamentLabelDefault.setVisible(false);
        }
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

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    }

    private String encodeParameters(){
        String recoveryGroups = "";
        for(Recovery recovery : listOfRecoveries){
            recoveryGroups +=
                    recovery.getRecoveryDate().format(format) +
                    "n" +
                    recovery.getNumberOfHours().toString()
                    + "m";
        }
        recoveryGroups = recoveryGroups.substring(0, recoveryGroups.length() - 1);
        return ClientStart.userName + "," +
                ClientStart.senderMail.split("@")[0] +"," +
                (ClientStart.superiorName.equals("") ? (ClientStart.departmentSuperior) : (ClientStart.superiorName + "&" + ClientStart.departmentSuperior)) + "," +
                desiredLeave.getLeaveDate().format(format) + "," +
                desiredLeave.getNumberOfHours().toString() + "," +
                LocalDateTime.now().format(format) + "," +
                LocalTime.now().format(hourFormatterWithSeconds) + "," +
                recoveryGroups;
    }
    private void showDialogBox(String statusMessage){
        if(!statusMessage.equals("Mail has been sent succsessfully!")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setContentText(statusMessage);
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification sent");
            alert.setContentText(statusMessage + "\n The application will now close");
            alert.showAndWait();
        }
    }

    public void generateMailData(){
        Superior directLeader = null;
        Superior departmentLeader;
        String encodedMessage = encodeParameters();
        if(ClientStart.userPosition.equals("User")){
            directLeader = sefiDirecti.stream()
                    .filter(boss -> boss.getName().equals(sefDirectLabel.getText())).findFirst().get();
            departmentLeader = sefiDepartament.stream()
                    .filter(departLeader ->  departLeader.getName().equals(sefDepartamentLabel.getText()))
                    .findFirst().get();
        }else{
            departmentLeader = sefiDepartament.stream()
                    .filter(departLeader ->  departLeader.getName().equals(sefDepartamentLabel.getText()))
                    .findFirst().get();
        }

        StringBuilder message = new StringBuilder("Ai primit o cerere de invoire de la <b>" + nume.getCharacters().toString().toUpperCase() + "</b> pentru data de <b>" +
                desiredLeave.getLeaveDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</b>, durata: <b>" +
                desiredLeave.getNumberOfHours().toString() + "</b> ore.<br><br>" +
                "Subsemnatul/a doreste sa recupereze orele invoite in:<br>");

        for (Recovery recovery: listOfRecoveries) {
            message.append("<b>" +  recovery.getRecoveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</b> - <b>" +
                    recovery.getNumberOfHours().toString() + "</b> ore<br>"
                    );
        }

        message.append("<br>");
        message.append("<a href=\"invoiri:" + encodedMessage + "\">Apasa aici pentru a rezolva cererea</a>");

        if(ClientStart.userPosition.equals("Team Leader")){
            try{
                MailConfiguration.sendMessage(departmentLeader.getEmail(), "[INVOIRI] Cerere Invoire", message.toString());
                showDialogBox("Mail has been sent successfully!");
                System.exit(0);
            }catch (Exception mailExcepiton){
                showDialogBox(mailExcepiton.getMessage());
            }
            return;
        }

        if (directLeader.getAvailable()) {
            try{
                MailConfiguration.sendMessage(directLeader.getEmail(), "{INVOIRI] Cerere Invoire", message.toString());
                showDialogBox("Mail has been sent successfully!");
                System.exit(0);
            }catch (Exception mailExcepiton){
                showDialogBox(mailExcepiton.getMessage());
            }

        } else {
            try{
                MailConfiguration.sendMessage(departmentLeader.getEmail(), "[INVOIRI] Cerere Invoire", message.toString());
                showDialogBox("Mail has been sent successfully!");
                System.exit(0);
            }catch (Exception mailException){
                showDialogBox(mailException.getMessage());
            }

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

}
