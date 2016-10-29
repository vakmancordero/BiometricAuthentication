package biometricauthentication.admin.dialog.record;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

/**
 *
 * @author VakSF
 */
public class BinnacleRecordController implements Initializable {
    
    @FXML
    private JFXTextField nameTF, checkInTF, checkOutTF;
    
    @FXML
    private DatePicker datePicker;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
    }
    
    @FXML
    private void create() {
        
        nameTF.getText();
        
    }
    
}
