package forms;

public class PacksCreateForm implements BaseForm {

    private final FormEntry<String> name;
    private final FormEntry<String> description;
    private final FormEntry<String> front_color;
    private final FormEntry<String> back_color;
    private final FormEntry<Boolean> is_public;

    public PacksCreateForm() {
        name = new FormEntry<>("");
        description = new FormEntry<>("");
        front_color = new FormEntry<>("#FFFFFF");
        back_color = new FormEntry<>("#FFFFFF");
        is_public = new FormEntry<>(false);
    }

    public PacksCreateForm(String name, String description, String front_color, String back_color, boolean is_public) {
        this.name = new FormEntry<>(name);
        this.description = new FormEntry<>(description);
        this.front_color = new FormEntry<>(front_color);
        this.back_color = new FormEntry<>(back_color);
        this.is_public = new FormEntry<>(is_public);
    }

    @Override
    public boolean hasErrors() {
        return name.hasErrors() | description.hasErrors() | front_color.hasErrors() | back_color.hasErrors() | is_public.hasErrors();
    }

    public FormEntry<String> getName() {
        return name;
    }

    public FormEntry<String> getDescription() {
        return description;
    }

    public FormEntry<String> getFrontColor() {
        return front_color;
    }

    public FormEntry<String> getBackColor() {
        return back_color;
    }

    public FormEntry<Boolean> isPublic() {
        return is_public;
    }
}