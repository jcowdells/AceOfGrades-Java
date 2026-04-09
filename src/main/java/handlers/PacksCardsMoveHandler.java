package handlers;

import aog.*;
import core.Identifier;
import core.Pair;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksCardsMoveHandler implements Handler {
    private final PackManager pack_manager;
    private final MarkdownHTML md_parser;

    public PacksCardsMoveHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.pack_manager = pack_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Pair<Integer, Integer> user_dest_id = PacksCardsStealHandler.getUserDestID(context, pack_manager);
        if (user_dest_id == null) {
            return;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }
        if (!pack_manager.isPackCreator(user_dest_id.getB(), user_dest_id.getA())) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("destination pack"));
            return;
        }

        if (pack_id.getID() == user_dest_id.getB()) {
            Renderer.renderError(context, "Pack cannot move cards into itself!");
            return;
        }

        List<Card> card_thumbnails = pack_manager.getPackCards(pack_id.getID());
        List<CardThumbnail> cards = new ArrayList<>();
        for (Card card_thumbnail : card_thumbnails) {
            cards.add(new CardThumbnail(
                    card_thumbnail.getID(),
                    md_parser.markdownToHTML(card_thumbnail.getFront()),
                    card_thumbnail.getFrontColor(),
                    false
            ));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("cards", cards);
        model.put("pack_id", pack_id.getID());
        model.put("dest_id", user_dest_id.getB());
        Renderer.render(context, "/templates/card/card_move.ftl", model);
    }
}
