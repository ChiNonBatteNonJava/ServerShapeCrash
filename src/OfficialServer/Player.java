package OfficialServer;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 25/03/14.
 */

public class Player
{
    private String name;
    private SocketChannel socket;
    private int color;
    private Point2f position;
    private float direction;                        //?
    private float speed;                              //?
    private int id;
    //private PhysicCar car;
    private SelectionKey selectionKey;

    public static final int CAR_FORWARD = 1;
    public static final int CAR_BACKWARD = -1;
    public static final int CAR_STEERING_RIGHT = 1;
    public static final int CAR_STEERING_LEFT = -1;
    public static final int CAR_STEERING_NULL = 0;


    public Player(SocketChannel sc, int id, String world){
        this.id = id;
        socket = sc;
    }

    public SocketChannel getSocket(){
        Log.log("getSocket"+socket);
        return socket;
    }

    public int getPlayerId(){
        return id;
    }

    public String recive() throws IOException{
        String msg = "";
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        int byteRead = socket.read(byteBuffer);
        if(byteRead != -1){
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                msg += (char) byteBuffer.get();
            }
            Log.log(socket.socket().getInetAddress().toString()+" > "+msg);
        }else{
            String er = socket.socket().getInetAddress().toString()+" - Connection closed";
            JSONObject json = new JSONObject();
            json.put("code", ConnectionRequestManager.EXIT);
            msg = json.toJSONString();
        }
        return msg;
    }

    public void send(JSONObject msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put((msg.toJSONString()+"\n").getBytes());
        byteBuffer.flip();
        socket.write(byteBuffer);
        Log.log(socket.socket().getInetAddress().toString() + " < " + msg.toJSONString());
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("id",id);
        return json;
    }

    public SelectionKey getSelectionKey(){
        Log.log("selectionKey");
        return selectionKey;
    }

    public void setKey(SelectionKey key){
        selectionKey = key;
        Log.log("setKey"+key);
    }
/*
    public PhysicCar getCar(){
        return car;
    }
    */
}
