package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import aog.User;
import aog.UserLeaderboard;
import aog.UserStats;
import auth.AogRole;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;

public class UserManager implements DBManager {
    private final DataSource data_source;

    public UserManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblUser (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, passwordHash TEXT, emailAddress TEXT, role TEXT);"
            );
        }
    }

    @Override
    public boolean hasID(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblUser WHERE id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    @Override
    public boolean canAccessID(int id, int user_id) {
        return id == user_id;
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
            return new User(user_id, name, email, AogRole.valueOf(role));
        }
    }

    public List<User> getAllUsers() throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT id, username, emailAddress, role FROM tblUser");
            List<User> user_data = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt(1);
                String name = result.getString(2);
                String email = result.getString(3);
                String role = result.getString(4);
                user_data.add(new User(id, name, email, AogRole.valueOf(role)));
            }
            return user_data;
        }
    }

    public UserStats getUserStats(int user_id)  throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            int num_cards = 0;
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT COUNT(tblCard.id) FROM tblCard INNER JOIN tblPack ON tblPack.id = tblCard.pack_id WHERE creator_id = ?"
            )) {
                p_statement.setInt(1, user_id);
                ResultSet result = p_statement.executeQuery();
                if (result.next())
                    num_cards = result.getInt(1);
            }

            int num_attempts = 0;
            int num_correct = 0;
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT SUM(attempts), SUM(correct) FROM tblCardStats WHERE user_id = ?"
            )) {
                p_statement.setInt(1, user_id);
                ResultSet result = p_statement.executeQuery();
                if (result.next()) {
                    num_attempts = result.getInt(1);
                    num_correct = result.getInt(2);
                }
            }

            int num_packs = 0;
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT COUNT(id) FROM tblPack WHERE creator_id = ?"
            )) {
                p_statement.setInt(1, user_id);
                ResultSet result = p_statement.executeQuery();
                if (result.next()) {
                    num_packs = result.getInt(1);
                }
            }

            int best_card = 0;
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT card_id, (attempts * 1.0 / correct) AS ratio FROM tblCardStats WHERE user_id = ? ORDER BY ratio LIMIT 1"
            )) {
                p_statement.setInt(1, user_id);
                ResultSet result = p_statement.executeQuery();
                if (result.next()) {
                    best_card = result.getInt(1);
                }
            }

            int worst_card = 0;
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT card_id, (attempts * 1.0 / correct) AS ratio FROM tblCardStats WHERE user_id = ? ORDER BY ratio DESC LIMIT 1"
            )) {
                p_statement.setInt(1, user_id);
                ResultSet result = p_statement.executeQuery();
                if (result.next()) {
                    worst_card = result.getInt(1);
                }
            }

            return new UserStats(
                    num_cards, num_packs,
                    num_attempts, num_correct,
                    best_card, worst_card
            );
        }
    }

    public List<UserLeaderboard> getLeaderboard() throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            List<UserLeaderboard> leaderboard = new ArrayList<>();
            try (PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT username, SUM(attempts) AS num FROM tblCardStats INNER JOIN tblUser ON id = user_id GROUP BY id ORDER BY num DESC"
            )) {
                ResultSet result = p_statement.executeQuery();
                while (result.next()) {
                    leaderboard.add(new UserLeaderboard(
                            result.getString(1),
                            result.getInt(2)
                    ));
                }
            }
            return leaderboard;
        }
    }
}
