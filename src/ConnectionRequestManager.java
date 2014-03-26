/**
 * Created by ShilleR on 25/03/14.
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ConnectionRequestManager extends Thread{


    final static int LIST_ROOM   = 0;
    final static int JOIN_ROOM   = 1;
    final static int CREATE_ROOM = 2;
    final static int EXIT        = 10000;
    final static int TIME_OUT    = 30000; // 30 sec;

    private ArrayList<Room> rooms;
    
    private Selector selector;
    private boolean finish;

    public ConnectionRequestManager() throws IOException {
        selector = Selector.open();
        rooms = new ArrayList<Room>();
        finish = false;
    }

    public void run(){

        while(!finish) {
            try{
                int readyChannels = selector.select();
                if(readyChannels == 0){ Thread.sleep(1); continue;}
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if(key.isValid()){
                        if (key.isReadable()) {
                            String msg = recive(key);
                            int requestType = -1;
                            try{
                                JSONParser parser = new JSONParser();
                                System.out.println(parser.parse(msg));
                                JSONObject obj = (JSONObject) parser.parse(msg);
                                System.out.println((String)obj.get("request"));
                                requestType = Integer.valueOf((String)obj.get("request"));
                            }catch(Exception e){

                            }
                            String resp = "";
                            switch (requestType){
                                case LIST_ROOM:
                                    resp = "You have request the list of room.";
                                    break;
                                case JOIN_ROOM:
                                    resp = "You want join in a room but it's not yet implements.";
                                    break;
                                case CREATE_ROOM:
                                    resp = "You want join in a room but it's not yet implements.";
                                    break;
                                case EXIT:
                                    resp = "The connection will be closed.";
                                default:
                                    resp ="Your request is invalide.";
                            }
                            send(key, resp);
                        }
                    }
                }
            }catch (Exception e){
                Log.log(e.getMessage());
            }
        }
    }

    private String listRoom(){
        return null;
    }

    private void joinRoom(){

    }

    private void createRoom(){

    }

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

    public void exit(){
        finish = true;
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
        selector.wakeup();
    }

    public void addSocketChannel(SocketChannel sc) throws IOException {
        selector.wakeup();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

}


