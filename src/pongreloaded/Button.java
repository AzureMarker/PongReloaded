package pongreloaded;

import java.awt.*;

public class Button {
	private Rectangle rec;
    private String text;
    private int tx, ty;
    private boolean hover;
    private boolean center;
    private Dimension screenSize;
    
    public Button(int x, int y, int width, int height, String text, Dimension screenSize) {
        rec = new Rectangle(x, y, width, height);
        setText(text);
        hover = false;
        center = false;
        this.screenSize = screenSize;
    }
    
    public void draw(Graphics g) {
    	// Center Text
    	if(center) {
    		centerText(g);
    		center = false;
    	}
    	
    	if(!hover)
    		g.setColor(Color.CYAN);
    	else
    		g.setColor(Color.PINK);
        g.fillRect(rec.x, rec.y, rec.width, rec.height);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.GRAY);
        g.drawString(text, tx, ty);
    }
    
    private void centerText(Graphics g) {
    	FontMetrics fm = g.getFontMetrics();
    	int totalWidth = (fm.stringWidth(text) * 2) + 4;
    	tx = (int) ((screenSize.width - totalWidth) / 2) + fm.stringWidth(text) + 2;
        ty = (int) ((screenSize.height - fm.getHeight()) / 2) + fm.getAscent();
    }
    
    public void adjustHover(int x, int y) {
    	if(intersects(x, y))
    		setHover(true);
    	else
    		setHover(false);
    }
    
    public boolean intersects(int x, int y) {
        return (x > rec.x && x < rec.x+rec.width && y > rec.y && y < rec.y+rec.height);
    }
    
    public void setText(String text) {
        this.text = text;
        center = true;
    }
    
    public String getText() {
        return text;
    }
    
    public void setRectangle(Rectangle rec) {
        this.rec = rec;
        center = true;
    }
    
    public Rectangle getRectangle() {
        return rec;
    }
    
    public void setHover(boolean hover) {
    	this.hover = hover;
    }
    
    public boolean getHover() {
    	return hover;
    }
    
    public String toString() {
        return "Text: " + text + "\n" +
               "x: " + rec.x + "\n" +
               "y: " + rec.y + "\n";
    }
}
