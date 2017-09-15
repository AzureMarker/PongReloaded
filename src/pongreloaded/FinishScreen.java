package pongreloaded;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

/**
 * @author Mcat12
 */
public class FinishScreen implements Screen {
    private Button mainMenuButton = new Button(100, 275, 200, 25, "Main Menu");
    private Dimension screenSize;
    private int winID;
    private ArrayList<Winner> winners;
    private Winner winner;

    FinishScreen(Dimension screenSize, int winID, Winner winner) {
        this.screenSize = screenSize;
        this.winID = winID;
        this.winner = winner;
        System.out.println("Player " + winID + " Won!");
        setupLeaderboard();
    }

    /**
     * Sets up the winners ArrayList
     */
    private void setupLeaderboard() {
        getWinners();
        Collections.sort(winners);
        Collections.reverse(winners);
        if(winners.size() > 10) {
            while(winners.size() > 10) {
                winners.remove(winners.size()-1);
            }
        }
        try {
            saveConfig();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file and writes to the winners ArrayList
     */
    private void getWinners() {
        winners = new ArrayList<>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream("leaderboard.xml");
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            Winner item = new Winner("", 0);

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()) {
                    if(event.asStartElement().getName().getLocalPart().equals("name")) {
                        event = eventReader.nextEvent();
                        item.setName(event.asCharacters().getData());
                        continue;
                    }
                }
                if(event.isStartElement()) {
                    if(event.asStartElement().getName().getLocalPart().equals("score")) {
                        event = eventReader.nextEvent();
                        item.setScore(Integer.parseInt(event.asCharacters()
                                .getData()));
                        continue;
                    }
                }
                if(event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if(endElement.getName().getLocalPart().equals("winner")) {
                        winners.add(item);
                        item = new Winner("", 0);
                    }
                }
            }
        }
        catch(FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        winners.add(winner);
    }

    /**
     * Saves the file from winners ArrayList
     */
    private void saveConfig() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream("leaderboard.xml"));
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);
        eventWriter.add(end);
        eventWriter.add(eventFactory.createStartElement("", "", "config"));
        eventWriter.add(end);

        for(Winner w : winners) {
            createNode(eventWriter, "winner", w);
        }

        eventWriter.add(eventFactory.createEndElement("", "", "config"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
    }

    /**
     * Creates a new node
     */
    private void createNode(XMLEventWriter eventWriter, String name, Winner value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(end);

        eventWriter.add(tab);
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", "name"));
        eventWriter.add(eventFactory.createCharacters(value.getName()));
        eventWriter.add(eventFactory.createEndElement("", "", "name"));
        eventWriter.add(end);

        eventWriter.add(tab);
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", "score"));
        eventWriter.add(eventFactory.createCharacters("" + value.getScore()));
        eventWriter.add(eventFactory.createEndElement("", "", "score"));
        eventWriter.add(end);

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(end);
    }

    public void displayOutput(Graphics g) {
        // Finish Menu
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Player " + winID + " Won the Game!", 95, 75);
        
        // Button
        mainMenuButton.draw(g);
        
        // Leaderboard
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(100, 100, 200, 150);
        g.setColor(Color.CYAN);
        for(int i = 0; i < winners.size(); i++) {
            if(i % 2 == 0) {
                g.drawString(winners.get(i).getName(), 105, (i*16)+115);
                g.drawString(""+winners.get(i).getScore(), 170, (i*16)+115);
            }
            else {
                g.drawString(winners.get(i).getName(), 200, (i*16)+99);
                g.drawString(""+winners.get(i).getScore(), 270, (i*16)+99);
            }
        }
    }

    /**
     * Shows a grid with 50x50 blocks
     */
    public void displayGrid(Graphics g) {
        g.setColor(Color.GRAY);
        for(int i = 0; i < screenSize.width; i = i + 50) {
            g.drawLine(i, 0, i, screenSize.height);
        }
        for(int i = 0; i < screenSize.height; i = i + 50) {
            g.drawLine(0, i, screenSize.width, i);
        }
    }

    public Screens getScreenType() {
        return Screens.FINSIH;
    }

    public Screen respondToUserInput(KeyEvent key) {
        return this;
    }

    public Screen respondToUserInputReleased(KeyEvent key) {
        return this;
    }

    public Screen respondToUserInputHover(MouseEvent mouse) {
        int mx = mouse.getX();
        int my = mouse.getY();

        mainMenuButton.adjustHover(mx, my);

        return this;
    }

    public Screen respondToUserInputClick(MouseEvent mouse) {
        int mx = mouse.getX();
        int my = mouse.getY();

        if(mainMenuButton.intersects(mx, my))
            return new MainMenu(screenSize);
        
        return this;
    }

    public Screen respondToUserInput(MouseWheelEvent mouse) {
        return this;
    }

    public Screen windowClosingEvent(WindowEvent window) {
        return this;
    }
}
