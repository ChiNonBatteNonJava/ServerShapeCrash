package AndroidServerTest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 3/5/14.
 */
public class Car extends Thread{

    private CarStatus status;
    private int id;
    private SocketChannel sc;
    private boolean exit;
    private CircularBuffer circularBuffer;
    private long startTime;

    public Car(int id, SocketChannel sc, CircularBuffer buff, long startTime){
        status = new CarStatus();
        this.id = id;
        this.sc = sc;
        circularBuffer = buff;
        this.startTime = startTime;
    }

    public void send(String msg) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.put(msg.getBytes());
        bb.flip();
        sc.write(bb);
    }

    public String recive() throws IOException {
        String msg = "";
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        int byteRead = sc.read(byteBuffer);
        if(byteRead != -1){
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                msg += (char) byteBuffer.get();
            }
        }else{
            new EOFException();
        }
        return msg;
    }

    public void run(){
        while(exit){
            try {
                String msg = recive();
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                Long l = (Long) json.get("type");
                circularBuffer.insert(new CarAction(System.currentTimeMillis()-startTime, id, l.intValue()), System.currentTimeMillis());
            }catch (EOFException e){
                exit = true;
            }catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public int getCarId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SocketChannel getSc() {
        return sc;
    }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }


}
