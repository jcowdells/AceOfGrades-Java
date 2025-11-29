package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTable {

    public static void setup(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS tblUser (id INTEGER PRIMARY KEY, username TEXT UNIQUE, passwordHash INTEGER, emailAddress TEXT);"
        );
    }

    public static boolean hasUser(Connection connection, String username) throws SQLException {
        PreparedStatement p_statement = connection.prepareStatement(
                "SELECT 1 FROM tblUser WHERE username = ?"
        );
        p_statement.setString(1, username);
        ResultSet result = p_statement.executeQuery();
        return result.next();
    }

    public static void createUser(Connection connection, String username, String password, String email_address) throws SQLException {
        PreparedStatement p_statement = connection.prepareStatement(
                "INSERT INTO tblUser(username, passwordHash, emailAddress) VALUES(?, ?, ?);"
        );
        p_statement.setString(1, username);
        p_statement.setLong(2, AuthKey.hash(password.getBytes()));
        p_statement.setString(3, email_address);
        p_statement.execute();
    }

    public static int getUserID(Connection connection, String username) throws SQLException {
        PreparedStatement p_statement = connection.prepareStatement(
                "SELECT id FROM tblUser WHERE username = ?"
        );
        p_statement.setString(1, username);
        ResultSet result = p_statement.executeQuery();
        if (!result.next())
            return -1;
        return result.getInt(1);
    }

    public static boolean checkPassword(Connection connection, int user_id, String password) throws SQLException {
        // get password hash
        int password_hash = AuthKey.hash(password.getBytes());

        // grab check hash from database
        PreparedStatement p_statement = connection.prepareStatement(
                "SELECT passwordHash FROM tblUser WHERE id = ?"
        );
        p_statement.setInt(1, user_id);
        ResultSet result = p_statement.executeQuery();

        // if no user, then cannot match
        if (!result.next())
            return false;

        // check hash matches
        int check_hash = result.getInt(1);
        return check_hash == password_hash;
    }

    public static UserData getUserData(Connection connection, int user_id) throws SQLException {
        PreparedStatement p_statement = connection.prepareStatement(
                "SELECT username, emailAddress FROM tblUser WHERE id = ?"
        );
        p_statement.setInt(1, user_id);
        ResultSet result = p_statement.executeQuery();
        if (!result.next())
            return null;

        String name = result.getString(1);
        String email = result.getString(2);
        return new UserData(name, email);
    }

    public static List<UserData> getAllUsers(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT username, emailAddress FROM tblUser");
        List<UserData> user_data = new ArrayList<>();
        while (result.next()) {
            user_data.add(new UserData(result.getString(1), result.getString(2)));
        }
        return user_data;
    }
}
