package handlers;

import aog.User;
import auth.AogRole;
import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class BaseHandler implements Handler {
    private final UserManager user_manager;

    public BaseHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        User user;
        if (user_id == null) {
            user = User.anyone();
        } else {
            user = user_manager.getUserData(user_id);
        }
        context.attribute("user", user);
    }
}
