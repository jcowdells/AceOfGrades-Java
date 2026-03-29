package api_handlers;

import aog.Card;
import aog.JsonString;
import aog.MarkdownHTML;
import aog.Renderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PacksGetCardsHandler implements Handler {
    private final PackManager pack_manager;
    private final MarkdownHTML md_parser;
    private final JsonString json_string;
    private final String[] styles = {
            "random", "weakest", "burnout"
    };

    public PacksGetCardsHandler(PackManager pack_manager, MarkdownHTML md_parser, JsonString json_string) {
        this.pack_manager = pack_manager;
        this.md_parser = md_parser;
        this.json_string = json_string;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderJsonError(context, 404, "Error", pack_id.getErrorMessage());
            return;
        }

        final Integer user_id = context.sessionAttribute("user_id");
        boolean is_pack_creator = user_id != null && pack_manager.isPackCreator(pack_id.getID(), user_id);

        if (!pack_manager.isPublic(pack_id.getID())) {
            if (!is_pack_creator) {
                Renderer.renderJsonError(context, 404, "Error", Identifier.resourceDoesNotExistMessage("pack"));
                return;
            }
        }

        final String content_type = context.contentType();
        if (content_type == null || !content_type.equalsIgnoreCase("application/json")) {
            Renderer.renderJsonError(context, 406, "Unsupported Type", "Only application/json accepted.");
            return;
        }

        final String body = context.body();
        JsonNode request;
        try {
            request = json_string.getJsonNode(body);
        } catch (JsonProcessingException e) {
            Renderer.renderJsonError(context, 400, "Bad formatting", "Could not parse json.");
            return;
        }

        if (!request.has("quiz-style") || !request.has("num-cards") || !request.get("num-cards").isInt()) {
            Renderer.renderJsonError(context, 400, "Bad layout", "Could not understand json.");
            return;
        }

        String quiz_style = request.get("quiz-style").asText();
        int max_num_cards = pack_manager.getNumCards(pack_id.getID());
        int num_cards = request.get("num-cards").asInt(-1);
        if (num_cards < 0 || num_cards > max_num_cards) {
            Renderer.renderJsonError(context, 400, "Invalid values", "Must be a positive base 10 integer number of cards, less than pack size.");
            return;
        }

        // validate style
        boolean style_valid = false;
        for (String style : styles) {
            if (quiz_style.equals(style)) {
                style_valid = true;
                break;
            }
        }
        if (!style_valid) {
            Renderer.renderJsonError(context, 400, "Invalid values", "Must be a valid quiz style.");
            return;
        }
        if ((user_id == null) && !quiz_style.equals("random")) {
            Renderer.renderJsonError(context, 401, "No credentials", "Could not identify user.");
            context.header("WWW-Authenticate", "Try WWW Authenticate This, Bro");
            return;
        }

        List<Card> card_list;
        if (quiz_style.equals("random")) {
            card_list = pack_manager.getPackCards(pack_id.getID());
            // whittle down the list to the specified size
            Random random = new Random();
            while (card_list.size() > num_cards) {
                final int remove_index = random.nextInt(0, card_list.size());
                card_list.remove(remove_index);
            }
        } else {
            card_list = pack_manager.getPackCardsByRatio(pack_id.getID(), user_id, num_cards);
        }

        // shuffle cards to random order
        Collections.shuffle(card_list);

        List<Map<String, Object>> card_json_list = new ArrayList<>();
        for (Card card : card_list) {
            // put in the data that needs no conversion
            Map<String, Object> card_json = new HashMap<>();
            card_json.put("id", card.getID());
            card_json.put("front-color", card.getFrontColor());
            card_json.put("back-color", card.getBackColor());

            // now convert md to html
            card_json.put("front", md_parser.MarkdownToHTML(card.getFront()));
            card_json.put("back", md_parser.MarkdownToHTML(card.getBack()));

            // add ownership details
            card_json.put("is-owner", user_id != null && card.getCreatorID() == user_id);

            card_json_list.add(card_json);
        }

        Map<String, Object> cards_json = new HashMap<>();
        cards_json.put("num-cards", num_cards);
        cards_json.put("cards", card_json_list);
        cards_json.put("quiz-style", quiz_style);
        cards_json.put("post-results", user_id != null);
        cards_json.put("is-owner", is_pack_creator);

        context.json(cards_json);
    }
}
