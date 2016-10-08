package biometricauthentication.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author VakSF
 */
public class XMLUtil {
    
    private File configurationFile;

    public XMLUtil(String url) {
        this.configurationFile = new File(url);
    }
    
    public void buildConfig(Check check) {
        
        this.buildConfig(
                check.getEarlyIn(), check.getNormalIn(), check.getLateIn(),
                check.getEarlyOut(), check.getNormalOut()
        );
        
    }
    
    public boolean buildConfig(Integer earlyIn, Integer normalIn, 
            Integer lateIn, Integer earlyOut, Integer normalOut) {
        
        try {
            
            Document document = DocumentHelper.createDocument();
            
            Element root = document.addElement("biometric");
            
            
            Element checkIn = root.addElement("checkin")
                    .addAttribute("id", "axkan");
            
            checkIn.addElement("earlyIn").addText(earlyIn.toString());
            
            checkIn.addElement("normalIn").addText(normalIn.toString());
            
            checkIn.addElement("lateIn").addText(lateIn.toString());
            
            
            Element checkOut = root.addElement("checkout")
                    .addAttribute("id", "axkan");
            
            checkOut.addElement("earlyOut").addText(earlyOut.toString());
            
            checkOut.addElement("normalOut").addText(normalOut.toString());
            
            
            XMLWriter writer = new XMLWriter(
                    new FileWriter(this.configurationFile));
            
            writer.write(document);
            writer.close();
            
            return true;
            
        } catch (UnsupportedEncodingException ex) {
            
            ex.printStackTrace();
            
        } catch (IOException ex) {
            
            ex.printStackTrace();
            
        }
        
        return false;
        
    }
    
    public Check getConfig() {
        
        Check check = null;
        
        if (this.configurationFile.exists()) {
            
            check = new Check();
            
            try {
                
                SAXReader reader = new SAXReader();
                Document document = reader.read(this.configurationFile);
                
                Node checkIn = document.selectSingleNode("/biometric/checkin[@id='axkan']");
                Node checkOut = document.selectSingleNode("/biometric/checkout[@id='axkan']");
                
                check.setCheckIn(
                        this.getIntegerValue(checkIn, "earlyIn"),
                        this.getIntegerValue(checkIn, "normalIn"),
                        this.getIntegerValue(checkIn, "lateIn")
                );
                
                check.setCheckOut(
                        this.getIntegerValue(checkOut, "earlyOut"),
                        this.getIntegerValue(checkOut, "normalOut")
                );
                
            } catch (DocumentException ex) {
                
                ex.printStackTrace();
                
            }
            
        }
        
        return check;
        
    }
    
    private Integer getIntegerValue(Node parent, String name) {
        return Integer.parseInt(
                parent.selectSingleNode(name).getText()
        );
    }
    
}
