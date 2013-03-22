package client;

import javax.swing.*;
import java.awt.*;

public class SeaBattleClient {
    public static client.Frame frame;
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                frame = new client.Frame();
                frame.toFront();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}



