package api_handlers;

import aog.Pack;
import aog.Renderer;
import core.Identifier;
import core.Pair;
import db.PackManager;
import forms.PacksCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PacksEditApiHandler implements Handler {
    private final PackManager pack_manager;
    private final Pattern color_regex;

    public PacksEditApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
        final String color_pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        color_regex = Pattern.compile(color_pattern);
    }

    public static Integer getPackID(@NotNull Context context, PackManager pack_manager) throws SQLException {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "No user ID!");
            return null;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderHXError(context, pack_id.getErrorMessage());
            return null;
        }
        if (!pack_manager.isPackCreator(pack_id.getID(), user_id)) {
            Renderer.renderHXError(context, Identifier.resourceDoesNotExistMessage("pack"));
            return null;
        }

        return pack_id.getID();
    }

    public static Pack getPackFormData(@NotNull Context context, Pattern color_regex, String form_template, int pack_id) {
        final String name = context.formParam("name");
        final String description = context.formParam("description");
        final String front_color = context.formParam("front_color");
        final String back_color = context.formParam("back_color");
        final String is_public_str = context.formParam("is_public");
        final boolean is_public = is_public_str != null && !is_public_str.isEmpty();

        PacksCreateForm create_pack_form = new PacksCreateForm(
                name,
                description,
                front_color,
                back_color,
                is_public
        );

        if (name == null || name.isEmpty()) {
            create_pack_form.getName().addError("Name cannot be empty!");
        }
        if (description == null || description.isEmpty()) {
            create_pack_form.getDescription().addError("Description cannot be empty!");
        }
        if (front_color == null || !color_regex.matcher(front_color).matches()) {
            create_pack_form.getFrontColor().addError("Invalid front colour!");
        }
        if (back_color == null || !color_regex.matcher(back_color).matches()) {
            create_pack_form.getBackColor().addError("Invalid back colour!");
        }

        if (create_pack_form.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("form", create_pack_form);
            if (pack_id > 0) {
                model.put("pack_id", pack_id);
            }
            context.render(form_template, model);
            return null;
        }

        return new Pack(-1, -1, name, description, front_color, back_color, is_public);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer pack_id = getPackID(context, pack_manager);
        if (pack_id == null) {
            return;
        }

        Pack pack = getPackFormData(context, color_regex, "/common/forms/card/pack_edit.ftl", pack_id);
        if (pack == null) {
            return;
        }

        pack_manager.updatePack(
                pack_id,
                pack.getName(), pack.getDescription(),
                pack.getFrontColor(), pack.getBackColor(),
                pack.isPublic()
        );
        context.header("HX-Redirect", String.format("/packs/%d", pack_id));
    }
}
