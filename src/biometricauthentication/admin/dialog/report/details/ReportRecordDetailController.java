package biometricauthentication.admin.dialog.report.details;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;
import biometricauthentication.admin.dialog.report.details.justify.JustifyController;
import biometricauthentication.model.BinnacleRecord;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author VakSF
 */
public class ReportRecordDetailController implements Initializable {
    
    @FXML
    private TableView<BinnacleRecord> binnacleRecordTV;
    
    @FXML
    private TableColumn justifyColumn;
    
    @FXML
    private JFXTextField nameTF, assistanceTF, deelaysTF, lacksTF, justificationsTF;
    
    private ObservableList<BinnacleRecord> binnacleRecordList;
    
    private ReportRecord reportRecord;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setupTV();
    }
    
    public void setData(
            ArrayList<BinnacleRecord> binnacleRecords,
            ReportRecord reportRecord) {
        
        this.binnacleRecordList.addAll(
                binnacleRecords
        );
        
        this.fillFields(reportRecord);
        
    }
    
    private void fillFields(ReportRecord reportRecord) {
        
        this.reportRecord = reportRecord;
        
        this.nameTF.setText(reportRecord.getEmployee().toString());
        
        this.assistanceTF.setText(
                String.valueOf(reportRecord.getAssistance())
        );
        
        this.deelaysTF.setText(
                String.valueOf(reportRecord.getDeelays())
        );
        
        this.lacksTF.setText(
                String.valueOf(reportRecord.getLacks())
        );
        
        this.justificationsTF.setText(
                String.valueOf(reportRecord.getJustifications())
        );
        
    }
    
    @FXML
    private void getBack(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
    private void setupTV() {
        
        this.binnacleRecordList = FXCollections.observableArrayList();
        
        this.binnacleRecordTV.setItems(binnacleRecordList);
        
        justifyColumn.setCellFactory(new PropertyValueFactory("details"));
        
        Callback<TableColumn<BinnacleRecord, String>, TableCell<BinnacleRecord, String>> cellFactory = 
                (TableColumn<BinnacleRecord, String> value) -> {
                    
                    TableCell<BinnacleRecord, String> cell = new TableCell<BinnacleRecord, String>() {
                        
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            
                            final JFXButton button = new JFXButton("Justificar");
                            
                            button.setTextFill(Paint.valueOf("#ffffff"));
                            
                            button.setStyle("-fx-background-color: #107F32");
                            
                            super.updateItem(item, empty);
                            
                            if (empty) {
                                
                                super.setGraphic(null);
                                
                                super.setText(null);
                                
                            } else {
                                
                                button.setOnAction((ActionEvent event) -> {
                                    
                                    int index = super.getIndex();
                                    
                                    BinnacleRecord binnacleRecord = getTableView().getItems().get(
                                            index
                                    );
                                    
                                    openJustification(binnacleRecord, reportRecord, index);
                                    
                                });
                                
                                super.setGraphic(button);
                                
                                super.setAlignment(Pos.CENTER);
                                
                                super.setText(null);
                                
                            }
                            
                        }
                        
                    };
                    
                    return cell;
                    
        };
        
        justifyColumn.setCellFactory(cellFactory);
        
    }
    
    @FXML
    private void openJustification(
            BinnacleRecord binnacleRecord,
            ReportRecord reportRecord,
            int index) {
        
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/biometricauthentication/admin/dialog/report/details/justify/JustifyFXML.fxml"
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene((Pane) loader.load()));

            JustifyController justifyController = loader.<JustifyController>getController();

            justifyController.setData(binnacleRecord, reportRecord);
            
            stage.setOnCloseRequest((event) -> {
                
                this.binnacleRecordList.set(index, justifyController.getBinnacleRecord());
                
                this.binnacleRecordTV.refresh();
                
            });
            
            stage.setOnHidden((event) -> {
                
                this.binnacleRecordList.set(index, justifyController.getBinnacleRecord());
                
                this.binnacleRecordTV.refresh();
                
            });
            
            stage.showAndWait();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
