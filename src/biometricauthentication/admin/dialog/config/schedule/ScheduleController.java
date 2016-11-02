package biometricauthentication.admin.dialog.config.schedule;

import biometricauthentication.model.Company;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ComboBox;

import biometricauthentication.model.Config;
import biometricauthentication.utils.Biometric;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author VakSF
 */
public class ScheduleController implements Initializable {
    
    @FXML
    private ComboBox<Integer> earlyInCB, normalInCB, lateInCB, earlyOutCB, normalOutCB;
    
    private Biometric biometric;
    
    private Config config;
    
    private boolean selectedSchedule;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initCBs();
    }

    public boolean isSelectedSchedule() {
        return selectedSchedule;
    }

    public void setSelectedSchedule(boolean selectedSchedule) {
        this.selectedSchedule = selectedSchedule;
    }
    
    public void setBiometric(Biometric biometric) {
        
        this.biometric = biometric;
        
        this.setConfigCBs();
        
    }
    
    private void initCBs() {
        
        for (int i = 0; i <= 59; i++) {
            
            this.normalInCB.getItems().add(i);
            
            this.lateInCB.getItems().add(i);
            
        }
        
        for (int i = 0; i >= -59; i--) {
            
            this.earlyInCB.getItems().add(i);
            
            this.earlyOutCB.getItems().add(i);
            
        }
        
        this.normalOutCB.getItems().addAll(1, 2, 3, 4, 5);
        
        this.earlyInCB.getSelectionModel().selectFirst();   this.normalInCB.getSelectionModel().selectFirst();
            
        this.lateInCB.getSelectionModel().selectFirst();    this.earlyOutCB.getSelectionModel().selectFirst();
        
        this.normalOutCB.getSelectionModel().selectFirst();
        
    }
    
    private void setConfigCBs() {
        
        this.config = biometric.getConfig();
        
        System.out.println(this.config);
        
        this.earlyInCB.setValue(this.config.getEarlyIn());    
        
        this.normalInCB.setValue(this.config.getNormalIn());
        
        this.lateInCB.setValue(this.config.getLateIn());    
        
        this.earlyOutCB.setValue(this.config.getEarlyOut());    
        
        this.normalOutCB.setValue(this.config.getNormalOut());
        
    }
    
    @FXML
    private void setConfiguration() {
        
        Integer earlyIn = this.earlyInCB.getValue();
        Integer normalIn = this.normalInCB.getValue();
        Integer lateIn = this.lateInCB.getValue();
        
        Integer earlyOut = this.earlyOutCB.getValue();
        Integer normalOut = this.normalOutCB.getValue();
        
        Company company = this.biometric.getCompany();
        
        this.config.setConfig(
                company, earlyIn, normalIn, lateIn, earlyOut, normalOut
        );
        
        this.biometric.saveConfiguration(config);
        
        Optional<ButtonType> result = new Alert(
                AlertType.INFORMATION,
                "La configuración ha sido establecida"
        ).showAndWait();
        
        if (result.get() == ButtonType.OK) {
            
            this.setSelectedSchedule(true);
            
            earlyInCB.getScene().getWindow().hide();
            
        }
        
    }
    
}