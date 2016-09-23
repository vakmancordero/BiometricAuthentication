package biometricauthentication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Application;

/**
 *
 * @author VakSF
 */
public class BiometricAuthentication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("BiometricFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
        
        stage.setTitle("Biometric Authentication");
        stage.setScene(scene);
        stage.show();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}