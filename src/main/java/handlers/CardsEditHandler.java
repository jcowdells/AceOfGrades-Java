package handlers;

import aog.Card;
import aog.Renderer;
import api_handlers.CardsEditApiHandler;
import db.CardManager;
import db.PackManager;
import forms.CardsCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CardsEditHandler implements Handler {

    private final CardManager card_manager;
    private final PackManager pack_manager;

    public CardsEditHandler(CardManager card_manager, PackManager pack_manager) {
        this.card_manager = card_manager;
        this.pack_manager = pack_manager;
    }

    public static void genericHandle(@NotNull Context context, CardManager card_manager, PackManager pack_manager, String template) throws SQLException {
        final Integer card_id = CardsEditApiHandler.getCardId(context, card_manager);
        if (card_id == null)
            return;

        int pack_id = card_manager.getPackID(card_id);
        String pack_name = pack_manager.getPackName(pack_id);
        String pack_description = pack_manager.getPackDescription(pack_id);

        Card card = card_manager.getCard(card_id);

        Map<String, Object> model = new HashMap<>();
        model.put("form", new CardsCreateForm(card.getFront(), card.getBack(), card.getFrontColor(), card.getBackColor()));
        model.put("card_id", card_id);
        model.put("pack_name", pack_name);
        model.put("pack_description", pack_description);
        Renderer.render(context, template, model);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        genericHandle(context, card_manager, pack_manager, "/templates/card/card_edit.ftl");
    }
}
