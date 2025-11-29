package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import aog.User;
import auth.AogRole;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;

public class UserManager {
    private final DataSource data_source;

    public UserManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblUser (id INTEGER PRIMARY KEY, username TEXT UNIQUE, passwordHash TEXT, emailAddress TEXT, role TEXT);"
            );
        }
    }

    public boolean hasUser(String username) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblUser WHERE username = ?"
            );
            p_statement.setString(1, username);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    public void createUser(String username, String password, String email_address, AogRole role) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblUser(username, passwordHash, emailAddress, role) VALUES(?, ?, ?, ?);"
            );
            p_statement.setString(1, username);
            p_statement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            p_statement.setString(3, email_address);
            p_statement.setString(4, role.toString());
            p_statement.execute();
        }
    }

    public int getUserID(String username) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT id FROM tblUser WHERE username = ?"
            );
            p_statement.setString(1, username);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return -1;
            return result.getInt(1);
        }
    }

    public boolean checkPassword(int user_id, String password) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
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
            String check_hash = result.getString(1);
            return BCrypt.checkpw(password, check_hash);
        }
    }

    public User getUserData(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT username, emailAddress, role FROM tblUser WHERE id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;

            String name = result.getString(1);
            String email = result.getString(2);
            String role = result.getString(3);
            return new User(name, email, AogRole.valueOf(role));
        }
    }

    public List<User> getAllUsers() throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT username, emailAddress, role FROM tblUser");
            List<User> user_data = new ArrayList<>();
            while (result.next()) {
                String name = result.getString(1);
                String email = result.getString(2);
                String role = result.getString(3);
                user_data.add(new User(name, email, AogRole.valueOf(role)));
            }
            return user_data;
        }
    }
}
