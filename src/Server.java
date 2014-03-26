import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 21/03/14.
 */
public class Server {
    public static void main(String[] args) {
        try{
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(4444));

            RoomManager rm = new RoomManager();
            Thread t = new Thread(rm);
            t.start();

            while(true){
                SocketChannel socketChannel = serverSocketChannel.accept();
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                byteBuffer.clear();
                int byteRead = socketChannel.read(byteBuffer);
                if (byteRead != -1){
                    byteBuffer.flip();
                    String msg ="";
                    while(byteBuffer.hasRemaining()){
                        msg += (char) byteBuffer.get();
                    }
                    if(msg.equals("save")){
                        System.out.println("saveaaa");
                        Storage.getInstance().addSocketChannel(socketChannel);
                        System.out.println("Socket aggiunto alla lista");
                        byteBuffer.flip();
                        byteBuffer.clear();
                        byteBuffer.put(("Readed: "+msg).getBytes());
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                    }else{
                        System.out.println(msg);
                        byteBuffer.flip();
                        byteBuffer.clear();
                        byteBuffer.put(("Readed: "+msg).getBytes());
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                        socketChannel.close();
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }



}
