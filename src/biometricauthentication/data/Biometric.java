package biometricauthentication.data;

import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author VakSF
 */
public class Biometric {
    
    private SessionFactory sessionFactory;

    public Biometric() {   
        this.sessionFactory = HibernateUtil.getSessionFactory();   
    }
    
    public void saveEmployee(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        session.saveOrUpdate(employee);
        
        transaction.commit();
        
        session.flush(); session.close();
        
    }
    
    public List<Shift> getShifts() {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        List<Shift> shifts = new ArrayList<>();
        
        try {
            
            shifts = session.createQuery("FROM Shift").list();
            
            for (Shift shift : shifts) {
                
                System.out.println("Biometric Class -> getShifts() -> " + shift.getDescription());
                
            }
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            ex.printStackTrace();
              
            if (transaction != null) {
                
                transaction.rollback();
                
            }
             
        } finally {
            
            session.close();
            
        }
        
        return shifts;
        
    }
    
    public List<Employee> getEmployees() {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        List<Employee> employees = new ArrayList<>();
        
        try {
            
            employees = session.createQuery("FROM Employee").list();
            
            for (Employee employee : employees) {
                
                System.out.println("Biometric Class -> " + employee.toString());
                
            }
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            ex.printStackTrace();
              
            if (transaction != null) {
                
                transaction.rollback();
                
            }
             
        } finally {
            
            session.close();
            
        }
        
        return employees;
        
    }
    
    public byte[] serializeTemplate(DPFPTemplate template) {
        
        return template.serialize();
        
    }
    
    public DPFPTemplate deserializeTemplate(Employee employee) {
        
        DPFPTemplate template = DPFPGlobal.getTemplateFactory().createTemplate();
        
        try {
            
            template.deserialize(employee.getTemplate());
            
        } catch (IllegalArgumentException ex) {
            
            template = null;
            
        }
        
        return template;
        
    }
    
    public byte[] serializeFile(File file) throws IOException {
        
        byte[] bytes = new byte[(int) file.length()];
            
        FileInputStream inputStream = new FileInputStream(file);
        
        inputStream.read(bytes);
        
        return bytes;
    }
    
    public File deserializeFile(Employee employee) throws FileNotFoundException, IOException {
        
        byte[] bytes = employee.getPhoto();
        
        File file = new File("image.jpg");
        
        System.out.println(file.length());
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            
            fos.write(bytes);
            
        }
        
        System.out.println(file.length());
        
        return file;
        
    }
    
    public boolean verify(DPFPSample sample, DPFPTemplate template)  {
        
        boolean verified = false;
        
        try {
            
            DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPFeatureSet featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
            
            DPFPVerification matcher = DPFPGlobal.getVerificationFactory().createVerification();
            matcher.setFARRequested(DPFPVerification.LOW_SECURITY_FAR);
            
            DPFPVerificationResult result = matcher.verify(featureSet, template);
            
            verified = result.isVerified();
            
            if (verified) {
                
                System.out.println((double)result.getFalseAcceptRate() / DPFPVerification.PROBABILITY_ONE);
                
            }
            
        } catch (DPFPImageQualityException ex) {
            
            ex.printStackTrace();
            
        }
        
        return verified;
        
    }
    
}
