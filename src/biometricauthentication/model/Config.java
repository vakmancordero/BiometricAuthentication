package biometricauthentication.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author VakSF
 */
@Entity
@Table (name = "config")
public class Config implements Serializable {
    
    @Id
    private int id;
    
    @Column
    private String name;
    
    @Column (name = "early_in")
    private int earlyIn;
    
    @Column (name = "normal_in")
    private int normalIn;
    
    @Column (name = "late_in")
    private int lateIn;
    
    @Column (name = "early_out")
    private int earlyOut;
    
    @Column (name = "normal_out")
    private int normalOut;
    
    public Config() {
        
        /* CheckIn */
        this.earlyIn = -30;
        this.normalIn = 20;
        this.lateIn = 30;
        
        /* CheckOut */
        this.earlyOut = -15;
        this.normalOut = 3;
        
    }

    public Config(String name, int earlyIn, int normalIn, int lateIn, int earlyOut, int normalOut) {
        this.name = name;
        this.earlyIn = earlyIn;
        this.normalIn = normalIn;
        this.lateIn = lateIn;
        this.earlyOut = earlyOut;
        this.normalOut = normalOut;
    }
    
    public void setConfig(String name, int earlyIn, int normalIn, int lateIn, int earlyOut, int normalOut) {
        this.name = name;
        this.earlyIn = earlyIn;
        this.normalIn = normalIn;
        this.lateIn = lateIn;
        this.earlyOut = earlyOut;
        this.normalOut = normalOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEarlyIn() {
        return earlyIn;
    }

    public void setEarlyIn(int earlyIn) {
        this.earlyIn = earlyIn;
    }

    public int getNormalIn() {
        return normalIn;
    }

    public void setNormalIn(int normalIn) {
        this.normalIn = normalIn;
    }

    public int getLateIn() {
        return lateIn;
    }

    public void setLateIn(int lateIn) {
        this.lateIn = lateIn;
    }

    public int getEarlyOut() {
        return earlyOut;
    }

    public void setEarlyOut(int earlyOut) {
        this.earlyOut = earlyOut;
    }

    public int getNormalOut() {
        return normalOut;
    }

    public void setNormalOut(int normalOut) {
        this.normalOut = normalOut;
    }
    
    public String checkIn(int hours, int minutes) {
        
        System.out.println("Entrada");
            
        if (hours == 0) {
            
            if (minutes < 0) {
                
                if (minutes >= earlyIn) {
                    
                    return "normal";
                    
                } else {

                    return "temprano";

                }

            } else {

                if (minutes <= normalIn) {

                    return "normal";

                } else {

                    if (minutes <= lateIn) {

                        return "retardo";

                    } else {

                        return "falta";

                    }

                }

            }

        } else {

            if (hours < 0) {

                return "temprano";

            } else {
                
                return "tarde";  
               
            }

        }
        
    }
    
    public String checkOut(int hours, int minutes) {
        
        System.out.println("Salida");
        
        if (hours == 0) {
            
            if (minutes >= earlyOut) {
                
                return "normal";
                
            } else {
                
                return "temprano";
                
            }
            
        } else {
            
            if (hours < 0) {
                
                return "temprano";
                
            } else {
                
                if (hours <= normalOut) {
                    
                    return "normal";
                    
                } else {
                    
                    return "tarde";
                    
                }
                
            }
            
        }
        
    }

    @Override
    public String toString() {
        return "\nCheckIn:\n\tearlyIn: " + this.earlyIn + 
               "\n\tnormalIn: " + this.normalIn + 
               "\n\tlateIn: " + this.lateIn + 
               "\nCheckOut:\n\tearlyOut: " + this.earlyOut +
               "\n\tnormalOut: " + this.normalOut;
    }
    
}
