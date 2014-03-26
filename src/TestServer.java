import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Created by ShilleR on 24/03/14.
 */
public class TestServer {

    private int port;
    private ServerSocketChannel serverSocket;
    private ArrayList<Room> rooms;
    private boolean finish;
    private CommandLine cmd;
    private Thread tcmd;
    private ConnectionRequestManager crm;
    private Thread tcrm;


    public static void main(String[] args) {
        TestServer server = new TestServer(4444);
    }

    public TestServer(int port) {
        this.finish = false;
        this.port = port;
        cmd = new CommandLine(this);
        tcmd = new Thread(cmd);
        try {
            crm = new ConnectionRequestManager();
            tcrm = new Thread(crm);
            tcrm.start();
            tcmd.start();
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(port));
            Log.log("Server listening on port: "+port);
            while(!finish){
                SocketChannel sc = serverSocket.accept();
                Log.log("Connection request from "+sc.socket().getInetAddress().toString());
                crm.addSocketChannel(sc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit(){
        finish = true;
        crm.exit();
        while(tcrm.isAlive()){}
        Log.log("Server terminated");
    }
}


class CommandLine extends Thread{

    private TestServer ts;

    public CommandLine(TestServer server){
        ts = server;
    }

    public void run(){
        boolean finish = false;
        Scanner sc = new Scanner(System.in);
        String line = "";
        while (!finish){
            line = sc.nextLine();
            System.out.println(line);
            if(line.equals("exit")){
                finish = true;
                ts.exit();
            }
        }
    }
}