package aog;

public class CardThumbnail {

    private final int id;
    private final String front;
    private final String front_color;
    private final boolean selected;

    public CardThumbnail(int id, String front, String front_color) {
        this.id = id;
        this.front = front;
        this.front_color = front_color;
        this.selected = false;
    }

    public CardThumbnail(int id, String front, String front_color, boolean selected) {
        this.id = id;
        this.front = front;
        this.front_color = front_color;
        this.selected = selected;
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

    public boolean getSelected() { return selected; }

}
