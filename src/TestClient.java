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

            JSONObject json = new JSONObject();
            json.put("request",new Integer(2));

            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("127.0.0.1",4444));
            ByteBuffer bb = ByteBuffer.allocate(512);
            bb.put(json.toJSONString().getBytes());
            bb.flip();
            sc.write(bb);

            Thread.sleep(10000);


            /*
            JSONObject o = new JSONObject();
            o.put("prova",new Integer(2));

            String j =o.toJSONString();
            JSONParser p = new JSONParser();
            JSONObject ob = (JSONObject)p.parse(j);
            System.out.println(ob.get("prova"));

            /*
            JSONObject o1 = new JSONObject();
            o1.put("questa","e' sparta");
            o1.put("json",o);
            
            String js = o1.toJSONString();
            System.out.println("js = " + js);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject)parser.parse(js);
            JSONObject j2 =(JSONObject) json.get("json");

            System.out.println(j2.get("altra"));
            */



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
