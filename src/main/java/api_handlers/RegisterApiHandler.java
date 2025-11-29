package api_handlers;

import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import org.jetbrains.annotations.NotNull;

public class RegisterApiHandler implements Handler {
    private final UserManager user_manager;

    public RegisterApiHandler(UserManager user_manager) {
        this.user_manager = user_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        System.out.println(context.formParam("username"));
        System.out.println(context.formParam("email-address"));
        System.out.println(context.formParamMap());
    }
}
