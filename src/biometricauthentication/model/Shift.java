package biometricauthentication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 *
 * @author VakSF
 */
@Entity
@Table (name = "shift")
public class Shift implements Serializable {
    
    @Id
    private int id;
    
    @Column
    private String description;
    
    @Column
    private String check_in;
    
    @Column
    private String check_out;
    
    @Column
    private int to_work;
    
    @Column
    private int status;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCheck_in() {
        return check_in;
    }
    
    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }
    
    public String getCheck_out() {
        return check_out;
    }
    
    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }
    
    public int getTo_work() {
        return to_work;
    }
    
    public void setTo_work(int to_work) {
        this.to_work = to_work;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return this.id + " : " + this.description + " : " + to_work + " horas";
    }
    
}