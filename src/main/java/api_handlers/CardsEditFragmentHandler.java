package api_handlers;

import aog.Card;
import db.CardManager;
import forms.CardsCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CardsEditFragmentHandler implements Handler {
    private final CardManager card_manager;

    public CardsEditFragmentHandler(CardManager card_manager) {
        this.card_manager = card_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer card_id = CardsEditApiHandler.getCardId(context, card_manager);
        if (card_id == null)
            return;

        Card card = card_manager.getCard(card_id);

        Map<String, Object> model = new HashMap<>();
        model.put("form", new CardsCreateForm(card.getFront(), card.getBack(), card.getFrontColor(), card.getBackColor()));
        model.put("card_id", card_id);
        context.render("/common/forms/edit_card.ftl", model);
    }
}
