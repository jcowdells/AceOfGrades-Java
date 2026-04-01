package forms;

public class SpotlightCreateForm implements BaseForm {
    private final FormEntry<String> name;

    public SpotlightCreateForm() {
        name = new FormEntry<>("");
    }

    public SpotlightCreateForm(String name) {
        this.name = new FormEntry<>(name);
    }

    public FormEntry<String> getName() {
        return name;
    }

    @Override
    public boolean hasErrors() {
        return name.hasErrors();
    }
}
