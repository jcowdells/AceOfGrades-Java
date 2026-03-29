package api_handlers;

import aog.Card;
import aog.MarkdownHTML;
import aog.Renderer;
import core.Identifier;
import db.CardManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CardsApiHandler implements Handler {
    private final CardManager card_manager;
    private final MarkdownHTML md_parser;

    public CardsApiHandler(CardManager card_manager, MarkdownHTML md_parser) {
        this.card_manager = card_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer user_id = context.sessionAttribute("user_id");

        Identifier card_id = new Identifier(
                context, card_manager,
                "card_id", "card"
        );
        if (card_id.hasFailed()) {
            Renderer.renderJsonError(context, 404, "Error", card_id.getErrorMessage());
            return;
        }

        if (user_id == null && !card_manager.isPublic(card_id.getID())) {
            Renderer.renderJsonError(context, 404, "Error", Identifier.resourceDoesNotExistMessage("card"));
            return;
        }

        Card card = card_manager.getCard(card_id.getID());
        Map<String, Object> card_json = new HashMap<>();
        card_json.put("id", card.getID());
        card_json.put("front", md_parser.MarkdownToHTML(card.getFront()));
        card_json.put("back", md_parser.MarkdownToHTML(card.getBack()));
        card_json.put("front-color", card.getFrontColor());
        card_json.put("back-color", card.getBackColor());
        card_json.put("is-owner", user_id != null && user_id == card.getCreatorID());
        context.json(card_json);
    }
}
