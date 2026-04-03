package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.SpotlightManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class SpotlightsDeleteApiHandler implements Handler {
    private final SpotlightManager spotlight_manager;

    public SpotlightsDeleteApiHandler(SpotlightManager spotlight_manager) {
        this.spotlight_manager = spotlight_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderJsonError(context, 401, "No credentials", "Could not identify user.");
            context.header("WWW-Authenticate", "Try WWW Authenticate This, Bro");
            return;
        }

        Identifier spotlight_id = new Identifier(
                context, spotlight_manager,
                "spotlight_id", "spotlight"
        );
        if (spotlight_id.hasFailed()) {
            Renderer.renderJsonError(context, 404, "Not found", "No such spotlight.");
            return;
        }

        if (!spotlight_manager.canEditSpotlight(spotlight_id.getID(), user_id)) {
            Renderer.renderJsonError(context, 404, "Not found", "No such spotlight.");
            return;
        }

        spotlight_manager.deleteSpotlight(spotlight_id.getID());
        context.status(200);
    }
}
