package biometricauthentication;



import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Application;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author VakSF
 */
public class BiometricAuthentication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        /*
        Parent root = FXMLLoader.load(getClass().getResource("BiometricFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
        
        stage.setTitle("Biometric Authentication");
        stage.setScene(scene);
        stage.show();
        */
        
        stage.show();
        
        File myFile = new FileChooser().showOpenDialog(null); 
        FileInputStream fis = new FileInputStream(myFile); 
        
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis); 
        
        XSSFSheet mySheet = myWorkBook.getSheetAt(0); 
        
        Iterator<Row> rowIterator = mySheet.iterator();
        
        while (rowIterator.hasNext()) {
            
            Row row = rowIterator.next();
            
            Iterator<Cell> cellIterator = row.cellIterator();
            
            while (cellIterator.hasNext()) {
                
                Cell cell = cellIterator.next();
                
                System.out.println(cell.getStringCellValue());
                
            }
            
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}