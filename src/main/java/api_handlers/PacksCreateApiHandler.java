package api_handlers;

import aog.Renderer;
import db.PackManager;
import forms.PacksCreateForm;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PacksCreateApiHandler implements Handler {
    private final PackManager pack_manager;
    private final Pattern color_regex;

    public PacksCreateApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
        final String color_pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        color_regex = Pattern.compile(color_pattern);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderHXError(context, "Failed to get user id!");
            return;
        }

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
            context.render("/common/forms/create_pack.ftl", model);
            return;
        }

        pack_manager.createPack(user_id, name, description, front_color, back_color, false);
        context.header("HX-Redirect", "/");
    }
}
