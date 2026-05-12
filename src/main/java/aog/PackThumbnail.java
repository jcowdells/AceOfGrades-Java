package aog;

public class PackThumbnail {
    private final int id;
    private final String name;
    private final String description;
    private final String front_color;
    private final String back_color;
    private final String creator;
    private final String front_text_color;
    private final String back_text_color;

    public PackThumbnail(int id, String name, String description, String front_color, String back_color, String creator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.front_color = front_color;
        this.back_color = back_color;
        this.front_text_color = Card.getOptimalTextColor(front_color);
        this.back_text_color = Card.getOptimalTextColor(back_color);
        this.creator = creator;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFrontColor() {
        return front_color;
    }

    public String getBackColor() {
        return back_color;
    }

    public String getCreator() {
        return creator;
    }

    public String getFrontTextColor() {
        return front_text_color;
    }

    public String getBackTextColor() {
        return back_text_color;
    }
}
