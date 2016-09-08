/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometricauthentication.dialog;

import biometricauthentication.data.Biometric;
import biometricauthentication.data.Employee;
import java.io.File;
import java.io.IOException;
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
    private Label nameLabel, hourLabel;
    
    @FXML
    private ImageView imageView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void initData(Employee employee, String hour) {
        
        this.nameLabel.setText(employee.getName());
        this.hourLabel.setText(hour);
        
        this.setImage(employee);
        
    }
    
    private void setImage(Employee employee) {
        
        try {
            
            Biometric biometric = new Biometric();
            File file = biometric.deserializeFile(employee);
            
            this.imageView.setImage(new Image(file.toURI().toString()));
            
        } catch (IOException ex) {
            
        }
        
    }
    
}
