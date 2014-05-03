package AndroidServerTest;

/**
 * Created by ShilleR on 3/5/14.
 */
public class CarAction {
    private long time;
    private int dir;
    private int id;

    public CarAction(long time, int dir, int id) {
        this.time = time;
        this.dir = dir;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
