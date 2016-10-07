package biometricauthentication.utils;

import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Company;
import biometricauthentication.model.Employee;
import biometricauthentication.model.Shift;


/**
 *
 * @author VakSF
 */
public class Biometric {
    
    private SessionFactory sessionFactory;
    
    private DateUtil dateUtil;
    
    private Check check;

    public Biometric() {   
        this.sessionFactory = HibernateUtil.getSessionFactory();   
        this.dateUtil = new DateUtil();
        this.check = new Check();
        
        this.createRoot();
    }
    
    /**
     * Crea la carpeta raíz de las imágenes
     */
    private void createRoot() {
        
        File file = new File("C:\\Biometric\\");
        
        if (!file.exists()) {
            
            if (file.mkdir()) {
                
                System.out.println("Root creado: " + file.getAbsolutePath());
                
            }
            
        }
        
    }
    
    /**
     * Guarda un empleado
     * 
     * @param user Nombre de usuario¡
     * @param password Contraseña de usuario
     * @return La existencia del usuario
     */
    public boolean login(String user, String password) {
        
        /*
            Se obtiene un registro correspondiente
            al usuario y contraseña
        */
        return sessionFactory.openSession().createSQLQuery(
                "SELECT * FROM user_account WHERE "
              + "user = '" + user + "' AND password = '" + password + "'"
        ).setMaxResults(1).uniqueResult() != null;
        
    }
    
    /**
     * Guarda un empleado
     * 
     * @param employee es el empleado que se guardará
     * @see         Employee
     */
    public void saveEmployee(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se guarda o actualiza el empleado
        */
        session.saveOrUpdate(employee);
        
        transaction.commit();
        session.flush(); session.close();
        
    }
    
    /**
     * Retorna el ultimo registro del empleado que recibe.
     * 
     * @param employee es el empleado que contiene el registro a buscar
     * @return      El último registro del empleado
     * @see         BinnacleRecord
     */
    private BinnacleRecord getLastBinnacleRecord(Employee employee) {
        
        // Se obtiene el ID del empleado
        int employee_id = employee.getId();
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        BinnacleRecord binnacleRecord = null;
        
        try {
            
            /*
                Se obtienen los registros, se ordenan en orden descendiente y 
                se devuelve un único registro
            */
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
    
    /**
     * Retorna la información del guardado en bitácora.
     * El tipo puede ser "time" o "date"
     * 
     * @param employee especifica a que empleado se le guadará la bitacora
     * @return      informacion del guardado
     * @see         Information
     */
    public Information saveBinnacleRecord(Employee employee) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        String operation = null, verification = null;
        
        /*
            Se obtiene la fecha actual
        */
        Date currentDate = new Date();
        
        /*
            Se obtiene el último registro del empleado.
        */
        BinnacleRecord lastBinnacleRecord = getLastBinnacleRecord(employee);
        
        /*
            En caso que ya exista un registro
        */
        
        if (lastBinnacleRecord != null) {
            
            /*
                Se obtiene el Check In del último registro
            */
            Date check_in = lastBinnacleRecord.getCheck_in();
            
            /*
                Se comprueba si existe un Check In 
                para evitar algún error en el registro.
                Si no existe, se crea.
            */
            if (check_in == null) {
                
                verification = this.verifyRange(employee, currentDate, "check_in");
                
                if (!verification.equals("early")) {
                    
                    lastBinnacleRecord.setCheck_in(currentDate);
                
                    /*
                        La operación es de entrada
                    */
                    operation = "Entrada";
                    
                }
            
            /*
                Si existe un Check In, se verifica la
                existencia de un Check Out
            */
            } else {
                
                /*
                    Se obtiene el Check Out del último registro
                */
                Date check_out = lastBinnacleRecord.getCheck_out();
                
                /*
                    Se comprueba si existe un Check Out
                    Si no existe, se crea.
                */
                if (check_out == null) {
                    
                    verification = this.verifyRange(employee, currentDate, "check_out");
                    
                    if (!verification.equals("temprano")) {
                        
                        lastBinnacleRecord.setCheck_out(currentDate);

                        /*
                            La operación es de salida
                        */
                        operation = "Salida";
                        
                    }
                
                /*
                    En caso que ya exista un registro completo
                    se comprobará si la fecha del registro entrante
                    es una fecha posterior a la del último reigstro.
                */
                } else {
                    
                    /*
                        Se obtiene la fecha simple del último registro
                    */
                    Date lastBinnacleRecordDate = this.dateUtil.parseSimpleDate(
                            lastBinnacleRecord.getDate(), "date"
                    );
                    
                    /*
                        Se asigna la fecha simple del registro actual a la fecha actual
                    */
                    currentDate = this.dateUtil.parseSimpleDate(
                            currentDate, "date"
                    );
                    
                    /*
                        Se realiza la comprobación de fechas.
                        
                        Si la fecha entrante es posterior a la del
                        último registro, se creará un nuevo registro.
                    */
                    if (currentDate.after(lastBinnacleRecordDate)) {
                        
                        /*
                            Creación de un nuevo registro.
                        */
                        
                        currentDate = new Date();
                        
                        verification = this.createBinnacleRecord(employee, currentDate);
            
                        operation = "Entrada";
                        
                    /*
                        Si la comprobación de fechas demuestra
                        que las fechas son iguales o la fecha actual
                        es "anterior", se retornará "same_day".
                    */
                    } else {
                        
                        operation = "same_day";
                        verification = "same_day";
                        
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
            verification = this.createBinnacleRecord(employee, currentDate);
            
            operation = "Entrada";
            
        }
        
        Information info = new Information(operation, verification);
        
        transaction.commit();
        session.flush(); session.close();
        
        return info;
        
    }
    
    public String createBinnacleRecord(Employee employee, Date currentDate) {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se crea un nuevo registro.
            Se establecen los atributos date, employee_id, check_in.
        */
        
        String verified = this.verifyRange(employee, currentDate, "check_in");
        
        if (!verified.equals("early")) {
            
            Date newDate = new Date();
            
            BinnacleRecord binnacleRecord = new BinnacleRecord(
                    newDate, employee.getId(), newDate 
            );

            /*
                Se inserta el nuevo registro.
            */
            session.save(binnacleRecord);
            
        }
        
        transaction.commit();

        session.flush(); session.close();
        
        return verified;
        
    }
    
    private String verifyRange(Employee employee, Date currentDate, String type) {
        
        Shift shift = employee.getShift();
        
        currentDate = this.dateUtil.parseSimpleDate(currentDate, "time");
        
        if (type.equals("check_in")) {
            
            String checkInSt = shift.getCheck_in();
            
            Date checkIn = this.dateUtil.parseSimpleDate(checkInSt, "time");
            
            Map<TimeUnit, Long> difference = this.dateUtil.getDifference(checkIn, currentDate);
            
            int hours = this.dateUtil.getHours(difference);
            int minutes = this.dateUtil.getMinutes(difference);
            
            this.dateUtil.printDifference(difference);
            
            String cin = this.check.checkIn(hours, minutes);
            
            System.out.println(cin);
            
            return cin;
            
        } else {
            
            if (type.equals("check_out")) {
                
                String checkOutSt = shift.getCheck_out();
            
                Date checkOut = this.dateUtil.parseSimpleDate(checkOutSt, "time");

                Map<TimeUnit, Long> difference = this.dateUtil.getDifference(checkOut, currentDate);

                int hours = this.dateUtil.getHours(difference);
                int minutes = this.dateUtil.getMinutes(difference);
                
                String cout = this.check.checkOut(hours, minutes);
                
                System.out.println(checkOut);
                
                return cout;
                
            }
            
        }
        
        return "early";
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
    
    public List<Company> getCompanies() {
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se crea una lista para almacenar las compañias.
        */
        List<Company> shifts = new ArrayList<>();
        
        /*
            Se obtienen las compañias existentes.
        */
        try {
            
            shifts = session.createQuery("FROM Company").list();
            
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