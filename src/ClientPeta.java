import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by PierZz on 02.05.14.
 */
public class ClientPeta {

    public static void main(String[]args) throws IOException {
        ClientPeta x=new ClientPeta("10.62.162.84", 4444);
    }
    public SocketChannel sc;
    public int randomNum;
    public ClientPeta(String ip, int porta) throws IOException {
        randomNum = (int)(Math.random()*1000);
        String msg = "BENFAGAYY";
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(ip, porta));
        Receive2 r;
        r = new Receive2(sc);
        Thread a = new Thread(r);
        a.start();
        Send2 q;
        q = new Send2(sc, msg, randomNum);
        Thread b = new Thread(q);
        b.start();
    }

}

class Receive2 extends Thread{
    SocketChannel sc;
    public Receive2(SocketChannel sc) {
        this.sc = sc;
    }
    public void run(){
        while(true) {

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


class Send2 extends Thread{
    private SocketChannel sc;
    private String msg;

    public Send2(SocketChannel sc, String msg, int id) {
        this.sc = sc;
        this.msg = "ID: "+id+"   ->"+msg;

    }
    public void run() {
        long taskTime = 0;
        long sleepTime = 1000/10;
        while (true) {
            taskTime = System.currentTimeMillis();
            try {
                ByteBuffer buff = ByteBuffer.allocate(512);
                buff.clear();
                buff.put(msg.getBytes());
                buff.flip();
                sc.write(buff);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            taskTime = System.currentTimeMillis()-taskTime;
            if (sleepTime-taskTime > 0 ) {
                try {
                    Thread.sleep(sleepTime-taskTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}