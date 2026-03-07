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

import java.util.HashMap;
import java.util.Map;

public class PacksCardsCreateApiHandler implements Handler {
    private final PackManager pack_manager;
    private final CardManager card_manager;

    public PacksCardsCreateApiHandler(PackManager pack_manager, CardManager card_manager) {
        this.pack_manager = pack_manager;
        this.card_manager = card_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        // get user id
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            return;
        }

        // get pack id from url
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            context.status(401);
            return;
        }

        // get pack colours
        Pair<String, String> pack_colors = pack_manager.getPackColor(pack_id.getID());

        final String front = context.formParam("front");
        final String back = context.formParam("back");
        String front_color = context.formParam("front_color");
        String back_color = context.formParam("back_color");

        CardsCreateForm card_form = new CardsCreateForm(front, back, front_color, back_color);

        if (front == null || front.isEmpty()) {
            card_form.getFront().addError("Front cannot be empty!");
        }
        if (back == null || back.isEmpty()) {
            card_form.getBack().addError("Back cannot be empty!");
        }

        // if matches pack colour, don't store it. (will allow pack colour to change and affect all cards).
        if (front_color == null || pack_colors.getA().equals(front_color)) {
            front_color = "";
        }
        if (back_color == null || pack_colors.getB().equals(back_color)) {
            back_color = "";
        }

        // put pack id into model, allows api endpoint to be generated
        Map<String, Object> model = new HashMap<>();
        model.put("pack_id", pack_id.getID());

        if (card_form.hasErrors()) {
            model.put("form", card_form);
            context.render("/common/forms/create_card.ftl", model);
            return;
        }

        card_manager.createCard(pack_id.getID(), front, back, front_color, back_color);
        model.put("form", new CardsCreateForm(pack_colors.getA(), pack_colors.getB()));
        context.render("/common/forms/create_card.ftl", model);
    }
}
