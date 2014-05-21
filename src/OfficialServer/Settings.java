package OfficialServer;

/**
 * Created by ShilleR on 25/03/14.
 */

public class Settings {
    private int maxplayer;

    public Settings(int maxplayer){
        this.maxplayer = maxplayer;
    }

    public int getMaxplayer() {
        return maxplayer;
    }

    public void setMaxplayer(int maxplayer) {
        this.maxplayer = maxplayer;
    }
}
