package handlers;

import aog.Renderer;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PacksDeleteHandler implements Handler {
    private final PackManager pack_manager;

    public PacksDeleteHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer pack_id = PacksEditHandler.getPackID(context, pack_manager);
        if (pack_id == null) {
            return;
        }

        String pack_name = pack_manager.getPackName(pack_id);
        String pack_description = pack_manager.getPackDescription(pack_id);

        Map<String, Object> model = new HashMap<>();
        model.put("pack_name", pack_name);
        model.put("pack_description", pack_description);
        model.put("pack_id", pack_id);
        Renderer.render(context, "/templates/card/pack_delete.ftl", model);
    }
}
