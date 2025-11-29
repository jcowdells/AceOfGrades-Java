package api_handlers;

import db.UserManager;
import forms.LoginForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LoginApiHandler implements Handler {

    private final UserManager user_manager;

    public LoginApiHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String username = context.formParam("username");
        String password = context.formParam("password");

        LoginForm login_form = new LoginForm(username);

        if (username == null || username.isEmpty()) {
            login_form.getUsername().addError("Username cannot be empty!");
        } else if (!user_manager.hasUser(username)) {
            login_form.getUsername().addError("Username not associated with an account!");
        }

        final int user_id = user_manager.getUserID(username);

        boolean valid_password = false;
        if (password == null || password.isEmpty()) {
            login_form.getPassword().addError("Password cannot be empty!");
        } else {
            valid_password = user_manager.checkPassword(user_id, password);
            if (!valid_password) {
                login_form.getPassword().addError("Incorrect password");
            }
        }

        // this should return if the password is wrong
        if (login_form.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("form", login_form);
            context.render("/common/forms/login.ftl", model);
            return;
        }

        // extra safety net
        if (!valid_password) {
            throw new UnauthorizedResponse();
        }

        // if all is well, we can log the user in.
        context.sessionAttribute("user_id", user_id);
        context.header("HX-Redirect", "/");
    }

}
