package db;

import aog.Card;
import aog.CardThumbnail;
import aog.Pack;
import aog.PackThumbnail;
import core.Pair;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackManager implements DBManager {
    private final DataSource data_source;

    public PackManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblPack (id INTEGER PRIMARY KEY AUTOINCREMENT, creator_id INTEGER, name TEXT, description TEXT, front_color TEXT, back_color TEXT, is_public INTEGER, FOREIGN KEY(creator_id) REFERENCES tblUser(id));"
            );
        }
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblPackLink (pack_id INTEGER, user_id INTEGER, PRIMARY KEY(pack_id, user_id), FOREIGN KEY(pack_id) REFERENCES tblPack(id), FOREIGN KEY(user_id) REFERENCES tblUser(id));"
            );
        }
    }

    @Override
    public boolean hasID(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    @Override
    public boolean canAccessID(int pack_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT creator_id, is_public FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            int creator_id = result.getInt(1);
            int is_public = result.getInt(2);
            if (is_public == 1) return true;
            return creator_id == user_id;
        }
    }

    public int createPack(int creator_id, String name, String description, String front_color, String back_color, boolean is_public) throws SQLException {
        int pack_id;
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblPack(creator_id, name, description, front_color, back_color, is_public) VALUES(?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            p_statement.setInt(1, creator_id);
            p_statement.setString(2, name);
            p_statement.setString(3, description);
            p_statement.setString(4, front_color);
            p_statement.setString(5, back_color);
            p_statement.setInt(6, is_public ? 1 : 0);
            p_statement.executeUpdate();
            ResultSet result = p_statement.getGeneratedKeys();
            if (!result.next())
                return -1;
            pack_id = result.getInt(1);
        }
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblPackLink(pack_id, user_id) VALUES(?, ?);"
            );
            p_statement.setInt(1, pack_id);
            p_statement.setInt(2, creator_id);
            p_statement.executeUpdate();
        }
        return pack_id;
    }

    public void updatePack(int pack_id, String name, String description, String front_color, String back_color, boolean is_public) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "UPDATE tblPack SET name = ?, description = ?, front_color = ?, back_color = ?, is_public = ? WHERE id = ?"
            );
            p_statement.setString(1, name);
            p_statement.setString(2, description);
            p_statement.setString(3, front_color);
            p_statement.setString(4, back_color);
            p_statement.setInt(5, is_public ? 1 : 0);
            p_statement.setInt(6, pack_id);
            p_statement.executeUpdate();
        }
    }

    public Pack getPack(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT * FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            int id = result.getInt(1);
            int creator_id = result.getInt(2);
            String name = result.getString(3);
            String description = result.getString(4);
            String front_color = result.getString(5);
            String back_color = result.getString(6);
            int is_public = result.getInt(7);
            return new Pack(id, creator_id, name, description, front_color, back_color, is_public == 1);
        }
    }

    public Pair<String, String> getPackColor(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT front_color, back_color FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return new Pair<>("#000000", "#000000");
            return new Pair<>(result.getString(1), result.getString(2));
        }
    }

    public String getPackName(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT name FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            return result.getString(1);
        }
    }

    public String getPackDescription(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT description FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return null;
            return result.getString(1);
        }
    }

    public boolean isPackCreator(int pack_id, int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT creator_id FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            int creator_id = result.getInt(1);
            return creator_id == user_id;
        }
    }

    private List<Card> generateCardList(ResultSet result) throws SQLException {
        Map<Integer, Pair<String, String>> color_cache = new HashMap<>();

        List<Card> card_list = new ArrayList<>();
        while (result.next()) {
            int card_id = result.getInt(1);
            int origin_pack_id = result.getInt(2);
            String front = result.getString(3);
            String back = result.getString(4);
            String front_color = result.getString(5);
            String back_color = result.getString(6);
            int creator_id = result.getInt(7);

            Pair<String, String> pack_color;
            if (color_cache.containsKey(origin_pack_id)) {
                pack_color = color_cache.get(origin_pack_id);
            } else {
                pack_color = getPackColor(origin_pack_id);
                color_cache.put(origin_pack_id, pack_color);
            }

            if (front_color == null || front_color.isEmpty())
                front_color = pack_color.getA();

            if (back_color == null || back_color.isEmpty())
                back_color = pack_color.getB();

            card_list.add(new Card(
                    card_id,
                    front, back,
                    front_color, back_color,
                    creator_id
            ));
        }
        return card_list;
    }

    public List<Card> getPackCards(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.id, tblCard.pack_id, tblCard.front, tblCard.back, tblCard.front_color, tblCard.back_color, tblPack.creator_id FROM tblCard INNER JOIN tblCardLink ON tblCardLink.card_id = tblCard.id INNER JOIN tblPack ON tblCard.pack_id = tblPack.id WHERE tblCardLink.pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            return generateCardList(result);
        }
    }

    public List<Card> getPackCardsByRatio(int pack_id, int user_id, int num_cards) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.id, tblCard.pack_id, tblCard.front, tblCard.back, tblCard.front_color, tblCard.back_color, (tblCardStats.correct * 1.0 / tblCardStats.attempts) AS ratio FROM tblCard INNER JOIN tblCardLink ON tblCardLink.card_id = tblCard.id INNER JOIN tblCardStats ON tblCardStats.card_id = tblCard.id AND tblCardStats.user_id = ? WHERE tblCardLink.pack_id = ? ORDER BY ratio LIMIT ?"
            );
            p_statement.setInt(1, user_id);
            p_statement.setInt(2, pack_id);
            p_statement.setInt(3, num_cards);
            ResultSet result = p_statement.executeQuery();
            return generateCardList(result);
        }
    }

    public List<Integer> getPackCardIDs(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.id FROM tblCard INNER JOIN tblCardLink ON tblCardLink.card_id = tblCard.id WHERE tblCardLink.pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            List<Integer> card_ids = new ArrayList<>();
            while (result.next()) {
                card_ids.add(result.getInt(1));
            }
            return card_ids;
        }
    }

    private List<PackThumbnail> getPackList(ResultSet result) throws SQLException {
        List<PackThumbnail> packs = new ArrayList<>();
        while (result.next()) {
            int pack_id = result.getInt(1);
            String name = result.getString(2);
            String description = result.getString(3);
            String front_color = result.getString(4);
            String back_color = result.getString(5);
            String username = result.getString(6);

            packs.add(new PackThumbnail(
                    pack_id, name, description,
                    front_color, back_color, username
            ));
        }
        return packs;
    }

    public List<PackThumbnail> getUserCreatedPacks(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblUser ON creator_id = tblUser.id WHERE creator_id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }

    public List<PackThumbnail> getUserPacks(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblPackLink ON tblPackLink.pack_id = tblPack.id INNER JOIN tblUser ON tblPack.creator_id = tblUser.id WHERE tblPackLink.user_id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }

    public List<PackThumbnail> getPublicPacks() throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblUser ON tblPack.creator_id = tblUser.id WHERE is_public = 1"
            );
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }

    public boolean isPublic(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT is_public FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next())
                return false;
            return result.getInt(1) == 1;
        }
    }

    public List<CardThumbnail> getCardThumbnails(int pack_id) throws SQLException {
        Map<Integer, String> color_cache = new HashMap<>();
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.id, tblCard.pack_id, tblCard.front, tblCard.front_color FROM tblCard INNER JOIN tblCardLink ON tblCard.id = tblCardLink.card_id WHERE tblCardLink.pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            List<CardThumbnail> card_thumbnails = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt(1);
                int origin_pack_id = result.getInt(2);
                String front = result.getString(3);
                String front_color = result.getString(4);

                if (front_color == null || front_color.isEmpty()) {
                    if (color_cache.containsKey(origin_pack_id)) {
                        front_color = color_cache.get(origin_pack_id);
                    } else {
                        Pair<String, String> pack_color = getPackColor(origin_pack_id);
                        color_cache.put(origin_pack_id, pack_color.getA());
                        front_color = pack_color.getA();
                    }
                }

                card_thumbnails.add(new CardThumbnail(
                        id, front, front_color
                ));
            }
            return card_thumbnails;
        }
    }

    public boolean containsCard(int pack_id, int card_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblCardLink WHERE card_id = ? AND pack_id = ?"
            );
            p_statement.setInt(1, card_id);
            p_statement.setInt(2, pack_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
        }
    }

    public void linkCards(int pack_id, List<Integer> card_ids) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblCardLink (card_id, pack_id) VALUES (?, ?)"
            );
            for (Integer card_id : card_ids) {
                p_statement.setInt(1, card_id);
                p_statement.setInt(2, pack_id);
                p_statement.addBatch();
            }
            p_statement.executeBatch();
        }
    }

    public void unlinkCards(int pack_id, List<Integer> card_ids) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "DELETE FROM tblCardLink WHERE card_id = ? AND pack_id = ?"
            );
            for (Integer card_id : card_ids) {
                p_statement.setInt(1, card_id);
                p_statement.setInt(2, pack_id);
                p_statement.addBatch();
            }
            p_statement.executeBatch();
        }
    }

    public int getNumCards(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT COUNT(pack_id) FROM tblCardLink WHERE pack_id = ? GROUP BY pack_id"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            if (!result.next()) return 0;
            return result.getInt(1);
        }
    }

    public void deletePack(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // delete occurrences in card link table
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCardLink WHERE pack_id = ? OR card_id IN (SELECT id FROM tblCard WHERE tblCard.pack_id = ?) "
                )) {
                    p_statement.setInt(1, pack_id);
                    p_statement.setInt(2, pack_id);
                    p_statement.executeUpdate();
                }
                // delete card statistics
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCardStats WHERE card_id IN (SELECT id FROM tblCard WHERE tblCard.pack_id = ?)"
                )) {
                    p_statement.setInt(1, pack_id);
                    p_statement.executeUpdate();
                }

                // delete the cards
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCard WHERE pack_id = ?"
                )) {
                    p_statement.setInt(1, pack_id);
                    p_statement.executeUpdate();
                }

                // delete pack links
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblPackLink WHERE pack_id = ?"
                )) {
                    p_statement.setInt(1, pack_id);
                    p_statement.executeUpdate();
                }

                // delete the pack
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblPack WHERE id = ?"
                )) {
                    p_statement.setInt(1, pack_id);
                    p_statement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void changeOwnership(List<Integer> card_ids, int new_pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "DELETE FROM tblCardLink WHERE card_id = ? AND EXISTS (SELECT 1 FROM tblCard WHERE tblCard.id = ? AND tblCard.pack_id = tblCardLink.pack_id)"
                )) {
                    for (Integer card_id : card_ids) {
                        p_statement.setInt(1, card_id);
                        p_statement.setInt(1, card_id);
                        p_statement.addBatch();
                    }
                    p_statement.executeBatch();
                }

                try (PreparedStatement p_statement = connection.prepareStatement(
                        "UPDATE tblCard SET pack_id = ? WHERE id = ?"
                )) {
                    for (Integer card_id : card_ids) {
                        p_statement.setInt(1, new_pack_id);
                        p_statement.setInt(2, card_id);
                        p_statement.addBatch();
                    }
                    p_statement.executeBatch();
                }

                // add to card link if they are not already linked
                try (PreparedStatement p_statement = connection.prepareStatement(
                        "INSERT OR IGNORE INTO tblCardLink (card_id, pack_id) VALUES (?, ?)"
                )) {
                    for (Integer card_id : card_ids) {
                        p_statement.setInt(1, card_id);
                        p_statement.setInt(2, new_pack_id);
                        p_statement.addBatch();
                    }
                    p_statement.executeBatch();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
