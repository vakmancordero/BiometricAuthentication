package biometricauthentication;

import biometricauthentication.dialog.EmployeeDialogController;
import biometricauthentication.model.Company;

import biometricauthentication.model.Employee;

import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.reader.Reader;
import biometricauthentication.utils.authentication.Clock;
import biometricauthentication.utils.authentication.Information;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Service;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.util.List;
import javafx.scene.control.Button;

/**
 *
 * @author VakSF
 */
public class BiometricController implements Initializable {
    
    public static Reader readerEvent;
    
    public static Thread readerThread;
    
    @FXML
    private Label hourLabel, dateLabel, dayLabel, companyLabel;
    
    @FXML
    private Button adminButton;
    
    private Service<Void> clock;
    
    private Biometric biometric;
    
    private Alert errorDialog;
    
    private Stage employeeDialog;
    
    private EmployeeDialogController dialogEmployeeController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.initClock();
        this.initInformation();
        this.initDialogs();
        this.initObserver();
        
        this.setCompany();
        
        readerThread = new Thread(readerEvent);
        readerThread.start();
        
    }
    
    private void setCompany() {
        
        Company company = this.biometric.getCompany();
        
        if (company != null) {
            
            this.companyLabel.setText(company.toString());
            
        } else {
            
            new Alert(
                    AlertType.WARNING,
                    "No se ha establecido alguna compañía aún"
            ).showAndWait();
            
        }
        
    }
    
    private void initDialogs() {
        
        this.errorDialog = new Alert(Alert.AlertType.ERROR);
        this.errorDialog.setTitle("Captura biométrica");
        this.errorDialog.setHeaderText("Captura de huella");
        
        this.employeeDialog = new Stage();
        
        this.initDialogEmployee();
        
    }
    
    private void initObserver() {
        
        try {
            
            Observer observer = (Observable observable, Object sampleObject) -> {

                DPFPSample sample = (DPFPSample) sampleObject;

                Platform.runLater(() -> {

                    boolean verified = false;
                    
                    List<Employee> employees = biometric.getEmployees();
                    
                    System.out.println("size() of the list = " + employees.size());
                    
                    for (Employee employee : employees) {
                        
                        DPFPTemplate template = biometric.deserializeTemplate(employee);

                        if (template != null) {

                            verified = biometric.verify(sample, template);

                            if (verified) {

                                Information info = biometric.saveBinnacleRecord(employee);

                                String verification = info.getVerification();

                                if (!verification.equals("temprano")) {

                                    if (!verification.equals("sameDay")) {

                                        if (!verification.equals("outOfRange")) {

                                            this.openDialogEmployee(employee, info);

                                        } else {

                                            errorDialog.setContentText("Fuera del rango de tiempo");
                                            errorDialog.show();

                                        }

                                    } else {

                                        errorDialog.setContentText("Usted ya ha checado un turno completo");
                                        errorDialog.show();

                                    }

                                } else {

                                    errorDialog.setContentText("Aún es muy temprano para checar");
                                    errorDialog.show();

                                }

                                return;

                            }

                        }

                    }

                    if (!verified) {

                        errorDialog.setContentText("No encontrado, inténtelo de nuevo");
                        errorDialog.show();

                    }

                    this.closeDialogs();

                });
            };

            readerEvent = new Reader();
            readerEvent.addObserver(observer);
            
        } catch (Exception ex) {
            
            new Alert(
                    AlertType.ERROR,
                    "Error"
            ).show();
            
        }
        
    }
    
    private void closeDialogs() {

        if (employeeDialog.isShowing()) {

            try {

                Thread.sleep(3000);

                employeeDialog.close();

            } catch (InterruptedException ex) {

            }

        }
        
    }
    
    private void initInformation() {
        
        Date date = new Date();
        
        String _date = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String _day = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
        
        _day = _day.substring(0, 1).toUpperCase() + _day.substring(1);
        
        this.dateLabel.setText(_date);
        this.dayLabel.setText(_day);
        
    }
    
    private void initClock() {
        
        this.clock = new Clock();
        
        this.hourLabel.textProperty().bind(this.clock.messageProperty());
        
        this.clock.restart();
        
    }
    
    @FXML
    private void openAuthentication() throws IOException {
        
        this.openFXML(
                "/biometricauthentication/admin/login/LoginFXML.fxml", "Login"
        );
        
    }
    
    private void initDialogEmployee() {
        
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/biometricauthentication/dialog/EmployeeDialogFXML.fxml"
            ));

            this.employeeDialog.setScene(new Scene((Pane) loader.load()));
            
            this.dialogEmployeeController = loader.<EmployeeDialogController>getController();
            
        } catch (IOException ex) {
            
            System.out.println("Error de ruta -> FXML");
            
        }
        
    }
    
    private void openDialogEmployee(Employee employee, Information info) {
        
        this.dialogEmployeeController.setData(employee, this.hourLabel.getText(), info);
        
        this.employeeDialog.show();
        
    }
    
    private void openFXML(String fxml, String title) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml)); 
       
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        
        this.adminButton.setDisable(true);
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            
            readerEvent.setIsRunning(true);
            readerThread = new Thread(readerEvent);
            
            readerThread.start();
            
            this.adminButton.setDisable(false);
            
        });
        
        stage.setOnHiding((event) -> {
            this.adminButton.setDisable(false);
        });
        
        stage.setOnHidden((event) -> {
            this.adminButton.setDisable(false);
        });
        
        stage.setTitle(title);
        
        stage.showAndWait();
        
    }
    
}