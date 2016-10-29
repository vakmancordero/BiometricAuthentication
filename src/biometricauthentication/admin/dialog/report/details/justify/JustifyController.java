package biometricauthentication.admin.dialog.
            report.details.justify;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.utils.Biometric;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;

/**
 *
 * @author VakSF
 */
public class JustifyController implements Initializable {
    
    @FXML
    private JFXTextArea observationTA;
    
    @FXML
    private JFXTextField nameTF, dateTF;
    
    private BinnacleRecord binnacleRecord;
    
    private Biometric biometric;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.biometric = new Biometric();
    }
    
    public void setData(BinnacleRecord binnacleRecord, 
            ReportRecord reportRecord) {
        
        this.binnacleRecord = binnacleRecord;
        
        this.dateTF.setText(
                binnacleRecord.getDate().toString()
        );
        
        this.nameTF.setText(
                reportRecord.getEmployee().toString()
        );
    }
    
    public BinnacleRecord getBinnacleRecord() {
        return this.binnacleRecord;
    }
    
    @FXML
    private void justify(ActionEvent event) {
        
        String text = observationTA.getText();
        
        if (!text.isEmpty()) {
            
            binnacleRecord.setObservation(text);
            
            binnacleRecord.setReport("normal");
            
            biometric.saveBinnacleRecord(binnacleRecord);
            
            ButtonType finishButton = new ButtonType("Terminar");
            
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Información");
            alert.setHeaderText("Registro justificado");
            alert.setContentText("El registro ha sido actualizado");
            alert.getButtonTypes().setAll(finishButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() == finishButton) {
                ((Node) event.getSource()).getScene().getWindow().hide();
            }
            
        } else {
            
            new Alert(
                    AlertType.INFORMATION,
                    "Por favor introduzca una observación"
            ).show();
            
        }
        
        
    }
    
}
