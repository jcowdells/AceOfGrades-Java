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

public class PacksSelectHandler implements Handler {
    private final MarkdownHTML md_parser;
    private final PackManager pack_manager;

    public PacksSelectHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.md_parser = md_parser;
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        if (!pack_manager.isPublic(pack_id.getID()) && (user_id == null || !pack_manager.isPackCreator(pack_id.getID(), user_id))) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("pack"));
            return;
        }

        List<Card> card_thumbnails = pack_manager.getPackCards(pack_id.getID());
        List<CardThumbnail> cards = new ArrayList<>();
        for (Card card_thumbnail : card_thumbnails) {
            cards.add(new CardThumbnail(
                    card_thumbnail.getID(),
                    md_parser.MarkdownToHTML(card_thumbnail.getFront()),
                    card_thumbnail.getFrontColor()
            ));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("cards", cards);
        model.put("pack_id", pack_id.getID());
        Renderer.render(context, "/templates/select_cards.ftl", model);
    }
}
