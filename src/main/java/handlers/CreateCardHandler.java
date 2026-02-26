package handlers;

import aog.Renderer;
import api_handlers.CreateCardApiHandler;
import core.Pair;
import db.PackManager;
import forms.CreateCardForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreateCardHandler implements Handler {
    private final PackManager pack_manager;

    public CreateCardHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Pair<String, Integer> pack_id = CreateCardApiHandler.getPackID(pack_manager, context.queryParam("pack"));
        if (pack_id.getB() == null) {
            Renderer.renderError(context, pack_id.getA());
            return;
        }

        Pair<String, String> pack_colors = pack_manager.getPackColor(pack_id.getB());
        Map<String, Object> model = new HashMap<>();
        model.put("form", new CreateCardForm(pack_colors.getA(), pack_colors.getB()));
        model.put("pack_id", pack_id.getB());
        Renderer.render(context, "/templates/create_card.ftl", model);
    }

}