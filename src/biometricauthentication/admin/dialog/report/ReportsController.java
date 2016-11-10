package biometricauthentication.admin.dialog.report;

import java.time.Month;

import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import javafx.geometry.Pos;

import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;
import biometricauthentication.admin.dialog.report.beans.SimpleReportRecord;
import biometricauthentication.admin.dialog.report.details.ReportRecordDetailController;

import biometricauthentication.model.Company;
import biometricauthentication.model.Employee;
import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.EmployeeType;
import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.report.Report;
import java.io.File;

import java.net.URISyntaxException;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.stage.FileChooser;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author VakSF
 */
public class ReportsController implements Initializable {
    
    @FXML
    private TableView<ReportRecord> reportTV;
    
    @FXML
    private TableColumn detailsColumn;
    
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
    
    private Company company;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.reportList = FXCollections.observableArrayList();
        
        this.report = new Report();
        
        this.setupTV();
        
    }
    
    public void setBiometric(Biometric biometric) {
        
        this.biometric = biometric;
        
        this.fillInputs();
        
        this.company = this.biometric.getCompany();
        
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
        
        this.report.setCompany(this.biometric.getCompany());
        
        List<ReportRecord> reportRecords = this.report.getReportRecords(
                company, employeeType, year, month, fortnight
        );
        
        if (!reportRecords.isEmpty()) {
            
            this.reportList.addAll(reportRecords);
            
        } else {
            
            new Alert(
                    AlertType.INFORMATION,
                    "No se han encontrado resultados"
            ).show();
            
        }
        
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
        
        this.monthCB.setConverter(new StringConverter<Month>() {
            
            @Override
            public String toString(Month month) {
                
                String displayName = month.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                
                displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
                
                return displayName;
                
            }
            
            @Override
            public Month fromString(String month) {
                return Month.valueOf(month);
            }
            
        });
        
        this.monthCB.getSelectionModel().selectFirst();
        
        this.fortnightCB.getItems().addAll(
                "Primera",
                "Segunda"
        );
        
        this.fortnightCB.getSelectionModel().selectFirst();
        
    }
    
    private void setupTV() {
        
        this.reportTV.setItems(this.reportList);
        
        this.detailsColumn.setCellFactory(new PropertyValueFactory("details"));
        
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
                                    
                                    Employee employee = reportRecord.getEmployee();
                                    
                                    ArrayList<BinnacleRecord> binnacleRecords = 
                                            report.getRecordContainer().getMap().get(employee);
                                    
                                    openReportRecordDetail(
                                            binnacleRecords, reportRecord
                                    );
                                    
                                });
                                
                                super.setGraphic(button);
                                
                                super.setAlignment(Pos.CENTER);
                                
                                super.setText(null);
                                
                            }
                            
                        }
                        
                    };
                    
                    return cell;
                    
        };
        
        this.detailsColumn.setCellFactory(cellFactory);
        
    }
    
    @FXML
    private void export() throws URISyntaxException {
        
        if (!this.reportList.isEmpty()) {
            
            String fileName = "";
            
            File templateFile = new File("C:\\Biometric\\Reportes\\mexica_template.jasper");
            
            if (templateFile.exists()) {
                
                fileName = templateFile.getAbsolutePath();
                
            } else {
                
                File file = new FileChooser().showOpenDialog(null);

                if (file != null) {
                    
                    fileName = file.getAbsolutePath();
                    
                }
                
            }
            
            String details = ("Reporte - " + this.fortnightCB.getValue() + " quincena de " +
                            this.monthCB.getValue().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + " de " + this.yearCB.getValue()).toUpperCase();

            ArrayList<SimpleReportRecord> filterDataSource = new ArrayList<>();

            filterDataSource.add(new SimpleReportRecord());

            for (ReportRecord reportRecord : this.reportList) {
                filterDataSource.add(new SimpleReportRecord(reportRecord));
            }

            JRBeanCollectionDataSource beans = new JRBeanCollectionDataSource(filterDataSource);

            Map parameters = new HashMap();

            parameters.put("company", this.company.toString());
            parameters.put("details", details);

            try {

                JasperPrint print = JasperFillManager.fillReport(fileName, parameters, beans);

                JasperViewer jasperViewer = new JasperViewer(print, false);
                
                jasperViewer.setVisible(true);
                
                jasperViewer.setTitle("Reporte");

            } catch (JRException ex) {

                ex.printStackTrace();
            }
            
        } else {
            
            new Alert(
                    AlertType.INFORMATION,
                    "No hay registros para exportar"
            ).show();
            
        }
        
    }
    
    @FXML
    private void openReportRecordDetail(
            ArrayList<BinnacleRecord> binnacleRecords, 
            ReportRecord reportRecord) {
        
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/biometricauthentication/admin/dialog/report/details/ReportRecordDetailFXML.fxml"
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene((Pane) loader.load()));

            ReportRecordDetailController reportDetailController = 
                    loader.<ReportRecordDetailController>getController();

            reportDetailController.setData(binnacleRecords, reportRecord);
            
            stage.setOnCloseRequest((event) -> {
                this.generate();
            });
            
            stage.setOnHidden((event) -> {
                this.generate();
            });
            
            stage.showAndWait();
            
        } catch (IOException ex) {
            
            ex.printStackTrace();
            
        }
        
    }
    
}