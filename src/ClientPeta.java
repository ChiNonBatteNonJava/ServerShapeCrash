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
    public ClientPeta(String ip, int porta) throws IOException {

        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(ip, porta));
        Receive2 r;
        r = new Receive2(sc);
        Thread a = new Thread(r);
        a.start();
        sender("BENFAGAYYYYYY");
    }

    public void sender(String msg) {
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