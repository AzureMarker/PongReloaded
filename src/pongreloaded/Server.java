package pongreloaded;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import pongreloaded.Pong.GameVars;

/**
 * @author Mark
 */
public class Server extends JFrame implements Runnable{
    // Server Variables
    int character;
    boolean first = true;
    boolean isHostConnected = false;
    boolean isClientConnected = false;
    String ipAddress;
    String host;
    String TimeStamp;
    String returnCode;
    String hProcess;
    StringBuffer process;
    BufferedInputStream is;
    ObjectInputStream ois;
    InputStreamReader isr;
    BufferedOutputStream os;
    ObjectOutputStream oos;
    OutputStreamWriter osw;
    ServerSocket serverSocket;
    Socket connection;
    
    //GameVars
    GameVars gVS;
    
    // Threading Variables
    boolean run = true;
    
    // JFrame Components
    JTextArea console = new JTextArea();
    JScrollPane sp;
    
    // Variables for Screen Size
    int GWIDTH = 400, GHEIGHT = 300;
    
    // Dimension of GWIDTH * GHEIGHT
    Dimension screenSize = new Dimension(GWIDTH, GHEIGHT);
    
    public Server(int port){
        // Server Setup
        try{
            ipAddress = InetAddress.getLocalHost().getHostAddress();
            host = InetAddress.getLocalHost().getHostName();
            serverSocket = new ServerSocket(port);
            
            // Frame Setup
            setTitle("Pong Reloaded Server");
            setSize(screenSize);
            setResizable(false);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            console.setEditable(false);
            sp = new JScrollPane(console);
            add(sp);
            setVisible(true);
            
            conPrint("IP: " + ipAddress + "\nSocket: " + serverSocket.getLocalPort());
        }
        catch(IOException ioe){
            System.out.println("Please enter a valid/unused port");
        }
        catch(Exception e){
            System.out.println("Error:" + e.getMessage());
        }
    }
    
    private void tick(){
        conPrint("Server Tick...");
        try{
            if(first){
                connection = serverSocket.accept();
                isClientConnected = true;
                is = new BufferedInputStream(connection.getInputStream());
                isr = new InputStreamReader(is);
                ois = new ObjectInputStream(connection.getInputStream());
                process = new StringBuffer();
                os = new BufferedOutputStream(connection.getOutputStream());
                oos = new ObjectOutputStream(connection.getOutputStream());
                osw = new OutputStreamWriter(os, "US-ASCII");
                readFromClient();
            }
            conPrint(readStringFromClient());
            readFromClient();
            sendGVToClient();
        }
        catch(IOException ioe){
            try {
                conPrint("IOException, closing connection...");
                connection.close();
                conPrint("Connection closed.");
            }
            catch (IOException e){
                
            }
        }
    }
    
    public void sendGVToClient(){
        try{
            oos.writeObject(gVS);
            oos.flush();
        }
        catch(IOException ioe){
            
        }
    }
    
    public String readStringFromClient(){
        try{
            TimeStamp = new java.util.Date().toString();
            process.setLength(0);
            while((character = isr.read()) != 13 && character != (int) -1) {
                process.append((char)character);
            }
            character = 0;
        }
        catch(IOException ioe){
            conPrint("IOException, closing server...");
            closeServer();
        }
        
        return TimeStamp + " [" + connection.getInetAddress().getHostAddress() + "] " + process;
    }
    
    public GameVars readFromClient(){
        GameVars gameVs = gVS;
        TimeStamp = new java.util.Date().toString();
        try {
            gameVs = (GameVars) ois.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
        conPrint(TimeStamp + " [" + connection.getInetAddress().getHostAddress() + "] Recieved GameVars");
        conPrint(""+gameVs.hostScore + "\n"+gameVs.multiScore + "\n"+gameVs.hostX + "\n"+gameVs.hostY + "\n"+gameVs.multiX + "\n"+gameVs.multiY);
        return gameVs;
    }
    
    public void closeServer(){
        try{
            System.out.println("Closing Server...");
            dispose();
            is.close();
            isr.close();
            os.close();
            osw.close();
            ois.close();
            oos.close();
            serverSocket.close();
            run = false;
            System.out.println("Server Closed Sucessfully!");
        }
        catch(Exception e){
            conPrint("Error: " + e.getMessage());
        }
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public boolean isRun() {
        return run;
    }
    
    public void setRun(boolean run) {
        this.run = run;
    }
    
    public void conPrint(String str){
        console.setText(console.getText() + str + "\n");
        console.setCaretPosition(console.getDocument().getLength());
    }
    
    @Override
    public void run() {
        while(run){
            try {
                tick();
                Thread.sleep(100);
                if(first)
                    first = false;
            } catch (InterruptedException ex) {
                conPrint(ex.getMessage());
            }
        }
    }
}
