/**
 * @author Matteo Piergiovanni
 * @version Alfa
 */

public class Point2f {
    private float x, y;
    private Point2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
