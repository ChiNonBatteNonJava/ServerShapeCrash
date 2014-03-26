/**
 * @author Matteo Piergiovanni
 * @version Alfa
 */

public class Settings {
    public int getMaxplayer() {
        return maxplayer;
    }

    public void setMaxplayer(int maxplayer) {
        this.maxplayer = maxplayer;
    }

    //Fields
    private int maxplayer;

    public Settings(int maxplayer){
        this.maxplayer = maxplayer;

    }
}
