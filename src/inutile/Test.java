package inutile;

import AndroidServerTest.CarAction;
import AndroidServerTest.CircularBuffer;
import AndroidServerTest.CircularBufferIterator;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.tools.internal.xjc.generator.util.ExistingBlockReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by ShilleR on 22/03/14.
 */
public class Test {
    public static void main(String[] args) {
        /*CircularBuffer cb = new CircularBuffer(10,100);
        cb.insert(new CarAction(10, 1, 1),60);
        cb.insert(new CarAction(24,3,2),60);
        cb.insert(new CarAction(13,1,1),60);
        cb.insert(new CarAction(1,1,1), 110);
        CircularBufferIterator cbi = cb.getIterator();
        while(cbi.hasNext()){
            System.out.println(cbi.next().getTime());
        }
        cb.clear(120);
        cbi = cb.getIterator();
        while(cbi.hasNext()){
            System.out.println(cbi.next().getTime());
        }*/

        CircularBuffer cb = new CircularBuffer(10,100);
        Ttt t = new Ttt(cb);
        synchronized (t){
                t.start();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cb.insert(new CarAction(10, 10, 10), 50);
                cb.clear(1000);
        }

        cb.getIterator();
        System.out.println("ASD");
    }
}

class Ttt extends Thread{

    CircularBuffer cb;

    public Ttt(CircularBuffer cc){
        cb = cc;
    }

    public void run(){
        synchronized (this){
            System.out.println("qweqweqweqw");
        }
    }
}
