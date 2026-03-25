package handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PacksQuizHandler implements Handler {

    private final PackManager pack_manager;

    public PacksQuizHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        // get pack id from url
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        if (!pack_manager.isPublic(pack_id.getID())) {
            // get user id
            final Integer user_id = context.sessionAttribute("user_id");
            if (user_id == null || !pack_manager.isPackCreator(pack_id.getID(), user_id)) {
                Renderer.renderError(context, "Failed to get user id!");
                return;
            }
        }

        // html bootstrapping for javascript needs the pack id to get cards from api
        Map<String, Object> model = new HashMap<>();
        model.put("pack_id", pack_id.getID());
        Renderer.render(context, "/templates/card/card_quiz.ftl", model);
    }
}
