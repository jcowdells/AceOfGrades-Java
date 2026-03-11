package api_handlers;

import aog.Card;
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

public class PacksGetCardsHandler implements Handler {
    private final PackManager pack_manager;
    private final MarkdownHTML md_parser;

    public PacksGetCardsHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.pack_manager = pack_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            context.status(401);
            return;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            context.status(404);
            return;
        }

        List<Card> card_list = pack_manager.getPackCards(pack_id.getID());

        List<Map<String, Object>> card_json_list = new ArrayList<>();
        for (Card card : card_list) {
            // put in the data that needs no conversion
            Map<String, Object> card_json = new HashMap<>();
            card_json.put("id", card.getID());
            card_json.put("front_color", card.getFrontColor());
            card_json.put("back_color", card.getBackColor());

            // now convert md to html
            card_json.put("front", md_parser.MarkdownToHTML(card.getFront()));
            card_json.put("back", md_parser.MarkdownToHTML(card.getBack()));
            card_json_list.add(card_json);
        }

        Map<String, Object> cards_json = new HashMap<>();
        cards_json.put("num_cards", card_list.size());
        cards_json.put("cards", card_json_list);

        context.json(cards_json);
    }
}
