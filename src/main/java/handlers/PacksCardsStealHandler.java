package handlers;

import aog.Card;
import aog.CardThumbnail;
import aog.MarkdownHTML;
import aog.Renderer;
import core.Identifier;
import core.Pair;
import db.PackManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacksCardsStealHandler implements Handler {
    private final MarkdownHTML md_parser;
    private final PackManager pack_manager;

    public PacksCardsStealHandler(PackManager pack_manager, MarkdownHTML md_parser) {
        this.md_parser = md_parser;
        this.pack_manager = pack_manager;
    }

    public static Pair<Integer, Integer> getUserDestID(@NotNull Context context, PackManager pack_manager) throws SQLException {
        Integer user_id = context.sessionAttribute("user_id");
        if (user_id == null) {
            Renderer.renderError(context, "Could not find user ID!");
            return null;
        }

        String dest_id_str = context.queryParam("dest-id");
        if (dest_id_str == null) {
            Renderer.renderError(context, Identifier.noIDMessage("destination pack"));
            return null;
        }
        int dest_id;
        try {
            dest_id = Integer.parseInt(dest_id_str);
        } catch (NumberFormatException e) {
            Renderer.renderError(context, Identifier.notIntegerMessage("destination pack"));
            return null;
        }
        if (!pack_manager.hasID(dest_id) || !pack_manager.isPackCreator(dest_id, user_id)) {
            Renderer.renderError(context, Identifier.resourceDoesNotExistMessage("destination pack"));
            return null;
        }

        return new Pair<>(user_id, dest_id);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        Pair<Integer, Integer> user_dest_id = getUserDestID(context, pack_manager);
        if (user_dest_id == null) {
            return;
        }

        Identifier pack_id = new Identifier(
                context, pack_manager,
                "pack_id", "pack",
                user_dest_id.getA()
        );
        if (pack_id.hasFailed()) {
            Renderer.renderError(context, pack_id.getErrorMessage());
            return;
        }

        if (pack_id.getID() == user_dest_id.getB()) {
            Renderer.renderError(context, "Pack cannot steal its own cards!");
            return;
        }

        List<Card> card_thumbnails = pack_manager.getPackCards(pack_id.getID());
        List<CardThumbnail> cards = new ArrayList<>();
        for (Card card_thumbnail : card_thumbnails) {
            boolean selected = pack_manager.containsCard(user_dest_id.getB(), card_thumbnail.getID());
            cards.add(new CardThumbnail(
                    card_thumbnail.getID(),
                    md_parser.markdownToHTML(card_thumbnail.getFront()),
                    card_thumbnail.getFrontColor(),
                    selected
            ));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("cards", cards);
        model.put("pack_id", pack_id.getID());
        model.put("dest_id", user_dest_id.getB());
        Renderer.render(context, "/templates/card/card_select.ftl", model);
    }
}
