package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import db.SpotlightManager;
import forms.SpotlightCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksSpotlightCreateApiHandler implements Handler {
    private final SpotlightManager spotlight_manager;
    private final PackManager pack_manager;

    public PacksSpotlightCreateApiHandler(SpotlightManager spotlight_manager, PackManager pack_manager) {
        this.spotlight_manager = spotlight_manager;
        this.pack_manager = pack_manager;
    }

    public static Integer getPackID(@NotNull Context context, PackManager pack_manager) throws SQLException {
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return null;
        }

        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null || !pack_manager.isPackCreator(pack_id.getID(), user_id)) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("pack"));
            return null;
        }

        return pack_id.getID();
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer pack_id = getPackID(context, pack_manager);
        if (pack_id == null) {
            return;
        }

        // validate form and that
        String name = context.formParam("spotlight-name");
        List<String> cards = context.formParams("cards");

        SpotlightCreateForm form = new SpotlightCreateForm(name);
        if (name == null || name.isEmpty()) {
            form.getName().addError("Name cannot be empty!");
        }
        if (cards.isEmpty()) {
            form.getName().addError("Spotlight cannot have zero cards!");
        }

        // convert inputted data to integers
        List<Integer> card_ids = new ArrayList<>();
        for (String card : cards) {
            try {
                Integer card_id = Integer.valueOf(card);
                card_ids.add(card_id);
            } catch (NumberFormatException e) {
                form.getName().addError("Stop trying to break my website!");
                break;
            }
        }

        // make sure that all the supplied cards belong to the pack
        for (Integer card_id : card_ids) {
            if (!pack_manager.containsCard(pack_id, card_id)) {
                form.getName().addError("Stop trying to break my website!");
                break;
            }
        }

        // return errors if occurred
        Map<String, Object> model = new HashMap<>();
        if (form.hasErrors()) {
            model.put("form", form);
            Renderer.render(context, "/common/forms/card/spotlight_form.ftl", model);
            return;
        }

        // all good, im doin the dam thing
        int spotlight_id = spotlight_manager.createSpotlight(pack_id, name);
        spotlight_manager.linkCards(spotlight_id, card_ids);

        context.header("HX-Redirect", String.format("/packs/%d", pack_id));
    }
}