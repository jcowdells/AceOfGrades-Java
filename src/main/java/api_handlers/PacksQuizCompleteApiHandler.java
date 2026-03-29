package api_handlers;

import aog.JsonString;
import aog.Renderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import db.CardManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PacksQuizCompleteApiHandler implements Handler {
    private final CardManager card_manager;
    private final JsonString json_string;

    public PacksQuizCompleteApiHandler(CardManager card_manager, JsonString json_string) {
        this.card_manager = card_manager;
        this.json_string = json_string;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
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

        if (!request.has("cards") || !request.get("cards").isArray()) {
            Renderer.renderJsonError(context, 400, "Bad layout", "Could not understand json.");
            return;
        }

        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderJsonError(context, 401, "No credentials", "Could not identify user.");
            context.header("WWW-Authenticate", "Try WWW Authenticate This, Bro");
            return;
        }

        JsonNode cards = request.get("cards");
        List<Integer> integer_data = new ArrayList<>();
        for (JsonNode card : cards) {
            if (!card.has("id") || !card.has("attempts") || !card.has("correct")) {
                Renderer.renderJsonError(context, 400, "Bad layout", "Could not understand card json.");
                return;
            }
            final int id = card.get("id").asInt(-1);
            final int attempts = card.get("attempts").asInt(-1);
            final int correct = card.get("correct").asInt(-1);
            if (correct < 0 || attempts < 0 || correct > attempts) {
                Renderer.renderJsonError(context, 400, "Invalid values", "Number of cards must be positive.");
                return;
            }
            if (id == -1 || !card_manager.hasID(id)) {
                Renderer.renderJsonError(context, 404, "No card", "Card with specified ID does not exist.");
                return;
            }
            integer_data.add(id);
            integer_data.add(attempts);
            integer_data.add(correct);
        }

        // hopefully json junk gets garbage collected now D:
        // once we are certain the data is good, we can add it to the database
        for (int i = 0; i < integer_data.size(); i += 3) {
            final int card_id = integer_data.get(i);
            final int attempts = integer_data.get(i + 1);
            final int correct = integer_data.get(i + 2);
            card_manager.updateCardStats(card_id, user_id, attempts, correct);
        }

        context.status(200);
    }
}