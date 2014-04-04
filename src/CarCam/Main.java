package CarCam;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ShilleR on 18/03/14.
 */
public class Main {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("FrameDemo");
        frame.setBounds(0,0,1024,768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final SecondPanel p = new SecondPanel();
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
