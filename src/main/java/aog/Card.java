package aog;

import java.util.HashMap;
import java.util.Map;

public class Card {

    private int id;
    private String front;
    private String back;
    private String front_color;
    private String back_color;

    public Card(int id, String front, String back, String front_color, String back_color) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.front_color = front_color;
        this.back_color = back_color;
    }

    public int getID() {
        return id;
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
