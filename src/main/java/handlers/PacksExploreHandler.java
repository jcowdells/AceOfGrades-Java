package handlers;

import aog.Pack;
import aog.Renderer;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksExploreHandler implements Handler {
    private final PackManager pack_manager;

    public PacksExploreHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        List<Pack> packs = pack_manager.getPublicPacks();
        packs.sort(Comparator.comparing(p -> p.getName().toLowerCase() + p.getDescription().toLowerCase()));
        Map<String, Object> model = new HashMap<>();
        model.put("packs", packs);
        Renderer.render(context, "/templates/card/explore.ftl", model);
    }
}
