import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by ShilleR on 26/03/14.
 */
public class SocketManager {

    private String recive(SelectionKey key) throws IOException, EOFException {
        String msg = "";
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        SocketChannel sc = (SocketChannel) key.channel();
        int byteRead = sc.read(byteBuffer);
        if(byteRead != -1){
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                msg += (char) byteBuffer.get();
            }
            Log.log(sc.socket().getInetAddress().toString()+" > "+msg);
        }else{
            key.cancel();
            sc.close();
            String er = sc.socket().getInetAddress().toString()+" - Connection closed";
            throw new EOFException(er);
        }
        return msg;
    }

    private void send(SelectionKey key , String msg) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
        Log.log(sc.socket().getInetAddress().toString()+" < "+msg);
    }

}
