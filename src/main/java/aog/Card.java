package aog;

public class Card {

    private String front;
    private String back;
    private String front_color;
    private String back_color;

    public Card(String front, String back, String front_color, String back_color) {
        this.front = front;
        this.back = back;
        this.front_color = front_color;
        this.back_color = back_color;
    }

    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

    public String getFrontColor() {
        return front_color;
    }

    public String getBackColor() {
        return back_color;
    }

}
