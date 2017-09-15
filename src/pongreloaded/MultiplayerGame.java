package pongreloaded;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

/**
 * @author Mcat12
 */
public class MultiplayerGame implements Screen {
    private String inLine;
    private String[] inFormat = new String[10];
    private int hostPort;
    private int playerNum = 3;
    private int otherPlayerNum = 3;
    private int[] msgBody = new int[9];
    private int[] arrayXY = new int[9];
    private boolean isHost;
    private boolean acceptedStop = false;
    private boolean remoteAcceptedStop = false;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Game
    private Ball bClient;
    private int winScore;
    private int cachedP1Score = 0;
    private int cachedP2Score = 0;

    // Threads
    private Thread bC;
    private Thread pC;
    private Thread pM;

    MultiplayerGame(String ip, int port, int winScore) {
        this.winScore = winScore;
        isHost = false;
        bClient = new Ball(193, 143, this);
        bC = new Thread(bClient);
        pC = new Thread(bClient.p1);
        pM = new Thread(bClient.p2);
        connect(ip, port);
    }

    MultiplayerGame(int hostPort, int winScore) {
        this.hostPort = hostPort;
        this.winScore = winScore;
        isHost = true;
        bClient = new Ball(193, 143, this);
        bC = new Thread(bClient);
        pC = new Thread(bClient.p1);
        pM = new Thread(bClient.p2);
        host();
    }

    private void startRemoteGame() {
        System.out.println("Starting Remote Game");
        bC.start();
        pC.start();
        pM.start();
        
        // Send Variables to Player
        sendVars();
    }

    public boolean isFinished() {
        return bClient.getP1Score() >= winScore || bClient.getP2Score() >= winScore;
    }
    
    public int getWinner() {
        if(bClient.getP1Score() >= winScore)
            return 1;
        if(bClient.getP2Score() >= winScore)
            return 2;
        return 0;
    }
    
    private void updateScore() {
        if(isHost && bClient.getP1Score() != cachedP1Score || bClient.getP2Score() != cachedP2Score) {
            System.out.println("Scores aren't syncronized, updating...");
            sendUpdatedScore();
            cachedP1Score = bClient.getP1Score();
            cachedP2Score = bClient.getP2Score();
        }
    }

    private void host() {
        System.out.println("Server Initializing...");
        try {
            ServerSocket server = new ServerSocket(hostPort);
            System.out.println("Created Server, waiting for Client...");
            socket = server.accept();
            System.out.println("Client connected, setting up I/O");
            setUpIO();
            getPlayerNumber();
            socket.setSoTimeout(1000);
            startRemoteGame();
        }
        catch (IOException e) {
            System.out.println("Couldn't start server");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private void connect(String ip, int port) {
        System.out.println("Client Initializing...");
        try {
            socket = new Socket(ip, port);
            System.out.println("Connected to Host, setting up I/O");
            setUpIO();
            getPlayerNumber();
            socket.setSoTimeout(1000);
            startRemoteGame();
        }
        catch (UnknownHostException e) {
            System.out.println("Couldn't find host");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (IOException e) {
            System.out.println("Couldn't connect to host, ioe");
            e.printStackTrace();
        }
        
    }
    
    private void getPlayerNumber() {
        Object[] options = {
                "Player 1",
                "Player 2"
        };
        if(isHost)
            playerNum = JOptionPane.showOptionDialog(null,
                    "What Player are you?",
                    "Host - Enter Player Number",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
        else
            playerNum = JOptionPane.showOptionDialog(null,
                    "What Player are you?",
                    "Client - Enter Player Number",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
        if(!isHost) {
            System.out.println("Waiting for other player to choose number...");
            while(otherPlayerNum != 0 && otherPlayerNum != 1) {
                sendNBVars();
            }
        }
        switch(playerNum) {
            case 0:
                if(otherPlayerNum != 0)
                    playerNum = 0;
                else {
                    JOptionPane.showMessageDialog(null, "The other player is already player 1, you are player 2 now.");
                    playerNum = 1;
                }

                break;
            case 1:
                if(otherPlayerNum != 1)
                    playerNum = 1;
                else {
                    JOptionPane.showMessageDialog(null, "The other player is already player 2, you are player 1 now.");
                    playerNum = 0;
                }
                break;
        }
        System.out.println("Got number, sending to host");
        sendVars();
        System.out.println("Sent number, waiting for player");
        if(isHost) {
            System.out.println("Waiting for other player to choose number...");
            while(otherPlayerNum != 0 && otherPlayerNum != 1) {
                sendNBVars();
            }
        }
        if(playerNum == 0)
            bClient.p1.setPlayerNum(playerNum);
        if(playerNum == 1)
            bClient.p2.setPlayerNum(playerNum);
        sendNBVars();
    }
    
    private void setUpIO() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Runnable socketWork = new Runnable() {
                boolean isRunning = true;

                void getVariables() {
                    if (isHost) {
                        bClient.p2.setY(arrayXY[0]);
                        bClient.p2.setYDirection(arrayXY[1]);
                        otherPlayerNum = arrayXY[2];
                    } else {
                        bClient.p1.setY(arrayXY[0]);
                        bClient.p1.setYDirection(arrayXY[1]);
                        bClient.setX(arrayXY[2]);
                        bClient.setY(arrayXY[3]);
                        bClient.setXDirection(arrayXY[4]);
                        bClient.setYDirection(arrayXY[5]);
                        winScore = arrayXY[6];
                        otherPlayerNum = arrayXY[7];
                    }
                }

                void getNBVariables() {
                    if (!isHost)
                        bClient.setWinScore(arrayXY[0]);
                    otherPlayerNum = arrayXY[1];
                }

                void getUpdatedScore() {
                    bClient.setP1Score(arrayXY[0]);
                    bClient.setP2Score(arrayXY[1]);
                }

                void checkForPacket() {
                    try {
                        if (in.readLine() != null) {
                            inLine = in.readLine();
                            inFormat = inLine.split(",");
                            inFormat[1] = inFormat[1].replace("[", "");
                            inFormat[9] = inFormat[9].replace("]", "");
                            for (int i = 1; i < 10; i++) {
                                arrayXY[i - 1] = Integer.parseInt(inFormat[i].trim());
                            }

                            switch (inFormat[0]) {
                                case "getVars":
                                    getVariables();
                                    break;
                                case "getNBVars":
                                    getNBVariables();
                                    break;
                                case "getUpdatedScore":
                                    getUpdatedScore();
                                    break;
                                case "stop":
                                    System.out.println("Host says to stop");
                                    acceptedStop = true;
                                    acceptStopCommand();
                                    System.out.println("Sent acceptedStop, stopping serverWork and closing connection");
                                    stop();
                                    closeConnection();
                                    System.exit(0);
                                    break;
                                case "acceptStop":
                                    System.out.println("Host accepted stop, stopping serverWork");
                                    remoteAcceptedStop = true;
                                    stop();
                            }

                        }
                    } catch (IOException e) {
                        System.out.println("Couldn't read input, closing connection");
                        stop();
                        closeConnection();
                        System.exit(-1);
                    } catch (NullPointerException npe) {
                        System.out.println("Server closed, closing");
                        closeConnection();
                        System.exit(0);
                    } catch (Exception e) {
                        System.out.println("Exception!\nMessage ID: " + inFormat[0] + "\nMessage: " + inLine);
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }

                void stop() {
                    isRunning = false;
                }

                public void run() {
                    while (isRunning) {
                        checkForPacket();
                    }
                }
            };
            Thread sW = new Thread(socketWork);
            sW.start();
        }
        catch (IOException e) {
            System.out.println("Failed setting up I/O");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private void sendVars() {
        if(isHost) {
            msgBody[0] = bClient.p1.getY();
            msgBody[1] = bClient.p1.getYDirection();
            msgBody[2] = bClient.getX();
            msgBody[3] = bClient.getY();
            msgBody[4] = bClient.getXDirection();
            msgBody[5] = bClient.getYDirection();
            msgBody[6] = winScore;
            msgBody[7] = playerNum;
        }
        else {
            msgBody[0] = bClient.p2.getY();
            msgBody[1] = bClient.p2.getYDirection();
            msgBody[2] = playerNum;
        }
        try {
            out.println("getVars," + Arrays.toString(msgBody));
        }
        catch(Exception e) {
            System.out.println("Message failed to send, shutting down connection...");
            closeConnection();
            System.exit(-1);
        }
    }
    
    private void sendNBVars() {
        if(isHost)
            msgBody[0] = winScore;
        msgBody[1] = playerNum;
        try {
            out.println("getNBVars," + Arrays.toString(msgBody));
        }
        catch(Exception e) {
            System.out.println("Message failed to send, shutting down connection...");
            closeConnection();
            System.exit(-1);
        }
    }
    
    private void sendUpdatedScore() {
        msgBody[0] = bClient.getP1Score();
        msgBody[1] = bClient.getP2Score();
        try {
            out.println("getUpdatedScore," + Arrays.toString(msgBody));
        }
        catch(Exception e) {
            System.out.println("Message failed to send, shutting down connection...");
            closeConnection();
            System.exit(-1);
        }
    }
    
    private void tellHostToStop() {
        try {
            out.println("stop," + Arrays.toString(msgBody));
        }
        catch(Exception e) {
            System.out.println("Message failed to send, shutting down connection...");
            closeConnection();
            System.exit(-1);
        }
    }
    
    private void acceptStopCommand() {
        try {
            out.println("acceptStop," + Arrays.toString(msgBody));
        }
        catch(Exception ignored) { }
    }
    
    private void closeConnection() {
            System.out.println("Closing connection...");
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close connection, ioe");
                e.printStackTrace();
                System.exit(-1);
            }
            System.out.println("Connection closed.");
    }

    public void displayOutput(Graphics g) {
        // Game
        bClient.draw(g);
        bClient.p1.draw(g);
        bClient.p2.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+bClient.getP1Score(), 15, 50);
        g.drawString(""+bClient.getP2Score(), 370, 50);
        
        // Send info to other player
        sendVars();
        if(isHost)
            updateScore();
    }

    public Screens getScreenType() {
        return Screens.MULTIGAME;
    }

    int getPlayerNum() {
        return playerNum;
    }

    public Screen respondToUserInput(KeyEvent key) {
        bClient.p1.keyPressed(key);
        bClient.p2.keyPressed(key);
        
        return this;
    }

    public Screen respondToUserInputReleased(KeyEvent key) {
        bClient.p1.keyReleased(key);
        bClient.p2.keyReleased(key);
        
        return this;
    }

    public Screen respondToUserInputHover(MouseEvent mouse) {
        return this;
    }

    public Screen respondToUserInputClick(MouseEvent mouse) {
        return this;
    }

    public Screen respondToUserInput(MouseWheelEvent mouse) {
        return this;
    }

    public Screen windowClosingEvent(WindowEvent window) {
        if(!acceptedStop) {
            if(isHost)
                System.out.println("Telling Client to stop");
            else
                System.out.println("Telling Host to stop");
            tellHostToStop();
            System.out.println("Waiting for host's response...");
            while(!remoteAcceptedStop) { }
            closeConnection();
        }

        return this;
    }
}
