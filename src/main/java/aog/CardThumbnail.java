package aog;

public class CardThumbnail {

    private final int id;
    private final String front;
    private final String front_color;

    public CardThumbnail(int id, String front, String front_color) {
        this.id = id;
        this.front = front;
        this.front_color = front_color;
    }

    public int getID() {
        return id;
    }

    public String getFront() {
        return front;
    }

    public String getFrontColor() {
        return front_color;
    }

}
