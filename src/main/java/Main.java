import aog.MarkdownHTML;
import api_handlers.*;
import auth.AogAccessHandler;
import auth.AogRole;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import db.CardManager;
import db.PackManager;
import db.UserManager;
import freemarker.template.Configuration;
import handlers.*;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        // create da database
        HikariConfig hikari_config = new HikariConfig();
        hikari_config.setJdbcUrl("jdbc:sqlite:aog.sqlite3");
        DataSource data_source = new HikariDataSource(hikari_config);
        UserManager user_manager = new UserManager(data_source);
        PackManager pack_manager = new PackManager(data_source);
        CardManager card_manager = new CardManager(data_source);
        MarkdownHTML md_parser = new MarkdownHTML();

        // create da app
        Javalin app = Javalin.create(config -> {
            Configuration fm_config = new Configuration(Configuration.getVersion());
            fm_config.setClassLoaderForTemplateLoading(
                    Thread.currentThread().getContextClassLoader(),
                    "/"
            );
            fm_config.setBooleanFormat("c");
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
        app.get("/explore/", new ExploreHandler(pack_manager));
        app.get("/packs/", new PacksHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/view/", new PacksViewHandler(pack_manager));
        app.get("/packs/{pack_id}/cards/create/", new PacksCardsCreateHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/packs/{pack_id}/quiz/", new PacksQuizHandler(pack_manager));

        // all api shenanigans
        // forms
        app.post("/forms/register/", new RegisterApiHandler(user_manager));
        app.post("/forms/login/", new LoginApiHandler(user_manager));
        app.post("/forms/logout/", new LogoutApiHandler());
        app.post("/forms/packs/create/", new PacksCreateApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/forms/packs/{pack_id}/cards/create/", new PacksCardsCreateApiHandler(pack_manager, card_manager), AogRole.USER, AogRole.ADMIN);
        app.get("/forms/cards/{card_id}/edit/", new CardsEditFragmentHandler(card_manager));
        app.post("/forms/cards/{card_id}/edit/", new CardsEditApiHandler(card_manager));

        // api, javascript/json focused
        app.post("/api/packs/{pack_id}/cards/", new PacksGetCardsHandler(pack_manager, md_parser));

        app.start(4409);
    }

}
