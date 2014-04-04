import org.json.JSONException;
import org.json.JSONObject;

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

        AlphaClient x=new AlphaClient("127.0.0.1", 4444);

    }
    private SocketChannel sock;


    //constructor
    public AlphaClient(String ip, int gate) throws IOException {
        this.sock = SocketChannel.open();
        this.sock.connect(new InetSocketAddress(ip, gate));


    }


    public void socketConnection(String ip, int gate) throws IOException {
        this.sock.connect(new InetSocketAddress(ip, gate));

    }

    public void listRoom(){


    }

    public void joinRoom(){


    }

    public void createRoom(){


    }

    public void manageRoom(){


    }

    public void leftRoom(){


    }

    public void startPlay(){


    }

    public void action(){


    }

    public void endPlay(){


    }

    public void exit() throws JSONException {
        JSONObject js = new JSONObject();
        try {
            js.put("code", "100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Send sender;
        sender = new Send(this.sock, js.toString());
        Thread b = new Thread(sender);
        b.start();
    }

    class Send extends Thread{
        private final SocketChannel sock;
        private final String jString;
        SocketChannel sc;
        public Send(SocketChannel sc, String jString) {
            this.sock = sc;
            this.jString = jString;
        }
        public void run() {
            while (true) {
                try {
                    ByteBuffer buff = ByteBuffer.allocate(512);
                    buff.clear();
                    buff.put(jString.getBytes());
                    buff.flip();
                    sc.write(buff);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            }
        }

    }

}
