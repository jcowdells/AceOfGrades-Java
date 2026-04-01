package handlers;

import aog.*;
import core.Identifier;
import db.CardManager;
import db.UserManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProfilesHandler implements Handler {
    private final UserManager user_manager;
    private final CardManager card_manager;
    private final MarkdownHTML md_parser;

    public ProfilesHandler(UserManager user_manager, CardManager card_manager, MarkdownHTML md_parser) {
        this.user_manager = user_manager;
        this.card_manager = card_manager;
        this.md_parser = md_parser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Identifier user_id = new Identifier(
                context, user_manager,
                "user_id", "user"
        );
        if (user_id.hasFailed()) {
            Renderer.renderError(context, user_id.getErrorMessage());
            return;
        }

        Integer viewer_id = context.sessionAttribute("user_id");

        User user = user_manager.getUserData(user_id.getID());
        UserStats user_stats = user_manager.getUserStats(user_id.getID());
        Map<String, Object> model = new HashMap<>();
        model.put("user_stats", user_stats);
        model.put("user_data", user);

        boolean is_owner = viewer_id != null && user_id.getID() == viewer_id;
        model.put("is_owner", is_owner);

        if (is_owner) {
            Card best_card = card_manager.getCard(user_stats.getBestCard());
            Card worst_card = card_manager.getCard(user_stats.getWorstCard());

            model.put("best_card", new CardThumbnail(
                    best_card.getID(),
                    md_parser.markdownToHTML(best_card.getFront()),
                    best_card.getFrontColor()
            ));
            model.put("best_card_pack", card_manager.getPackID(best_card.getID()));
            model.put("worst_card", new CardThumbnail(
               worst_card.getID(),
               md_parser.markdownToHTML(worst_card.getFront()),
               worst_card.getFrontColor()
            ));
            model.put("worst_card_pack", card_manager.getPackID(worst_card.getID()));
        }

        Renderer.render(context, "/templates/user/profile.ftl", model);
    }
}
