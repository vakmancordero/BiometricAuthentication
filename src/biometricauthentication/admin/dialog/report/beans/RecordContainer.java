package biometricauthentication.admin.dialog.report.beans;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Employee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author VakSF
 */
public class RecordContainer {
    
    private Map<Employee, ArrayList<BinnacleRecord>> map;

    public RecordContainer(List<Employee> employees) {
        this.map = new HashMap<>();
        
        employees.forEach((employee) -> {
            this.map.put(employee, new ArrayList<>());
        });
    }

    public Map<Employee, ArrayList<BinnacleRecord>> getMap() {
        return map;
    }
    
}
/**
 * 
 * No we
 * es para mira...
 * 
 * si te das cuenta en el tipo de Mapping que se está haciendo
 * Es que cada empleado contiene un ArrayList<BinnacleRecord> entonces, para poder
 * colocar los binnacleRecord correspondientes a cada empleado, tienes que utilizar el map
 * 
 * 
 * 
 */