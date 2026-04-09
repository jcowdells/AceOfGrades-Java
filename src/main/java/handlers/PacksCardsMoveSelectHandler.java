package handlers;

import aog.PackThumbnail;
import aog.Renderer;
import core.Pair;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksCardsMoveSelectHandler implements Handler {
    private final PackManager pack_manager;

    public PacksCardsMoveSelectHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Pair<Integer, Integer> pack_user_id = PacksCardsStealSelectHandler.getPackAndUserID(context, pack_manager);
        if (pack_user_id == null) {
            return;
        }

        List<PackThumbnail> packs = PacksCardsStealSelectHandler.getPacks(pack_manager, pack_user_id.getA(), pack_user_id.getB());

        Map<String, Object> model = new HashMap<>();
        model.put("packs", packs);
        model.put("pack_id", pack_user_id.getA());
        Renderer.render(context, "/templates/card/card_move_select.ftl", model);
    }
}
