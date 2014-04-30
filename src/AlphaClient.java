import com.oracle.javafx.jmx.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//import org.json.JSONArray;

/**
 * Created by matteopiergiovanni on 04.04.14.
 */

public class AlphaClient {
    public static void main(String[]args) throws IOException {

        AlphaClient x = new AlphaClient("10.62.162.205", 4444);
        x.action();

    }
    private SocketChannel sock;


    //constructor
    public AlphaClient(String ip, int gate) throws IOException {
        this.sock = SocketChannel.open();
        this.sock.connect(new InetSocketAddress(ip, gate));
        Recive r;
        r = new Recive(this.sock);
        Thread a = new Thread(r);
        a.start();

    }


    public void socketConnection(String ip, int gate) throws IOException {
        this.sock.connect(new InetSocketAddress(ip, gate));

    }

    public void listRoom(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinRoom(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 1);
            js.put("room_id", 0);
            js.put("player_id", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void createRoom(){
        JSONObject js = new JSONObject();
        JSONObject settings = new JSONObject();
        try {
            settings.put("max_player", 2);
            js.put("code", 2);
            js.put("player_id", 1);
            js.put("room_name", "Pierzz");
            js.put("room_password", "");
            js.put("password_request", 0);
            js.put("settings", settings);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void manageRoom(){


    }

    public void leftRoom(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void startPlay(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void action(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 6);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void endPlay(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void exit(){
        JSONObject js = new JSONObject();
        try {
            js.put("code", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer buff = ByteBuffer.allocate(512);
            buff.clear();
            buff.put(js.toJSONString().getBytes());
            buff.flip();
            this.sock.write(buff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


class Recive extends Thread {
    SocketChannel sc;

    public Recive(SocketChannel sc) {
        this.sc = sc;
    }

    public void run() {
        while (true) {
            try {
                ByteBuffer buff = ByteBuffer.allocate(512);
                buff.clear();
                int nbyte = sc.read(buff);
                String str = "";
                if (nbyte != -1) {
                    buff.flip();
                    while (buff.hasRemaining()) {
                        str += (char) buff.get();
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
}


