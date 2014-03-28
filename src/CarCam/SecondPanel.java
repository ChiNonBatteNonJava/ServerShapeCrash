package CarCam;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

/**
 * Created by ShilleR on 18/03/14.
 */
public class SecondPanel extends JPanel implements KeyListener {

    int sfondoX,sfondoY,planeX, planeY;
    int pvx=0,pvy=0;
    int cvx=0, cvy=0;
    int x = 0, y = 0;
    ImageIcon sfondo, car;

    public SecondPanel(){
        super();
        sfondo    = new ImageIcon("sfondo.jpg");
        car       = new ImageIcon("plane.png");
        sfondoX = -(2880-1024)/2;
        sfondoY = -(1800-768)/2;
        planeX = 487;
        planeY = 360;
        addKeyListener(this);
        SocketChannel sc = null;
        try{
            connection(sc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void paintComponent (Graphics g) {

        cvx = -(int)(Math.tan((planeX-487)/487.0*Math.PI/4)*20);
        cvy = -(int)(Math.tan((planeY-360)/360.0*Math.PI/4)*20);

        planeX += pvx;
        planeY += pvy;
        planeX += cvx;
        planeY += cvy;
        sfondoY += cvy;
        sfondoX += cvx;

        g.drawImage(sfondo.getImage(),sfondoX, sfondoY,this);
        g.drawImage(car.getImage(), planeX, planeY, 50, 50, this);

    }


    public void connection(SocketChannel sc) throws IOException {
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",4444));
        Reciver r = new Reciver(sc);
        Thread t = new Thread(r);
        t.start();
        JSONObject request = new JSONObject();
        //request.put("request","1");
        //request.put("room_id","0");
        //request.put("request","2");
        //request.put("name","provae");
        //request.put("password","");
        //JSONObject settings = new JSONObject();
        //settings.put("max_player","2");
        //request.put("settings", settings);
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
            pvy-=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            pvy+=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            pvx-=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            pvx+=10;
        }
    }

    public void keyReleased(KeyEvent k){
        if(k.getKeyCode() == KeyEvent.VK_W){
            pvy+=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            pvy-=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            pvx+=10;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            pvx-=10;
        }



    }
}

class Reciver extends Thread{

    SocketChannel socket;

    public Reciver(SocketChannel sc){
        socket = sc;
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

            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }

}
