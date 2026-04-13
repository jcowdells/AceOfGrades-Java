package api_handlers;

import aog.Pack;
import aog.Renderer;
import db.PackManager;
import forms.PacksCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PacksCreateApiHandler implements Handler {
    private final PackManager pack_manager;
    private final Pattern color_regex;

    public PacksCreateApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
        final String color_pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        color_regex = Pattern.compile(color_pattern);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            return;
        }

        Pack pack = PacksEditApiHandler.getPackFormData(context, color_regex, "/common/forms/card/pack_create.ftl", -1);
        if (pack == null) {
            return;
        }

        int pack_id = pack_manager.createPack(
                user_id,
                pack.getName(), pack.getDescription(),
                pack.getFrontColor(), pack.getBackColor(),
                pack.isPublic()
        );
        if (pack_id == -1) {
            Renderer.renderHXError(context, "Failed to create pack.");
            return;
        }

        context.header("HX-Redirect", String.format("/packs/%d", pack_id));
    }
}
