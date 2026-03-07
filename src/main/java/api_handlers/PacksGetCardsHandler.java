package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PacksGetCardsHandler implements Handler {
    private final PackManager pack_manager;

    public PacksGetCardsHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            context.status(401);
            return;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            context.status(404);
            return;
        }

        List<Map<String, Object>> card_list = pack_manager.getPackCards(pack_id.getID());
        context.json(card_list);
    }
}
