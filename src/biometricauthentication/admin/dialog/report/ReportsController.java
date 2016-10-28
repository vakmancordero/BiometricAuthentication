package biometricauthentication.admin.dialog.report;

import java.time.Month;

import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;

import javafx.util.Callback;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;

import biometricauthentication.model.Company;
import biometricauthentication.model.EmployeeType;
import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.Report;

/**
 *
 * @author VakSF
 */
public class ReportsController implements Initializable {
    
    @FXML
    private TableView<ReportRecord> reportTV;
    
    @FXML
    private JFXComboBox<Company> companyCB;
    
    @FXML
    private JFXComboBox<EmployeeType> typeCB;
    
    @FXML
    private JFXComboBox<Month> monthCB;
    
    @FXML
    private JFXComboBox<String> yearCB, fortnightCB;
    
    private ObservableList<ReportRecord> reportList;
    
    private Biometric biometric;
    
    private Report report;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.reportList = FXCollections.observableArrayList();
        
        this.biometric = new Biometric();
        
        this.report = new Report();
        
        this.setupTV();
        this.fillInputs();
    }
    
    @FXML
    private void generate() {
        
        if (!this.reportList.isEmpty()) {
            this.reportList.clear();
        }
        
        Company company = this.companyCB.getValue();
        
        EmployeeType employeeType = this.typeCB.getValue();
        
        String year = this.yearCB.getValue();
        
        Month month = this.monthCB.getValue();
        
        String fortnight = this.fortnightCB.getValue();
        
        this.report.setEmployees(this.biometric.getEmployees());
        
        List<ReportRecord> reportRecords = this.report.getReportRecords(
                company, employeeType, year, month, fortnight
        );
        
        this.reportList.addAll(reportRecords);
        
    }
    
    private void fillInputs() {
        
        this.companyCB.getItems().addAll(
                this.biometric.getCompanies()
        );
        
        this.companyCB.getSelectionModel().selectFirst();
        
        this.typeCB.getItems().addAll(
                this.biometric.getEmployeeTypes()
        );
        
        this.typeCB.getSelectionModel().selectFirst();
        
        this.yearCB.getItems().addAll(
                "2016", "2017", "2018" 
        );
        
        this.yearCB.getSelectionModel().selectFirst();
        
        this.monthCB.getItems().addAll(
                Month.values()
        );
        
        this.monthCB.getSelectionModel().selectFirst();
        
        this.fortnightCB.getItems().addAll(
                "Primera",
                "Segunda"
        );
        
        this.fortnightCB.getSelectionModel().selectFirst();
        
    }
    
    private void setupTV() {
        
        this.reportTV.setItems(this.reportList);
        
        TableColumn detailsColumn = new TableColumn("Detalles");
        
        detailsColumn.setCellFactory(new PropertyValueFactory("details"));
        
        Callback<TableColumn<ReportRecord, String>, TableCell<ReportRecord, String>> cellFactory = 
                (TableColumn<ReportRecord, String> value) -> {
                    
                    TableCell<ReportRecord, String> cell = new TableCell<ReportRecord, String>() {
                        
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            
                            final JFXButton button = new JFXButton("Ver detalles");
        
                            button.setTextFill(Paint.valueOf("#ffffff"));

                            button.setStyle("-fx-background-color: #324E7F");
                            
                            super.updateItem(item, empty);
                            
                            if (empty) {
                                
                                super.setGraphic(null);
                                
                                super.setText(null);
                                
                            } else {
                                
                                button.setOnAction((ActionEvent event) -> {
                                    
                                    ReportRecord reportRecord = getTableView().getItems().get(
                                            super.getIndex()
                                    );
                                    
                                    System.out.println(reportRecord.toString());
                                    
                                });
                                
                                super.setGraphic(button);
                                
                                super.setText(null);
                                
                            }
                            
                        }
                        
                    };
                    
                    return cell;
                    
        };
        
        detailsColumn.setCellFactory(cellFactory);
        
        this.reportTV.getColumns().add(detailsColumn);
        
    }
    
}
