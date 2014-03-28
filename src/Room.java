import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Matteo Piergiovanni
 * @version Alfa
 */

public class Room extends Thread {

    public static int STATUS_WAITING = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_INGAME = 2;


    private Selector selector;
    private ArrayList<Player> players;
    private Settings settings;
    private Player gameowner;
    private int status;
    private int id;
    private String name;
    private String password;
    private boolean finish;
    private static int static_id = 0;

    public Room(Player gameowner, Settings settings, String name, String password){
        id = static_id++;
        this.gameowner = gameowner;
        this.players  = new ArrayList<Player>();
        this.players.add(this.gameowner);
        this.settings = settings;
        this.name = name;
        this.password = password;
        this.status = STATUS_WAITING;
        try{
            selector = Selector.open();
            Log.log("Room "+id+" created");
        }catch (Exception e){
            Log.log("Room "+id+" - "+e.getMessage());
        }
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
                        JSONObject action = (JSONObject) new JSONParser().parse(msg);
                        action.put("time",String.valueOf(System.currentTimeMillis()));
                        action.put("action","2");
                        broadcase(action);
                    }
                }
            }catch (Exception e){
                Log.log(e.getMessage());
            }
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

    private void broadcase(JSONObject json) throws IOException {
        for(Player p: players){
            send(p.getSocket(), json);
        }
    }

    private void send(SocketChannel sc, JSONObject json) throws IOException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.clear();
        byteBuffer.put(json.toJSONString().getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
        Log.log(sc.socket().getInetAddress().toString()+" < "+json.toJSONString());
    }

    private void send(SelectionKey key , JSONObject json) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        send(sc, json);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) throws IOException {
        if (settings.getMaxplayer() > players.size()) {
            this.players.add(player);
            selector.wakeup();
            player.getSocket().register(selector,SelectionKey.OP_READ);
            JSONObject json = new JSONObject();
            json.put("action","1");
            json.put("id",String.valueOf(Math.random()));
            json.put("time",String.valueOf(System.currentTimeMillis()));
            broadcase(json);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player){
        this.players.remove(player);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Player getGameowner() {
        return gameowner;
    }

    public void setGameowner(Player gameowner) {
        this.gameowner = gameowner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRoomId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return name;
    }

    public void setRoomName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString(){
        return this.getRoomId()+"\t"+this.getRoomName()+"\t"+this.getSettings().getMaxplayer();
    }

    public JSONObject toJson(){
        JSONObject json  = new JSONObject();
        json.put("id",String.valueOf(this.getRoomId()));
        json.put("name",this.getRoomName());
        json.put("max_player",this.getSettings().getMaxplayer());
        return json;
    }
}
