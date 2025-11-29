package handlers;

import aog.Renderer;

import forms.RegisterForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("form", new RegisterForm());
        Renderer.render(context, "/templates/register.ftl", model);
    }
}
