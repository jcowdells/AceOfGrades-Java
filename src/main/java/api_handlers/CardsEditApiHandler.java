package api_handlers;

import aog.Renderer;
import core.Identifier;
import core.Pair;
import db.CardManager;
import db.PackManager;
import forms.CardsCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CardsEditApiHandler implements Handler {
    private final CardManager card_manager;
    private final PackManager pack_manager;

    public CardsEditApiHandler(CardManager card_manager, PackManager pack_manager) {
        this.card_manager = card_manager;
        this.pack_manager = pack_manager;
    }

    public static Integer getCardID(@NotNull Context context, CardManager card_manager) throws SQLException {
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

    public static CardsCreateForm getCardsCreateForm(String front, String back, String front_color, String back_color, Pair<String, String> pack_colors) {
        CardsCreateForm card_form = new CardsCreateForm(front, back, front_color, back_color);

        if (front == null || front.isEmpty()) {
            card_form.getFront().addError("Front cannot be empty!");
        }
        if (back == null || back.isEmpty()) {
            card_form.getBack().addError("Back cannot be empty!");
        }
        return card_form;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer card_id = getCardID(context, card_manager);
        if (card_id == null)
            return;

        // get pack colours
        int pack_id = card_manager.getPackID(card_id);
        Pair<String, String> pack_colors = pack_manager.getPackColor(pack_id);

        final String front = context.formParam("front");
        final String back = context.formParam("back");
        String front_color = context.formParam("front_color");
        String back_color = context.formParam("back_color");

        CardsCreateForm card_form = CardsEditApiHandler.getCardsCreateForm(
                front, back,
                front_color, back_color,
                pack_colors
        );

        String pack_name = pack_manager.getPackName(pack_id);
        String pack_description = pack_manager.getPackDescription(pack_id);

        Map<String, Object> model = new HashMap<>();
        model.put("pack_name", pack_name);
        model.put("pack_description", pack_description);
        model.put("card_id", card_id);
        model.put("form", card_form);
        if (card_form.hasErrors()) {
            Renderer.render(context, "/common/forms/card/card_edit.ftl", model);
            return;
        }

        // if matches pack colour, don't store it. (will allow pack colour to change and affect all cards).
        if (front_color == null || pack_colors.getA().equals(front_color)) {
            front_color = "";
        }
        if (back_color == null || pack_colors.getB().equals(back_color)) {
            back_color = "";
        }

        card_manager.editCard(card_id, front, back, front_color, back_color);
        model.put("message", "Card successfully updated!");
        Renderer.render(context, "/common/forms/card/card_edit.ftl", model);
    }
}
