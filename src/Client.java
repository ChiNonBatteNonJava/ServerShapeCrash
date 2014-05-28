import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Created by matteo piergiovanni on 24.03.14.
 */

public class Client {
    public static void main(String[]args) throws IOException {
        Client x=new Client("127.0.0.1", 4444);
    }
    public SocketChannel sc;

    public Client(String ip, int porta) throws IOException {

        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(ip, porta));
        Receive r;
        r = new Receive(sc);
        Thread a = new Thread(r);
        a.start();
        Send q;
        q = new Send(sc);
        Thread b = new Thread(q);
        b.start();
    }

}

class Receive extends Thread{
    SocketChannel sc;
    public Receive(SocketChannel sc) {
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
                    //System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}


class Send extends Thread{
    SocketChannel sc;
    public Send(SocketChannel sc) {
        this.sc = sc;
    }
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String msg;
            msg = scanner.nextLine();
            try {
                ByteBuffer buff = ByteBuffer.allocate(512);
                buff.clear();
                buff.put(msg.getBytes());
                buff.flip();
                sc.write(buff);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
    }

}