package biometricauthentication.admin.dialog.record;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import com.jfoenix.controls.JFXTextField;

import biometricauthentication.model.Employee;
import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.utils.Biometric;
import java.sql.Time;
import java.util.Locale;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author VakSF
 */
public class BinnacleRecordController implements Initializable {
    
    @FXML
    private ListView<Employee> employeesLV;
    
    @FXML
    private JFXTextField nameTF, checkInTF, checkOutTF;
    
    @FXML
    private DatePicker datePicker;
    
    private Biometric biometric;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.datePicker.setValue(
                LocalDate.now()
        );
        
        this.checkInTF.setText("07:00");
        this.checkOutTF.setText("15:00");
        
        this.employeesLV.getSelectionModel()
                .selectedItemProperty()
                    .addListener(new ChangeListener<Employee>() {
                        
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldEmployee, Employee newEmployee) {
                
                nameTF.setText(
                        newEmployee.getId() +  " : " + newEmployee.toString()
                );
                
            }
            
        });
        
    }
    
    public void setData(ObservableList<Employee> employeesList) {
        this.employeesLV.setItems(employeesList);
        
        if (!this.employeesLV.getItems().isEmpty()) {
            this.employeesLV.getSelectionModel().selectFirst();
        }
    }
    
    @FXML
    private void create() {
        
        try {
            
            Employee employee = 
                    this.employeesLV.getSelectionModel().getSelectedItem();
            
            LocalDate localDate = this.datePicker.getValue();
            
            String checkInString = this.checkInTF.getText();
            String checkOutString = this.checkOutTF.getText();
            
            Date checkIn = this.createCheck(
                    localDate, checkInString
            );
            
            Date checkOut = this.createCheck(
                    localDate, checkOutString
            );
            
            Date date = createCheck(localDate, "");
            
            String day = new SimpleDateFormat(
                    "EEEE", new Locale("es", "ES")
            ).format(date);
            
            BinnacleRecord binnacleRecord = new BinnacleRecord(
                    employee.getId(),
                    date, checkIn, checkOut
            );
            
            binnacleRecord.setDay(day);
            binnacleRecord.setReport("normal");
            binnacleRecord.setWorked_hours(new Time(8, 0, 0));
            
            this.biometric.saveBinnacleRecord(binnacleRecord);
            
            new Alert(
                    AlertType.INFORMATION,
                    "Registro guardado"
            ).show();
            
        } catch (Exception ex) {
            
            System.out.println("Unparseable string error");            
        }
        
    }
    
    private Date createCheck(LocalDate localDate, String check) {
        
        Date date = null;
        
        try {
            
            String dateString = localDate.toString();
        
            if (!check.isEmpty()) {
                
                dateString += " " + check;

                date = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm"
                ).parse(dateString);

            } else {
                
                date = new SimpleDateFormat(
                        "yyyy-MM-dd"
                ).parse(dateString);
                
            }
            
            System.out.println(date);
        
        } catch (ParseException ex) {
            
            new Alert(
                    AlertType.ERROR,
                    "Los valores introducidos no cumplen con"
                  + "el formato de fecha"
            ).show();
            
        }
        
        return date;
    }
    
}
