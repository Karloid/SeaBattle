package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Frame extends JFrame
{
    private static final int DEFAULT_WIDTH = 600, DEFAULT_HEIGHT = 300;
    private static final String Name = "Sea Battle Client";
    private static JPanel panel;
    private static boolean createWarship;
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket sock;
    private int id;
    public static boolean fireCheck = true;

    public Frame()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        setLocation((screenWidth / 2)-DEFAULT_WIDTH/2, (screenHeight / 2)-DEFAULT_HEIGHT/2);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle(Name);
        setResizable(false);

        panel = new JPanel();
        panel.setFocusable(true);
        panel.setLayout(null);
        add(panel);


        JButton NewGame = new JButton("New game!");
        NewGame.setBounds(450, 10, 130, 30);
        panel.add(NewGame);

        JButton button2 = new JButton("createWarship ?:)");
        button2.setBounds(450, 60, 130, 30);
        panel.add(button2);

        panel.setBackground(Color.blue);

        panel.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                if (!createWarship) {
                    mousePosition(10, 50);
                } else {
                    mousePosition(230, 50);
                }
            }
        });
        NewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawSquares();
            }
        });


        button2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                createWarship = !createWarship;
            }
        });

        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

    }

    public void mousePosition(int positionX, int positionY) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        int x = (int) ((location.getX() - SeaBattleClient.frame.getLocation().x-positionX)/20);
        int y = (int) ((location.getY() - SeaBattleClient.frame.getLocation().y-positionY)/20);
        if (x>=0 && y>=0 && x<=9 && y<=9) {
            if (fireCheck) {
                writer.println(String.valueOf(createWarship) + " " + x + " " + y + " " + id + " " + String.valueOf(fireCheck));
                System.out.println(String.valueOf(createWarship) + " " + x + " " + y);
                writer.flush();
            }
        }
    }


    private void paintSquare(int x, int y, int ym, int xm, Color color) {
        x = xm + (x)*20;
        y = ym + (y)*20;
        Panel.paintSquare2(getGraphics(), x, y, 20 ,20,color);
        drawSquares();
    }

    public void drawSquares() {
        Panel.paintSquare(getGraphics(), 10, 50, 200 ,200);
        Panel.paintSquare(getGraphics(), 230, 50, 200 ,200);
        for (int i=0; i<10; i++) {
            Panel.paintSquare(getGraphics(), i*20 + 10, 50, 20 ,200);
            Panel.paintSquare(getGraphics(), i*20 + 230, 50, 20 ,200);
            Panel.paintSquare(getGraphics(), 10, i*20 + 50, 200 ,20);
            Panel.paintSquare(getGraphics(), 230, i*20 + 50, 200 ,20);
        }
    }

    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Networking established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if ("id".equals(message.split(" ")[0])) {
                        id = Integer.parseInt(message.split(" ")[1]);
                    } else if ("create".equals(message.split(" ")[0])) {
                        if ("sucess".equals(message.split(" ")[1]))
                             paintSquare(Integer.parseInt(message.split(" ")[3]), Integer.parseInt(message.split(" ")[2]), 50, 10, Color.BLACK);
                    } else if ("sucess".equals(message.split(" ")[0])) {
                            paintSquare(Integer.parseInt(message.split(" ")[2]), Integer.parseInt(message.split(" ")[1]), 50, Integer.parseInt(message.split(" ")[3]), Color.red);
                            fireCheck = "true".equals(message.split(" ")[4]);
                    } else {
                            paintSquare(Integer.parseInt(message.split(" ")[2]), Integer.parseInt(message.split(" ")[1]), 50, Integer.parseInt(message.split(" ")[3]), Color.white);
                            fireCheck = "true".equals(message.split(" ")[4]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}