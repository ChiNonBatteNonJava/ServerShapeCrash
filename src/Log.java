import java.util.Calendar;

/**
 * Created by ShilleR on 24/03/14.
 */
public class Log {
    public static void log(String msg){
        String log = Calendar.getInstance().getTime().toString();
        log += " > "+msg;
        System.out.println(log);
    }
}
