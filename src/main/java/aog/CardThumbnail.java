package aog;

public class CardThumbnail {

    private final int id;
    private final String front;
    private final String front_color;
    private final boolean selected;
    private final String front_text_color;

    public CardThumbnail(int id, String front, String front_color, String text_color) {
        this.id = id;
        this.front = front;
        this.front_color = front_color;
        this.selected = false;
        this.front_text_color = text_color;
    }

    public CardThumbnail(int id, String front, String front_color, String text_color, boolean selected) {
        this.id = id;
        this.front = front;
        this.front_color = front_color;
        this.selected = selected;
        this.front_text_color = text_color;
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

    public String getFrontTextColor() {
        return front_text_color;
    }

}
