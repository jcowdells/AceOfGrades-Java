import aog.JsonString;
import aog.MarkdownHTML;
import api_handlers.*;
import auth.AogAccessHandler;
import auth.AogRole;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import db.CardManager;
import db.PackManager;
import db.SpotlightManager;
import db.UserManager;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import handlers.*;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        // load da config
        String db_path = args.length > 0 ? args[0] : "aog.sqlite3";

        // create da database
        HikariConfig hikari_config = new HikariConfig();
        hikari_config.setJdbcUrl("jdbc:sqlite:" + db_path);
        DataSource data_source = new HikariDataSource(hikari_config);
        UserManager user_manager = new UserManager(data_source);
        PackManager pack_manager = new PackManager(data_source);
        CardManager card_manager = new CardManager(data_source);
        SpotlightManager spotlight_manager = new SpotlightManager(data_source);
        MarkdownHTML md_parser = new MarkdownHTML();
        JsonString json_string = new JsonString();

        // create da app
        Javalin app = Javalin.create(config -> {
            Configuration fm_config = new Configuration(Configuration.getVersion());
            fm_config.setClassLoaderForTemplateLoading(
                    Thread.currentThread().getContextClassLoader(),
                    "/"
            );
            fm_config.setBooleanFormat("c");
            fm_config.setOutputFormat(HTMLOutputFormat.INSTANCE);
            fm_config.setAutoEscapingPolicy(Configuration.ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY);
            config.fileRenderer(new JavalinFreemarker(fm_config));
            config.staticFiles.add(static_files -> {
                static_files.hostedPath = "/static";
                static_files.directory = "static";
            });
            config.staticFiles.add(static_files -> {
                static_files.hostedPath = "/";
                static_files.directory = "favicon";
            });
        });

        // javalin
        app.beforeMatched(new AogAccessHandler(user_manager));
        app.before(new BaseHandler(user_manager));

        // all html shenanigans
        // pages
        app.get("/", new IndexHandler());
        app.get("/register/", new RegisterHandler());
        app.get("/login/", new LoginHandler());
        app.get("/packs/create/", new PacksCreateHandler(), AogRole.USER, AogRole.ADMIN);
        app.get("/explore/", new PacksExploreHandler(pack_manager));
        app.get("/packs/", new PacksHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/", new PacksViewHandler(pack_manager, spotlight_manager));
        app.get("/packs/{pack_id}/edit/", new PacksEditHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/create/", new PacksCardsCreateHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/quiz/", new PacksQuizHandler(pack_manager));
        app.get("/packs/{pack_id}/cards/steal/select/", new PacksCardsStealSelectHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/steal/", new PacksCardsStealHandler(pack_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/move/select/", new PacksCardsMoveSelectHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/move/", new PacksCardsMoveHandler(pack_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/edit/", new PacksCardsEditHandler(pack_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/delete", new PacksDeleteHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/cards/delete", new PacksCardsDeleteHandler(pack_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/spotlights/create", new PacksSpotlightCreateHandler(pack_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/cards/{card_id}/edit/", new CardsEditHandler(card_manager, pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/cards/{card_id}/delete/", new CardsDeleteHandler(card_manager, md_parser), AogRole.USER, AogRole.ADMIN);
        app.get("/profiles/{user_id}/", new ProfilesHandler(user_manager, card_manager, md_parser));
        app.get("/leaderboard/", new LeaderboardHandler(user_manager));

        // all api shenanigans
        // forms
        app.post("/forms/register/", new RegisterApiHandler(user_manager));
        app.post("/forms/login/", new LoginApiHandler(user_manager));
        app.post("/forms/logout/", new LogoutApiHandler());
        app.post("/forms/packs/create/", new PacksCreateApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/edit", new PacksEditApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/cards/create/", new PacksCardsCreateApiHandler(pack_manager, card_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/cards/steal/", new PacksCardsStealApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/quiz/start/", new PacksQuizStartApiHandler(pack_manager));
        app.post("/forms/packs/{pack_id}/delete/", new PacksDeleteApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/spotlights/create", new PacksSpotlightCreateApiHandler(spotlight_manager, pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/forms/cards/{card_id}/edit/", new CardsEditFragmentHandler(card_manager, pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/cards/{card_id}/edit/", new CardsEditApiHandler(card_manager, pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/cards/{card_id}/delete/", new CardsDeleteApiHandler(card_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/cards/move", new PacksCardsMoveApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);

        // api, javascript/json focused
        app.post("/api/packs/{pack_id}/cards/", new PacksGetCardsHandler(pack_manager, spotlight_manager, md_parser, json_string));
        app.post("/api/packs/{pack_id}/quiz/complete/", new PacksQuizCompleteApiHandler(card_manager, json_string), AogRole.USER, AogRole.ADMIN);
        app.post("/api/cards/{card_id}/", new CardsApiHandler(card_manager, md_parser));
        app.post("/api/spotlights/{spotlight_id}/delete", new SpotlightsDeleteApiHandler(spotlight_manager), AogRole.USER, AogRole.ADMIN);

        app.start(4409);
    }

}
