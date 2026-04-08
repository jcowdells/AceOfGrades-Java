package handlers;

import aog.PackThumbnail;
import aog.Renderer;
import core.Identifier;
import core.Pair;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

public class PacksStealSelectHandler implements Handler {
    private final PackManager pack_manager;

    public PacksStealSelectHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    public static Pair<Integer, Integer> getPackAndUserID(@NotNull Context context, PackManager pack_manager) {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Could not get user ID!");
            return null;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_id
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return null;
        }

        return new Pair<>(pack_id.getID(), user_id);
    }

    public static List<PackThumbnail> getPacks(PackManager pack_manager, int pack_id, int user_id) throws SQLException {
        List<PackThumbnail> packs = pack_manager.getUserCreatedPacks(user_id);
        // dont show the option to steal a pack to itself, but will defend more rigorously elsewhere anyway.
        packs.removeIf(pack -> pack.getID() == pack_id);
        packs.sort(Comparator.comparing(p -> p.getName().toLowerCase() + p.getDescription().toLowerCase()));
        return packs;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Pair<Integer, Integer> pack_user_id = getPackAndUserID(context, pack_manager);
        if (pack_user_id == null) {
            return;
        }

        List<PackThumbnail> packs = getPacks(pack_manager, pack_user_id.getA(), pack_user_id.getB());
        Map<String, Object> model = new HashMap<>();
        model.put("pack_id", pack_user_id.getA());
        model.put("packs", packs);
        Renderer.render(context,"/templates/card/card_steal_select.ftl", model);
    }
}
