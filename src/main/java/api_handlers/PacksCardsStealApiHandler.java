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

public class PacksCardsStealApiHandler implements Handler {
    private final PackManager pack_manager;

    public PacksCardsStealApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    public static int getDestID(@NotNull Context context, PackManager pack_manager, int user_id, int pack_id) throws SQLException {
        // attempt to get destination pack id and check it can be added to
        int dest_id;
        String dest_id_str = context.formParam("dest_id");
        if (dest_id_str == null) {
            Renderer.renderHXError(context, Identifier.noIDMessage("destination pack"));
            return -1;
        }
        try {
            dest_id = Integer.parseInt(dest_id_str);
        } catch (NumberFormatException e) {
            Renderer.renderHXError(context, Identifier.notIntegerMessage("destination pack"));
            return -1;
        }

        if (pack_id == dest_id) {
            Renderer.renderHXError(context, "Destination pack cannot be the same as the source!");
            return -1;
        }

        if (!pack_manager.hasID(dest_id) || !pack_manager.isPackCreator(dest_id, user_id)) {
            Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("destination pack"));
            return -1;
        }

        return dest_id;
    }

    public static List<Integer> getCardIDs(@NotNull Context context, PackManager pack_manager, int pack_id) throws SQLException {
        // attempt to get card ids
        List<String> cards = context.formParams("cards");
        List<Integer> card_ids = new ArrayList<>();
        for (String card : cards) {
            try {
                Integer card_id = Integer.valueOf(card);
                card_ids.add(card_id);
            } catch (NumberFormatException e) {
                Renderer.renderHXError(context, "Could not understand card id.");
                return null;
            }
        }

        // make sure that all the supplied cards belong to the pack we are stealing from
        for (Integer card_id : card_ids) {
            if (!pack_manager.containsCard(pack_id, card_id)) {
                Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("card"));
                return null;
            }
        }

        return card_ids;
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

        int dest_id = getDestID(context, pack_manager, user_id, pack_id.getID());
        if (dest_id == -1) {
            return;
        }

        List<Integer> card_ids = getCardIDs(context, pack_manager, pack_id.getID());
        if (card_ids == null) {
            return;
        }

        // find all the IDs that are in the SOURCE, but are not selected
        // these need to be removed.
        List<Integer> remove_card_ids = pack_manager.getPackCardIDs(pack_id.getID());
        remove_card_ids.removeAll(card_ids);

        // if the card isnt in the pack anyway, dont try and bloomin remove it
        remove_card_ids.removeIf(card_id -> {
           try {
               return !pack_manager.containsCard(dest_id, card_id);
           } catch (SQLException e) {
               return true;
           }
        });

        // dont attempt to add cards that are already in the destination pack
        card_ids.removeIf(card_id -> {
            try {
                return pack_manager.containsCard(dest_id, card_id);
            } catch (SQLException e) {
                return true; // dont think this'll ever fail but this is the safest option
            }
        });

        // ok cool, now we can actually add the cards to the destination pack
        // and remove the cards that are not selected any more
        pack_manager.linkCards(dest_id, card_ids);
        pack_manager.unlinkCards(dest_id, remove_card_ids);
        context.header("HX-Redirect", String.format("/packs/%d", pack_id.getID()));
    }
}
