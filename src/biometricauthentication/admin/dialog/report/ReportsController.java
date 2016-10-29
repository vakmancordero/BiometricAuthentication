package biometricauthentication.admin.dialog.report;

import java.time.Month;

import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

import java.net.URL;

import javafx.util.Callback;

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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;
import biometricauthentication.admin.dialog.report.details.ReportRecordDetailController;
import biometricauthentication.model.BinnacleRecord;

import biometricauthentication.model.Company;
import biometricauthentication.model.Employee;
import biometricauthentication.model.EmployeeType;
import biometricauthentication.utils.Biometric;
import biometricauthentication.utils.Report;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

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
    private void export() throws ClassNotFoundException, DRException {
        
        JasperPdfExporterBuilder fileExporter = export.pdfExporter("file.pdf");
        
        TextColumnBuilder<String> employeeColumn = 
                col.column("Empleado", "employee", type.stringType());
        
        TextColumnBuilder<String> typeColumn = 
                col.column("Tipo", "type", type.stringType());
        
        TextColumnBuilder<Integer> assistanceColumn = 
                col.column("Asistencias", "assistance", type.integerType());
        
        TextColumnBuilder<Integer> deelaysColumn = 
                col.column("Retardos", "deelays", type.integerType());
        
        TextColumnBuilder<Integer> lacksColumn = 
                col.column("Faltas", "lacks", type.integerType());
        
        TextColumnBuilder<Integer> justificactionsColumn = 
                col.column("Justificaciones", "justifications", type.integerType());
        
        try {
            
            report()
                    
                .setTemplate(Templates.reportTemplate)
                    
                .columns(
                        employeeColumn,
                        typeColumn,
                        assistanceColumn,
                        deelaysColumn,
                        lacksColumn,
                        justificactionsColumn
                )
                    
                .title(Templates.createTitleComponent("Reporte de bitácora"))
                    
                .pageFooter(Templates.footerComponent)
                    
                .setDataSource(createDataSource())
                    
                .toPdf(fileExporter)
                    
                .show();
            
        } catch (DRException ex) {
            ex.printStackTrace();
        }
    }
    
    private JRDataSource createDataSource() {
        
        DRDataSource dataSource = new DRDataSource(
                "employee", 
                "type", 
                "assistance", 
                "deelays", 
                "lack",
                "justifications"
        );
      
        for (ReportRecord reportRecord : this.reportList) {
            
            EmployeeType employeeType = reportRecord.getEmployeeType();
            
            String employeeTypeSt = 
                    employeeType != null ? 
                        employeeType.toString() :
                        "";
            
            dataSource.add(
                    reportRecord.getEmployee().toString(),
                    employeeTypeSt,
                    reportRecord.getAssistance(),
                    reportRecord.getDeelays(),
                    reportRecord.getLacks(),
                    reportRecord.getJustifications()
            );
        }
      
        return dataSource;
        
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