package handlers;

import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LoginHandler implements Handler {
    private final UserManager user_manager;

    public LoginHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {

    }
}
