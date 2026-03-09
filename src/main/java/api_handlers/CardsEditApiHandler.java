package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.CardManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CardsEditApiHandler implements Handler {
    private final CardManager card_manager;

    public CardsEditApiHandler(CardManager card_manager) {
        this.card_manager = card_manager;
    }

    public static Integer getCardId(@NotNull Context context, CardManager card_manager) throws SQLException {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            return null;
        }

        Identifier card_id = new Identifier(
                context, card_manager,
                "card_id", "card",
                user_id
        );
        if (card_id.hasFailed()) {
            Renderer.renderHXError(context, card_id.getErrorMessage());
            return null;
        }

        // ensure that card is allowed to be edited. this is stricter than identifier.
        if (!card_manager.canEditCard(card_id.getID(), user_id)) {
            Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("card"));
            return null;
        }

        return card_id.getID();
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer card_id = getCardId(context, card_manager);
        if (card_id == null)
            return;
    }
}
