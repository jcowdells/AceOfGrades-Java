package api_handlers;

import db.CardManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class CardsDeleteApiHandler implements Handler {
    private final CardManager card_manager;

    public CardsDeleteApiHandler(CardManager card_manager) {
        this.card_manager = card_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer card_id = CardsEditApiHandler.getCardID(context, card_manager);
        if (card_id == null) {
            return;
        }

        this.card_manager.deleteCard(card_id);
        context.header("HX-Redirect", "/packs/");
    }
}
