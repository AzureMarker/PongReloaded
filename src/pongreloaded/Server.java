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
    char[] com = new char[3];
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
    InputStreamReader isr;
    BufferedOutputStream os;
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
        TimeStamp = new java.util.Date().toString();
        try{
            if(first){
                connection = serverSocket.accept();
                isClientConnected = true;
                is = new BufferedInputStream(connection.getInputStream());
                isr = new InputStreamReader(is);
                process = new StringBuffer();
                os = new BufferedOutputStream(connection.getOutputStream());
                osw = new OutputStreamWriter(os, "US-ASCII");
                conPrint(TimeStamp + " [" + connection.getInetAddress().getHostAddress() + "] Client successfully connected");
            }
            readFromClient();
            sendToClient();
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
    
    public void sendToClient(){
        try{
            osw.write(gVS.hostX);
            osw.write(gVS.hostY);
            osw.write(gVS.hostScore);
            osw.flush();
        }
        catch(IOException ioe){
            
        }
    }
    
    /*public String readStringFromClient(){
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
    }*/
    
    public void readFromClient(){
        TimeStamp = new java.util.Date().toString();
        try {
            isr.read(com);
            gVS.multiX = com[0];
            gVS.multiY = com[1];
            gVS.multiScore = com[2];
        } catch (IOException ex) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
        conPrint(TimeStamp + " [" + connection.getInetAddress().getHostAddress() + "] Recieved GameVars");
        conPrint(""+gVS.hostScore + "\n"+gVS.multiScore + "\n"+gVS.hostX + "\n"+gVS.hostY + "\n"+gVS.multiX + "\n"+gVS.multiY);
    }
    
    public void closeServer(){
        try{
            System.out.println("Closing Server...");
            dispose();
            is.close();
            isr.close();
            os.close();
            osw.close();
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
