package CarCam;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ShilleR on 06/04/14.
 */
public class CarCamServer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("server");
        frame.setBounds(0,0,1024,768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ServerPanel p = new ServerPanel();
        p.setBounds(0,0,1024,768);
        frame.add(p);
        frame.setVisible(true);
        final Timer timer = new Timer(20, null);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.repaint();
            }
        };
        timer.addActionListener(al);
        timer.start();
    }
}
