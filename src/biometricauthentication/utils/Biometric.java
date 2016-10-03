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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Employee;
import biometricauthentication.model.Shift;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
     * Retorna una fecha simple en base a un tipo y una fecha definida.
     * El tipo puede ser "time" o "date"
     * 
     * @param date es una fecha completa
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     */
    private String getSimpleDate(Date date, String type) {
        
        // Se obtiene un calendario
        Calendar calendar = Calendar.getInstance();
        
        // Se le establece una fecha
        calendar.setTime(date);
        
        /*
            Si el tipo es por tiempo, se retornará un String
            en forma de tiempo separado por dos puntos.
        */
        if (type.equals("time")) {
            
            return calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND);
        
        /*
            Si el tipo es por fecha, se retornará un String
            en forma de fecha separado por guiones medios.
        */
        } else {
            
            if (type.equals("date")) {
                
                return calendar.get(Calendar.DAY_OF_MONTH) + "-"
                        + calendar.get(Calendar.MONTH) + "-"
                        + calendar.get(Calendar.YEAR);
                
            }
            
        }
        
        return null;
        
    }
    
    /**
     * Parsea y retorna una fecha simple en base a un tipo y una fecha definida.
     * El tipo puede ser "time" o "date"
     * 
     * @param date es una fecha completa
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     * @see         Date
     */
    private Date parseSimpleDate(Date date, String type) {
        
        // Se obtiene un formato específico
        DateFormat dateFormat = type.equals("time") ? 
                
                // Si el tipo es de tiempo
                new SimpleDateFormat("HH:mm:ss") : 
                
                // Si el tipo es de fecha
                new SimpleDateFormat("dd-MM-yyyy");
        
        String simpleDate = getSimpleDate(date, type);
        
        try {
            
            // Se parsea la fecha
            return dateFormat.parse(simpleDate);
            
        } catch (ParseException ex) {
            
            return null;
            
        }
    }
    
    
    /**
     * Parsea y retorna una fecha simple en base a un tipo y una cadena.
     * El tipo puede ser "time" o "date"
     * 
     * @param dateSt es una fecha en String
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     * @see         Date
     */
    private Date parseSimpleDate(String dateSt, String type) {
        
        // Se obtiene un formato específico
        DateFormat dateFormat = type.equals("time") ? 
                
                // Si el tipo es de tiempo
                new SimpleDateFormat("HH:mm:ss") :
                
                // Si el tipo es de fecha
                new SimpleDateFormat("dd-MM-yyyy");
        
        try {
            
            // Se parsea la fecha
            return dateFormat.parse(dateSt);
            
        } catch (ParseException ex) {
            
            return null;
            
        }
        
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
            String check_in = lastBinnacleRecord.getCheck_in();
            
            /*
                Se comprueba si existe un Check In 
                para evitar algún error en el registro.
                Si no existe, se crea.
            */
            if (check_in == null) {
                
                verification = this.verifyRange(employee, currentDate, "check_in");
                
                if (!verification.equals("early")) {
                    
                    lastBinnacleRecord.setCheck_in(new SimpleDateFormat("HH:mm:ss").format(currentDate));
                
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
                String check_out = lastBinnacleRecord.getCheck_out();
                
                /*
                    Se comprueba si existe un Check Out
                    Si no existe, se crea.
                */
                if (check_out == null) {
                    
                    verification = this.verifyRange(employee, currentDate, "check_out");
                    
                    if (!verification.equals("early")) {
                        
                        lastBinnacleRecord.setCheck_out(new SimpleDateFormat("HH:mm:ss").format(currentDate));

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
                    Date lastBinnacleRecordDate = this.parseSimpleDate(
                            lastBinnacleRecord.getDate(), "date"
                    );
                    
                    /*
                        Se asigna la fecha simple del registro actual a la fecha actual
                    */
                    currentDate = this.parseSimpleDate(
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
            
            BinnacleRecord binnacleRecord = new BinnacleRecord(
                    currentDate, employee.getId(), new SimpleDateFormat("HH:mm:ss").format(currentDate)
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
        
        currentDate = this.parseSimpleDate(currentDate, "time");
        
        if (type.equals("check_in")) {
            
            System.out.println("CheckIn");
            
            String check_in_st = shift.getCheck_in();
            
            Date check_in = this.parseSimpleDate(check_in_st, "time");
            
            check_in = this.parseSimpleDate(check_in, "time");
            
            Map<TimeUnit, Long> computeDiff = computeDiff(check_in, currentDate);
            
            int hours = computeDiff.get(TimeUnit.HOURS).intValue();
            int minutes = computeDiff.get(TimeUnit.MINUTES).intValue();
            
            System.out.println("Horas = " + hours);
            System.out.println("Minutos = " + minutes);
            
            int early = -15;
            int normal = 15;
            int lack = 30;
            
            if (hours == 0) {
                
                if (minutes < 0) {
                    
                    if (minutes >= early) {
                        
                        return "normal";

                    } else {
                        
                        return "early";
                        
                    }
                    
                } else {
                    
                    if (minutes <= normal) {
                        
                        return "normal";
                        
                    } else {
                        
                        if (minutes < lack) {
                            
                            return "late";
                            
                        } else {
                            
                            return "lack";
                            
                        }
                        
                    }
                    
                }
                
            } else {
                
                if (hours < 0) {
                    
                    return "early";
                    
                } else {
                    
                    return "lack";
                    
                }
                
            }
            
        } else {
            
            if (type.equals("check_out")) {
                
                System.out.println("CheckOut");
                
                String check_out_st = shift.getCheck_out();
            
                Date check_out = this.parseSimpleDate(check_out_st, "time");

                check_out = this.parseSimpleDate(check_out, "time");

                Map<TimeUnit, Long> computeDiff = computeDiff(check_out, currentDate);

                int hours = computeDiff.get(TimeUnit.HOURS).intValue();
                int minutes = computeDiff.get(TimeUnit.MINUTES).intValue();
                
                System.out.println("Horas = " + hours);
                System.out.println("Minutos = " + minutes);

                int early = -10;
                int maxHours = 4;

                if (hours >= 0 && hours < maxHours) {
                    
                    if (minutes < early || minutes == 0) {

                        return "early";

                    } else {
                        
                        return "normal_out";
                        
                    }
                    
                } else {
                    
                    return "early";
                    
                }
                
            }
            
        }
        
        return "empty";
    }
    
    public Map<TimeUnit,Long> computeDiff(Date oldDate, Date currentDate) {
        
        System.out.println(oldDate);
        System.out.println(currentDate);
        
        long diffInMillies = currentDate.getTime() - oldDate.getTime();
        
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        
        Collections.reverse(units);
        
        Map<TimeUnit,Long> result = new LinkedHashMap<>();
        
        long milliesRest = diffInMillies;
        
        for (TimeUnit unit : units) {
            
            long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit, diff);
            
        }
        
        return result;
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