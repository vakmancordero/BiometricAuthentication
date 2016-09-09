package biometricauthentication;

import biometricauthentication.utils.SQLConnection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author VakSF
 */
public class BiometricAuthentication extends Application {
    
    public static SQLConnection sqlConnection;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BiometricFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        this.initConnection();
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
        
        stage.setTitle("Biometric Authentication");
        stage.setScene(scene);
        stage.show();
    }
    
    public void initConnection() throws SQLException {
        
        try {
            
            BiometricAuthentication.sqlConnection = new SQLConnection("root", "jaqart_56923", "localhost", "biometric", "mysql");
            
            System.out.println("Conexion a la base de datos exitosa!");
            
        } catch (ClassNotFoundException | SQLException ex) {
            
            System.out.println("Error en la conexion a la base de datos...");
            
        }
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}