import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ShilleR on 21/03/14.
 */
public class RoomManager extends Thread {

    private Selector selector;
    private ArrayList<SelectionKey> selectionKeys;

    public RoomManager(){
        selectionKeys = Storage.getInstance().getKeys();
        selector = Storage.getInstance().getSelector();
    }


    public void run(){
        int i=0;
        while(true) {
                try{
                    int readyChannels = selector.select();

                    if(readyChannels == 0){Thread.sleep(1); continue;}

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while(keyIterator.hasNext()){

                        SelectionKey key = keyIterator.next();

                        if(key.isAcceptable()) {
                            // a connection was accepted by a ServerSocketChannel.

                        } else if (key.isConnectable()) {
                            // a connection was established with a remote server.

                        } else if (key.isReadable()) {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                            SocketChannel sc = (SocketChannel)key.channel();
                            sc.read(byteBuffer);
                            byteBuffer.flip();
                            String msg ="";
                            while(byteBuffer.hasRemaining()){
                                msg += (char) byteBuffer.get();
                            }
                            System.out.println("msg = " + msg);
                            byteBuffer.flip();
                            byteBuffer.clear();
                            byteBuffer.put(("Read"+msg).getBytes());
                            byteBuffer.flip();
                            sc.write(byteBuffer);
                        } else if (key.isWritable()) {
                            // a channel is ready for writing
                        }

                        keyIterator.remove();
                    }
                }catch (Exception e ){
                    System.out.println(e.getMessage());
                    break;
                }
        }
        System.out.println("end");
    }

}
