package aog;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Renderer {
    public static void render(@NotNull Context context, String template, Map<String, Object> model) {
        User user = context.attribute("user");
        model.put("user", user);
        context.render(template, model);
    }
}
