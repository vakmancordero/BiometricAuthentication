package biometricauthentication;

import biometricauthentication.dialog.EmployeeDialogController;

import biometricauthentication.model.Employee;

import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.Reader;
import biometricauthentication.utils.Clock;
import biometricauthentication.utils.Information;

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
import javafx.scene.layout.Pane;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;

/**
 *
 * @author VakSF
 */
public class BiometricController implements Initializable {
    
    @FXML
    private Label hourLabel, dateLabel, dayLabel;
    
    private Service<Void> clock;
    
    private Biometric biometric;
    
    private Alert errorDialog;
    
    private Stage employeeDialog;
    
    private EmployeeDialogController dialogEmployeeController;
    
    public static Reader readerEvent;
    
    public static Thread readerThread;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.initServices();
        this.initInformation();
        this.initDialogs();
        this.initObserver();
        
        readerThread = new Thread(readerEvent);
        readerThread.start();
        
    }
    
    private void initDialogs() {
        
        this.errorDialog = new Alert(Alert.AlertType.ERROR);
        this.errorDialog.setTitle("Captura biométrica");
        this.errorDialog.setHeaderText("Captura de huella");
        
        this.employeeDialog = new Stage();
        
        this.initDialogEmployee();
        
    }
    
    private void initObserver() {
        
        Observer observer = (Observable observable, Object sampleObject) -> {
            
            DPFPSample sample = (DPFPSample) sampleObject;
            
            Platform.runLater(() -> {
                
                boolean verified = false;
                
                for (Employee employee : biometric.getEmployees()) {
                    
                    DPFPTemplate template = biometric.deserializeTemplate(employee);
                    
                    if (errorDialog.isShowing()) errorDialog.close();
                    
                    if (employeeDialog.isShowing()) employeeDialog.close();
                    
                    if (template != null) {
                        
                        verified = biometric.verify(sample, template);

                        if (verified) {
                            
                            Information info = biometric.saveBinnacleRecord(employee);
                            
                            if (!info.getVerification().equals("temprano")) {
                                
                                if (!info.getOperation().equals("same_day")) {
                                
                                    this.openDialogEmployee(employee, info);

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
                
            });
        };
        
        readerEvent = new Reader();
        readerEvent.addObserver(observer);
        
    }
    
    private void initInformation() {
        
        Date date = new Date();
        
        String _date = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String _day = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
        
        _day = _day.substring(0, 1).toUpperCase() + _day.substring(1);
        
        this.dateLabel.setText(_date);
        this.dayLabel.setText(_day);
        
    }
    
    private void initServices() {
        
        this.clock = new Clock();
        
        this.hourLabel.textProperty().bind(this.clock.messageProperty());
        
        this.clock.restart();
        
    }
    
    @FXML
    private void openAuthentication() throws IOException {
        
        this.openFXML("/biometricauthentication/admin/login/LoginFXML.fxml", "Login");
        
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
        
        stage.setTitle(title);
        
        stage.show();
        
    }
    
}