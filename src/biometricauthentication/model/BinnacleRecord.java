package biometricauthentication.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Arturh
 */

@Entity
@Table (name = "binnacle_record")
public class BinnacleRecord implements Serializable {
    
    @Id
    private int id;
    
    @Column
    private int operation;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date;
    
    @Column
    private int employee_id;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date check_in;
    
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date check_out;
    
    @Column
    private int hours_worked;
    
    @Column
    private int situation;
    
    @Column
    private String observation;

    public BinnacleRecord() {
    }

    public BinnacleRecord(Date date, int employee_id, Date check_in) {
        this.date = date;
        this.employee_id = employee_id;
        this.check_in = check_in;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public Date getCheck_in() {
        return check_in;
    }

    public void setCheck_in(Date check_in) {
        this.check_in = check_in;
    }

    public Date getCheck_out() {
        return check_out;
    }

    public void setCheck_out(Date check_out) {
        this.check_out = check_out;
    }

    public int getHours_worked() {
        return hours_worked;
    }

    public void setHours_worked(int hours_worked) {
        this.hours_worked = hours_worked;
    }

    public int getSituation() {
        return situation;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
    
    @Override
    public String toString() {
        return this.id + " : " + this.operation + " : " + this.date + " : " 
             + this.check_in + " : " + this.check_out;
    }
    
}
