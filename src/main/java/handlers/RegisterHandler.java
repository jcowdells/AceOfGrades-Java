package handlers;

import aog.Renderer;
import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Renderer.render(context, "/templates/register.ftl", new HashMap<>());
    }
}
