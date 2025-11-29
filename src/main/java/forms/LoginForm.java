package forms;

public class LoginForm implements BaseForm {

    private final FormEntry<String> username;
    private final FormEntry<String> password;

    public LoginForm() {
        username = new FormEntry<>("");
        password = new FormEntry<>("");
    }

    public LoginForm(String username) {
        this.username = new FormEntry<>(username);
        password = new FormEntry<>("");
    }

    @Override
    public boolean hasErrors() {
        return username.hasErrors() || password.hasErrors();
    }

    public FormEntry<String> getUsername() {
        return username;
    }

    public FormEntry<String> getPassword() {
        return password;
    }

}
