import java.util.ArrayList;

/**
 * @author Matteo Piergiovanni
 * @version Alfa
 */

public class Room {

    public static int STATUS_WAITING = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_INGAME = 2;


    private ArrayList<Player> players;
    private Settings settings;
    private Player gameowner;
    private int status;
    private int id;
    private String name;
    private String password;

    public Room(Player gameowner, Settings settings, String name, String password, int id){
        this.gameowner = gameowner;
        this.players.add(this.gameowner);
        this.settings = settings;
        this.name = name;
        this.password = password;
        this.id = id;

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player){
        if (settings.getMaxplayer() < players.size() ) {
            this.players.add(player);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
