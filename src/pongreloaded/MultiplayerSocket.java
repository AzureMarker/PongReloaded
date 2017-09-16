package pongreloaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class MultiplayerSocket implements Runnable {
    private boolean isRunning = true;
    private boolean isHost;
    private boolean acceptedStop = false;
    private boolean remoteAcceptedStop = false;
    private int playerNum;
    private int otherPlayerNum = 3;
    private int[] msgBody = new int[9];
    private int[] arrayXY = new int[9];
    private String inLine;
    private String[] inFormat = new String[10];

    private Socket socket;
    private Ball bClient;
    private BufferedReader in;
    private PrintWriter out;
    private int winScore;

    MultiplayerSocket(Ball bClient, Socket socket, boolean isHost, int winScore) {
        try {
            this.isHost = isHost;
            this.winScore = winScore;
            this.socket = socket;
            this.bClient = bClient;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Failed setting up I/O");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void getVariables() {
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

    private void getNBVariables() {
        if (!isHost)
            bClient.setWinScore(arrayXY[0]);
        otherPlayerNum = arrayXY[1];
    }

    private void getUpdatedScore() {
        bClient.setP1Score(arrayXY[0]);
        bClient.setP2Score(arrayXY[1]);
    }

    private void checkForPacket() {
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

    void sendVars() {
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


    void sendNBVars() {
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

    void sendUpdatedScore() {
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

    void close() {
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

    private void stop() {
        isRunning = false;
    }

    public void run() {
        while (isRunning) {
            checkForPacket();
        }
    }

    int getOtherPlayerNum() {
        return otherPlayerNum;
    }

    void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
}
