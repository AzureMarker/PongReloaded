package pongreloaded;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Mcat12
 */
public class Pong extends JFrame implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener  {
	private static final long serialVersionUID = -6694074482949248838L;
	// Double Buffering
    Image dbImage;
    Graphics dbg;
    
    // Multiplayer Text Fields
    static JTextField ipText = new JTextField() {
		private static final long serialVersionUID = -8131047528678819183L;
		
		@Override
        public void setBorder(Border border) {
        }
    };
    static JTextField hostPortText = new JTextField() {
		private static final long serialVersionUID = 1627939752505205466L;
		
		@Override
        public void setBorder(Border border) {
        }
    };
    static JTextField connectPortText = new JTextField() {
		private static final long serialVersionUID = -9217986345323034088L;

		@Override
        public void setBorder(Border border) {
        }
    };
    
    // Game
    @SuppressWarnings("unused")
	private static Pong p;
    static Screen screen;
    Screens screens;
    
    // FPS
    private long now;
    private long framesTimer = 0;
    @SuppressWarnings("unused")
	private long beforeTime;
    private int framesCount = 0;
    private int framesCountAvg = 0;
    
    // Flags
    static boolean quit = false;
    static boolean retro = false;
    static boolean disposeMainMenu = false;
    static boolean isFinished = false;
    static boolean showFPS = false;
    static int winID;
    
    // Variables for Screen Size
    int GWIDTH = 400, GHEIGHT = 300;
    
    // Dimension of GWIDTH * GHEIGHT
    Dimension screenSize = new Dimension(GWIDTH, GHEIGHT);
    
    // Create Constructor to Spawn Window
    public Pong(String[] args) {
        this.setTitle("Pong Reloaded");
        this.setSize(screenSize);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(this);
        this.getContentPane().setBackground(Color.DARK_GRAY);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
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
        for(String s : args)
    		System.out.println("Arguments: " + s);
        if(args.length > 0) {
        	if(args[0].equals("-fps")) {
        		System.out.println("FPS mode activated");
        		showFPS = true;
        	}
        }
        screen = new MainMenu(screenSize);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        p = new Pong(args);
    }
    
    public void keyPressed(KeyEvent key) {
    	screen = screen.respondToUserInput(key);
    }
    
    public void keyReleased(KeyEvent key) {
    	screen = screen.respondToUserInputReleased(key);
    }
    
    public void keyTyped(KeyEvent key) { }
    
    public void mousePressed(MouseEvent mouse) {
    	screen = screen.respondToUserInputClick(mouse);
    }
    
    public void mouseMoved(MouseEvent mouse) {
    	screen = screen.respondToUserInputHover(mouse);
    }
    
    public void mouseWheelMoved(MouseWheelEvent mouseWheel) {
    	screen = screen.respondToUserInput(mouseWheel);
    }
    
    @Override
	public void windowClosing(WindowEvent window) {
		screen = screen.windowClosingEvent(window);
	}
	
    @Override
    public void paint(Graphics g) {
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        draw(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }
    
    public void draw(Graphics g) {
        super.paint(g);
        screen.displayOutput(g);
        evaluateFlags(g);
        repaint();
    }
    
    public void evaluateFlags(Graphics g) {
    	if(screen.getScreenType() == Screens.MULTIMENU) {
        	ipText.setVisible(true);
        	connectPortText.setVisible(true);
        	hostPortText.setVisible(true);
        }
        else {
        	ipText.setVisible(false);
        	connectPortText.setVisible(false);
        	hostPortText.setVisible(false);
        }
        
        if(quit == true)
        	dispose();
        
        if(retro == true)
        	this.getContentPane().setBackground(Color.BLACK);
        else
        	this.getContentPane().setBackground(Color.DARK_GRAY);
        
        if(isFinished == true) {
        	screen = new FinishScreen(screenSize, winID);
        	isFinished = false;
        }
        
        if(showFPS == true) {
        	beforeTime = System.nanoTime();
        	now = System.currentTimeMillis();
        	g.drawString(""+framesCountAvg, 190, 280);
        	framesCount++;
        	if(now - framesTimer > 1000) {
        		framesTimer = now;
        		framesCountAvg = framesCount;
        		framesCount = 0;
        	}
        }
    }
    
    public void mouseClicked(MouseEvent mouse) {}
    public void mouseDragged(MouseEvent mouse) {}
    public void mouseReleased(MouseEvent mouse) {}
    public void mouseEntered(MouseEvent mouse) {}
    public void mouseExited(MouseEvent mouse) {}
	public void windowActivated(WindowEvent window) {}
	public void windowClosed(WindowEvent window) {}
	public void windowDeactivated(WindowEvent window) {}
	public void windowDeiconified(WindowEvent window) {}
	public void windowIconified(WindowEvent window) {}
	public void windowOpened(WindowEvent window) {}
}