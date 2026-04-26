package handlers;

import aog.PackThumbnail;
import aog.Renderer;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksHandler implements Handler {
    private final PackManager pack_manager;

    public PacksHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Failed to get user id!");
            return;
        }

        List<PackThumbnail> packs = pack_manager.getUserPacks(user_id);
        packs.sort(Comparator.comparing(p -> p.getName().toLowerCase() + p.getDescription().toLowerCase()));
        Map<String, Object> model = new HashMap<>();
        model.put("packs", packs);
        Renderer.render(context, "/templates/card/packs.ftl", model);
    }
}
