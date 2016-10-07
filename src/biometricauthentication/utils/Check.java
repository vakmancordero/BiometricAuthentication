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
    
    public void setEntrada(int earlyIn, int normalIn, int lateIn) {
        this.earlyIn = earlyIn;
        this.normalIn = normalIn;
        this.lateIn = lateIn;
    }
    
    public void setSalida(int earlyOut, int normalOut) {
        this.earlyOut = earlyOut;
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
    
}