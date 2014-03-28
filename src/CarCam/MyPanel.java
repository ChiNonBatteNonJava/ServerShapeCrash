package CarCam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 18/03/14.
 */
public class MyPanel extends JPanel implements KeyListener {

    int sfondoX,sfondoY,planeX, planeY;
    int pvx=0,pvy=0;
    int cvx=0, cvy=0;
    int x = 0, y = 0;
    ImageIcon sfondo, car;

    public MyPanel(){
        super();
        sfondo    = new ImageIcon("sfondo.jpg");
        car       = new ImageIcon("plane.png");
        sfondoX = -(2880-1024)/2;
        sfondoY = -(1800-768)/2;
        planeX = 487;
        planeY = 360;
        addKeyListener(this);
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("10.62.162.205",4444));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerSend ss = new ServerSend(this,sc);
        ServerRecive sr = new ServerRecive(this,sc);
        Thread ts = new Thread(ss);
        Thread tr = new Thread(sr);
        ts.start();
        tr.start();
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

    public void setDir(int x, int y){
        pvx = x*10;
        pvy = y*10;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent k){
        if(k.getKeyCode() == KeyEvent.VK_W){
            y-=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            y+=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            x-=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            x+=1;
        }
    }

    public void keyReleased(KeyEvent k){
        if(k.getKeyCode() == KeyEvent.VK_W){
            y+=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_S){
            y-=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_A){
            x+=1;
        }
        if(k.getKeyCode() == KeyEvent.VK_D){
            x-=1;
        }



    }
}









class ServerSend extends Thread{


    SocketChannel sc;
    MyPanel p;

    public ServerSend(MyPanel mp, SocketChannel s){
        sc = s;
        p = mp;
    }

    @Override
    public void run() {
        while(true){
            try {
               send(p.getX() + "=" + p.getY() + "!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(256);
        bb.put(msg.getBytes());
        bb.flip();
        sc.write(bb);
    }
}

class ServerRecive extends Thread{

    MyPanel p;
    SocketChannel sc;

    public ServerRecive(MyPanel mp, SocketChannel s){
        p = mp;
        sc = s;
    }

    public void run(){
        while(true){
            try {
                String dir = recive();
                String[] h = dir.split("!");
                String[] xy = h[0].split("=");
                p.setDir(Integer.valueOf(xy[0]),Integer.valueOf(xy[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String recive() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(256);
        int dataRead = sc.read(bb);
        bb.flip();
        String msg = "";
        if (dataRead != -1){
            while(bb.hasRemaining()){
                msg += (char) bb.get();
            }
        }
        return msg;
    }
}
