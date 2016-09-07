package biometricauthentication.data;

import java.util.Observable;
import com.digitalpersona.onetouch.DPFPSample;

import biometricauthentication.utils.DPFPReader;

/**
 *
 * @author Arturh
 */
public class ReaderEvent extends Observable implements Runnable {
        
    private volatile boolean isRunning;
    private DPFPReader myReader;
    
    public ReaderEvent() {
        this.myReader = new DPFPReader();
        this.isRunning = true;
    }

    @Override
    public void run() {

        while (isRunning) {
            
            myReader.findReader();
            
            if (!myReader.getActiveReader().equals("empty")) {
                
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
