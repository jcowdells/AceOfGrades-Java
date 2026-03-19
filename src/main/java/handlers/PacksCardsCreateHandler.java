package handlers;

import aog.Renderer;
import core.Identifier;
import core.Pair;
import db.PackManager;
import forms.CardsCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PacksCardsCreateHandler implements Handler {
    private final PackManager pack_manager;

    public PacksCardsCreateHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Failed to get user id!");
            return;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        Pair<String, String> pack_colors = pack_manager.getPackColor(pack_id.getID());
        Map<String, Object> model = new HashMap<>();
        model.put("form", new CardsCreateForm(pack_colors.getA(), pack_colors.getB()));
        model.put("pack_id", pack_id.getID());
        String pack_name = pack_manager.getPackName(pack_id.getID());
        String pack_description = pack_manager.getPackDescription(pack_id.getID());
        model.put("pack_name", pack_name);
        model.put("pack_description", pack_description);
        Renderer.render(context, "/templates/card/card_create.ftl", model);
    }

}