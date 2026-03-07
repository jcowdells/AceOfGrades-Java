package forms;

public class CardsCreateForm implements BaseForm {

    private final FormEntry<String> front;
    private final FormEntry<String> back;
    private final FormEntry<String> front_color;
    private final FormEntry<String> back_color;

    public CardsCreateForm() {
        front = new FormEntry<>("");
        back = new FormEntry<>("");
        front_color = new FormEntry<>("");
        back_color = new FormEntry<>("");
    }

    public CardsCreateForm(String front_color, String back_color) {
        front = new FormEntry<>("");
        back = new FormEntry<>("");
        this.front_color = new FormEntry<>(front_color);
        this.back_color = new FormEntry<>(back_color);
    }

    public CardsCreateForm(String front, String back, String front_color, String back_color) {
        this.front = new FormEntry<>(front);
        this.back = new FormEntry<>(back);
        this.front_color = new FormEntry<>(front_color);
        this.back_color = new FormEntry<>(back_color);
    }

    @Override
    public boolean hasErrors() {
        return front.hasErrors() || back.hasErrors() || front_color.hasErrors() || back_color.hasErrors();
    }

    public FormEntry<String> getFront() {
        return front;
    }

    public FormEntry<String> getBack() {
        return back;
    }

    public FormEntry<String> getFrontColor() {
        return front_color;
    }

    public FormEntry<String> getBackColor() {
        return back_color;
    }
}
