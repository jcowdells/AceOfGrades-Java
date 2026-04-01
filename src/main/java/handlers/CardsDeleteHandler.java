package handlers;

import aog.Card;
import aog.MarkdownHTML;
import aog.Renderer;
import api_handlers.CardsEditApiHandler;
import db.CardManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CardsDeleteHandler implements Handler {
    private final CardManager card_manager;
    private final MarkdownHTML md_parser;

    public CardsDeleteHandler(CardManager card_manager, MarkdownHTML md_parser) {
        this.card_manager = card_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer card_id = CardsEditApiHandler.getCardID(context, card_manager);
        if (card_id == null) {
            return;
        }

        Card card = card_manager.getCard(card_id);
        String card_front = md_parser.markdownToText(card.getFront(), 20);
        Map<String, Object> model = new HashMap<>();
        model.put("card_front", card_front);
        model.put("card_id", card_id);
        Renderer.render(context, "/templates/card/card_delete.ftl", model);
    }
}
