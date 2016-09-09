package biometricauthentication.dialog;

import biometricauthentication.data.Biometric;
import biometricauthentication.data.Employee;
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
public class DialogEmployeeController implements Initializable {
    
    @FXML
    private Label nameLabel, hourLabel, operationLabel;
    
    @FXML
    private ImageView imageView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void initData(Employee employee, String hour, String operation) {
        
        this.nameLabel.setText(employee.getName());
        this.hourLabel.setText(hour);
        this.operationLabel.setText(operation);
        
        this.setImage(employee);
        
    }
    
    private void setImage(Employee employee) {
        
        Biometric biometric = new Biometric();
        
        File file = biometric.deserializeFile(employee);
        
        if (file != null) {
            
            this.imageView.setImage(new Image(file.toURI().toString()));
            
        }
        
    }
    
}
