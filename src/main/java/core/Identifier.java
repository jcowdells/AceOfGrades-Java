package core;

import db.DBManager;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Identifier {

    private final int id;
    private final String error_message;
    private final boolean failed;

    public Identifier(@NotNull Context context, DBManager db_manager, String param_name, String resource_name, int user_id) {
        String identifier = context.pathParam(param_name);

        // firstly, make sure that the identifier exists
        if (identifier == null) {
            error_message = "No " + resource_name + " ID provided!";
            id = -1;
            failed = true;
            return;
        }

        // next, attempt to get it into integer form
        int tmp_id;
        try {
            tmp_id = Integer.parseInt(identifier);
        } catch (NumberFormatException e) {
            error_message = "The " + resource_name + " ID must be an integer!";
            id = -1;
            failed = true;
            return;
        }

        // now check it exists in the database
        boolean exists;
        try {
            exists = db_manager.hasID(tmp_id) && db_manager.canAccessID(tmp_id, user_id);
        } catch (SQLException e) {
            exists = false;
        }
        if (!exists) {
            error_message = "Specified " + resource_name + " does not exist!";
            id = -1;
            failed = true;
            return;
        }

        // if all 3 are good, then it is a valid ID
        error_message = null;
        id = tmp_id;
        failed = false;
    }

    public int getID() {
        return id;
    }

    public String getErrorMessage() {
        return error_message;
    }

    public boolean hasFailed() {
        return failed;
    }

}
