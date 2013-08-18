package pongreloaded;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Mcat12
 */
public class Pong extends JFrame {
    // Double Buffering
    Image dbImage;
    Graphics dbg;
    
    // Menu Buttons
    Rectangle startButton = new Rectangle(150, 100, 100, 25);
    Rectangle diffButton = new Rectangle(85, 185, 100, 25);
    Rectangle playersButton = new Rectangle(215, 185, 100, 25);
    Rectangle modeButton = new Rectangle(85, 140, 100, 25);
    Rectangle scoreButton = new Rectangle(85, 225, 100, 25);
    Rectangle returnButton = new Rectangle(100, 75, 200, 25);
    Rectangle mainMenuButton = new Rectangle(100, 125, 200, 25);
    Rectangle exitButton = new Rectangle(215, 225, 100, 25);
    Rectangle multiButton = new Rectangle(215, 140, 100, 25);
    Rectangle connectButton = new Rectangle(50, 170, 100, 25);
    Rectangle hostButton = new Rectangle(250, 140, 100, 25);
    Rectangle multiToMainButton = new Rectangle(25, 250, 100, 25);
    
    // Multiplayer Text Fields
    JTextField ipText = new JTextField(){
        @Override
        public void setBorder(Border border){
        }
    };
    JTextField hostPortText = new JTextField(){
        @Override
        public void setBorder(Border border){
        }
    };
    JTextField connectPortText = new JTextField(){
        @Override
        public void setBorder(Border border){
        }
    };
    
    // Multiplayer Server
    Server server;
    Thread s;
    
    // Client Variables
    public class GameVars{
        int hostScore;
        int multiScore;
        int hostX;
        int hostY;
        int multiX;
        int multiY;
        
        public GameVars (int hS, int mS, int hX, int hY, int mX, int mY) {
            hostScore = hS;
            multiScore = mS;
            hostX = hX;
            hostY = hY;
            multiX = mX;
            multiY = mY;
        }
    }
    GameVars gV;
    int character;
    int pHX = 0;
    int pHY = 0;
    char[] com = new char[3];
    boolean clientRun = true;
    boolean first = true;
    boolean isHost;
    String TimeStamp;
    String process = "";
    StringBuffer instr;
    BufferedInputStream is;
    InputStreamReader isr;
    BufferedOutputStream bos;
    OutputStreamWriter osw;
    InetAddress address;
    Socket connection;
    Paddle pClient;
    Ball bClient;
    Thread pC;
    Thread bC;
    Rectangle pHost;
    
    // Ball Objects
    static Ball b = new Ball(193, 143, true);
    
    // Threads
    Thread ball = new Thread(b);
    Thread p1 = new Thread(b.p1);
    Thread p2 = new Thread(b.p2);
    
    // Game Variables
    int difficulty = 1;
    int players = 1;
    int mode = 2;
    int winScore = 10;
    int winID;
    boolean localGameStarted = false;
    boolean remoteGameStarted = false;
    boolean gameFin = false;
    boolean startHover;
    boolean diffHover;
    boolean playersHover;
    boolean modeHover;
    boolean scoreHover;
    boolean returnHover;
    boolean mainMenuHover;
    boolean exitHover;
    boolean multiHover;
    boolean connectHover;
    boolean hostHover;
    boolean multiToMainHover;
    boolean isLocalPaused = false;
    boolean isFirstRun = true;
    boolean isMultiMenu = false;
    
    // Variables for Screen Size
    int GWIDTH = 400, GHEIGHT = 300;
    
    // Dimension of GWIDTH * GHEIGHT
    Dimension screenSize = new Dimension(GWIDTH, GHEIGHT);
    
    // Create Constructor to Spawn Window
    public Pong(){
        this.setTitle("Pong Reloaded");
        this.setSize(screenSize);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.DARK_GRAY);
        this.addKeyListener(new KeyHandler());
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseHandler());
        this.addMouseWheelListener(new MouseHandler());
        ipText.setBounds(48, 83, 100, 25);
        ipText.setBackground(Color.CYAN);
        hostPortText.setBounds(235, 83, 125, 25);
        hostPortText.setBackground(Color.CYAN);
        connectPortText.setBounds(72, 113, 50, 25);
        connectPortText.setBackground(Color.CYAN);
        this.add(ipText);
        this.add(hostPortText);
        this.add(connectPortText);
        ipText.setVisible(false);
        hostPortText.setVisible(false);
        connectPortText.setVisible(false);
        this.setVisible(true);
    }
    
    public static void main(String[] args){
        Pong p = new Pong();
    }
    
    public void startLocalGame(){
        if(isLocalPaused == true){
            try{
                switchLocalPause();
            }
            catch (InterruptedException ie) {
                System.out.println("Error" + ie.getMessage());
            }
        }
        ball.start();
        p1.start();
        p2.start();
        localGameStarted = true;
    }
    
    public void startRemoteGame(){
        bC.start();
        pC.start();
        
        // Send GameVars to Server
        sendToServer();
        
        remoteGameStarted = true;
    }
    
    @Override
    public void paint(Graphics g){
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        draw(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }
    
    public void drawMenu(Graphics g){
        // Menu
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Pong Reloaded", 108, 75);
        
        // Start Button
        if(!startHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(startButton.x, startButton.y, startButton.width, startButton.height);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.GRAY);
        if(isFirstRun == true)
            g.drawString("Start Game", startButton.x+20, startButton.y+17);
        else
            g.drawString("Resume", startButton.x+27, startButton.y+17);
        
        // Multiplayer Button
        if(!multiHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(multiButton.x, multiButton.y, multiButton.width, multiButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Multiplayer", multiButton.x+20,  multiButton.y+17);
        
        // Difficulty Button
        if(!diffHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        
        g.fillRect(diffButton.x, diffButton.y-5, diffButton.width, diffButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Difficulty:", diffButton.x+7, diffButton.y+12);
        switch(difficulty){
            case 1:
                g.drawString("Easy", diffButton.x+63, diffButton.y+12);
                break;
            case 2:
                g.drawString("Med", diffButton.x+63, diffButton.y+12);
                break;
            case 3:
                g.drawString("Hard", diffButton.x+63, diffButton.y+12);
                break;
        }
        
        // Players Button
        if(!playersHover && !isFinished())
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(playersButton.x, playersButton.y-5, playersButton.width, playersButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Players:", playersButton.x+8, playersButton.y+12);
        switch(players){
            case 1:
                g.drawString("Single", playersButton.x+58, playersButton.y+12);
                break;
            case 2:
                g.drawString("Multi", playersButton.x+58, playersButton.y+12);
                break;
        }
        
        // Mode Button
        if(!modeHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(modeButton.x, modeButton.y, modeButton.width, modeButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Mode:", modeButton.x+5, modeButton.y+17);
        switch(mode){
            case 1:
                g.drawString("Original", modeButton.x+45, modeButton.y+17);
                break;
            case 2:
                g.drawString("Reloaded", modeButton.x+45, modeButton.y+17);
                break;
        }
        
        // Score Button
        if(!scoreHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(scoreButton.x, scoreButton.y, scoreButton.width, scoreButton.height);
        g.setColor(Color.GRAY);
        g.drawString("To Win:", scoreButton.x+15, scoreButton.y+17);
        g.drawString(""+winScore, scoreButton.x+61, scoreButton.y+17);
        
        // Exit Button
        if(!exitHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Exit", exitButton.x+38, exitButton.y+17);
    }
    
    public void drawMultiMenu(Graphics g){
        // Multiplayer Header
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Multiplayer", 130, 75);
        
        // Ip Adress Input
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Enter Ip Address & Port", 37, 100);
        ipText.setVisible(true);
        connectPortText.setVisible(true);
        
        // Connect Button
        if(!connectHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(connectButton.x, connectButton.y, connectButton.width, connectButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Connect", connectButton.x+26, connectButton.y+17);
        
        // Host Port Input
        g.setColor(Color.WHITE);
        g.drawString("Enter Port Number", 250, 100);
        hostPortText.setVisible(true);
        
        // Host Button
        if(!hostHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(hostButton.x, hostButton.y, hostButton.width, hostButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Host", hostButton.x+35, hostButton.y+17);
        
        // Back Button (Multiplayer to Main)
        if(!multiToMainHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(multiToMainButton.x, multiToMainButton.y, multiToMainButton.width, multiToMainButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Back", multiToMainButton.x+35, multiToMainButton.y+17);
    }
    
    public void drawLocalGame(Graphics g){
        // Game
        b.draw(g);
        b.p1.draw(g);
        b.p2.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+b.p1Score, 15, 50);
        g.drawString(""+b.p2Score, 370, 50);
    }
    
    public void drawMultiGame(Graphics g){
        // Read GameVars from server
        if(isHost)
            hReadFromServer();
        else
            readFromServer();
        
        // Game
        bClient.draw(g);
        pClient.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+gV.hostScore, 15, 50);
        g.drawString(""+gV.multiScore, 370, 50);
        
        // Send GameVars to Server
        if(isHost)
            hSendToServer();
        else
            sendToServer();
    }
    
    public void drawPaused(Graphics g){
        drawLocalGame(g);
        // Pause Menu
        g.setColor(new Color(0f,0f,0f,0.3f));
        g.fillRect(0,0, GWIDTH, GHEIGHT);
        
        // Return Button
        if(!returnHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(returnButton.x, returnButton.y, returnButton.width, returnButton.height);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.GRAY);
        g.drawString("Return to Game", returnButton.x+55, returnButton.y+17);
        
        // Main Menu Button
        if(!mainMenuHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(mainMenuButton.x, mainMenuButton.y, mainMenuButton.width, mainMenuButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Main Menu", mainMenuButton.x+70, mainMenuButton.y+17);
    }
    
    public void drawFinished(Graphics g){
        // Finish Menu
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Player "+winID+" Won the Game!", 100, 75);
    }
    
    public void draw(Graphics g){
        super.paint(g);
        if(!localGameStarted && !isFinished()){
            if(isMultiMenu && !remoteGameStarted)
                drawMultiMenu(g);
            else if(!isMultiMenu && !remoteGameStarted)
                drawMenu(g);
            else if(!isMultiMenu && remoteGameStarted)
                drawMultiGame(g);
        }
        else if(!isFinished()){
            drawLocalGame(g);
            
            if(isLocalPaused == true){
                drawPaused(g);
            }
        }
        else if(isFinished()){
            drawFinished(g);
        }
        
        repaint();
    }
    
    public boolean isFinished(){
        if(b.p1Score >= winScore){
            winID = 1;
            return true;
        }
        if(b.p2Score >= winScore){
            winID = 2;
            return true;
        }
        return false;
    }
    
    public void connect(String ip, int port){
        System.out.println("Client Initializing...");
        instr = new StringBuffer();
        try{
            address = InetAddress.getByName(ip);
            connection = new Socket(address, port);
            System.out.println("Client Initialized!");
            bos = new BufferedOutputStream(connection.getOutputStream());
            osw = new OutputStreamWriter(bos, "US-ASCII");
            sendToServer();
            System.out.println("Sent First GameVars");
            readFromServer();
        }
        catch (IOException f) {
            System.out.println("IOException: " + f.getMessage());
        }
        catch (Exception g) {
            System.out.println("Exception: " + g.getMessage());
        }
    }
    
    /*public void sendToServer(String message){
        try{
            TimeStamp = new java.util.Date().toString();
            System.out.println(TimeStamp + " [CLIENT] " + message);
            if(isHost){
                server.hProcess = message;
            }
            else{
                osw.write((char) 12 + message + (char) 13);
                osw.flush();
            }
        }
        catch(IOException ioe){
            
        }
    }*/
    
    public void sendToServer(){
        try{
            osw.write(gV.multiX);
            osw.write(gV.multiY);
            osw.write(gV.multiScore);
            osw.flush();
        }
        catch(IOException ioe){
            
        }
    }
    
    public void hSendToServer(){
        server.gVS.hostX = gV.hostX;
        server.gVS.hostY = gV.hostY;
        server.gVS.hostScore = gV.hostScore;
    }
    
    public void readFromServer(){
        try {
            isr.read(com);
            gV.hostX = com[0];
            gV.hostY = com[1];
            gV.hostScore = com[2];
        } catch (IOException ex) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(TimeStamp + " [" + connection.getInetAddress().getHostAddress() + "] Recieved Host GameVars");
        System.out.println(""+gV.hostScore + "\n"+gV.multiScore + "\n"+gV.hostX + "\n"+gV.hostY + "\n"+gV.multiX + "\n"+gV.multiY);
    }
    
    public void hReadFromServer(){
        gV.multiX = server.gVS.multiX;
        gV.multiY = server.gVS.multiY;
        gV.multiScore = server.gVS.multiScore;
        System.out.println(TimeStamp + " [" + server.ipAddress + "] Recieved Multi GameVars");
        System.out.println(""+gV.hostScore + "\n"+gV.multiScore + "\n"+gV.hostX + "\n"+gV.hostY + "\n"+gV.multiX + "\n"+gV.multiY);
    }
    
    public void closeConnection(){
        try{
            System.out.println("Closing connection...");
            is.close();
            isr.close();
            bos.close();
            osw.close();
            connection.close();
            System.out.println("Connection closed.");
        }
        catch(IOException ioe){
            try {
                System.out.println("IOException, closing connection...");
                connection.close();
                System.out.println("Connection closed.");
            }
            catch (IOException e) {}
        }
    }
    
    /////////EVENT LISTENER CLASSES\\\\\\\\\
    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            b.p1.keyPressed(e);
            b.p2.keyPressed(e);
            try{
                menuKeyHandler(e);
            }
            catch(InterruptedException ie){
                System.out.println("Error: " + ie.getMessage());
            }
            
        }
        
        @Override
        public void keyReleased(KeyEvent e){
            b.p1.keyReleased(e);
            b.p2.keyReleased(e);
        }
    }
    
    public void menuKeyHandler(KeyEvent e) throws InterruptedException{
        if(e.getKeyCode() == e.VK_ESCAPE && localGameStarted)
            switchLocalPause();
    }
    
    public void switchLocalPause() throws InterruptedException{
        if(isLocalPaused == false){
                isLocalPaused = true;
                b.setPaused(isLocalPaused);
                b.p1.setPaused(isLocalPaused);
                b.p2.setPaused(isLocalPaused);
            }
            else if(isLocalPaused == true){
                isLocalPaused = false;
                b.setPaused(isLocalPaused);
                b.p1.setPaused(isLocalPaused);
                b.p2.setPaused(isLocalPaused);
            }
    }
    
    public class MouseHandler extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e){
            int mx = e.getX();
            int my = e.getY();
            
            // Check if Hovering over Start Button
            if(mx > startButton.x && mx < startButton.x+startButton.width && my > startButton.y && my < startButton.y+startButton.height)
                startHover = true;
            else
                startHover = false;
            
            // Check if Hovering over Difficulty Button
            if(mx > diffButton.x && mx < diffButton.x+diffButton.width && my > diffButton.y && my < diffButton.y+diffButton.height)
                diffHover = true;
            else
                diffHover = false;
            
            // Check if Hovering over Players Button
            if(mx > playersButton.x && mx < playersButton.x+playersButton.width && my > playersButton.y && my < playersButton.y+playersButton.height)
                playersHover = true;
            else
                playersHover = false;
            
            // Check if Hovering over Mode Button
            if(mx > modeButton.x && mx < modeButton.x+modeButton.width && my > modeButton.y && my < modeButton.y+modeButton.height)
                modeHover = true;
            else
                modeHover = false;
            
            // Check if Hovering over Score Button
            if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height)
                scoreHover = true;
            else
                scoreHover = false;
            
            // Check if Hovering over Return Button
            if(mx > returnButton.x && mx < returnButton.x+returnButton.width && my > returnButton.y && my < returnButton.y+returnButton.height)
                returnHover = true;
            else
                returnHover = false;
            
            // Check if Hovering over Main Menu Button
            if(mx > mainMenuButton.x && mx < mainMenuButton.x+mainMenuButton.width && my > mainMenuButton.y && my < mainMenuButton.y+mainMenuButton.height)
                mainMenuHover = true;
            else
                mainMenuHover = false;
            
            // Check if Hovering over Exit Button
            if(mx > exitButton.x && mx < exitButton.x+exitButton.width && my > exitButton.y && my < exitButton.y+exitButton.height)
                exitHover = true;
            else
                exitHover = false;
            
            // Check if Hovering over Multiplayer Button
            if(mx > multiButton.x && mx < multiButton.x+multiButton.width && my > multiButton.y && my < multiButton.y+multiButton.height)
                multiHover = true;
            else
                multiHover = false;
            
            // Check if Hovering over Connect Button
            if(mx > connectButton.x && mx < connectButton.x+connectButton.width && my > connectButton.y && my < connectButton.y+connectButton.height)
                connectHover = true;
            else
                connectHover = false;
            
            // Check if Hovering over Host Button
            if(mx > hostButton.x && mx < hostButton.x+hostButton.width && my > hostButton.y && my < hostButton.y+hostButton.height)
                hostHover = true;
            else
                hostHover = false;
            
            // Check if Hovering over Multiplayer To Main Button
            if(mx > multiToMainButton.x && mx < multiToMainButton.x+multiToMainButton.width && my > multiToMainButton.y && my < multiToMainButton.y+multiToMainButton.height)
                multiToMainHover = true;
            else
                multiToMainHover = false;
        }
        
        @Override
        public void mouseReleased(MouseEvent e){
            int mx = e.getX();
            int my = e.getY();
            
            // Check if just Pressed Start Button
            if(mx > startButton.x && mx < startButton.x+startButton.width && my > startButton.y && my < startButton.y+startButton.height && localGameStarted == false && isMultiMenu == false)
                startLocalGame();
            
            // Check if just Pressed Score Button
            if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height && localGameStarted == false && isMultiMenu == false){
                winScore++;
                if(winScore > 100)
                    winScore = 1;
            }
            
            // Check if just Released Difficulty Button
            if(mx > diffButton.x && mx < diffButton.x+diffButton.width && my > diffButton.y && my < diffButton.y+diffButton.height && localGameStarted == false && isMultiMenu == false){
                switch(difficulty){
                    case 1:
                        b.setDifficulty(4);
                        if(players == 1)
                            b.p2.setDifficulty(6);
                        difficulty = 2;
                        break;
                    case 2:
                        b.setDifficulty(2);
                        if(players == 1)
                            b.p2.setDifficulty(3);
                        difficulty = 3;
                        break;
                    case 3:
                        b.setDifficulty(8);
                        if(players == 1)
                            b.p2.setDifficulty(9);
                        difficulty = 1;
                        break;
                }
            }
            
            // Check if just Released Players Button
            if(mx > playersButton.x && mx < playersButton.x+playersButton.width && my > playersButton.y && my < playersButton.y+playersButton.height && localGameStarted == false && isMultiMenu == false){
                switch(players){
                    case 1:
                        players = 2;
                        b.p2.setPlayers(players);
                        break;
                    case 2:
                        players = 1;
                        b.p2.setPlayers(players);
                        break;
                }
            }
            
            // Check if just Pressed Mode Button
            if(mx > modeButton.x && mx < modeButton.x+modeButton.width && my > modeButton.y && my < modeButton.y+modeButton.height && localGameStarted == false && isMultiMenu == false){
                switch(mode){
                    case 1:
                        mode = 2;
                        Pong.this.getContentPane().setBackground(Color.DARK_GRAY);
                        b.p1.setMode(mode);
                        b.p2.setMode(mode);
                        break;
                    case 2:
                        mode = 1;
                        Pong.this.getContentPane().setBackground(Color.BLACK);
                        b.p1.setMode(mode);
                        b.p2.setMode(mode);
                        break;
                }
            }
            
            // Check if just Pressed Return Button
            if(mx > returnButton.x && mx < returnButton.x+returnButton.width && my > returnButton.y && my < returnButton.y+returnButton.height && localGameStarted == true && isLocalPaused == true && isMultiMenu == false){
                try{
                    switchLocalPause();
                }
                catch(InterruptedException ie){
                    System.out.println("Error: " + ie.getMessage());
                }
            }
            
            // Check if just Pressed Main Menu Button
            if(mx > mainMenuButton.x && mx < mainMenuButton.x+mainMenuButton.width && my > mainMenuButton.y && my < mainMenuButton.y+mainMenuButton.height && localGameStarted == true && isLocalPaused == true && isMultiMenu == false){
                localGameStarted = false;
                gameFin = false;
                ball.interrupt();
                ball = new Thread(b);
                p1.interrupt();
                p1 = new Thread(b.p1);
                p2.interrupt();
                p2 = new Thread(b.p2);
                isFirstRun = false;
            }
            
            // Check if just Pressed Exit Button
            if(mx > exitButton.x && mx < exitButton.x+exitButton.width && my > exitButton.y && my < exitButton.y+exitButton.height && localGameStarted == false && isMultiMenu == false){
                try{
                    if(Pong.this.connection != null)
                        Pong.this.connection.close();
                    if(server != null)
                    {
                        server.conPrint("Closing...");
                        server.closeServer();
                    }
                    dispose();
                }catch(IOException ioe){
                    System.out.println("IOException!: " + ioe.getMessage());
                }
            }
            
            // Check if just Pressed Multiplayer Button
            if(mx > multiButton.x && mx < multiButton.x+multiButton.width && my > multiButton.y && my < multiButton.y+multiButton.height && localGameStarted == false && isMultiMenu == false)
                isMultiMenu = true;
            
            // Check if just Pressed Connect Button
            if(mx > connectButton.x && mx < connectButton.x+connectButton.width && my > connectButton.y && my < connectButton.y+connectButton.height && localGameStarted == false && isMultiMenu == true){
                if(!isHost)
                    connect(ipText.getText(), Integer.parseInt(connectPortText.getText()));
                else
                    ipText.setText("You are host");
            }
            
            // Check if just Pressed Host Button
            if(mx > hostButton.x && mx < hostButton.x+hostButton.width && my > hostButton.y && my < hostButton.y+hostButton.height && localGameStarted == false && isMultiMenu == true){
                try{
                    server = new Server(Integer.parseInt(hostPortText.getText()));
                    server.serverSocket.getLocalPort();
                    s = new Thread(server);
                    s.start();
                    isHost = true;
                    isMultiMenu = false;
                    ipText.setVisible(false);
                    connectPortText.setVisible(false);
                    hostPortText.setVisible(false);
                    remoteGameStarted = false;
                    pClient = new Paddle(15, 140, 3);
                }
                catch(NumberFormatException nfe){
                    
                }
                catch(NullPointerException npe){
                    
                }
            }
            
            
            // Check if just Pressed Multiplayer To Main Button
            if(mx > multiToMainButton.x && mx < multiToMainButton.x+multiToMainButton.width && my > multiToMainButton.y && my < multiToMainButton.y+multiToMainButton.height && localGameStarted == false && isMultiMenu == true){
                Pong.this.ipText.setVisible(false);
                Pong.this.hostPortText.setVisible(false);
                Pong.this.connectPortText.setVisible(false);
                isMultiMenu = false;
            }
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e){
            int mx = e.getX();
            int my = e.getY();
            int mwDir = e.getWheelRotation();
            
            // Check if just Scolled Over Score Button
            if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height && localGameStarted == false && isMultiMenu == false){
                if(mwDir < 0)
                    winScore++;
                else
                    winScore--;
                if(winScore > 100)
                    winScore = 1;
                if(winScore < 1)
                    winScore = 100;
            }
        }
    }
    /////////END EVENT LISTENER CLASSES\\\\\\\\\
}