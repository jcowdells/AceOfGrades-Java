package handlers;

import aog.Card;
import aog.CardThumbnail;
import aog.MarkdownHTML;
import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksStealHandler implements Handler {
    private final MarkdownHTML md_parser;
    private final PackManager pack_manager;

    public PacksStealHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.md_parser = md_parser;
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Could not find user ID!");
            return;
        }

        String dest_id_str = context.queryParam("dest-id");
        if (dest_id_str == null) {
            Renderer.renderError(context, Identifier.noIDMessage("destination pack"));
            return;
        }
        int dest_id;
        try {
            dest_id = Integer.parseInt(dest_id_str);
        } catch (NumberFormatException e) {
            Renderer.renderError(context, Identifier.notIntegerMessage("destination pack"));
            return;
        }
        if (!pack_manager.hasID(dest_id) || !pack_manager.isPackCreator(dest_id, user_id)) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("destination pack"));
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

        if (pack_id.getID() == dest_id) {
            Renderer.renderError(context, "Pack cannot steal its own cards!");
            return;
        }

        List<Card> card_thumbnails = pack_manager.getPackCards(pack_id.getID());
        List<CardThumbnail> cards = new ArrayList<>();
        for (Card card_thumbnail : card_thumbnails) {
            boolean selected = pack_manager.containsCard(dest_id, card_thumbnail.getID());
            cards.add(new CardThumbnail(
                    card_thumbnail.getID(),
                    md_parser.MarkdownToHTML(card_thumbnail.getFront()),
                    card_thumbnail.getFrontColor(),
                    selected
            ));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("cards", cards);
        model.put("pack_id", pack_id.getID());
        model.put("dest_id", dest_id);
        Renderer.render(context, "/templates/card/card_select.ftl", model);
    }
}
