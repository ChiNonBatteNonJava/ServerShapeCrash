package AndroidServerTest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by ShilleR on 2/5/14.
 */
public class ServerTest {

    public ServerSocketChannel ssc;
    public JPanel panel;
    public ArrayList<SocketChannel> scList;

    public static void main(String[] args) {
        ServerTest s = new ServerTest();
    }

    public ServerTest(){
        try{
            scList = new ArrayList<SocketChannel>();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(4444));
            while(true){
                SocketChannel sc = ssc.accept();
                String msg = read(sc);
                System.out.println(msg);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                json.put("code",0);
                CarReciver cr = new CarReciver(sc,scList);
                Thread t = new Thread(cr);
                t.start();
                for(SocketChannel s: scList){
                    write(s, json.toJSONString());
                }
                scList.add(sc);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

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
}


class CarReciver extends Thread{

    SocketChannel sc;
    ArrayList<SocketChannel> scList;

    public CarReciver(SocketChannel sc, ArrayList<SocketChannel> al){
        this.sc = sc;
        scList = al;
    }

    public void run() {

        while (true) {
            try {
                String msg = read(sc);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                json.put("code", 1);
                for (SocketChannel s : scList) {
                    write(s, json.toJSONString());
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
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
        System.out.println(msg);
        return msg;
    }

    public void write(SocketChannel sc, String msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
    }
}


