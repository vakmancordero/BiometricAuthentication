package biometricauthentication.utils;

import java.util.Observable;
import com.digitalpersona.onetouch.DPFPSample;

/**
 *
 * @author Arturh
 */
public class Reader extends Observable implements Runnable {
        
    private volatile boolean isRunning;
    private DPFPReader myReader;
    
    public Reader() {
        this.myReader = new DPFPReader();
        this.isRunning = true;
    }

    @Override
    public void run() {

        while (this.isRunning) {
            
            this.myReader.findReader();
            
            if (!this.myReader.getActiveReader().equals("empty")) {
                
                try {

                    DPFPSample sample = this.myReader.getSample();

                    super.setChanged();
                    super.notifyObservers(sample);

                } catch (InterruptedException ex) {

                }
                
            }
        }

    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
