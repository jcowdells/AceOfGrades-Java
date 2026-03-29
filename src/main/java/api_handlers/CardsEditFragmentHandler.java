package api_handlers;

import db.CardManager;
import db.PackManager;
import handlers.CardsEditHandler;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class CardsEditFragmentHandler implements Handler {
    private final CardManager card_manager;
    private final PackManager pack_manager;

    public CardsEditFragmentHandler(CardManager card_manager, PackManager pack_manager) {
        this.card_manager = card_manager;
        this.pack_manager = pack_manager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        CardsEditHandler.genericHandle(context, card_manager, pack_manager, "/common/forms/card/card_edit_fragment.ftl");
    }
}
