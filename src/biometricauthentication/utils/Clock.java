package biometricauthentication.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Arturh
 */
public class Clock extends Service<Void> {
    
    @Override
    protected Task<Void> createTask() {
        
        return new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                while (true) {
                    
                    String hour = new SimpleDateFormat("HH:mm:ss").format(new Date()) + " hrs";
                    
                    updateMessage(hour);
                    
                }
                
            }
            
        };
        
    }
    
}
