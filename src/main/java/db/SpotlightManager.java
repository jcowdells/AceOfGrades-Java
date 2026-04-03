package db;

import aog.Card;
import aog.Spotlight;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpotlightManager implements DBManager {
    private final DataSource data_source;

    public SpotlightManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
             connection.setAutoCommit(false);
            try {
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS tblSpotlight (id INTEGER PRIMARY KEY AUTOINCREMENT, pack_id INTEGER, name TEXT, FOREIGN KEY(pack_id) REFERENCES tblPack)"
                )) {
                    p_statement.executeUpdate();
                }
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS tblSpotlightLink (spotlight_id INTEGER, card_id INTEGER, PRIMARY KEY (spotlight_id, card_id), FOREIGN KEY (spotlight_id) REFERENCES tblSpotlight, FOREIGN KEY (card_id) REFERENCES tblCard)"
                )) {
                    p_statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean hasID(int spotlight_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblSpotlight WHERE id = ?"
            );
            p_statement.setInt(1, spotlight_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    @Override
    public boolean canAccessID(int spotlight_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT creator_id, is_public FROM tblSpotlight INNER JOIN tblPack ON tblPack.id = tblSpotlight.pack_id WHERE tblSpotlight.id = ?"
            );
            p_statement.setInt(1, spotlight_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            int creator_id = result.getInt(1);
            int is_public = result.getInt(2);
            return is_public == 1 || creator_id == user_id;
        }
    }

    public List<Spotlight> getSpotlights(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT id, name FROM tblSpotlight WHERE pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            List<Spotlight> spotlights = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt(1);
                String name = result.getString(2);
                spotlights.add(new Spotlight(
                   id, name
                ));
            }
            return spotlights;
        }
    }

    public List<Integer> getSpotlightCardIDs(int spotlight_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT card_id FROM tblSpotlightLink WHERE spotlight_id = ?"
            );
            p_statement.setInt(1, spotlight_id);
            ResultSet result = p_statement.executeQuery();
            List<Integer> card_ids = new ArrayList<>();
            while (result.next()) {
                card_ids.add(result.getInt(1));
            }
            return card_ids;
        }
    }

    public int createSpotlight(int pack_id, String name) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblSpotlight (pack_id, name) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            p_statement.setInt(1, pack_id);
            p_statement.setString(2, name);
            p_statement.executeUpdate();
            ResultSet result = p_statement.getGeneratedKeys();
            return result.next() ? result.getInt(1) : -1;
        }
    }

    public void linkCards(int spotlight_id, List<Integer> card_ids) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblSpotlightLink (spotlight_id, card_id) VALUES (?, ?)"
            );
            for (Integer card_id : card_ids) {
                p_statement.setInt(1, spotlight_id);
                p_statement.setInt(2, card_id);
                p_statement.addBatch();
            }
            p_statement.executeBatch();
        }
    }

    public boolean canEditSpotlight(int spotlight_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT creator_id FROM tblPack INNER JOIN tblSpotlight ON tblSpotlight.pack_id = tblPack.id WHERE tblSpotlight.id = ?"
            );
            p_statement.setInt(1, spotlight_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            return result.getInt(1) == user_id;
        }
    }

    public void deleteSpotlight(int spotlight_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblSpotlightLink WHERE spotlight_id = ?"
                )) {
                    p_statement.setInt(1, spotlight_id);
                    p_statement.executeUpdate();
                }

                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblSpotlight WHERE id = ?"
                )) {
                    p_statement.setInt(1, spotlight_id);
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