package db;

import aog.Card;
import core.Pair;

import javax.sql.DataSource;
import java.sql.*;

public class CardManager implements DBManager {
    private final DataSource data_source;

    public CardManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblCard (id INTEGER PRIMARY KEY AUTOINCREMENT, pack_id INTEGER, front TEXT, back TEXT, front_color TEXT, back_color TEXT, FOREIGN KEY(pack_id) REFERENCES tblPack(id));"
            );
        }
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblCardLink (card_id INTEGER, pack_id INTEGER, PRIMARY KEY(card_id, pack_id), FOREIGN KEY (card_id) REFERENCES tblCard(id), FOREIGN KEY (pack_id) REFERENCES tblPack(id));"
            );
        }
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblCardStats (card_id INTEGER, user_id INTEGER, attempts INTEGER, correct INTEGER, PRIMARY KEY(card_id, user_id), FOREIGN KEY (card_id) REFERENCES tblCard(id), FOREIGN KEY (user_id) REFERENCES tblUser(id));"
            );
        }
    }

    @Override
    public boolean hasID(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblCard WHERE id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    @Override
    public boolean canAccessID(int card_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.creator_id, tblPack.is_public FROM tblPack INNER JOIN tblCard ON tblPack.id = tblCard.pack_id WHERE tblCard.id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            int creator_id = result.getInt(1);
            int is_public = result.getInt(2);
            if (is_public == 1) return true;
            return creator_id == user_id;
        }
    }

    public Integer getPackID(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT pack_id FROM tblCard WHERE id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            return result.getInt(1);
        }
    }

    public boolean canEditCard(int card_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.creator_id FROM tblPack INNER JOIN tblCard ON tblPack.id = tblCard.pack_id WHERE tblCard.id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            return result.getInt(1) == user_id;
        }
    }

    public void createCard(int pack_id, String front, String back, String front_color, String back_color) throws SQLException {
        int card_id;
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblCard(pack_id, front, back, front_color, back_color) VALUES(?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            p_statement.setInt(1, pack_id);
            p_statement.setString(2, front);
            p_statement.setString(3, back);
            p_statement.setString(4, front_color);
            p_statement.setString(5, back_color);
            p_statement.executeUpdate();
            ResultSet result = p_statement.getGeneratedKeys();
            if (!result.next())
                return;
            card_id = result.getInt(1);
        }
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblCardLink(card_id, pack_id) VALUES(?, ?);"
            );
            p_statement.setInt(1, card_id);
            p_statement.setInt(2, pack_id);
            p_statement.executeUpdate();
        }
    }

    public Pair<String, String> getPackColor(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.front_color, tblPack.back_color FROM tblPack INNER JOIN tblCard ON tblPack.id = tblCard.pack_id WHERE tblCard.id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            String front_color = result.getString(1);
            String back_color = result.getString(2);
            return new Pair<>(front_color, back_color);
        }
    }

    public Card getCard(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            // attempt to get card data
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT front, back, tblCard.front_color, tblCard.back_color, creator_id FROM tblCard INNER JOIN tblPack ON tblCard.pack_id = tblPack.id WHERE tblCard.id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            String front = result.getString(1);
            String back = result.getString(2);
            String front_color = result.getString(3);
            String back_color = result.getString(4);
            int creator_id = result.getInt(5);

            // check if card colour is not set, if so get the pack colour
            boolean front_empty = front_color == null || front_color.isEmpty();
            boolean back_empty = back_color == null || back_color.isEmpty();

            if (front_empty || back_empty) {
                Pair<String, String> pack_color = getPackColor(card_id);
                if (pack_color == null)
                    return null;
                if (front_empty)
                    front_color = pack_color.getA();
                if (back_empty)
                    back_color = pack_color.getB();
            }

            return new Card(card_id, front, back, front_color, back_color, creator_id);
        }
    }

    public boolean hasAttemptedCard(int user_id, int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblCardStats WHERE card_id = ? AND user_id = ?"
            );
            p_statement.setInt(1, card_id);
            p_statement.setInt(2, user_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    public void updateCardStats(int card_id, int user_id, int attempts, int correct) throws SQLException {
        if (!hasAttemptedCard(user_id, card_id)) {
            try (Connection connection = data_source.getConnection()) {
                PreparedStatement p_statement = connection.prepareStatement(
                        "INSERT INTO tblCardStats (card_id, user_id, attempts, correct) VALUES (?, ?, ?, ?)"
                );
                p_statement.setInt(1, card_id);
                p_statement.setInt(2, user_id);
                p_statement.setInt(3, attempts);
                p_statement.setInt(4, correct);
                p_statement.executeUpdate();
            }
        } else {
            try (Connection connection = data_source.getConnection()) {
                PreparedStatement p_statement = connection.prepareStatement(
                        "UPDATE tblCardStats SET attempts = attempts + ?, correct = correct + ? WHERE card_id = ? AND user_id = ?"
                );
                p_statement.setInt(1, attempts);
                p_statement.setInt(2, correct);
                p_statement.setInt(3, card_id);
                p_statement.setInt(4, user_id);
                p_statement.executeUpdate();
            }
        }
    }

    public void editCard(int card_id, String front, String back, String front_color, String back_color) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "UPDATE tblCARD SET front = ?, back = ?, front_color = ?, back_color = ? WHERE id = ?"
            );
            p_statement.setString(1, front);
            p_statement.setString(2, back);
            p_statement.setString(3, front_color);
            p_statement.setString(4, back_color);
            p_statement.setInt(5, card_id);
            p_statement.executeUpdate();
        }
    }

    public boolean isPublic(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT is_public FROM tblCard INNER JOIN tblPack ON tblPack.id = tblCard.pack_id WHERE tblCard.id = ?"
            );
            p_statement.setInt(1, card_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            return result.getInt(1) == 1;
        }
    }

    public void deleteCard(int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // delete card from link table
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCardLink WHERE card_id = ?"
                )) {
                    p_statement.setInt(1, card_id);
                    p_statement.executeUpdate();
                }

                // delete card from stats table
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCardStats WHERE card_id = ?"
                )) {
                    p_statement.setInt(1, card_id);
                    p_statement.executeUpdate();
                }

                // delete card data
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCard WHERE id = ?"
                )) {
                    p_statement.setInt(1, card_id);
                    p_statement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
