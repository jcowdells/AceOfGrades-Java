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

        context.header("HX-Redirect", String.format("/packs/%d/quiz?quiz-style=%s&num-cards=%s", pack_id.getID(),
                quiz_style_str == null ? "" : quiz_style_str,
                num_cards_str == null ? "" : num_cards_str
        ));
    }
}
