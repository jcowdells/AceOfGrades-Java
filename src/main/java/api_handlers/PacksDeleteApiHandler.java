package api_handlers;

import aog.Renderer;
import core.Identifier;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class PacksDeleteApiHandler implements Handler {
    private final PackManager pack_manager;

    public PacksDeleteApiHandler(PackManager pack_manager) {
        this.pack_manager = pack_manager;
    }

    public static Integer getPackID(@NotNull Context context, PackManager pack_manager) throws SQLException {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "No user ID!");
            return null;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack"
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return null;
        }

        if (!pack_manager.isPackCreator(pack_id.getID(), user_id)) {
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

        pack_manager.deletePack(pack_id);
        context.header("HX-Redirect", "/packs/");
        context.status(200);
    }
}
