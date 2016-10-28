package biometricauthentication.utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;

/**
 *
 * @author VakSF
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        
        try {
            
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            
        } catch (HibernateException ex) {
            
            System.err.println("Initial SessionFactory creation failed." + ex);
            
            throw new ExceptionInInitializerError(ex);
            
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}