package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacksCardsMoveApiHandler implements Handler {
    private final PackManager pack_manager;

    public PacksCardsMoveApiHandler(PackManager pack_manager) {
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
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            return;
        }
        if (!pack_manager.isPackCreator(pack_id.getID(), user_id)) {
            Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("pack"));
            return;
        }

        int dest_id = PacksStealApiHandler.getDestID(context, pack_manager, user_id, pack_id.getID());
        if (dest_id == -1) {
            return;
        }

        List<Integer> card_ids = PacksStealApiHandler.getCardIDs(context, pack_manager, pack_id.getID());
        if (card_ids == null) {
            return;
        }

        pack_manager.changeOwnership(card_ids, dest_id);

        context.header("HX-Redirect", String.format("/packs/%d", pack_id.getID()));
    }
}
