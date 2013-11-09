package pongreloaded;

/**
 * @author Mcat12
 */
public class MsgHandler {
    MultiplayerGame MGscreen;
    
    public MsgHandler(MultiplayerGame mg) {
    	MGscreen = mg;
    }
    
    public void getVariables() {
    	if(MGscreen.playerNum == 0) {
    		MGscreen.bClient.p2.x = MGscreen.arrayXY[0];
    		MGscreen.bClient.p2.y = MGscreen.arrayXY[1];
    		MGscreen.bClient.p2Score = MGscreen.arrayXY[2];
        	System.out.println(
            		"p2 x: "+MGscreen.arrayXY[0] + "\n" +
            		"p2 y: "+MGscreen.arrayXY[1] + "\n" +
            		"p2Score: "+MGscreen.arrayXY[2]);
    	}
    	else if(MGscreen.playerNum == 1) {
    		MGscreen.bClient.p1.x = MGscreen.arrayXY[0];
    		MGscreen.bClient.p1.y = MGscreen.arrayXY[1];
    		MGscreen.bClient.x = MGscreen.arrayXY[2];
    		MGscreen.bClient.y = MGscreen.arrayXY[3];
    		MGscreen.bClient.p1Score = MGscreen.arrayXY[4];
            System.out.println(
            		"p1 x: "+MGscreen.arrayXY[0] + "\n" +
            		"p1 y: "+MGscreen.arrayXY[1] + "\n" +
            		"b x: "+MGscreen.arrayXY[2] + "\n" +
            		"b y: "+MGscreen.arrayXY[3] + "\n" +
            		"p1Score: "+MGscreen.arrayXY[4]);
    	}
    	else {
        	System.out.println("Invalid Player number: " + MGscreen.playerNum);
        	System.out.println("Closing connection...");
        	MGscreen.closeConnection();
        }
    }
    
    public void getNBVariables() {
    	MGscreen.bClient.winScore = MGscreen.arrayXY[0];
    	MGscreen.otherPlayerNum = MGscreen.arrayXY[1];
        System.out.println(
        		"winScore: "+MGscreen.arrayXY[0] + "\n" +
        		"Other Player Num: " + MGscreen.arrayXY[1]);
    }
}