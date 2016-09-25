package biometricauthentication.utils;

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
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Employee;
import biometricauthentication.model.Shift;

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
                "SELECT * FROM user_account WHERE "
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
    
    private BinnacleRecord getLastBinnacleRecord(Employee employee) {
        
        int employee_id = employee.getId();
        
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
    
    private Date parseSimpleDate(Date date) {
        
        String simpleDate = getSimpleDate(date);
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        
        try {
            return df.parse(simpleDate);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    public String saveBinnacleRecord(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        String operation = null;
        
        /*
            Obtener la fecha actual
        */
        Date currentDate = new Date();
        
        /*
            Obtener el último registro del empleado.
        */
        BinnacleRecord lastBinnacleRecord = getLastBinnacleRecord(employee);
        
        /*
            En caso que ya exista un registro
        */
        if (lastBinnacleRecord != null) {
            
            /*
                Se obtiene el Check In del último registro
            */
            String check_in = lastBinnacleRecord.getCheck_in();
            
            /*
                Se comprueba si existe un Check In 
                para evitar algún error en el registro.
                Si no existe, se crea.
            */
            if (check_in == null) {
                
                lastBinnacleRecord.setCheck_in(check_in);
                
                /*
                    La operación es de entrada
                */
                operation = "Entrada";
            
            /*
                Si existe un Check In, se verifica la
                existencia de un Check Out
            */
            } else {
                
                /*
                    Se obtiene el Check Out del último registro
                */
                String check_out = lastBinnacleRecord.getCheck_out();
                
                /*
                    Se comprueba si existe un Check Out
                    Si no existe, se crea.
                */
                if (check_out == null) {
                    
                    lastBinnacleRecord.setCheck_out(new SimpleDateFormat("HH:mm:ss").format(currentDate));
                    
                    /*
                        La operación es de salida
                    */
                    operation = "Salida";
                
                /*
                    En caso que ya exista un registro completo
                    se comprobará si la fecha del registro entrante
                    es una fecha posterior a la del último reigstro.
                */
                } else {
                    
                    /*
                        Se obtiene la fecha simple del último registro
                    */
                    Date lastBinnacleRecordDate = this.parseSimpleDate(lastBinnacleRecord.getDate());
                    
                    /*
                        Se asigna la fecha simple del registro actual a la fecha actual
                    */
                    currentDate = this.parseSimpleDate(currentDate);
                    
                    /*
                        Se realiza la comprobación de fechas.
                        
                        Si la fecha entrante es posterior a la del
                        último registro, se creará un nuevo registro.
                    */
                    if (currentDate.after(lastBinnacleRecordDate)) {
                        
                        /*
                            Creación de un nuevo registro.
                        */
                        this.createBinnacleRecord(employee, currentDate);
            
                        operation = "Entrada";
                        
                    /*
                        Si la comprobación de fechas demuestra
                        que las fechas son iguales o la fecha actual
                        es "anterior", se retornará "same_day".
                    */
                    } else {
                        
                        operation = "same_day";
                        
                    }
                    
                }
                
            }
            
            /*
                Finalmente se realiza una actualización
                a la base de datos.
            */
            session.saveOrUpdate(lastBinnacleRecord);   
            
        /*
            No existe algún registro del empleado.
            Se realiza la creación de un nuevo registro para el empleado.
        */
        } else {
            
            /*
                Creación de un nuevo registro.
            */
            this.createBinnacleRecord(employee, currentDate);
            
            operation = "Entrada";
            
        }
        
        transaction.commit();
        session.flush(); session.close();
        
        return operation;
        
    }
    
    public void createBinnacleRecord(Employee employee, Date currentDate) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se crea un nuevo registro.
            Se establecen los atributos date, employee_id, check_in.
        */
        BinnacleRecord binnacleRecord = new BinnacleRecord(
                currentDate, employee.getId(), new SimpleDateFormat("HH:mm:ss").format(currentDate)
        );
        
        /*
            Se inserta el nuevo registro.
        */
        session.save(binnacleRecord);
        
        transaction.commit();

        session.flush(); session.close();
        
    }
    
    public List<Shift> getShifts() {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se crea una lista para almacenar los turnos.
        */
        List<Shift> shifts = new ArrayList<>();
        
        /*
            Se obtienen los turnos existentes.
        */
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
        
        /*
            Se crea una lista para almacenar a los empleados.
        */
        List<Employee> employees = new ArrayList<>();
        
        /*
            Se obtienen los empleados existentes.
        */
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
        
        /*
            Se crea un template vacío.
        */
        DPFPTemplate template = DPFPGlobal.getTemplateFactory().createTemplate();
        
        /*
            Se deserializa el template
        */
        try {
            
            /*
                Se obtienen los bytes del template del empleado.
                Se asigna los deserializado al template anteriormente vacío.
            */
            template.deserialize(employee.getTemplate());
            
        } catch (IllegalArgumentException ex) {
            
            /*
                Si existe algún problema con la deserialización, 
                el template será nulo.
            */
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
        
        /*
            Se genera un url para la imagen del empleado.
        */
        String url = "C:\\Biometric\\".concat(String.valueOf(employee.getId())).concat(".png");
        
        /*
            Se establece una instancia de un archivo, 
            el cual será almacenado.
        */
        File toSave = new File(url);
        
        /*
            Si el archivo no existe, se crea el archivo.
        */
        if (!toSave.exists()) toSave.createNewFile();
        
        /*
            Se lee archivo por un BufferedImage
        */
        BufferedImage image = ImageIO.read(file);
        
        /*
            Se escriben los datos del buffer en el archivo
            creado posteriormente.
        */
        boolean written = ImageIO.write(image, "png", toSave);
        
        return written;
    }
    
    public File getFile(Employee employee) {
        
        /*
            Se genera un url para la imagen del empleado.
        */
        String url = "C:\\Biometric\\".concat(String.valueOf(employee.getId())).concat(".png");
        
        /*
            Se establece una instancia de un archivo, 
            el cual será recuperado.
        */
        File file = new File(url); 
        
        /*
            Si el archivo existe, se retornará.
        */
        if (file.exists()) {
            
            return file;
        
        /*
            Si no existe, retornará nulo, para mostrar
            la imagen por defecto.
        */
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
