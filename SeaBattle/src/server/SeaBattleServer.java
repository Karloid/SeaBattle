package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SeaBattleServer {
    ArrayList<PrintWriter> clientOutputStreams;
    private int[][] warshipPosition;
    private List<int[][]> list = new ArrayList<int[][]>();
    private ArrayList<Integer> waitID = new ArrayList<Integer>();
    private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    public class Main implements Runnable {
        BufferedReader reader;
        Socket sock;

        public Main(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    boolean createWarship = "true".equals(message.split(" ")[0]);
                    int i = Integer.parseInt(message.split(" ")[2]);
                    int j = Integer.parseInt(message.split(" ")[1]);
                    int id = Integer.parseInt(message.split(" ")[3]);
                    System.out.println("read " + String.valueOf(createWarship) + " " + i + " " + j);
                    if (createWarship) {
                        boolean fireCheck = "true".equals(message.split(" ")[4]);
                        if (fireCheck) {
                            if (map.containsKey(id))
                                warshipPosition = list.get(map.get(id));
                            else
                                for (int key : map.keySet())
                                    if (id == map.get(key))
                                        warshipPosition = list.get(key);
                            if(warshipPosition[i][j] == 1) {
                                returnValue("sucess " + i + " " + j + " " + 230 + " true", id);
                                if (map.containsKey(id))
                                    returnValue("sucess " + i + " " + j + " " + 10 + " false", map.get(id));
                                else
                                    for (int key : map.keySet())
                                        if (id == map.get(key))
                                           returnValue("sucess " + i + " " + j + " " + 10 + " false", key);
                            } else {
                                returnValue("notsucess " + i + " " + j + " " + 230 + " false", id);
                                if (map.containsKey(id))
                                    returnValue("notsucess " + i + " " + j + " " + 10 + " true", map.get(id));
                                else
                                    for (int key : map.keySet())
                                        if (id == map.get(key))
                                            returnValue("notsucess " + i + " " + j + " " + 10 + " true", key);
                            }
                        }
                    }
                    else {
                        warshipPosition = list.get(id);
                        checkPosition(i, j, id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private  void checkPosition(int x, int y, int id) {
        int count = 0;
        int checkStart=0, checkEnd=3;
        int checkLeft=1, checkRight=1;
        int warshipCount=0;

        if (y==0) {
            checkStart++;
            count++;
        }
        if (y==9) {
            checkEnd--;
            count++;
        }
        if (x==0)
            checkLeft=0;
        if (x==9)
            checkRight=0;

        while (checkStart<checkEnd) {
            if (checkStart != 1) {
                if (warshipPosition[x-checkLeft][y-1+checkStart] == 0 && warshipPosition[x+checkRight][y-1+checkStart] == 0) {
                    count++;
                }
            } else {
                if (warshipPosition[x-checkLeft][y] != 0) {
                    warshipCount++;
                }
                if (warshipPosition[x+checkRight][y] != 0) {
                    warshipCount++;
                }
            }
            if (warshipPosition[x][y-1+checkStart] != 0) {
                warshipCount++;
            }
            checkStart++;
        }
//        if (warshipCount == 1)

        if (warshipCount < 2)
            if(count == 2) {
                warshipPosition[x][y] = 1;
                returnValue("create sucess " + x + " " + y, id);
            }
            else
                returnValue("create notsucess " + x + " " + y, id);
    }

    public void returnValue(String message, int id) {
        Iterator<PrintWriter> it = clientOutputStreams.listIterator(id);
            try {
                System.out.println(message);
                PrintWriter writer = it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void main(String[] args) {
        new SeaBattleServer().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new Main(clientSocket));
                t.start();
                System.out.println("got a connection");
                returnValue("id " + (clientOutputStreams.size()-1), clientOutputStreams.size()-1);
                list.add(new int[10][10]);
                if (waitID.size() == 0)
                    waitID.add(clientOutputStreams.size() - 1);
                else {
                    map.put(waitID.get(0), clientOutputStreams.size()-1);
                    waitID.remove(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
