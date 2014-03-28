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
                    if (key.isReadable()) {
                        String msg = recive(key);
                        int requestType = -1;
                        JSONObject obj = null;
                        try{
                            JSONParser parser = new JSONParser();
                            obj = (JSONObject) parser.parse(msg);
                            requestType = Integer.valueOf((String)obj.get("request"));
                        }catch(Exception e){}
                        JSONObject json = new JSONObject();
                        switch (requestType){
                            case LIST_ROOM:
                                listRoom(key);
                                break;
                            case JOIN_ROOM:
                                joinRoom(key, obj);
                                break;
                            case CREATE_ROOM:
                                createRoom(key, obj);
                                break;
                            case EXIT:
                                json.put("response","1");
                                json.put("message","connection closed");
                                send(key, json);
                                key.channel().close();
                                break;
                            default:
                                json.put("response","0");
                                json.put("message","Invalid request");
                                send(key, json);
                        }

                    }
                }

            }catch (Exception e){
                Log.log(e.getMessage());
            }
        }
    }

    private void listRoom(SelectionKey key) throws IOException {
        JSONObject json = new JSONObject();
        for(Room r: rooms){
            json.put(String.valueOf(r.getRoomId()),r.toJson());
        }
        send(key,json);
    }

    private void joinRoom(SelectionKey key, JSONObject obj) throws Exception {
        String idStr = (String) obj.get("room_id");
        JSONObject json = new JSONObject();
        if (idStr != null){
            int id = Integer.valueOf(idStr);
            boolean joined = false;
            boolean found = false;
            for(Room r: rooms){
                if(r.getRoomId()==id){
                    found = true;
                    Player p = new Player((SocketChannel) key.channel());
                    joined = r.addPlayer(p);
                }
            }
            if(found){
                if(joined){
                    key.cancel();
                }else{
                    json.put("response","0");
                    json.put("message","Room not found");
                    send(key,json);
                }
            }else{
                json.put("response", "0");
                json.put("message","Room not found");
                send(key, json);
            }
        }else{
            json.put("response","0");
            json.put("message","Invalid requset");
            send(key,json);
        }

    }

    private void createRoom(SelectionKey key, JSONObject obj) throws Exception {
        Player gameowner = new Player((SocketChannel)key.channel());
        JSONObject set = (JSONObject) obj.get("settings");
        String name = (String) obj.get("name");
        String password = (String) obj.get("password");
        if(set != null && name != null && password != null){
            String maxPlayer = (String)set.get("max_player");
            int iMaxPlayer = Integer.parseInt(maxPlayer);
            Settings settings = new Settings(iMaxPlayer);
            Room r = new Room(gameowner,settings,name,password);
            rooms.add(r);
            Thread t = new Thread(r);
            t.start();
            JSONObject json = new JSONObject();
            json.put("response", "1");
            json.put("message","room created");
            send(key, json);
            key.cancel();
        }else{
            JSONObject json = new JSONObject();
            json.put("response","0");
            json.put("message","Room information invalid");
            send(key, json);
        }

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

    private void send(SelectionKey key , JSONObject json) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(json.toJSONString().getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
        Log.log(sc.socket().getInetAddress().toString()+" < "+ json.toJSONString());
    }

    public void exit(){
        finish = true;
        selector.wakeup();
    }

    public void addSocketChannel(SocketChannel sc) throws IOException {
        selector.wakeup();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

}


