package AndroidServerTest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by ShilleR on 3/5/14.
 */
public class Server {

    private long beginTime;
    private ArrayList<Car> cars;
    private int id;
    private CircularBuffer circularBuffer;

    public Server(){
        circularBuffer = new CircularBuffer(10000, 1000);
        id = 0;
        beginTime = System.currentTimeMillis();
        cars = new ArrayList<Car>();
        try{
          //scList = new ArrayList<SocketChannel>();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(4444));
            while(true){
                SocketChannel sc = ssc.accept();
                String msg = read(sc);
                System.out.println(msg);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                long code = (Long) json.get("code");
                if (code == (long) 0) {
                    newCar(sc);
                } else {

                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void newCar(SocketChannel sc) throws IOException {
        JSONObject json = new JSONObject();
        long time = System.currentTimeMillis();
        json.put("time",time-beginTime);
        json.put("id", cars.size());
        //json.put("startPosition", ...);              --implement--
        //json.put("all other car");                   --implement--
        write(sc, json.toJSONString());
        //start thread
        Car c = new Car(newId(), sc, circularBuffer, beginTime);
        cars.add(c);
        c.start();
    }

    public String read(SocketChannel sc) throws IOException {
        String msg = "";
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        int byteRead = sc.read(byteBuffer);
        if(byteRead != -1){
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                msg += (char) byteBuffer.get();
            }
        }
        return msg;
    }

    public void write(SocketChannel sc, String msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
    }

    private int newId(){
        id++;
        return id;
    }
}
