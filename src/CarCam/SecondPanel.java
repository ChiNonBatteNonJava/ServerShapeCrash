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
        sfondoX = -(2880-1024)/2;
        sfondoY = -(1800-768)/2;
        id = -1 + (int) (Math.random() * ((10000 - (-1)) + 1));
        System.out.println("id = " + id);
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
        sc.connect(new InetSocketAddress("192.168.1.49",4444));
        Reciver r = new Reciver(sc, planes, this);
        Thread t = new Thread(r);
        t.start();
        JSONObject request = new JSONObject();
        int a=0;
        a=1;
        if(a==1){
            request.put("request","1");
            request.put("room_id","0");
            request.put("id_player",String.valueOf(id));
        }else{
            request.put("request","2");
            request.put("name","provae");
            request.put("password","");
            request.put("id_player",String.valueOf(id));
            JSONObject settings = new JSONObject();
            settings.put("max_player","5");
            request.put("settings", settings);
        }
        send(sc, request.toJSONString());
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        send(sc, request.toJSONString());
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
        obj.put("id_player",String.valueOf(id));
        obj.put("su",String.valueOf(mPLane.su));
        obj.put("giu",String.valueOf(mPLane.giu));
        obj.put("dx",String.valueOf(mPLane.dx));
        obj.put("sx",String.valueOf(mPLane.sx));
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
        obj.put("id_player",String.valueOf(id));
        obj.put("su",String.valueOf(mPLane.su));
        obj.put("giu",String.valueOf(mPLane.giu));
        obj.put("dx",String.valueOf(mPLane.dx));
        obj.put("sx",String.valueOf(mPLane.sx));
        try{
            send(sc, obj.toJSONString());
        }catch (Exception e){}
    }
}

class Reciver extends Thread{

    SocketChannel socket;
    ArrayList<Plane> planes;
    JPanel jPanel;

    public Reciver(SocketChannel sc, ArrayList<Plane> planes, JPanel jp){
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
                System.out.println("msg = " + msg);
                JSONObject json = (JSONObject) new JSONParser().parse(msg);
                String act = (String) json.get("action");
                if(act!=null){
                    int action = Integer.valueOf(act);
                    if(action == 1){
                        int id = Integer.valueOf((String) json.get("id_player"));
                        planes.add(new Plane(100,100, jPanel, id));
                        System.out.println("adsfasdf");
                    }
                    if(action == 2){
                        int id = Integer.valueOf((String) json.get("id_player"));
                        for(Plane p: planes){
                            System.out.println(p.id);
                            if(p.id == id){
                                p.su = Integer.valueOf((String) json.get("su"));
                                p.giu = Integer.valueOf((String) json.get("giu"));
                                p.dx = Integer.valueOf((String) json.get("dx"));
                                p.sx = Integer.valueOf((String) json.get("sx"));
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
