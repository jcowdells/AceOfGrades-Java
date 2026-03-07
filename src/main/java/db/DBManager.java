package db;

import java.sql.SQLException;

public interface DBManager {

    boolean hasID(int id) throws SQLException;
    boolean canAccessID(int id, int user_id) throws SQLException;

}
