package biometricauthentication.utils;

import biometricauthentication.admin.dialog.report.beans.RecordContainer;
import java.util.List;
import java.time.Month;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import biometricauthentication.utils.hibernate.HibernateUtil;

import biometricauthentication.admin.dialog.report.beans.ReportRecord;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.Company;
import biometricauthentication.model.Employee;
import biometricauthentication.model.EmployeeType;
import java.util.ArrayList;

import org.hibernate.SQLQuery;

/**
 *
 * @author VakSF
 */
public class Report {
    
    private final SessionFactory sessionFactory;
    
    private RecordContainer recordContainer;
    
    public Report() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public void setEmployees(List<Employee> employees) {
        this.recordContainer = new RecordContainer(employees);
    }
    
    public List<ReportRecord> getReportRecords(Company company, EmployeeType employeeType,
            String year, Month month, String fortnight) {
        
        Session session = this.sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        String initialDate = "'" + year + "-" + month.getValue() + "-";
        String finalDate = "'" + year + "-" + month.getValue() + "-";
        
        try {
            
            if (fortnight.equalsIgnoreCase("primera")) {
                
                initialDate += "01'";
                finalDate += "15'";
                
            } else {
                
                if (fortnight.equalsIgnoreCase("segunda")) {
                    
                    initialDate += "16'";
                    finalDate = "LAST_DAY(" + initialDate + ")";
                    
                }
                
            }
            
            String statement = "SELECT * FROM binnacle_record WHERE date BETWEEN "
                    + "str_to_date(" + initialDate + ", '%Y-%m-%d') AND "
                    + "str_to_date(" + finalDate + ", '%Y-%m-%d')";
            
            SQLQuery query = session.createSQLQuery(statement);
            
            query.addEntity(BinnacleRecord.class);
            
            List<BinnacleRecord> records = query.list();
            
            if (records.size() > 0) {
                
                records.forEach((record) -> {
                    
                    System.out.println(record);
                    
                    Employee employee = (Employee) session.get(Employee.class, record.getEmployeeId());
                    
                    System.out.println(employee);
                    
                    recordContainer.getMap().get(employee).add(record);

                });
                
            } else {
                
                System.out.println("No hay registros");
                
            }
            
            recordContainer.getMap().forEach((Employee employee, ArrayList<BinnacleRecord> list) -> {
                
                System.out.println("Employee = " + employee + " : Cantidad = " + list.size());
                
            });
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            ex.printStackTrace();
              
            if (transaction != null) {
                
                transaction.rollback();
                
            }
             
        } finally {
            
            session.close();
            
        }
        
        return null;
    }
    
}
