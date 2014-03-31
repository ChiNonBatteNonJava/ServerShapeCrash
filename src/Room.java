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
    private Boolean passwordRequest;
    private boolean finish;
    private static int static_id = 0;

    public Room(Player gameowner, Settings settings, String name, String password, Boolean passwordRequest){
        id = static_id++;
        this.gameowner = gameowner;
        this.players  = new ArrayList<Player>();
        this.settings = settings;
        this.name = name;
        this.password = password;
        this.passwordRequest = passwordRequest;
        this.status = STATUS_WAITING;
        try{
            selector = Selector.open();
            JSONObject json = new JSONObject();
            json.put("code", 2);
            json.put("room_id",id);
            gameowner.send(json);
            addPlayer(gameowner);
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
                    Log.log("richiesta arrivata");
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isReadable()) {
                        String msg = ((Player)key.attachment()).recive();
                        JSONParser parser = new JSONParser();
                        JSONObject json = null;
                        try{
                            json = (JSONObject) parser.parse(msg);
                        }catch (Exception e){
                            json = new JSONObject();
                        }
                        Long longCode = (Long) json.get("code");
                        Integer requestCode = longCode == null ? -2 : Integer.valueOf(longCode.intValue());
                        switch (requestCode){
                            case ConnectionRequestManager.PLAY_START:
                                if(((Player)key.attachment())==gameowner){
                                    gameStart();
                                }
                                break;
                            case ConnectionRequestManager.PLAYER_ACTION:
                                //to be defined
                                broadcast(json);
                                break;
                            case ConnectionRequestManager.LEFETED_ROOM:
                                //removePlayer();
                                break;
                            case ConnectionRequestManager.PLAY_END:
                                gameEnd();
                                break;
                            case ConnectionRequestManager.MODIFIED_ROOM:
                                if(((Player)key.attachment())==gameowner){
                                    //modify game
                                }
                                break;
                            case ConnectionRequestManager.EXIT:
                                //exit
                                break;
                            default:
                                json.put("code", ConnectionRequestManager.ERROR);
                                json.put("message","Invalid request");
                                ((Player) key.attachment()).send(json);
                        }
                    }
                }
            }catch (Exception e){
                Log.log(e.getMessage());
            }
        }
    }

    private void gameStart(){
    }

    private void gameEnd(){
    }

    private void broadcast(JSONObject json) throws IOException {
        for(Player p: players){
            p.send(json);
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) throws IOException {
        if(players.size() >= settings.getMaxplayer()){
            return false;
        }

        player.getSocket().configureBlocking(false);
        selector.wakeup();
        player.getSocket().register(selector, SelectionKey.OP_READ, player);
        JSONArray arrayPlayers = new JSONArray();
        for(Player p: players){
            arrayPlayers.add(p.toJson());
        }
        JSONObject listPlayer = new JSONObject();
        listPlayer.put("code",ConnectionRequestManager.JOIN_ROOM);
        listPlayer.put("players",arrayPlayers);
        player.send(listPlayer);
        players.add(player);
        JSONObject json = new JSONObject();
        json.put("code",101);
        json.put("player_id",player.getId());
        broadcast(json);

        return true;

    }

    public void removePlayer(Player player){
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
