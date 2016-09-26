package biometricauthentication;

import biometricauthentication.dialog.DialogEmployeeController;

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
import javafx.stage.WindowEvent;
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
    
    private Alert notFound;
    
    private Stage found;
    
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
        
        notFound = new Alert(Alert.AlertType.ERROR);
        notFound.setTitle("Captura biométrica");
        notFound.setHeaderText("Captura de huella");
        
        found = new Stage();
        
    }
    
    private void initObserver() {
        
        Observer observer = (Observable observable, Object sampleObject) -> {
            
            DPFPSample sample = (DPFPSample) sampleObject;
            
            Platform.runLater(() -> {
                
                boolean verified = false;
                
                for (Employee employee : biometric.getEmployees()) {
                    
                    DPFPTemplate template = biometric.deserializeTemplate(employee);
                    
                    if (notFound.isShowing()) notFound.close();
                    if (found.isShowing()) found.close();
                    
                    if (template != null) {
                        
                        verified = biometric.verify(sample, template);

                        if (verified) {
                            
                            Information info = biometric.saveBinnacleRecord(employee);
                            
                            if (!info.getVerification().equals("early")) {
                                
                                if (!info.getOperation().equals("same_day")) {
                                
                                    this.openDialogEmployee(employee, info);

                                } else {

                                    notFound.setContentText("Usted ya ha checado un turno completo");
                                    notFound.show();

                                }
                                
                            } else {
                                
                                notFound.setContentText("Aún es muy temprano para checar");
                                notFound.show();
                                
                            }
                            
                            return;

                        }

                    }

                }

                if (!verified) {
                    
                    notFound.setContentText("No encontrado, inténtelo de nuevo");
                    notFound.show();

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
        
        openFXML("/biometricauthentication/admin/login/LoginFXML.fxml", "Login");
        
    }
    
    private void openDialogEmployee(Employee employee, Information info) {
        
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/biometricauthentication/dialog/DialogEmployeeFXML.fxml")
            );
            
            this.found.setScene(new Scene((Pane) loader.load()));
            
            DialogEmployeeController employeeDialog = loader.<DialogEmployeeController>getController();
            
            employeeDialog.initData(employee, this.hourLabel.getText(), info);
            
            this.found.show();
            
        } catch (IOException ex) {
            
            System.out.println("Error de ruta -> FXML");
            
        }
        
    }
    
    private void openFXML(String fxml, String title) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml)); 
       
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            
//            readerEvent.setIsRunning(true);
//            readerThread = new Thread(readerEvent);
//            
//            readerThread.start();
            
        });
        
        stage.setTitle(title);
        
        stage.show();
        
    }
    
}