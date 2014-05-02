package AndroidServerTest;

import CarCam.Plane;
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
public class Server {

    public ServerSocketChannel ssc;
    public JPanel panel;
    public ArrayList<SocketChannel> scList;

    public static void main(String[] args) {
        Server s = new Server();
    }

    public Server(){
        try{
            scList = new ArrayList<SocketChannel>();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(4444));
            while(true){
                SocketChannel sc = ssc.accept();
                System.out.println("Asdca");
                String msg = read(sc);
                System.out.println(msg);
                scList.add(sc);
                //JSONObject json = (JSONObject) new JSONParser().parse(msg);
                //leggo dati creo una macchina
                //aggiungo la macchina ad un array
                //nuove thread per ricevere dati da quella macchina
                CarReciver cr = new CarReciver(sc,scList);
                Thread t = new Thread(cr);
                t.start();
                for(SocketChannel s: scList){
                    //dico a tutti che e' arrivata una nuova macchina
                    write(s,"asdfa");
                }
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

    public void run(){
        try {
            while(true){
                String msg = read(sc);
                /*
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                Long l = (Long) json.get("up");
                Integer up = Integer.valueOf(l.intValue());
                l = (Long) json.get("down");
                Integer down = Integer.valueOf(l.intValue());
                l = (Long) json.get("left");
                Integer left = Integer.valueOf(l.intValue());
                l = (Long) json.get("right");
                Integer right = Integer.valueOf(l.intValue());
                */
                for(SocketChannel s: scList){
                    write(s, "asd");//json.toJSONString());
                }
            }
        }catch (Exception e){
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

