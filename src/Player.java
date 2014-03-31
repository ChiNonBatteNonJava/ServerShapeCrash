import org.json.simple.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Matteo Piergiovanni
 * @version Alfa
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


    public Player(SocketChannel sc, int id){
        this.id = id;
        socket = sc;
    }

    public SocketChannel getSocket(){
        return socket;
    }

    public int getId(){
        return id;
    }

    public String recive() throws IOException, EOFException{
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
            throw new EOFException(er);
        }
        return msg;
    }

    public void send(JSONObject msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(msg.toJSONString().getBytes());
        byteBuffer.flip();
        socket.write(byteBuffer);
        Log.log(socket.socket().getInetAddress().toString() + " < " + msg.toJSONString());
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("id",id);
        return json;
    }

}
