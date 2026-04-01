package auth;

import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class AogAccessHandler implements Handler {
    private final UserManager user_manager;

    public AogAccessHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    private AogRole getUserRole(@NotNull Context context) {
        // get user id from session cookie
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            return AogRole.ANYONE;
        }

        try {
            return user_manager.getUserData(user_id).getRole();
        } catch (SQLException e) {
            return AogRole.ANYONE;
        }
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        // static files should not be access controlled
        if (context.path().startsWith("/static")) {
            return;
        }

        // if no roles are set, assume public
        if (context.routeRoles().isEmpty()) {
            return;
        }

        // otherwise get the user role and make sure it is allowed
        AogRole role = getUserRole(context);
        if (!context.routeRoles().contains(role)) {
            context.header("HX-Redirect", "/login");
            context.header("WWW-Authenticate", "Try WWW Authenticate This, Bro");
            context.status(401);
            context.redirect("/login");
        }
    }
}
