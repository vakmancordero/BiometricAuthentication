package biometricauthentication.utils.report;

import java.util.ArrayList;
import java.util.List;
import java.time.Month;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.SQLQuery;

import biometricauthentication.utils.hibernate.HibernateUtil;

import biometricauthentication.admin.dialog.report.beans.RecordContainer;
import biometricauthentication.admin.dialog.report.beans.ReportRecord;

import biometricauthentication.model.BinnacleRecord;
import biometricauthentication.model.EmployeeType;
import biometricauthentication.model.Employee;
import biometricauthentication.model.Company;

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
    
    public RecordContainer getRecordContainer() {
        return recordContainer;
    }
    
    public List<ReportRecord> getReportRecords(Company company, EmployeeType employeeType,
            String year, Month month, String fortnight) {
        
        List<ReportRecord> reports = new ArrayList<>();
        
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
            
            String statement = 
                    "SELECT * FROM binnacle_record WHERE date BETWEEN "
                        + "str_to_date(" + initialDate + ", '%Y-%m-%d')"
                        + " AND "
                        + "str_to_date(" + finalDate + ", '%Y-%m-%d')";
            
            SQLQuery query = session.createSQLQuery(statement);
            
            query.addEntity(BinnacleRecord.class);
            
            List<BinnacleRecord> recordsList = query.list();
            
            if (recordsList.size() > 0) {
                
                recordsList.forEach((BinnacleRecord binnacleRecord) -> {
                    
                    Employee employee = (Employee) session.get(
                            Employee.class, binnacleRecord.getEmployeeId()
                    );
                    
                    recordContainer.getMap().get(employee).add(binnacleRecord);
                    
                });
                
                recordContainer.getMap().forEach(
                        (Employee employee, ArrayList<BinnacleRecord> employeeBinnacleRecords) -> {
                
                    ReportRecord reportRecord = new ReportRecord();

                    reportRecord.setEmployee(employee);
                    
                    for (BinnacleRecord binnacleRecord : employeeBinnacleRecords) {
                        
                        String report = binnacleRecord.getReport();
                        
                        if (report.equalsIgnoreCase("normal")) {
                            
                            reportRecord.addAssistance();
                            
                        } else {
                            
                            if (report.equalsIgnoreCase("retardo")) {
                                
                                reportRecord.addDeelay();
                                
                            } else {
                                
                                if (report.equalsIgnoreCase("falta")) {
                                    
                                    reportRecord.addLack();
                                    
                                }
                                
                            }
                            
                        }
                        
                    }
                    
                    reports.add(reportRecord);
                    
                });
                
            } else {
                
                System.out.println("No hay registros");
                
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
        
        return reports;
    }
    
}