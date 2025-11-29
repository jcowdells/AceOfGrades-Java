package auth;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class AogAccessHandler implements Handler {
    private AogRole getUserRole(@NotNull Context context) {
        // get user id from session cookie
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            return AogRole.ANYONE;
        }

        return AogRole.USER;
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
            throw new UnauthorizedResponse();
        }
    }
}
