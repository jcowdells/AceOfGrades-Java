package aog;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Renderer {
    public static void render(@NotNull Context context, String template, Map<String, Object> model) {
        User user = context.attribute("user");
        model.put("user", user);
        context.render(template, model);
    }

    public static void renderError(@NotNull Context context, String error_message) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", error_message);
        Renderer.render(context, "/templates/error.ftl", model);
    }

    public static void renderHXError(@NotNull Context context, String error_message) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", error_message);
        Renderer.render(context, "/common/error.ftl", model);
    }

    public static void renderJsonError(@NotNull Context context, int status, String error, String reason) {
        context.status(status);
        context.contentType(ContentType.JSON);
        Map<String, String> error_json = new HashMap<>();
        error_json.put("error", error);
        error_json.put("reason", reason);
        context.json(error_json);
    }
}
