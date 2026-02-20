package handlers;

import aog.Renderer;
import forms.CreatePackForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreatePackHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("form", new CreatePackForm());
        Renderer.render(context, "/templates/create_pack.ftl", model);
    }
}
