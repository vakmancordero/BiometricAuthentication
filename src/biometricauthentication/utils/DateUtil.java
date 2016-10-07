package biometricauthentication.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author VakSF
 */
public class DateUtil {

    public DateUtil() {
        
    }
    
    /**
     * Retorna una mapping de diferencia de tiempo
     * entre dos fechas establecidas
     * 
     * @param oldDate es una fecha registrada
     * @param currentDate es la fecha actual
     * @return      un mapping con la diferencia de tiempo en unidades
     */
    public Map<TimeUnit, Long> getDifference(Date oldDate, Date currentDate) {
        
        System.out.println(oldDate);
        System.out.println(currentDate);
        
        /*
            Se obtiene la diferencia en milisegundos
        */
        long diffInMillies = currentDate.getTime() - oldDate.getTime();
        
        /*
            Se obtienen las unidades de tiempo de TimeUnit
        */
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        
        /*
            Se ordena inversamente la lista de unidades de tiempo
        */
        Collections.reverse(units);
        
        /*
            Se crea un Map para las unidades de tiempo
        */
        Map<TimeUnit,Long> result = new LinkedHashMap<>();
        
        long milliesRest = diffInMillies;
        
        /*
            Se asignan los valores correspondientes al Map
        */
        for (TimeUnit unit : units) {
            
            long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            
            milliesRest = milliesRest - diffInMilliesForUnit;
            
            result.put(unit, diff);
            
        }
        
        return result;
    }
    
    public int getHours(Map<TimeUnit, Long> difference) {
        return difference.get(TimeUnit.HOURS).intValue();
    }
    
    public int getMinutes(Map<TimeUnit, Long> difference) {
        return difference.get(TimeUnit.MINUTES).intValue();
    }
    
    /**
     * Imprime las unidades de tiempo de una 
     * diferencia de tiempo.
     * 
     * @param difference es un mapping de diferencia de tiempo
     */
    public void printDifference(Map<TimeUnit, Long> difference) {
        
        /*
            Se enlistan las unidades y se recorren con difference
        */
        EnumSet.allOf(TimeUnit.class).stream().forEach((timeUnit) -> {
            
            System.out.println(timeUnit + " : " + difference.get(timeUnit));
            
        });
        
    }
    
    /**
     * Retorna una fecha simple en base a un tipo y una fecha definida.
     * El tipo puede ser "time" o "date"
     * 
     * @param date es una fecha completa
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     */
    public String getSimpleDate(Date date, String type) {
        
        // Se obtiene un calendario
        Calendar calendar = Calendar.getInstance();
        
        // Se le establece una fecha
        calendar.setTime(date);
        
        /*
            Si el tipo es por tiempo, se retornará un String
            en forma de tiempo separado por dos puntos.
        */
        if (type.equals("time")) {
            
            return calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND);
        
        /*
            Si el tipo es por fecha, se retornará un String
            en forma de fecha separado por guiones medios.
        */
        } else {
            
            if (type.equals("date")) {
                
                return calendar.get(Calendar.DAY_OF_MONTH) + "-"
                        + calendar.get(Calendar.MONTH) + "-"
                        + calendar.get(Calendar.YEAR);
                
            }
            
        }
        
        return null;
        
    }
    
    /**
     * Parsea y retorna una fecha simple en base a un tipo y una fecha definida.
     * El tipo puede ser "time" o "date"
     * 
     * @param date es una fecha completa
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     * @see         Date
     */
    public Date parseSimpleDate(Date date, String type) {
        
        // Se obtiene un formato específico
        DateFormat dateFormat = type.equals("time") ? 
                
                // Si el tipo es de tiempo
                new SimpleDateFormat("HH:mm:ss") : 
                
                // Si el tipo es de fecha
                new SimpleDateFormat("dd-MM-yyyy");
        
        String simpleDate = getSimpleDate(date, type);
        
        try {
            
            // Se parsea la fecha
            return dateFormat.parse(simpleDate);
            
        } catch (ParseException ex) {
            
            return null;
            
        }
    }
    
    
    /**
     * Parsea y retorna una fecha simple en base a un tipo y una cadena.
     * El tipo puede ser "time" o "date"
     * 
     * @param dateSt es una fecha en String
     * @param type el tipo a convertir
     * @return      la fecha esperada en base al tipo
     * @see         Date
     */
    public Date parseSimpleDate(String dateSt, String type) {
        
        // Se obtiene un formato específico
        DateFormat dateFormat = type.equals("time") ? 
                
                // Si el tipo es de tiempo
                new SimpleDateFormat("HH:mm:ss") :
                
                // Si el tipo es de fecha
                new SimpleDateFormat("dd-MM-yyyy");
        
        try {
            
            // Se parsea la fecha
            return dateFormat.parse(dateSt);
            
        } catch (ParseException ex) {
            
            return null;
            
        }
        
    }
    
}
