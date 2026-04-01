package handlers;

import aog.CardThumbnail;
import aog.MarkdownHTML;
import aog.Renderer;
import core.Identifier;
import db.PackManager;
import forms.SpotlightCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksSpotlightCreateHandler implements Handler {
    private final PackManager pack_manager;
    private final MarkdownHTML md_parser;

    public PacksSpotlightCreateHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.pack_manager = pack_manager;
        this.md_parser = md_parser;
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

        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null || !pack_manager.isPackCreator(pack_id.getID(), user_id)) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("pack"));
            return;
        }

        Map<String, Object> model = new HashMap<>();
        List<CardThumbnail> card_thumbnails = new ArrayList<>();
        for (CardThumbnail card_thumbnail : pack_manager.getCardThumbnails(pack_id.getID())) {
            card_thumbnails.add(new CardThumbnail(
                    card_thumbnail.getID(),
                    md_parser.markdownToHTML(card_thumbnail.getFront()),
                    card_thumbnail.getFrontColor()
            ));
        }
        model.put("pack_id", pack_id.getID());
        model.put("cards", card_thumbnails);
        model.put("form", new SpotlightCreateForm());
        Renderer.render(context, "/templates/card/spotlight_create.ftl", model);
    }
}
