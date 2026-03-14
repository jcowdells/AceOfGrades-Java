package handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PacksViewHandler implements Handler {
    private final PackManager pack_manager;

    public PacksViewHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        final Integer user_id = context.sessionAttribute("user_id");
        boolean is_creator = user_id != null && pack_manager.isPackCreator(pack_id.getID(), user_id);
        Map<String, Object> model = new HashMap<>();
        model.put("is_creator", is_creator);
        Renderer.render(context, "/templates/view_pack.ftl", model);
    }
}
