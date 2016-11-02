package biometricauthentication.admin.dialog.config.company;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import biometricauthentication.model.Company;

import biometricauthentication.utils.Biometric;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author VakSF
 */
public class CompanyController implements Initializable {
    
    @FXML
    private ListView<Company> companyLV;
    
    private ObservableList<Company> companyList;
    
    private Biometric biometric;
    
    private Company currentCompany;
    
    private boolean selectedCompany;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.biometric = new Biometric();
        
        this.companyList = FXCollections.observableArrayList(
                this.biometric.getCompanies()
        );
        
        this.companyLV.setItems(companyList);
        
    }
    
    public void setBiometric(Biometric biometric) {
        this.biometric = biometric;
    }
    
    public void setCurrentCompany(Company currentCompany) {
        this.currentCompany = currentCompany;
    }

    public void setSelectedCompany(boolean selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public boolean isSelectedCompany() {
        return selectedCompany;
    }
    
    @FXML
    private void setCompany(ActionEvent event) {
        
        if (this.currentCompany != null) {
            
            this.currentCompany.setUUID(null);
            
            this.biometric.saveCompany(currentCompany);
            
        }
        
        Company company = this.companyLV.getSelectionModel().getSelectedItem();
        
        if (company != null) {
            
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmación");
            confirmation.setHeaderText("Está seguro?");
            confirmation.setContentText(
                    "Desea asignar la compañía \"" + company + "\" a éste equipo?"
            );

            Optional<ButtonType> option = confirmation.showAndWait();

            if (option.get() == ButtonType.OK) {

                String UUID = this.biometric.getUUID();

                company.setUUID(UUID);

                this.biometric.saveCompany(company);
                
                this.setSelectedCompany(true);

                ButtonType finishButton = new ButtonType("Terminar");

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Información");
                alert.setHeaderText("Compañía establecida");
                alert.setContentText(
                        "Se ha asignado la compañía \"" + company + "\" al equipo con UUID " + UUID
                );
                
                alert.getButtonTypes().setAll(finishButton);
                
                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.get() == finishButton) {
                    ((Node) event.getSource()).getScene().getWindow().hide();
                }

            }
            
        }
        
    }
    
}