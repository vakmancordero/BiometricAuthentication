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
                
                final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                
                while (true) {
                    
                    String hour = format.format(new Date()).concat(" hrs");
                    
                    super.updateMessage(hour);
                    
                    Thread.sleep(1000);
                    
                }
                
            }
            
        };
        
    }
    
}
