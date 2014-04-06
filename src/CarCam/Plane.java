package CarCam;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ShilleR on 29/03/14.
 */
public class Plane {
    public int id;
    public int x,y;
    public int su,giu,dx,sx;
    public static int VELOCITA = 10;
    public ImageIcon plane;
    public JPanel parent;



    public Plane(int x, int y, JPanel parent, int id){
        this.parent = parent;
        plane = new ImageIcon("plane.png");
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void paint(Graphics g){
        if(su==1){
            y -= VELOCITA;
        }
        if(giu==1){
            y += VELOCITA;
        }
        if(dx==1){
            x += VELOCITA;
        }
        if(sx==1){
            x -= VELOCITA;
        }

        g.drawImage(plane.getImage(), x, y, 50,50, parent);

    }

    public void setKey(int su, int giu, int dx, int sx){
        this.su = su;
        this.giu = giu;
        this.dx = dx;
        this.sx = sx;
    }



}
