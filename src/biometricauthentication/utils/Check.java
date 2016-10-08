package biometricauthentication.utils;

/**
 *
 * @author VakSF
 */
public class Check {
    
    /* Tiempo para entrada */
    private int earlyIn, normalIn, lateIn;
    
    /* Tiempo para salida */
    private int earlyOut, normalOut;
    
    public Check() {
        
        /* CheckIn */
        this.earlyIn = -30;
        this.normalIn = 20;
        this.lateIn = 30;
        
        /* CheckOut */
        this.earlyOut = -15;
        this.normalOut = 3;
        
    }
    
    public void setCheckIn(int earlyIn, int normalIn, int lateIn) {
        this.earlyIn = earlyIn;
        this.normalIn = normalIn;
        this.lateIn = lateIn;
    }
    
    public void setCheckOut(int earlyOut, int normalOut) {
        this.earlyOut = earlyOut;
        this.normalOut = normalOut;
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