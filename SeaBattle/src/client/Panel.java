package client;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class Panel extends JPanel
{
    public static void paintSquare(Graphics g, int x, int y, int w, int h)
    {
        Graphics2D g2 = (Graphics2D) g;

        Rectangle2D field = new Rectangle2D.Double(x, y, w, h);
        g2.draw(field);
    }

    public static void paintSquare2(Graphics g, int x, int y, int w, int h, Color c)
    {
        Graphics2D g2 = (Graphics2D) g;

        Rectangle2D field = new Rectangle2D.Double(x, y, w, h);
        g2.setColor(c);
        g2.fill(field);
    }
}
