package biometricauthentication.dialog;

import biometricauthentication.utils.Biometric;
import biometricauthentication.model.Employee;
import biometricauthentication.utils.Information;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author VakSF
 */
public class EmployeeDialogController implements Initializable {
    
    @FXML
    private Label nameLabel, hourLabel, operationLabel, verificationLabel;
    
    @FXML
    private ImageView imageView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setData(Employee employee, String hour, Information info) {
        
        this.nameLabel.setText(employee.getName());
        this.hourLabel.setText(hour);
        
        this.operationLabel.setText(info.getOperation());
        this.verificationLabel.setText(info.getVerification());
        
        this.setImage(employee);
        
    }
    
    private void setImage(Employee employee) {
        
        Biometric biometric = new Biometric();
        
        File file = biometric.getFile(employee);
        
        if (file != null) {
            
            this.imageView.setImage(new Image(file.toURI().toString()));
            
        }
        
    }
    
}