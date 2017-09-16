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
    // Game
    private Ball bClient;
    private int winScore;
    private int cachedP1Score = 0;
    private int cachedP2Score = 0;

    // Networking
    private MultiplayerSocket multiplayerSocket;
    private int hostPort;
    private int playerNum;
    private boolean isHost;
    private Socket socket;

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
        multiplayerSocket.sendVars();
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
            multiplayerSocket.sendUpdatedScore();
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
        multiplayerSocket.setPlayerNum(playerNum);
        if(!isHost) {
            System.out.println("Waiting for other player to choose number...");
            while(multiplayerSocket.getOtherPlayerNum() != 0 && multiplayerSocket.getOtherPlayerNum() != 1) {
                multiplayerSocket.sendNBVars();
            }
        }
        switch(playerNum) {
            case 0:
                if(multiplayerSocket.getOtherPlayerNum() != 0)
                    playerNum = 0;
                else {
                    JOptionPane.showMessageDialog(null, "The other player is already player 1, you are player 2 now.");
                    playerNum = 1;
                }

                break;
            case 1:
                if(multiplayerSocket.getOtherPlayerNum() != 1)
                    playerNum = 1;
                else {
                    JOptionPane.showMessageDialog(null, "The other player is already player 2, you are player 1 now.");
                    playerNum = 0;
                }
                break;
        }
        multiplayerSocket.setPlayerNum(playerNum);
        System.out.println("Got number, sending to host");
        multiplayerSocket.sendVars();
        System.out.println("Sent number, waiting for player");
        if(isHost) {
            System.out.println("Waiting for other player to choose number...");
            while(multiplayerSocket.getOtherPlayerNum() != 0 && multiplayerSocket.getOtherPlayerNum() != 1) {
                multiplayerSocket.sendNBVars();
            }
        }
        if(playerNum == 0)
            bClient.p1.setPlayerNum(playerNum);
        if(playerNum == 1)
            bClient.p2.setPlayerNum(playerNum);
        multiplayerSocket.setPlayerNum(playerNum);
        multiplayerSocket.sendNBVars();
    }
    
    private void setUpIO() {
        multiplayerSocket = new MultiplayerSocket(bClient, socket, isHost, winScore);
        new Thread(multiplayerSocket).start();
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
        multiplayerSocket.sendVars();
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
        multiplayerSocket.close();
        return this;
    }
}
