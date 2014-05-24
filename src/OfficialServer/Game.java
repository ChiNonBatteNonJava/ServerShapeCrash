package OfficialServer;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.lwjgl.Sys;

import java.util.ArrayList;

/**
 * Created by ShilleR on 21/5/14.
 */
public class Game extends Thread{

    private ArrayList<Player> players;
    private Room room;
    private PhysicsWorld world;
    private boolean end;
    public static final int MIN_DELTA = 50;

    public Game(ArrayList<Player> players, Room room){
        end = false;
        this.room = room;
        this.players = players;
        //world = PhysicsWorld.instance(String.valueOf(room.getRoomId()));
        for(Player p: players){
            //world.AddVehicle(p.getCar(), String.valueOf(p.getPlayerId()));
        }
    }

    public PhysicsWorld getWorld(){
        return world;
    }

    public void addPlayer(Player p){
        //world.AddVehicle(p.getCar(), String.valueOf(p.getPlayerId()));
    }

    public void run(){
        GameResourceManager grm = GameResourceManager.getInstance();
        grm.load3DObjModel(room.getMap());
        Game3DModel map = grm.get3DModelByName(room.getMap());
        Log.log("Game Start");
        long firstTime = System.currentTimeMillis();
        long lastTime = System.currentTimeMillis();
        long delta = firstTime - lastTime;
        while(!end){
            lastTime = System.currentTimeMillis();
            delta = lastTime - firstTime;
            firstTime = lastTime;
            room.sendCarPosition();
            lastTime = System.currentTimeMillis();
            if(lastTime-firstTime<MIN_DELTA){
                try {
                    Thread.sleep(MIN_DELTA-(lastTime-firstTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.log("Game end");
    }

    public void close(){
        end = true;
    }
}
