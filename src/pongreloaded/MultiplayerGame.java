package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * @author Mcat12
 */
public class MultiplayerGame implements Screen {
	// Multiplayer Connection
    String ip;
    String inLine;
    String outLine;
    String[] inFormat = new String[10];
    int port;
    int hostPort;
    int playerNum = 3;
    int otherPlayerNum = 3;
    int[] msgBody = new int[9];
    int[] arrayXY = new int[9];
    boolean isHost;
    boolean acceptedStop = false;
    boolean remoteAcceptedStop = false;
    ServerSocket server;
    Socket socket;
    MsgHandler msgHandler;
    BufferedReader in;
    PrintWriter out;
    Runnable socketWork;
	
	// Game
	Ball bClient;
	static int winScore;
	
	// Threads
	Thread bC;
    Thread pC;
    Thread pM;
    Thread sW;
	
	public MultiplayerGame(String ip, int port, int winScore) {
		this.ip = ip;
		this.port = port;
		this.winScore = winScore;
		isHost = false;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		connect(ip, port);
	}
	
	public MultiplayerGame(int hostPort, int winScore) {
		this.hostPort = hostPort;
		this.winScore = winScore;
		isHost = true;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		host();
	}
	
	public void startRemoteGame() {
		System.out.println("Starting Remote Game");
        bC.start();
        pC.start();
        pM.start();
        
        // Send Variables to Player
        sendVarsToServer();
    }
	
	public boolean isFinished() {
        if(bClient.p1Score >= winScore) {
            return true;
        }
        if(bClient.p2Score >= winScore) {
            return true;
        }
        return false;
    }
    
    public int getWinner() {
    	if(bClient.p1Score >= winScore) {
            return 1;
        }
        if(bClient.p2Score >= winScore) {
            return 2;
        }
        return 0;
    }
    
    public int getBallY() {
    	return bClient.getY();
    }
	
	public void host() {
        System.out.println("Server Initializing...");
        try {
			server = new ServerSocket(hostPort);
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
    
    public void connect(String ip, int port) {
        System.out.println("Client Initializing...");
        try {
			socket = new Socket(ip, port);
			System.out.println("Connected to Server, setting up I/O");
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
    
    public void getPlayerNumber() {
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
				sendNBVarsToServer();
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
		System.out.println("Got number, sending to server");
		sendVarsToServer();
		System.out.println("Sent number, waiting for player");
		if(isHost) {
			System.out.println("Waiting for other player to choose number...");
			while(otherPlayerNum != 0 && otherPlayerNum != 1) {
				sendNBVarsToServer();
			}
		}
		if(playerNum == 0)
			bClient.p1.setPlayerNum(playerNum);
		if(playerNum == 1)
			bClient.p2.setPlayerNum(playerNum);
		sendNBVarsToServer();
    }
    
    public void setUpIO() {
    	try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketWork = new Runnable() {
				boolean isRunning = true;
				
				public void getVariables() {
					if(isHost) {
			    		bClient.p2.setY(arrayXY[0]);
			    		bClient.p2.setYDirection(arrayXY[1]);
			    		bClient.p2Score = arrayXY[2];
			    		winScore = arrayXY[3];
			            otherPlayerNum = arrayXY[4];
			    	}
			    	else {
			    		bClient.p1.setY(arrayXY[0]);
			    		bClient.p1.setYDirection(arrayXY[1]);
			    		bClient.setX(arrayXY[2]);
			    		bClient.setY(arrayXY[3]);
			    		bClient.setXDirection(arrayXY[4]);
			    		bClient.setYDirection(arrayXY[5]);
			    		bClient.p1Score = arrayXY[6];
			    		winScore = arrayXY[7];
			            otherPlayerNum = arrayXY[8];
			    	}
				}
				
				public void getNBVariables() {
					if(!isHost)
						bClient.winScore = arrayXY[0];
			    	otherPlayerNum = arrayXY[1];
			    }
				
				public void checkForPacket() {
					try {
						if(in.readLine() != null) {
							inLine = in.readLine();
							inFormat = inLine.split(",");
							inFormat[1] = inFormat[1].replace("[", "");
							inFormat[9] = inFormat[9].replace("]", "");
							for(int i = 1; i < 10; i++) {
								arrayXY[i-1] = Integer.parseInt(inFormat[i].trim());
							}
							
							switch(inFormat[0]) {
								case "getVars":
									getVariables();
									break;
								case "getNBVars":
									getNBVariables();
									break;
								case "stop":
									System.out.println("Server says to stop");
									acceptedStop = true;
									acceptStopCommand();
									System.out.println("Sent acceptedStop, stopping serverWork and closing connection");
									stop();
									closeConnection();
									System.exit(0);
									break;
								case "acceptStop":
									System.out.println("Server accepted stop, stopping serverWork");
									remoteAcceptedStop = true;
									stop();
							}
							
						}
					}
					catch (IOException e) {
						System.out.println("Couldn't read input, closing connection");
						e.printStackTrace();
						stop();
						closeConnection();
						System.exit(-1);
					}
					catch(NullPointerException npe) {
						System.out.println("Server closed, closing");
						System.exit(0);
					}
					catch(Exception e) {
						System.out.println("Exception!\nMessage ID: " + inFormat[0] + "\nMessage: " + inLine);
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				public void stop() {
					isRunning = false;
				}
				
				public void run() {
					while(isRunning) {
						checkForPacket();
					}
				}
			};
			sW = new Thread(socketWork);
			sW.start();
		}
    	catch (IOException e) {
			System.out.println("Failed setting up I/O");
			e.printStackTrace();
			System.exit(-1);
		}
    }
    
    public void sendVarsToServer() {
        if(isHost) {
        	msgBody[0] = bClient.p1.getY();
        	msgBody[1] = bClient.p1.getYDirection();
        	msgBody[2] = bClient.getX();
            msgBody[3] = bClient.getY();
            msgBody[4] = bClient.getXDirection();
            msgBody[5] = bClient.getYDirection();
            msgBody[6] = bClient.p1Score;
            msgBody[7] = winScore;
            msgBody[8] = playerNum;
        }
        else {
        	msgBody[0] = bClient.p2.getY();
        	msgBody[1] = bClient.p2.getYDirection();
        	msgBody[2] = bClient.p2Score;
        	msgBody[3] = winScore;
            msgBody[4] = playerNum;
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
    
    public void sendNBVarsToServer() {
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
    
    public void tellServerToStop() {
    	try {
    		out.println("stop," + Arrays.toString(msgBody));
    	}
    	catch(Exception e) {
    		System.out.println("Message failed to send, shutting down connection...");
    		closeConnection();
    		System.exit(-1);
    	}
    }
    
    public void acceptStopCommand() {
    	try {
    		out.println("acceptStop," + Arrays.toString(msgBody));
    	}
    	catch(Exception e) { }
    }
    
    public void closeConnection() {
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
	
	public static void setWinScore(int score) {
		winScore = score;
	}
	
	public void displayOutput(Graphics g) {
		// Game
        bClient.draw(g);
        bClient.p1.draw(g);
        bClient.p2.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+bClient.p1Score, 15, 50);
        g.drawString(""+bClient.p2Score, 370, 50);
        
        // Send Variables to Server
        sendVarsToServer();
	}
	
	public Screens getScreenType() {
		return Screens.MULTIGAME;
	}
	
	public Screen getScreen() {
		return this;
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
			System.out.println("Telling Server to stop");
			tellServerToStop();
			System.out.println("Waiting for server's response...");
			while(!remoteAcceptedStop) { }
			closeConnection();
		}
		return this;
	}
}
