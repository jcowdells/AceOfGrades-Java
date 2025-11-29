package forms;

public class RegisterForm implements BaseForm {

    private final FormEntry<String> username;
    private final FormEntry<String> email_address;
    private final FormEntry<String> password;
    private final FormEntry<String> password_repeat;

    public RegisterForm() {
        username = new FormEntry<>("");
        email_address = new FormEntry<>("");
        password = new FormEntry<>("");
        password_repeat = new FormEntry<>("");
    }

    public RegisterForm(String username, String email_address) {
        this.username = new FormEntry<>(username);
        this.email_address = new FormEntry<>(email_address);
        password = new FormEntry<>("");
        password_repeat = new FormEntry<>("");
    }

    @Override
    public boolean hasErrors() {
        return username.hasErrors() || email_address.hasErrors() || password.hasErrors() || password_repeat.hasErrors();
    }

    public FormEntry<String> getUsername() {
        return username;
    }

    public FormEntry<String> getEmailAddress() {
        return email_address;
    }

    public FormEntry<String> getPassword() {
        return password;
    }

    public FormEntry<String> getPasswordRepeat() {
        return password_repeat;
    }

}
