package pongreloaded;

/**
 * @author Mcat12
 */
public class Winner implements Comparable<Winner>{
	private String name;
	private int score;
	
	public Winner() {}
	
	public Winner(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public int compareTo(Winner other) {
		if(other.getScore() > this.getScore())
			return -1;
		if(other.getScore() < this.getScore())
			return 1;
		return 0;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public String toString() {
		return "Name: " + name +
			   " Score: " + score;
	}
}
