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
    private final String front_text_color;
    private final String back_text_color;

    private static int getIntFromHexChar(Character hex_char) {
        if (('0' <= hex_char) && ('9' >= hex_char)) {
            return hex_char - '0';
        } else if (('A' <= hex_char) && ('F' >= hex_char)) {
            return hex_char - 'A' + 10;
        } else if (('a' <= hex_char) && ('f' >= hex_char)) {
            return hex_char - 'a' + 10;
        } else {
            return 0;
        }
    }

    private static int getIntFromHex(String hex) {
        return getIntFromHexChar(hex.charAt(0)) * 16 + getIntFromHexChar(hex.charAt(1));
    }

    public static float[] getRGBFromHex(String hex_color) {
        String red = hex_color.substring(1, 3);
        String green = hex_color.substring(3, 5);
        String blue = hex_color.substring(5, 7);
        return new float[] {
                (float)getIntFromHex(red) / 255.0f,
                (float)getIntFromHex(green) / 255.0f,
                (float)getIntFromHex(blue) / 255.0f
        };
    }

    public static String getOptimalTextColor(String color) {
        float[] color_array = getRGBFromHex(color);
        float luminance = 0.299f * color_array[0] + 0.587f * color_array[1] + 0.114f * color_array[2];
        if (luminance > 0.5f) {
            return "#000000";
        } else {
            return "#FFFFFF";
        }
    }

    public Card(int id, String front, String back, String front_color, String back_color, int creator_id) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.front_color = front_color;
        this.back_color = back_color;
        this.front_text_color = getOptimalTextColor(front_color);
        this.back_text_color = getOptimalTextColor(back_color);
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

    public String getFrontTextColor() {
        return front_text_color;
    }

    public String getBackTextColor() {
        return back_text_color;
    }

}
