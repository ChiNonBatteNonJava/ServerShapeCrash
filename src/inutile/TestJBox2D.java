package inutile;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * Created by ShilleR on 01/04/14.
 */
public class TestJBox2D {
    public static void main(String[] args) {
        Vec2 gravity = new Vec2(0.0f, -10.0f);
        boolean doSleep = true;
        World world = new World(gravity, doSleep);

    }
}
