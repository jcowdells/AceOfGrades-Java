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

        app.beforeMatched(new AogAccessHandler());
        app.before(new BaseHandler(user_manager));

        app.get("/", new IndexHandler());
        app.get("/register/", new RegisterHandler());
        app.get("/login/", new LoginHandler());
        app.get("/quiz/", new QuizHandler());
        app.get("/create_pack/", new CreatePackHandler(), AogRole.USER, AogRole.ADMIN);
        app.get("/create_card/", new CreateCardHandler(pack_manager), AogRole.USER, AogRole.ADMIN);

        app.post("/api/register/", new RegisterApiHandler(user_manager));
        app.post("/api/login/", new LoginApiHandler(user_manager));
        app.get("/api/logout/", new LogoutApiHandler());
        app.post("/api/create_pack/", new CreatePackApiHandler(pack_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/api/create_card/", new CreateCardApiHandler(pack_manager, card_manager), AogRole.USER, AogRole.ADMIN);
        app.post("/api/get_pack/", new GetPackApiHandler(pack_manager));

        app.start(4409);
    }

}
