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

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Integer pack_id = PacksEditApiHandler.getPackID(context, pack_manager);
        if (pack_id == null) {
            return;
        }

        pack_manager.deletePack(pack_id);
        context.header("HX-Redirect", "/packs/");
        context.status(200);
    }
}
