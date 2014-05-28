package OfficialServer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ShilleR on 25/03/14.
 */

public class Room extends Thread {

    public static int STATUS_WAITING = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_INGAME = 2;
    public static int STATUS_END = 3;

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
    private int player_id = 0;
    private ConnectionRequestManager crm;
    private SendPosition sendPosition;
    private String map = "pista2.obj";

    public Room(SocketChannel owner, Settings settings, String name, String password, Boolean passwordRequest, ConnectionRequestManager crm){
        id = static_id++;
        this.crm = crm;
        this.players  = new ArrayList<Player>();
        this.settings = settings;
        this.name = name;
        this.password = password;
        this.passwordRequest = passwordRequest;
        this.status = STATUS_WAITING;
        this.sendPosition = new SendPosition(players);
        //this.sendPosition.start();
        this.gameowner = new Player(owner, getNewPlayerId(), "" + id);
        try{
            selector = Selector.open();
            JSONObject json = new JSONObject();
            json.put("code", 2);
            json.put("room_id",id);
            json.put("player_id", gameowner.getPlayerId());
            gameowner.send(json);
            addOwner(gameowner);
            Log.log("OfficialServer.Room " + id + " created");
        }catch (Exception e){
            Log.log("OfficialServer.Room " + id + " - " + e.getMessage());
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
                    Log.log("richiesta arrivata ");
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isReadable()) {
                        String msg = ((Player)key.attachment()).recive();
                        Log.log(msg);
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

                                Player p = (Player)key.attachment();
                                p.setLastPosition(json);
                                break;
                            case ConnectionRequestManager.PLAYER_WIN:
                                if(status == STATUS_INGAME) {

                                    Player pl = (Player) key.attachment();
                                    json.put("player_id", pl.getPlayerId());
                                    broadcast(json);
                                    status = STATUS_END;
                                }
                            case ConnectionRequestManager.PLAYER_LEFT:
                                System.out.println("uscito");
                                crm.addSocketChannel((SocketChannel) key.channel());
                                removePlayer((Player)key.attachment());
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
                                removePlayer((Player) key.attachment());
                                key.cancel();
                                key.channel().close();
                                break;
                            case 998:

                                broadcast(json);
                                break;
                            case 999:

                                broadcast(json);
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
        Log.log("room " + id + " closed");
    }

    private void gameStart(){
        JSONObject json = new JSONObject();
        json.put("code", ConnectionRequestManager.PLAY_START);
        JSONArray arr = new JSONArray();
        int count = 0;
        for(Player p: players){
            JSONObject pl = new JSONObject();
            pl.put("id",p.getPlayerId());
            pl.put("x",(30*(count%3))-30);
            pl.put("z",(30*((int)(count/3))));
            arr.add(pl);
        }
        json.put("players",arr);
        sendPosition.start();
        status = STATUS_INGAME;
        try {
            broadcast(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameEnd(){
        sendPosition.end();
    }

    public void broadcast(JSONObject json) throws IOException {
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
        player.setKey(player.getSocket().register(selector, SelectionKey.OP_READ, player));
        JSONArray arrayPlayers = new JSONArray();
        JSONObject playerJoin = new JSONObject();
        playerJoin.put("code", ConnectionRequestManager.PLAYER_JOIN);
        playerJoin.put("player_id",player.getPlayerId());
        for(Player p: players){
            arrayPlayers.add(p.toJson());
            p.send(playerJoin);
        }

        JSONObject listPlayer = new JSONObject();
        listPlayer.put("code", ConnectionRequestManager.JOIN_ROOM);
        listPlayer.put("players",arrayPlayers);
        listPlayer.put("player_id", player.getPlayerId());
        player.send(listPlayer);
        players.add(player);

        return true;

    }

    public void addOwner(Player player) throws IOException {
        player.getSocket().configureBlocking(false);
        selector.wakeup();
        player.setKey(player.getSocket().register(selector, SelectionKey.OP_READ, player));
        players.add(player);
    }

    public void removePlayer(Player player) throws IOException {
        player.getSelectionKey().cancel();
        players.remove(player);
        if(player == gameowner){
            if(players.size()>0) {
                gameowner = players.get(0);
                JSONObject json = new JSONObject();
                json.put("code", ConnectionRequestManager.OWNER);
                gameowner.send(json);
            }
        }
//        PhysicsWorld.instance(""+id).delete(""+player.getPlayerId());
        for(Player p: players){
            JSONObject json = new JSONObject();
            json.put("code",ConnectionRequestManager.PLAYER_LEFT);
            json.put("player_id",player.getPlayerId());
            p.send(json);
        }
        if(players.size()==0){
            crm.getRooms().remove(this);
            finish = true;
            gameEnd();
            selector.wakeup();
        }
    }
/*
    public void playerAction(Player player, JSONObject json){
        Long longDir = (Long) json.get("dir");
        Long longSteering = (Long) json.get("steering");
        Integer dir = longDir == null ? null : longDir.intValue();
        Integer steering = longDir == null ? null : longSteering.intValue();
        if(dir != null && steering != null && (dir == Player.CAR_BACKWARD || dir == Player.CAR_FORWARD) && (steering == Player.CAR_STEERING_LEFT || steering == Player.CAR_STEERING_NULL || steering == Player.CAR_STEERING_RIGHT)){
            switch (dir){
                case Player.CAR_BACKWARD:
                    break;
                case Player.CAR_FORWARD:
                    break;
            }
            switch (steering){
                case Player.CAR_STEERING_LEFT:
                    player.getCar().LeftSteering();
                    break;
                case Player.CAR_STEERING_RIGHT:
                    player.getCar().RightSteering();
                    break;
                case Player.CAR_STEERING_NULL:
                    player.getCar().ResetSteering();
                    break;
            }

        }
    }
*/
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
        json.put("id",this.getRoomId());
        json.put("name",this.getRoomName());
        json.put("max_player",this.getSettings().getMaxplayer());
        return json;
    }

    public String getMap(){
        return map;
    }

    public int getNewPlayerId(){
        return player_id++;
    }

    public void sendCarPosition() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        json.put("code",6);
        for(Player p: players){
            JSONObject carJson = new JSONObject();
            carJson.put("As","ewr");
            jsonArray.add(carJson);
        }
        json.put("players",jsonArray);
        for(Player p: players){
            try {
                p.send(json);
            }catch (Exception e){
                Log.log(e.getMessage());
            }
        }

    }

}

class SendPosition extends Thread{

    private ArrayList<Player> players;
    private boolean end = false;
    private int MIN_DELTA = 50;

    public SendPosition(ArrayList<Player> players){
        this.players = players;
    }

    public void run(){
        long firstTime = System.currentTimeMillis();
        long lastTime = System.currentTimeMillis();
        long delta = firstTime - lastTime;

        while (!end) {
            try {
                lastTime = System.currentTimeMillis();
                delta = lastTime - firstTime;
                firstTime = lastTime;
                JSONObject json = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                for (Player p : players) {
                    if (p.getLastPosition() != null) {
                        jsonArray.add(p.getLastPosition());
                    }
                }

                json.put("code", 6);
                json.put("players", jsonArray);
                for (Player p : players) {
                    try {
                        p.send(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (lastTime - firstTime < MIN_DELTA) {
                    try {
                        Thread.sleep(MIN_DELTA - (lastTime - firstTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void end(){
        end = true;
    }
}
