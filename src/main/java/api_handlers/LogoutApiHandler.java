package api_handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LogoutApiHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.req().getSession().invalidate();
        context.redirect("/");
    }

}
