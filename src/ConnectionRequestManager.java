/**
 * Created by ShilleR on 25/03/14.
 */

import org.json.simple.JSONArray;
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


    final static int ERROR          = -1;
    final static int LIST_ROOM      = 0;
    final static int JOIN_ROOM      = 1;
    final static int CREATE_ROOM    = 2;
    final static int MODIFIED_ROOM  = 3;
    final static int LEFETED_ROOM   = 4;
    final static int PLAY_START     = 5;
    final static int PLAYER_ACTION  = 6;
    final static int PLAY_END       = 7;
    final static int EXIT           = 100;
    final static int PLAYER_JOIN    = 101;
    final static int PLAYER_LEFT    = 102;
    final static int TIME_OUT       = 30000; // 30 sec;

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
                        JSONParser parser = new JSONParser();
                        JSONObject json = null;
                        try{
                            json = (JSONObject) parser.parse(msg);
                        }catch (Exception e){}
                        Long longCode = (Long) json.get("code");
                        Integer requestCode = longCode == null ? null : Integer.valueOf(longCode.intValue());
                        switch (requestCode){
                            case LIST_ROOM:
                                listRoom(key);
                                break;
                            case JOIN_ROOM:
                                joinRoom(key, json);
                                break;
                            case CREATE_ROOM:
                                createRoom(key, json);
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
        JSONArray jsonArray = new JSONArray();
        for(Room r: rooms){
            jsonArray.add(r.toJson());
        }
        JSONObject json = new JSONObject();
        json.put("code",LIST_ROOM);
        json.put("rooms",jsonArray);
        send(key,json);
    }

    private void joinRoom(SelectionKey key, JSONObject obj) throws Exception {
        Long longPlayerId = (Long) obj.get("player_id");
        Integer playerId = longPlayerId == null ? null : Integer.valueOf(longPlayerId.intValue());
        Long longRoomId = (Long) obj.get("room_id");
        Integer roomId = longRoomId == null ? null : Integer.valueOf(longRoomId.intValue());
        Room room = null;
        Log.log(playerId+"--"+roomId+">>>");
        for(Room r: rooms){
            if(roomId != null && r.getRoomId() == roomId){
                room = r;
                break;
            }
        }
        if(playerId != null && roomId != null){
            if(room == null){
                JSONObject error = new JSONObject();
                error.put("code",ERROR);
                error.put("message","Room not found");
                send(key,error);
            }else{
                Player player = new Player((SocketChannel) key.channel(), playerId);
                if(!room.addPlayer(player)){
                    JSONObject error = new JSONObject();
                    error.put("code", ERROR);
                    error.put("message","the room is full");
                }else{
                    key.cancel();
                }
            }
        }else{
            JSONObject error = new JSONObject();
            error.put("code",ERROR);
            error.put("message","invalid join room request");
            send(key, error);
        }
    }

    private void createRoom(SelectionKey key, JSONObject obj) throws Exception {
        Long longPlayerId = (Long) obj.get("player_id");
        Integer playerId = longPlayerId == null ? null : Integer.valueOf(longPlayerId.intValue());
        String roomName = (String) obj.get("room_name");
        String roomPassword = (String) obj.get("room_password");
        Long longPasswordRequest = (Long) obj.get("password_request");
        Integer passwordRequest = longPasswordRequest == null ? null : Integer.valueOf(longPasswordRequest.intValue());
        JSONObject jsonSetting = (JSONObject) obj.get("settings");
        Long longMaxPlayer = (Long) jsonSetting.get("max_player");
        Integer maxPlayer = longMaxPlayer == null ? null : Integer.valueOf(longMaxPlayer.intValue());
        Settings settings = maxPlayer == null ? null : new Settings(maxPlayer);
        if(playerId != null && roomName != null && roomPassword != null && passwordRequest != null && settings != null){
            Player gameOwner = new Player((SocketChannel) key.channel(), playerId);
            Room room = new Room(gameOwner,settings, roomName, roomPassword,passwordRequest==1);
            rooms.add(room);
            key.cancel();
        }else{
            JSONObject error = new JSONObject();
            error.put("code", ERROR);
            error.put("message", "invalid create room request");
            send(key, error);
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


