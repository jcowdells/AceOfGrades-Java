package handlers;

import aog.Renderer;
import aog.Spotlight;
import core.Identifier;
import db.PackManager;
import db.SpotlightManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksViewHandler implements Handler {
    private final PackManager pack_manager;
    private final SpotlightManager spotlight_manager;

    public PacksViewHandler(PackManager pack_manager, SpotlightManager spotlight_manager) {
        this.pack_manager = pack_manager;
        this.spotlight_manager = spotlight_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        final Integer user_id = context.sessionAttribute("user_id");
        boolean is_creator = user_id != null && pack_manager.isPackCreator(pack_id.getID(), user_id);

        int num_cards = pack_manager.getNumCards(pack_id.getID());
        if (num_cards == -1) {
            Renderer.renderError(context, Identifier.noIDMessage("pack"));
            return;
        }

        // fetch spotlight data
        List<Spotlight> spotlights = spotlight_manager.getSpotlights(pack_id.getID());

        Map<String, Object> model = new HashMap<>();
        model.put("is_creator", is_creator);
        model.put("pack_id", pack_id.getID());
        model.put("num_cards", num_cards);
        model.put("spotlights", spotlights);
        model.put("pack_name", pack_manager.getPackName(pack_id.getID()));
        model.put("pack_description", pack_manager.getPackDescription(pack_id.getID()));
        Renderer.render(context, "/templates/card/pack_view.ftl", model);
    }
}
