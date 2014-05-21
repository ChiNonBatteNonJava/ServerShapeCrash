package inutile;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 24/03/14.
 */
public class TestClient {
    public static void main(String[] args) {
        try{
            JSONObject o = new JSONObject();
            o.put("prova",new Integer(2));

            String j =o.toJSONString();
            JSONParser p = new JSONParser();
            JSONObject ob = (JSONObject)p.parse(j);
            System.out.println(ob.get("prova"));


            JSONObject o1 = new JSONObject();
            o1.put("questa","e' sparta");
            o1.put("json",o);
            
            String js = o1.toJSONString();
            System.out.println("js = " + js);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject)parser.parse(js);
            JSONObject j2 =(JSONObject) json.get("json");

            System.out.println(j2.get("altra"));

            String b = (String) null;

            int a = Integer.valueOf(b);
            System.out.println(a);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
