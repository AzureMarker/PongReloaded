package pongreloaded;

import java.awt.*;

class Button {
    private int tx, ty;
    private boolean hover, center;
    private Rectangle rec;
    private String text;
    private Font font;
    
    Button(int x, int y, int width, int height, String text) {
        this(x, y, width, height, text, new Font("Arial", Font.BOLD, 12));
    }
    
    private Button(int x, int y, int width, int height, String text, Font font) {
        rec = new Rectangle(x, y, width, height);
        setHover(false);
        setCenter(true);
        setText(text);
        setFont(font);
    }
    
    void draw(Graphics g) {
        // Center Text
        if(center) {
            centerText(g);
            setCenter(false);
        }

        if(!hover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(rec.x, rec.y, rec.width, rec.height);
        g.setFont(font);
        g.setColor(Color.GRAY);
        g.drawString(getText(), getTextX(), getTextY());
    }
    
    private void centerText(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int totalWidth = fm.stringWidth(text);
        int totalHeight = fm.getHeight();
        tx = ((rec.width - totalWidth) / 2) + rec.x;
        ty = ((rec.height - totalHeight) / 2) + rec.y + totalHeight - fm.getDescent();
    }
    
    void adjustHover(int x, int y) {
        if(intersects(x, y))
            setHover(true);
        else
            setHover(false);
    }
    
    boolean intersects(int x, int y) {
        return (x > rec.x && x < rec.x+rec.width && y > rec.y && y < rec.y+rec.height);
    }
    
    void setText(String text) {
        this.text = text;
        center = true;
    }
    
    private String getText() {
        return text;
    }
    
    public void setTextXY(int tx, int ty) {
        this.tx = tx;
        this.ty = ty;
    }
    
    private int getTextX() {
        return tx;
    }
    
    private int getTextY() {
        return ty;
    }
    
    public void setRectangle(Rectangle rec) {
        this.rec = rec;
        center = true;
    }
    
    public Rectangle getRectangle() {
        return rec;
    }
    
    private void setHover(boolean hover) {
        this.hover = hover;
    }
    
    public boolean getHover() {
        return hover;
    }
    
    private void setCenter(boolean center) {
        this.center = center;
    }
    
    public boolean getCenter() {
        return center;
    }
    
    private void setFont(Font font) {
        this.font = font;
    }
    
    public Font getFont() {
        return font;
    }
    
    public String toString() {
        return "Text: " + text + "\n" +
               "x: " + rec.x + "\n" +
               "y: " + rec.y + "\n";
    }
}
