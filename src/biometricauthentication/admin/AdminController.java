package biometricauthentication.admin;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;

import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

import java.net.URL;
import java.sql.SQLException;

import java.util.ResourceBundle;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import biometricauthentication.admin.dialog.record.BinnacleRecordController;

import static biometricauthentication.BiometricController.readerEvent;
import static biometricauthentication.BiometricController.readerThread;

import biometricauthentication.admin.dialog.config.company.CompanyController;
import biometricauthentication.admin.dialog.config.schedule.ScheduleController;
import biometricauthentication.admin.dialog.report.ReportsController;

import biometricauthentication.utils.reader.DPFPReader;
import biometricauthentication.utils.Biometric;

import biometricauthentication.model.Employee;
import biometricauthentication.model.Shift;
import biometricauthentication.model.Company;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Arturh
 */
public class AdminController implements Initializable {
    
    @FXML
    private Accordion accordion;
    
    @FXML
    private TitledPane registerPane, createPane, editPane;
    
    @FXML
    private AnchorPane registerAnchorPane, createAnchorPane, editAnchorPane;
    
    @FXML
    private TextField companyTF, nameTF;
    
    @FXML
    private TextField createNameTF, createLastNameTF, createMothersLastNameTF;
    
    @FXML
    private TextField editNameTF, editLastNameTF, editMothersLastNameTF;
    
    @FXML
    private Label companyLabel;
    
    @FXML
    private ComboBox<Shift> shiftCB, createShiftCB, editShiftCB;
    
    @FXML
    private ComboBox<Company> createCompanyCB, editCompanyCB;
    
    @FXML
    private MenuItem configMI, reportMI, binnacleMI;
    
    @FXML
    private TableView<Employee> employeesTV;
    
    private ObservableList<Employee> employeesList;
    
    private Biometric biometric;
    
    private DPFPReader myReader;
    
    private TextField[] createArr, editArr;
    
    private Company company;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        readerEvent.setIsRunning(false);
        readerThread.interrupt();
        
        this.myReader = new DPFPReader();
        this.biometric = new Biometric();
        
        this.setCompany();
        
        this.initTV();
        
        this.fillEmployees();
        
        this.initCBs();
        
        this.initArrays();
        
        this.initAll();
        
    }
    
    private void setCompany() {
        this.company = this.biometric.getCompany();
    }
    
    public void initAll() {
        
        this.accordion.setExpandedPane(registerPane);
        
        if (this.company != null) {
            
            this.companyLabel.setText(this.company.toString());
            
            this.disableMI(false);
            
            if (!this.employeesList.isEmpty()) {
                
                this.accordion.setExpandedPane(registerPane);

                this.employeesTV.getSelectionModel().selectFirst();
                
                this.disablePanes(false);
                
            } else {
                
                this.binnacleMI.setDisable(true);
                
                this.accordion.setExpandedPane(createPane);
                
                this.registerAnchorPane.setDisable(true);
                this.editAnchorPane.setDisable(true);
                
            }
            
        } else {
            
            this.disableMI(true);
            
            this.disablePanes(true);
            
        }
        
    }
    
    private void initCBs() {
        
        this.fillShifts();
        
        this.fillCompanies();
        
    }
    
    private void initTV() {
        
        this.employeesList = FXCollections.observableArrayList();
        
        this.employeesTV.setItems(this.employeesList);
        
        this.employeesTV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                
                if (!employeesList.isEmpty()) {
                    
                    Employee employee = employeesTV.getSelectionModel().getSelectedItem();
                    
                    if (employee != null) {

                        String employeeFullName = employee.toString();
                        String employeeName = employee.getName();
                        String employeeLastName = employee.getLastName();
                        String employeeMothersLastName = employee.getMothersLastName();
                        
                        Company company = employee.getCompany();
                        
                        Shift shift = employee.getShift();
                        
                        nameTF.setText(employeeFullName);
                        companyTF.setText(company.toString());
                        shiftCB.getSelectionModel().select(shift);
                        
                        editNameTF.setText(employeeName);
                        editLastNameTF.setText(employeeLastName);
                        editMothersLastNameTF.setText(employeeMothersLastName);
                        editCompanyCB.getSelectionModel().select(company);
                        editShiftCB.getSelectionModel().select(shift);
                        
                    }
                    
                    registerAnchorPane.setDisable(false);
                    editAnchorPane.setDisable(false);
                    
                }
                
            }
            
        });
        
    }
    
    private void initArrays() {
        
        this.createArr = new TextField[] {
            this.createNameTF, 
            this.createLastNameTF, 
            this.createMothersLastNameTF
        };
        
        this.editArr = new TextField[] {
            this.editNameTF, 
            this.editLastNameTF, 
            this.editMothersLastNameTF
        };
        
    }
    
    private void fillShifts() {
        
        this.shiftCB.getItems().addAll(this.biometric.getShifts());
        
        if (!this.shiftCB.getItems().isEmpty()) {
            
            this.shiftCB.getSelectionModel().selectFirst();
            
            this.createShiftCB.getItems().addAll(this.shiftCB.getItems());
            this.createShiftCB.getSelectionModel().selectFirst();
            
            this.editShiftCB.getItems().addAll(this.shiftCB.getItems());
            this.editShiftCB.getSelectionModel().selectFirst();
            
        }
        
    }
    
    private void fillCompanies() {
        
        this.createCompanyCB.getItems().addAll(this.biometric.getCompanies());
        
        if (!this.createCompanyCB.getItems().isEmpty()) {
            
            this.createCompanyCB.getSelectionModel().selectFirst();
            
            this.editCompanyCB.getItems().addAll(this.createCompanyCB.getItems());
            this.editCompanyCB.getSelectionModel().selectFirst();
            
        }
        
    }
    
    private void fillEmployees() {
        this.employeesList.addAll(this.biometric.getEmployees());
    }
    
    @FXML
    private void saveEmployee() {
        
        if (this.check("create")) {
            
            String name = this.createNameTF.getText();
            String lastName = this.createLastNameTF.getText();
            String mothersLastName = this.createMothersLastNameTF.getText();

            Shift shift = this.createShiftCB.getValue();
            Company company = this.createCompanyCB.getValue();

            Employee employee = new Employee(
                    name, lastName, mothersLastName, shift, company
            );
            
            boolean saved = this.biometric.saveEmployee(employee);
            
            if (saved) {
                
                new Alert(
                        AlertType.INFORMATION, 
                        "Empleado creado"
                ).show();

                this.employeesList.add(employee);

                this.employeesTV.getSelectionModel().selectFirst();

                if (!employeesList.isEmpty()) {
                    
                    this.disablePanes(false);
                    
                }
                
            } else {
                
                new Alert(
                        AlertType.ERROR, 
                        "Empleado duplicado"
                ).show();
                
            }
            
        } else {
            
            this.emptyFields();
            
        }
        
    }
    
    @FXML
    private void updateEmployee() {
        
        if (this.check("update")) {
            
            Employee employee = this.employeesTV.getSelectionModel().getSelectedItem();
            
            String name = this.editNameTF.getText();
            String lastName = this.editLastNameTF.getText();
            String mothersLastName = this.editMothersLastNameTF.getText();
            
            Shift shift = this.editShiftCB.getValue();
            Company company = this.editCompanyCB.getValue();
            
            Employee auxEmployee = new Employee();
            auxEmployee.updateEmployee(employee);
            
            employee.updateEmployee(
                    new Employee(name, lastName, mothersLastName, shift, company)
            );
            
            boolean updated = this.biometric.saveEmployee(employee);
            
            if (updated) {
                
                new Alert(
                        AlertType.INFORMATION, 
                        "Empleado editado"
                ).show();
                
            } else {
                
                employee.updateEmployee(auxEmployee);
                
                new Alert(
                        AlertType.ERROR, 
                        "Empleado duplicado"
                ).show();
                
            }
            
            this.employeesTV.refresh();
            
        } else {
            
            this.emptyFields();
            
        }
        
    }
    
    @FXML
    private void setShift() {
        
        Employee employee = this.employeesTV.getSelectionModel().getSelectedItem();
        
        employee.setShift(this.shiftCB.getValue());
        
        this.biometric.saveEmployee(employee);
        
        this.employeesTV.refresh();
        
        new Alert(
                AlertType.INFORMATION,
                "El turno : " + employee.getShift() + ", ha sido establecido"
        ).show();
        
    }
    
    @FXML
    private void setFinger() throws SQLException, InterruptedException {
        
        Employee employee = this.employeesTV.getSelectionModel().getSelectedItem();
        
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        
        confirmation.setTitle("Confirmacion de modificación");
        confirmation.setHeaderText("Está seguro?");
        confirmation.setContentText(
                "Desea modificar la huella del empleado " + employee + "?"
        );
        
        Optional<ButtonType> option = confirmation.showAndWait();
        
        if (option.get() == ButtonType.OK) {
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Captura");
            alert.setHeaderText("Captura de datos");

            DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPEnrollment enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();

            this.myReader.findReader();

            if (!this.myReader.getActiveReader().equals("empty")) {

                try {

                    while (enrollment.getFeaturesNeeded() > 0) {

                        alert.setContentText("Ingresar dedo... " + enrollment.getFeaturesNeeded());
                        alert.show();
                        
                        DPFPSample sample = this.myReader.getSample();
                        
                        if (sample == null) {
                            continue;
                        }

                        DPFPFeatureSet featureSet;

                        try {

                            featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

                        } catch (DPFPImageQualityException ex) {

                            System.out.println("Error, mala calidad en la huella capturada");

                            continue;

                        }

                        alert.close();

                        enrollment.addFeatures(featureSet);
                    }

                } catch (DPFPImageQualityException | InterruptedException ex) {

                }

                DPFPTemplate template = enrollment.getTemplate();

                employee.setTemplate(this.biometric.serializeTemplate(template));

                this.biometric.saveFingerPrint(employee);

                new Alert(
                        AlertType.INFORMATION, 
                        "La huella ha sido registrada correctamente"
                ).show();

            } else {

                new Alert(
                        AlertType.ERROR, 
                        "No hay lector conectado"
                ).show();

            }
            
        }
        
    }
    
    @FXML
    private void setPhoto() throws FileNotFoundException, IOException {
        
        Employee employee = employeesTV.getSelectionModel().getSelectedItem();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir imagen");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Archivos de imagen", "*.png", "*.jpg")
        );
        
        File file = fileChooser.showOpenDialog(null);
        
        if (file != null) {
            
            if (file.length() / (1024 * 1024) < 8) {
                
                if (biometric.saveFile(employee, file)) {

                    new Alert(
                            AlertType.INFORMATION,
                            "La imagen del empleado: " + employee.getName() + ", ha sido establecida"
                    ).show();
                    
                }
                
            } else {
                
                new Alert(
                        AlertType.ERROR,
                        "La imagen es demasiado grande, intente con otra"
                ).show();
                
            }
            
        }
        
    }
    
    @FXML
    private void context(ActionEvent event) {
        
        MenuItem menuItem = (MenuItem) event.getSource();
        
        String item = menuItem.getText();
        
        if (item.equals("Registrar")) {
            
            this.accordion.setExpandedPane(registerPane);
            
        } else {
            
            if (item.equals("Crear")) {
                
                this.accordion.setExpandedPane(createPane);
                
            } else {
                
                if (item.equals("Editar")) {
                    
                    this.accordion.setExpandedPane(editPane);
                    
                } else {
                    
                    if (item.equals("Eliminar")) {
                        
                        this.deleteEmployee();
                        
                    }
                    
                }
                
            }
            
        }
        
    }
    
    private void deleteEmployee() {
        
        Employee employee = this.employeesTV.getSelectionModel().getSelectedItem();
        
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        
        confirmation.setTitle("Confirmacion de eliminación");
        confirmation.setHeaderText("Está seguro?");
        confirmation.setContentText(
                "Desea eliminar al empleado " + employee + "?"
        );
        
        Optional<ButtonType> option = confirmation.showAndWait();
        
        if (option.get() == ButtonType.OK) {
        
            this.biometric.deleteEmployee(employee);

            this.employeesList.remove(employee);
            
            if (this.employeesList.isEmpty()) {
                
                this.registerAnchorPane.setDisable(true);
                this.editAnchorPane.setDisable(true);
                
            }

            new Alert(
                    AlertType.INFORMATION, 
                    "Empleado eliminado"
            ).show();
            
        }
        
    }
    
    private boolean check(String type) {
        
        if (type.equals("create")) {
            
            for (TextField createTF : this.createArr) {
                
                if (createTF.getText().isEmpty()) {
                    return false;
                }
                
            }
            
        } else {
            
            if (type.equals("update")) {
                
                for (TextField editTF : this.editArr) {
                    
                    if (editTF.getText().isEmpty()) {
                        return false;
                    }
                    
                }
                
            }
            
        }
        
        return true;
        
    }
    
    private void emptyFields() {
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Ha olvidado llenar algún campo");
        alert.setContentText("Por favor introduzca los campos faltantes...");
        alert.showAndWait();
        
    }
    
    private void disablePanes(boolean value) {
        
        this.registerAnchorPane.setDisable(value);
        this.createAnchorPane.setDisable(value);
        this.editAnchorPane.setDisable(value);
        
    }
    
    private void disableMI(boolean value) {
        
        this.configMI.setDisable(value);
        this.reportMI.setDisable(value);
        this.binnacleMI.setDisable(value);
        
    }
    
    @FXML
    private void openCompanyConfiguration(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/biometricauthentication/admin/dialog/config/company/CompanyFXML.fxml"
        ));

        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        CompanyController controller = 
                loader.<CompanyController>getController();
        
        controller.setBiometric(this.biometric);
        
        controller.setCurrentCompany(this.company);
        
        stage.setTitle("Establecer compañia");
        
        stage.setOnHidden((windowEvent) -> {
            
            if (controller.isSelectedCompany()) {
                
                new Alert(
                        AlertType.WARNING,
                        "Deberá volver a abrir el panel de administrador"
                ).showAndWait();

                this.employeesTV.getScene().getWindow().hide();
                
            }
            
        });

        stage.showAndWait();
        
    }
    
    @FXML
    private void openScheduleConfiguration() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/biometricauthentication/admin/dialog/config/schedule/ScheduleFXML.fxml"
        ));
        
        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        ScheduleController controller = 
                loader.<ScheduleController>getController();
        
        controller.setBiometric(this.biometric);
        
        stage.setOnHidden((event) -> {
            
            if (controller.isSelectedSchedule()) {
                
                new Alert(
                        AlertType.WARNING,
                        "Se recomienda reiniciar por completo la aplicación"
                ).show();
                
            }
            
        });
        
        stage.setTitle("Configuración de horario");
        
        stage.showAndWait();
        
    }
    
    @FXML
    private void openReports() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/biometricauthentication/admin/dialog/report/ReportsFXML.fxml"
        ));
        
        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        ReportsController controller = 
                loader.<ReportsController>getController();
        
        controller.setBiometric(this.biometric);
        
        stage.setTitle("Reportes");
        
        stage.showAndWait();
        
    }
    
    @FXML
    private void createBinnacleRecord() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/biometricauthentication/admin/dialog/record/BinnacleRecordFXML.fxml"
        ));
        
        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setTitle("Registros");
        
        BinnacleRecordController controller = 
                loader.<BinnacleRecordController>getController();
        
        controller.init(this.employeesList);
        controller.setBiometric(this.biometric);
        
        stage.showAndWait();
        
    }
    
}
