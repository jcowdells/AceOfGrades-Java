package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PacksQuizStartApiHandler implements Handler {
    private final PackManager pack_manager;
    private final String[] styles = {
            "random", "weakest", "burnout"
    };

    public PacksQuizStartApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        // get pack id
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            return;
        }

        // check if allowed to access
        if (!pack_manager.isPublic(pack_id.getID())) {
            final Integer user_id = context.sessionAttribute("user_id");
            if (user_id == null || !pack_manager.isPackCreator(pack_id.getID(), user_id)) {
                Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("pack"));
                return;
            }
        }

        // grab form data
        String quiz_style_str = context.formParam("quiz-style");
        String num_cards_str = context.formParam("num-cards");
        if (quiz_style_str == null || num_cards_str == null) {
            Renderer.renderHXError(context, "Stop trying to break my website!");
            return;
        }

        // validate style
        boolean style_valid = false;
        for (String style : styles) {
            if (quiz_style_str.equals(style)) {
                style_valid = true;
                break;
            }
        }
        if (!style_valid) {
            Renderer.renderHXError(context, "Stop trying to break my website!");
            return;
        }

        // validate number of cards
        int num_cards;
        try {
            num_cards = Integer.parseInt(num_cards_str);
        } catch (NumberFormatException e) {
            Renderer.renderHXError(context, "Stop trying to break my website!");
            return;
        }
        int max_num_cards = pack_manager.getNumCards(pack_id.getID());
        if (num_cards < 0 || num_cards > max_num_cards) {
            Renderer.renderHXError(context, "Stop trying to break my website!");
            return;
        }

        context.header("HX-Redirect", String.format("/packs/%d/quiz?quiz-style=%s&num-cards=%d", pack_id.getID(), quiz_style_str, num_cards));
    }
}
