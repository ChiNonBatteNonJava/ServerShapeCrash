package CarCam;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.net.www.content.text.plain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ShilleR on 18/03/14.
 */
public class SecondPanel extends JPanel implements KeyListener {

    int sfondoX,sfondoY;
    int cvx=0, cvy=0;
    ImageIcon sfondo;
    Plane mPLane;
    ArrayList<Plane> planes;
    int id;
    SocketChannel sc;

    public SecondPanel(){
        super();
        sfondo    = new ImageIcon("sfondo.jpg");
        sfondoX = 0;
        sfondoY = 0;
        id = -1 + (int) (Math.random() * ((10000 - (-1)) + 1));
        mPLane = new Plane(360,487,this,id);
        planes = new ArrayList<Plane>();
        planes.add(mPLane);
        addKeyListener(this);

        SocketChannel sc = null;
        try{
            connection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void paintComponent (Graphics g) {

        g.drawImage(sfondo.getImage(),sfondoX, sfondoY,this);
        for(Plane p: planes){
            p.paint(g);
        }

    }

    public void connection() throws IOException {
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",4444));
        Reciver r = new Reciver(sc, planes, this);
        Thread t = new Thread(r);
        t.start();
        JSONObject request = new JSONObject();
        request.put("id",id);
        request.put("x",mPLane.x);
        request.put("y", mPLane.y);
        send(sc, request.toJSONString());
        try{
            Thread.sleep(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String recive(SocketChannel sc) throws IOException, EOFException {
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

    private void send(SocketChannel sc , String msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent k){
        if(k.getKeyCode() == KeyEvent.VK_W){
            mPLane.su = 1;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            mPLane.giu = 1;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            mPLane.sx = 1;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            mPLane.dx = 1;
        }
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put("up",mPLane.su);
        obj.put("down",mPLane.giu);
        obj.put("left", mPLane.sx);
        obj.put("right",mPLane.dx);
        try{
        send(sc, obj.toJSONString());
        }catch (Exception e){

        }
    }

    public void keyReleased(KeyEvent k){
        if(k.getKeyCode() == KeyEvent.VK_W){
            mPLane.su = 0;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            mPLane.giu = 0;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            mPLane.sx = 0;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            mPLane.dx = 0;
        }
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put("up",mPLane.su);
        obj.put("down",mPLane.giu);
        obj.put("left", mPLane.sx);
        obj.put("right",mPLane.dx);
        try{
            send(sc, obj.toJSONString());
        }catch (Exception e){}
    }
}

class Reciver extends Thread{

    SocketChannel socket;
    ArrayList<Plane> planes;
    SecondPanel jPanel;

    public Reciver(SocketChannel sc, ArrayList<Plane> planes, SecondPanel jp){
        socket = sc;
        this.planes = planes;
        jPanel = jp;
    }

    public void run(){
        while(true){
            try{
                ByteBuffer bb = ByteBuffer.allocate(512);
                int nByte = socket.read(bb);
                String msg = "";
                if(nByte!= -1){
                    bb.flip();
                    while(bb.hasRemaining()){
                        msg += (char) bb.get();
                    }
                }
                System.out.println(msg);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                Long l = (Long) json.get("code");
                Integer action = Integer.valueOf(l.intValue());
                l = (Long) json.get("id");
                int id = Integer.valueOf(l.intValue());
                if(id!=jPanel.mPLane.id){
                    if(action == 0){
                        l = (Long) json.get("x");
                        int x = Integer.valueOf(l.intValue());
                        l = (Long) json.get("y");
                        int y = Integer.valueOf(l.intValue());
                        Plane pl = new Plane(x,y,jPanel, id);
                        planes.add(pl);
                    }
                    if(action == 1){
                        for(Plane p: planes){
                            if(p.id == id){
                                l = (Long) json.get("up");
                                p.su = Integer.valueOf(l.intValue());
                                l = (Long) json.get("down");
                                p.giu = Integer.valueOf(l.intValue());
                                l = (Long) json.get("left");
                                p.sx = Integer.valueOf(l.intValue());
                                l = (Long) json.get("right");
                                p.dx = Integer.valueOf(l.intValue());
                            }
                        }
                    }
                }

            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }

}
