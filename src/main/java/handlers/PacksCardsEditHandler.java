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

public class PacksCardsEditHandler implements Handler {
    private final MarkdownHTML md_parser;
    private final PackManager pack_manager;

    public PacksCardsEditHandler(MarkdownHTML markdown_html, PackManager pack_manager) {
        this.md_parser = markdown_html;
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Could not find user ID!");
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

        List<Card> card_list = pack_manager.getPackCards(pack_id.getID());
        card_list.removeIf(card -> card.getCreatorID() != user_id);

        List<CardThumbnail> card_thumbnails = new ArrayList<>();
        for (Card card : card_list) {
            card_thumbnails.add(new CardThumbnail(
                    card.getID(),
                    md_parser.MarkdownToHTML(card.getFront()),
                    card.getFrontColor()
            ));
        }

        Map<String, Object> model = new HashMap<>();
        model.put("cards", card_thumbnails);
        Renderer.render(context, "/templates/card/card_edit_select.ftl", model);
    }
}
