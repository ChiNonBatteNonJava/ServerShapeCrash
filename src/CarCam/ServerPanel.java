package CarCam;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by ShilleR on 06/04/14.
 */
public class ServerPanel extends JPanel {

    public ServerSocketChannel ssc;
    public RealServer rs;
    public ArrayList<Plane> planes;
    public ImageIcon sfondo;

    public ServerPanel(){
        super();
        try{
            sfondo    = new ImageIcon("sfondo.jpg");
            planes = new ArrayList<Plane>();
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(4444));
            rs = new RealServer(ssc, planes, this);
            Thread t = new Thread(rs);
            t.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(sfondo.getImage(),0,0,this);
        for(Plane p: planes){
            p.paint(g);
        }
    }
}

class RealServer extends Thread{

    public ServerSocketChannel ssc;
    public ArrayList<Plane> planes;
    public JPanel panel;
    public ArrayList<SocketChannel> scList;

    public RealServer(ServerSocketChannel ssc, ArrayList<Plane> planes, JPanel panel){
        this.ssc = ssc;
        this.planes = planes;
        this.panel = panel;
        scList = new ArrayList<SocketChannel>();
    }

    public void run(){
        try{
            while(true){
                SocketChannel sc = ssc.accept();
                String msg = read(sc);
                System.out.println(msg);
                scList.add(sc);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                Long l = (Long) json.get("id");
                Integer id = Integer.valueOf(l.intValue());
                l = (Long) json.get("x");
                Integer x = Integer.valueOf(l.intValue());
                l = (Long) json.get("y");
                Integer y = Integer.valueOf(l.intValue());
                Plane p = new Plane(x, y, panel, id);
                planes.add(p);
                for(Plane pl: planes){
                    JSONObject jsonn = new JSONObject();
                    jsonn.put("id",pl.id);
                    jsonn.put("code",0);
                    jsonn.put("x",pl.x);
                    jsonn.put("y",pl.y);
                    write(sc, jsonn.toJSONString());
                    Thread.sleep(5);
                }
                PlaneReciver pr = new PlaneReciver(p,sc, scList);
                Thread t = new Thread(pr);
                t.start();
                json.put("code",0);
                for(SocketChannel s: scList){
                    write(s,json.toJSONString());
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


class PlaneReciver extends Thread{

    Plane p;
    SocketChannel sc;
    ArrayList<SocketChannel> scList;

    public PlaneReciver(Plane p, SocketChannel sc, ArrayList<SocketChannel> al){
        this.p = p;
        this.sc = sc;
        scList = al;
    }

    public void run(){
        try {
            while(true){
                String msg = read(sc);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                Long l = (Long) json.get("up");
                Integer up = Integer.valueOf(l.intValue());
                l = (Long) json.get("down");
                Integer down = Integer.valueOf(l.intValue());
                l = (Long) json.get("left");
                Integer left = Integer.valueOf(l.intValue());
                l = (Long) json.get("right");
                Integer right = Integer.valueOf(l.intValue());
                p.setKey(up,down,right, left);
                json.put("id",p.id);
                json.put("code",1);
                for(SocketChannel s: scList){
                    write(s, json.toJSONString());
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

//{"id":123,"x":100,"y":50}

//{"up":0,"down":1,"left":0,"right":0}