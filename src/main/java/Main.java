import api_handlers.RegisterApiHandler;
import auth.AogAccessHandler;
import auth.AogRole;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import db.UserManager;
import handlers.BaseHandler;
import handlers.IndexHandler;
import handlers.LoginHandler;
import handlers.RegisterHandler;
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

        // create da app
        Javalin app = Javalin.create(config -> {
           config.fileRenderer(new JavalinFreemarker());
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
        app.post("/api/register/", new RegisterApiHandler(user_manager));
        app.post("/login/", new LoginHandler(user_manager));
        app.start(4409);
    }

}
