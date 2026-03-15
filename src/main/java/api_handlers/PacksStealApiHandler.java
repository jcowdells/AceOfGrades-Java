package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacksStealApiHandler implements Handler {
    private final PackManager pack_manager;

    public PacksStealApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        // attempt to get user ID
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Could not get user ID!");
            return;
        }

        // attempt to get source pack id, ensures that it is public or owned by user
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            return;
        }

        // attempt to get destination pack id and check it can be added to
        int dest_id;
        String dest_id_str = context.formParam("dest_id");
        if (dest_id_str == null) {
            Renderer.renderHXError(context, Identifier.noIDMessage("destination pack"));
            return;
        }
        try {
            dest_id = Integer.parseInt(dest_id_str);
        } catch (NumberFormatException e) {
            Renderer.renderHXError(context, Identifier.notIntegerMessage("destination pack"));
            return;
        }
        if (!pack_manager.hasID(dest_id) || !pack_manager.isPackCreator(dest_id, user_id)) {
            Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("destination pack"));
            return;
        }

        // attempt to get card ids
        List<String> cards = context.formParams("cards");
        List<Integer> card_ids = new ArrayList<>();
        for (String card : cards) {
            try {
                Integer card_id = Integer.valueOf(card);
                card_ids.add(card_id);
            } catch (NumberFormatException e) {
                Renderer.renderHXError(context, "Could not understand card id.");
                return;
            }
        }

        // make sure that all the supplied cards belong to the pack we are stealing from
        for (Integer card_id : card_ids) {
            if (!pack_manager.containsCard(pack_id.getID(), card_id)) {
                Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("card"));
                return;
            }
        }

        // dont attempt to add cards that are already in the destination pack
        card_ids.removeIf(card_id -> {
            try {
                return pack_manager.containsCard(dest_id, card_id);
            } catch (SQLException e) {
                return true; // dont think this'll ever fail but this is the safest option
            }
        });

        // ok cool, now we can actually add the cards to the destination pack
        pack_manager.linkCards(dest_id, card_ids);
        context.header("HX-Redirect", String.format("/packs/%d", pack_id.getID()));
    }
}
