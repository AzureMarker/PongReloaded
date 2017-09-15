package pongreloaded;

/**
 * @author Mcat12
 */
class Winner implements Comparable<Winner>{
    private String name;
    private int score;

    Winner(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int compareTo(Winner other) {
        return Integer.compare(this.getScore(), other.getScore());
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }

    public String toString() {
        return "Name: " + name + " Score: " + score;
    }
}
