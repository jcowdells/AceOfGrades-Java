package api_handlers;

import auth.AogRole;
import db.UserManager;
import forms.RegisterForm;
import io.javalin.http.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterApiHandler implements Handler {
    private final UserManager user_manager;
    private final Pattern email_regex;

    public RegisterApiHandler(UserManager user_manager) {
        this.user_manager = user_manager;
        String email_pattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        email_regex = Pattern.compile(email_pattern);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final String username = context.formParam("username");
        final String email_address = context.formParam("email_address");
        final String password = context.formParam("password");
        final String password_repeat = context.formParam("password_repeat");

        RegisterForm register_form = new RegisterForm(
                username,
                email_address
        );

        // validate the input
        if (username == null || username.isEmpty()) {
            register_form.getUsername().addError("Username cannot be empty!");
        } else if (user_manager.hasUser(username)) {
            register_form.getUsername().addError("Username has been taken!");
        }

        if (email_address != null && !email_address.isEmpty() && !email_regex.matcher(email_address).matches()) {
            register_form.getEmailAddress().addError("Invalid email address!");
        }

        if (password == null || password.isEmpty()) {
            register_form.getPassword().addError("Password cannot be empty!");
        } else if (password_repeat == null || !password.contentEquals(password_repeat)) {
            register_form.getPasswordRepeat().addError("Passwords do not match!");
        }

        if (register_form.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("form", register_form);
            context.render("/common/forms/register.ftl", model);
            return;
        }

        user_manager.createUser(username, password, email_address, AogRole.USER);
        final int user_id = user_manager.getUserID(username);
        context.sessionAttribute("user_id", user_id);
        context.header("HX-Redirect", "/");
    }
}
