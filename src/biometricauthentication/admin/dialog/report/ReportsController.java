package biometricauthentication.admin.dialog.report;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.util.Callback;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

import com.jfoenix.controls.JFXButton;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;
import biometricauthentication.model.Company;
import biometricauthentication.model.EmployeeType;
import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.Report;
import com.jfoenix.controls.JFXComboBox;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author VakSF
 */
public class ReportsController implements Initializable {
    
    @FXML
    private TableView<ReportRecord> reportTV;
    
    @FXML
    private JFXComboBox<Company> companieCB;
    
    @FXML
    private JFXComboBox<EmployeeType> typeCB;
    
    @FXML
    private JFXComboBox<Month> monthCB;
    
    @FXML
    private JFXComboBox<String> yearCB, fortnightCB;
    
    private Biometric biometric;
    
    private Report report;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.report = new Report();
        
        this.setupTV();
        this.fillInputs();
    }
    
    @FXML
    private void generate() {
        
        Company company = this.companieCB.getValue();
        
        EmployeeType employeeType = this.typeCB.getValue();
        
        String year = this.yearCB.getValue();
        
        Month month = this.monthCB.getValue();
        
        String fortnight = this.fortnightCB.getValue();
        
        List<ReportRecord> reportRecords = this.report.getReportRecords(
                company, employeeType, year, month, fortnight
        );
        
    }
    
    private void fillInputs() {
        
        this.companieCB.getItems().addAll(
                this.biometric.getCompanies()
        );
        
        this.typeCB.getItems().addAll(
                this.biometric.getEmployeeTypes()
        );
        
        this.yearCB.getItems().addAll(
                "2016", "2017", "2018" 
        );
        
        this.monthCB.getItems().addAll(
                Month.values()
        );
        
        this.fortnightCB.getItems().addAll(
                "Primera",
                "Segunda"
        );
        
    }
    
    private void setupTV() {
        
        TableColumn detailsColumn = new TableColumn("Detalles");
        
        detailsColumn.setCellFactory(new PropertyValueFactory("details"));
        
        JFXButton button = new JFXButton("Ver detalles");
        
        button.setTextFill(Paint.valueOf("#ffffff"));
                        
        button.setStyle("-fx-background-color: #324E7F");
        
        Callback<TableColumn<ReportRecord, String>, TableCell<ReportRecord, String>> cellFactory = 
                (TableColumn<ReportRecord, String> value) -> {
                    
                    TableCell<ReportRecord, String> cell = new TableCell<ReportRecord, String>() {
                        
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            
                            super.updateItem(item, empty);
                            
                            if (empty) {
                                
                                super.setGraphic(null);
                                
                                super.setText(null);
                                
                            } else {
                                
                                button.setOnAction((ActionEvent event) -> {
                                    
                                    ReportRecord report = getTableView().getItems().get(
                                            super.getIndex()
                                    );
                                    
                                    System.out.println(report.getEmployee());
                                    
                                });
                                
                                super.setGraphic(button);
                                
                            }
                            
                        }
                        
                    };
                    
                    return cell;
                    
        };
        
        detailsColumn.setCellFactory(cellFactory);
        
        this.reportTV.getColumns().add(detailsColumn);
        
        this.reportTV.getItems().add(new ReportRecord());
        
    }
    
}
