package api_handlers;

import aog.Renderer;
import core.Pair;
import db.CardManager;
import db.PackManager;
import forms.CreateCardForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateCardApiHandler implements Handler {
    private final PackManager pack_manager;
    private final CardManager card_manager;

    public CreateCardApiHandler(PackManager pack_manager, CardManager card_manager) {
        this.pack_manager = pack_manager;
        this.card_manager = card_manager;
    }

    public static Pair<String, Integer> getPackID(PackManager pack_manager, String pack_id_str, int user_id) throws SQLException {
        if (pack_id_str == null) {
            return new Pair<>("No pack id provided!", null);
        }
        int pack_id;
        try {
            pack_id = Integer.parseInt(pack_id_str);
        } catch (NumberFormatException e) {
            return new Pair<>("Pack ID must be an integer!", null);
        }
        if (!pack_manager.hasPack(pack_id) || !pack_manager.isPackOwner(pack_id, user_id)) {
            return new Pair<>("Specified pack does not exist!", null);
        }
        return new Pair<>(null, pack_id);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            return;
        }

        final String pack_id_str = context.formParam("pack_id");
        Pair<String, Integer> pack_id = getPackID(pack_manager, pack_id_str, user_id);
        if (pack_id.getB() == null) {
            Renderer.renderHXError(context, pack_id.getA());
            return;
        }

        Pair<String, String> pack_colors = pack_manager.getPackColor(pack_id.getB());

        final String front = context.formParam("front");
        final String back = context.formParam("back");
        String front_color = context.formParam("front_color");
        String back_color = context.formParam("back_color");

        System.out.println(front);
        System.out.println(back);
        System.out.println(front_color);
        System.out.println(back_color);

        CreateCardForm card_form = new CreateCardForm(front, back, front_color, back_color);

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

        Map<String, Object> model = new HashMap<>();
        model.put("pack_id", pack_id.getB());

        if (card_form.hasErrors()) {
            model.put("form", card_form);
            context.render("/common/forms/create_card.ftl", model);
            return;
        }

        card_manager.createCard(pack_id.getB(), front, back, front_color, back_color);
        model.put("form", new CreateCardForm(pack_colors.getA(), pack_colors.getB()));
        context.render("/common/forms/create_card.ftl", model);
    }
}
