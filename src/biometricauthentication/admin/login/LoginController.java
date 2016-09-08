package biometricauthentication.admin.login;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import static biometricauthentication.BiometricAuthentication.sqlConnection;
import static biometricauthentication.BiometricController.readerEvent;
import static biometricauthentication.BiometricController.readerThread;

/**
 *
 * @author Arturh
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField userTF;
    
    @FXML
    private PasswordField passwordPF;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        readerEvent.setIsRunning(false);
        readerThread.interrupt();
        
    }
    
    @FXML
    private void login(ActionEvent event) throws IOException, SQLException {
        
        String user = this.userTF.getText();
        String password = this.passwordPF.getText();
        
        if (!user.isEmpty() && !password.isEmpty()) {
            
            ResultSet result = sqlConnection.search("SELECT user, password FROM user_accounts "
                    + "WHERE user = '" + user + "' AND password = '" + password + "';");
            
            if (result != null && result.first()) {
                    
                openFXML("/biometricauthentication/admin/AdminFXML.fxml", "Administrador \t-\t @Kaizen Soft by Arturo Cordero");
                ((Node) event.getSource()).getScene().getWindow().hide();
                
            } else {
                
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("No se ha podido iniciar sesión");
                alert.setContentText("Usuario y/o contraseña incorrectos...");
                alert.showAndWait();

            }
            
        } else {
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Ha olvidado llenar algún campo");
            alert.setContentText("Por favor introduzca los campos faltantes...");
            alert.showAndWait();
            
        }
        
    }
    
    private void openFXML(String fxml, String title) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml)); 
       
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setTitle(title);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            @Override
            public void handle(WindowEvent event) {
                                
                readerEvent.setIsRunning(true);
                readerThread = new Thread(readerEvent);
                
                readerThread.start();
                
            }
            
        });
        
        stage.show();
        
    }
    
}
