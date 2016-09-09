package biometricauthentication.admin;

import static biometricauthentication.BiometricController.readerEvent;
import static biometricauthentication.BiometricController.readerThread;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ButtonType;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

import biometricauthentication.data.Biometric;
import biometricauthentication.data.Employee;
import biometricauthentication.data.Shift;
import biometricauthentication.utils.DPFPReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Arturh
 */
public class AdminController implements Initializable {
    
    @FXML
    private Accordion accordion;
    
    @FXML
    private TitledPane first;
    
    @FXML
    private Button fingerPrintButton, imageButton;
    
    @FXML
    private TextField companyTF, nameTF;
    
    @FXML
    private ComboBox<Shift> shiftCB;
    
    @FXML
    private TableView<Employee> employeesTV;
    
    private ObservableList<Employee> employeesList;
    
    private Biometric biometric;
    
    private DPFPReader myReader;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        readerEvent.setIsRunning(false);
        readerThread.interrupt();
        
        this.myReader = new DPFPReader();
        
        this.biometric = new Biometric();
        
        this.employeesList = FXCollections.observableArrayList();
        
        this.employeesTV.setItems(employeesList);
        
        this.fillEmployees();
        
        this.fillShifts();
        
        this.initTV();
        
        if (!this.employeesTV.getItems().isEmpty()) {
            
            this.employeesTV.getSelectionModel().selectFirst();
            
            this.fingerPrintButton.setDisable(false);
            this.imageButton.setDisable(false);
            
        }
        
        this.accordion.setExpandedPane(first);
        
    }
    
    private void initTV() {
        
        this.employeesTV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                
                Employee employee = employeesTV.getSelectionModel().getSelectedItem();
                
                companyTF.setText(employee.getName());
                
                nameTF.setText(employee.getName());
                
                shiftCB.getSelectionModel().select(employee.getShift());
                
            }
            
        });
        
    }
    
    private void fillShifts() {
        
        this.shiftCB.getItems().addAll(
                
                biometric.getShifts()
                
        );
        
        if (!this.shiftCB.getItems().isEmpty()) {
            this.shiftCB.getSelectionModel().selectFirst();
        }
        
    }
    
    private void fillEmployees() {
        
        this.employeesList.addAll(
                biometric.getEmployees()
        );
        
    }
    
    @FXML
    private void setShift() {
        
        Shift shift = shiftCB.getValue();
        
        Employee employee = employeesTV.getSelectionModel().getSelectedItem();
        
        employee.setShift(shift);
        
        biometric.saveEmployee(employee);
        
        this.employeesTV.refresh();
        
        new Alert(
                Alert.AlertType.INFORMATION,
                "Turno del empleado asignado: " + employee.getShift() + ", ha sido establecida",
                ButtonType.OK
        ).showAndWait();
        
    }
    
    @FXML
    private void setFinger() throws SQLException, InterruptedException {
            
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
                
                ex.printStackTrace();
                
            }
            
            Employee employee = employeesTV.getSelectionModel().getSelectedItem();
            
            DPFPTemplate template = enrollment.getTemplate();
            
            employee.setTemplate(biometric.serializeTemplate(template));
            
            biometric.saveEmployee(employee);
            
            new Alert(Alert.AlertType.INFORMATION, "La huella ha sido registrada correctamente", ButtonType.OK).showAndWait();
            
        } else {
            
            new Alert(Alert.AlertType.ERROR, "No hay lector conectado", ButtonType.OK).showAndWait();
            
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
            
            if (file.length() / (1024 * 1024) < 3.0) {
                
                employee.setPhoto(biometric.serializeFile(file));
            
                biometric.saveEmployee(employee);

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "La imagen del empleado: " + employee.getName() + ", ha sido establecida",
                        ButtonType.OK
                ).showAndWait();
                
            } else {
                
                new Alert(
                        Alert.AlertType.ERROR,
                        "La imagen es demasiado grande, intente con otra",
                        ButtonType.OK
                ).showAndWait();
                
            }
            
        }
        
    }
    
}