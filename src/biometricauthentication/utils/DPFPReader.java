package biometricauthentication.utils;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.DPFPCapturePriority;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.readers.DPFPReadersCollection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Arturh
 */
public class DPFPReader {
    
    private String activeReader;
    
    public DPFPReader() {
        this.activeReader = this.selectReader();
    }

    public String getActiveReader() {
        return activeReader;
    }
    
    public void findReader() {
        this.activeReader = this.selectReader();
    }
    
    private String selectReader() throws IndexOutOfBoundsException {
        
        DPFPReadersCollection readers = DPFPGlobal.getReadersFactory().getReaders();
        
        String serialNumber = "empty";
        
        if (readers != null && !readers.isEmpty()) {
            
            serialNumber = readers.get(0).getSerialNumber();
            
        }
        
        return serialNumber;
    }
    
    public DPFPSample getSample() throws InterruptedException {
        
        DPFPSample sample = null;
        
        if (!this.activeReader.equals("empty")) {
            
            LinkedBlockingQueue<DPFPSample> samples = new LinkedBlockingQueue<>();
            
            DPFPCapture capture = DPFPGlobal.getCaptureFactory().createCapture();
            
            capture.setReaderSerialNumber(activeReader);
            
            capture.setPriority(DPFPCapturePriority.CAPTURE_PRIORITY_LOW);
            
            capture.addDataListener(new DPFPDataListener() {
                
                @Override
                public void dataAcquired(DPFPDataEvent event) {
                    
                    if (event != null && event.getSample() != null) {
                        
                        try {
                            
                            samples.put(event.getSample());
                            
                        } catch (InterruptedException ex) {

                        }
                        
                    }
                    
                }
                
            });
            
            capture.addReaderStatusListener(new DPFPReaderStatusAdapter() {

                int lastStatus = DPFPReaderStatusEvent.READER_CONNECTED;

                @Override
                public void readerConnected(DPFPReaderStatusEvent event) {

                    if (lastStatus != event.getReaderStatus()){

                        System.out.println("Reader is connected");

                    }

                    lastStatus = event.getReaderStatus();

                }

                @Override
                public void readerDisconnected(DPFPReaderStatusEvent event) {

                    if (lastStatus != event.getReaderStatus()) {

                        System.out.println("Reader is disconnected");

                    }

                    lastStatus = event.getReaderStatus();

                }

            });

            try {

                capture.startCapture();

                sample = samples.take();

            } catch (RuntimeException ex) {

                System.out.printf("Failed to start capture. Check that reader is not used by another application.\n");

                throw ex;

            } finally {

                capture.stopCapture();

            }
            
        }
        
        return sample;
        
    }
    
}
