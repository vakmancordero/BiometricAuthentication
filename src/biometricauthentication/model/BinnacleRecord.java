package biometricauthentication.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Arturh
 */

@Entity
@Table (name = "binnacle_record")
public class BinnacleRecord implements Serializable {
    
    @Id
    private int id;
    
    @Column (name = "employee_id")
    private int employeeId;
    
    @Column (name = "check_in")
    private Date checkIn;
    
    @Column (name = "check_out")
    private Date checkOut;
    
    @Column
    private Date date;
    
    @Column
    private String day;
    
    @Column
    private Time worked_hours;
    
    @Column (name = "report")
    private String report;
    
    @Column
    private String observation;

    public BinnacleRecord() {
        
    }

    public BinnacleRecord(Date date, int employee_id, Date check_in) {
        this.date = date;
        this.employeeId = employee_id;
        this.checkIn = check_in;
    }
    
    public void update(BinnacleRecord binnacleRecord) {
        
        this.observation = binnacleRecord.getObservation();
        this.report = binnacleRecord.getReport();
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Time getWorked_hours() {
        return worked_hours;
    }

    public void setWorked_hours(Time worked_hours) {
        this.worked_hours = worked_hours;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    @Override
    public String toString() {
        return "BinnacleRecord{" + "employeeId=" + employeeId + ", checkIn=" + checkIn + ", "
             + "checkOut=" + checkOut + ", date=" + date + ", day=" + day + ", worked_hours=" + worked_hours + ", "
             + "report=" + report + ", observation=" + observation + '}';
    }
    
}
