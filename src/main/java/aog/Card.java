package aog;

import java.util.HashMap;
import java.util.Map;

public class Card {

    private final int id;
    private final String front;
    private final String back;
    private final String front_color;
    private final String back_color;
    private final int creator_id;

    public Card(int id, String front, String back, String front_color, String back_color, int creator_id) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.front_color = front_color;
        this.back_color = back_color;
        this.creator_id = creator_id;
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

    public int getCreatorID() {
        return creator_id;
    }

}
