package api_handlers;

import aog.Renderer;
import core.Pair;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GetPackApiHandler implements Handler {
    private final PackManager pack_manager;

    public GetPackApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
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
        if (!pack_manager.hasPack(pack_id) || !pack_manager.canAccessPack(pack_id, user_id)) {
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

        Pair<String, Integer> pack_id = CreateCardApiHandler.getPackID(pack_manager, context.queryParam("pack"), user_id);
        if (pack_id.getB() == null) {
            Renderer.renderHXError(context, pack_id.getA());
            // TODO: error handling, set status code and message
            return;
        }

        List<Map<String, Object>> card_list = pack_manager.getPackCards(pack_id.getB());
        context.json(card_list);
    }
}
