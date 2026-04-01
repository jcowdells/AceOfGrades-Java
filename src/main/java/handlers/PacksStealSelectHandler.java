package handlers;

import aog.Card;
import aog.CardThumbnail;
import aog.Pack;
import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PacksStealSelectHandler implements Handler {
    private final PackManager pack_manager;

    public PacksStealSelectHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Could not get user ID!");
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

        List<Pack> packs = pack_manager.getUserCreatedPacks(user_id);
        // dont show the option to steal a pack to itself, but will defend more rigorously elsewhere anyway.
        packs.removeIf(pack -> pack.getID() == pack_id.getID());
        packs.sort(Comparator.comparing(p -> p.getName().toLowerCase() + p.getDescription().toLowerCase()));
        Map<String, Object> model = new HashMap<>();
        model.put("pack_id", pack_id.getID());
        model.put("packs", packs);
        Renderer.render(context,"/templates/card/card_steal_select.ftl", model);
    }
}
