package biometricauthentication.utils;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Employee;
import biometricauthentication.model.Shift;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;

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
    
    public boolean login(String user, String password) {
        
        return sessionFactory.openSession().createSQLQuery(
                "SELECT * FROM user_accounts WHERE "
              + "user = '" + user + "' AND password = '" + password + "'"
        ).setMaxResults(1).uniqueResult() != null;
        
    }
    
    public void saveEmployee(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        session.saveOrUpdate(employee);
        
        transaction.commit();
        session.flush(); session.close();
        
    }
    
    private BinnacleRecord getLastBinnacleRecord(int employee_id) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        BinnacleRecord binnacleRecord = null;
        
        try {
            
            binnacleRecord = (BinnacleRecord) session.createQuery(
                    "FROM BinnacleRecord where employee_id = " + employee_id + " order by id desc"
            ).setMaxResults(1).uniqueResult();
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            ex.printStackTrace();
              
            if (transaction != null) {
                
                transaction.rollback();
                
            }
             
        } finally {
            
            session.close();
            
        }
        
        return binnacleRecord;
    }
    
    private String getSimpleDate(Date date) {
        
        Calendar calendar = Calendar.getInstance();
                    
        calendar.setTime(date);
        
        return calendar.get(Calendar.DAY_OF_MONTH) + "-"
              + calendar.get(Calendar.MONTH) + "-"
              + calendar.get(Calendar.YEAR);
        
    }
    
    public String saveBinnacleRecord(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        String operation = "";
        
        Date date = new Date();
        
        BinnacleRecord binnacleRecord = getLastBinnacleRecord(employee.getId());
        
        if (binnacleRecord != null) {
            
            String check_in = binnacleRecord.getCheck_in();
            
            if (check_in == null) {
                
                binnacleRecord.setCheck_in(check_in);
                
                operation = "Entrada";
                
            } else {
                
                String check_out = binnacleRecord.getCheck_out();
                
                if (check_out == null) {
                    
                    binnacleRecord.setCheck_out(new SimpleDateFormat("HH:mm:ss").format(date));
                    
                    operation = "Salida";
                    
                } else {
                    
                    Date binnacleDate = binnacleRecord.getDate();
                    
                    if (date.after(binnacleDate)) {
                        
                        String simple_binnacle_date = getSimpleDate(binnacleDate);
                        
                        String current_simple_date = getSimpleDate(date);
                        
                        if (!simple_binnacle_date.equals(current_simple_date)) {
                            
                            binnacleRecord = new BinnacleRecord();
                            
                            String new_check_in = new SimpleDateFormat("HH:mm:ss").format(date);
                            
                            binnacleRecord.setCheck_in(new_check_in);
                            
                            binnacleRecord.setDate(date);
                            
                            binnacleRecord.setEmployee_id(employee.getId());
                            
                            operation = "Entrada";
                            
                        } else {
                            
                            operation = "same_day";
                            
                        }
                        
                    }
                    
                }
                
            }
            
            session.saveOrUpdate(binnacleRecord);   
        
        } else {
            
            binnacleRecord = new BinnacleRecord();

            binnacleRecord.setCheck_in(new SimpleDateFormat("HH:mm:ss").format(date));

            binnacleRecord.setDate(date);

            binnacleRecord.setEmployee_id(employee.getId());

            session.save(binnacleRecord);
            
            operation = "Entrada";
            
        }
        
        transaction.commit();

        session.flush(); session.close();
        
        return operation;
        
    }
    
    public List<Shift> getShifts() {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        List<Shift> shifts = new ArrayList<>();
        
        try {
            
            shifts = session.createQuery("FROM Shift").list();
            
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
    
    /*
    public byte[] serializeFile(File file) throws IOException {
        
        byte[] bytes = new byte[(int) file.length()];
            
        FileInputStream inputStream = new FileInputStream(file);
        
        inputStream.read(bytes);
        
        return bytes;
    }
    
    public File deserializeFile(Employee employee) {
        
        File file = new File("image.jpg");
        
        try {
            
            byte[] bytes = employee.getPhoto();
            
            FileOutputStream fos = new FileOutputStream(file);
            
            fos.write(bytes);
            
        } catch (IOException | NullPointerException ex) {
            
        }
        
        return file;
        
    }
    */
    
    public boolean saveFile(Employee employee, File file) throws IOException {
        
        String url = "C:\\Biometric\\".concat(String.valueOf(employee.getId())).concat(".png");
        
        File toSave = new File(url);
        
        if (!toSave.exists()) toSave.mkdir();
        
        BufferedImage image = ImageIO.read(file);
        
        boolean written = ImageIO.write(image, "png", toSave);
        
        return written;
    }
    
    public File getFile(Employee employee) {
        
        String url = "C:\\Biometric\\".concat(String.valueOf(employee.getId())).concat(".png");
        
        File file = new File(url); 
        
        if (file.exists()) {
            
            return file;
            
        } else {
            
            return null;
            
        }
        
    }
    
    public boolean verify(DPFPSample sample, DPFPTemplate template)  {
        
        boolean verified = false;
        
        try {
            
            DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPFeatureSet featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
            
            DPFPVerification matcher = DPFPGlobal.getVerificationFactory().createVerification();
            matcher.setFARRequested(DPFPVerification.MEDIUM_SECURITY_FAR);
            
            DPFPVerificationResult result = matcher.verify(featureSet, template);
            
            verified = result.isVerified();
                        
        } catch (DPFPImageQualityException ex) {
            
            ex.printStackTrace();
            
        }
        
        return verified;
        
    }
    
}
