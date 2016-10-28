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
    
    @Column (name = "check_in")
    private String checkIn;
    
    @Column (name = "check_out")
    private String checkOut;
    
    @Column (name = "to_work")
    private int toWork;
    
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

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public int getToWork() {
        return toWork;
    }

    public void setToWork(int toWork) {
        this.toWork = toWork;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
    
}