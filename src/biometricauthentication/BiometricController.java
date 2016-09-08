package biometricauthentication;

import biometricauthentication.data.Biometric;
import biometricauthentication.data.Employee;
import biometricauthentication.data.ReaderEvent;
import biometricauthentication.utils.Clock;

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
    
    public static ReaderEvent readerEvent;
    
    public static Thread readerThread;
    
    private Service<Void> clock;
    
    private Biometric biometric;
    
    private Alert alert;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.initServices();
        this.initInformation();
        this.initObserver();
        this.initAlert();

        readerThread = new Thread(readerEvent);
        readerThread.start();
        
    }
    
    private void initAlert() {
        
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Captura biométrica");
        alert.setHeaderText("Captura de huella");
        
    }
    
    private void initObserver() {
        
        Observer observer = (Observable observable, Object sampleObject) -> {
            
            DPFPSample sample = (DPFPSample) sampleObject;
            
            Platform.runLater(() -> {
                
                boolean verified = false;
                
                for (Employee employee : biometric.getEmployees()) {
                    
                    DPFPTemplate template = biometric.deserializeTemplate(employee);
                    
                    if (alert.isShowing()) {
                        alert.close();
                    }
                    
                    if (template != null) {

                        verified = biometric.verify(sample, template);

                        if (verified) {
                            
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText(employee.getName());
                            alert.show();
                            
                            return;

                        }

                    }

                }

                if (!verified) {
                    
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("No encontrado, inténtelo de nuevo");
                    alert.show();

                }
            });
        };
        
        readerEvent = new ReaderEvent();
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
        
        readerEvent.setIsRunning(false);
        readerThread.interrupt();
        
    }
    
    private void openDialogEmployee() {
        
        
        
        
    }
    
    private void openFXML(String fxml, String title) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml)); 
       
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setTitle(title);
        
        stage.show();
        
    }
    
}