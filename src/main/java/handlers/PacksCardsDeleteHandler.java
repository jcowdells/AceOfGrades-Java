package handlers;

import aog.CardThumbnail;
import aog.MarkdownHTML;
import aog.Renderer;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksCardsDeleteHandler implements Handler {
    private final PackManager pack_manager;
    private final MarkdownHTML md_parser;

    public PacksCardsDeleteHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.pack_manager = pack_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Map<String, Object> model = new HashMap<>();
        List<CardThumbnail> cards = PacksCardsEditHandler.getEditableCards(context, pack_manager, md_parser);
        if (cards == null) {
            return;
        }
        model.put("cards", cards);
        Renderer.render(context, "/templates/card/card_delete_select.ftl", model);
    }
}
