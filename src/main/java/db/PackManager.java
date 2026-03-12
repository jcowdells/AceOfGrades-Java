package db;

import aog.Card;
import aog.Pack;
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

    public void createPack(int creator_id, String name, String description, String front_color, String back_color, boolean is_public) throws SQLException {
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
                return;
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

    public boolean isPackOwner(int pack_id, int user_id) throws SQLException {
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

    public List<Card> getPackCards(int pack_id) throws SQLException {
        Pair<String, String> pack_color = getPackColor(pack_id);
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.id, tblCard.front, tblCard.back, tblCard.front_color, tblCard.back_color FROM tblCard INNER JOIN tblCardLink ON tblCardLink.card_id = tblCard.id WHERE tblCardLink.pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();

            List<Card> card_list = new ArrayList<>();
            while (result.next()) {
                int card_id = result.getInt(1);
                String front = result.getString(2);
                String back = result.getString(3);
                String front_color = result.getString(4);
                String back_color = result.getString(5);

                if (front_color == null || front_color.isEmpty())
                    front_color = pack_color.getA();

                if (back_color == null || back_color.isEmpty())
                    back_color = pack_color.getB();

                card_list.add(new Card(
                        card_id,
                        front, back,
                        front_color, back_color
                ));
            }
            return card_list;
        }
    }

    private List<Pack> getPackList(ResultSet result) throws SQLException {
        List<Pack> packs = new ArrayList<>();
        while (result.next()) {
            int pack_id = result.getInt(1);
            String name = result.getString(2);
            String description = result.getString(3);
            String front_color = result.getString(4);
            String back_color = result.getString(5);
            String username = result.getString(6);

            packs.add(new Pack(
                    pack_id, name, description,
                    front_color, back_color, username
            ));
        }
        return packs;
    }

    public List<Pack> getUserCreatedPacks(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblUser ON creator_id = tblUser.id WHERE creator_id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }

    public List<Pack> getUserPacks(int user_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblPackLink ON tblPackLink.pack_id = tblPack.id INNER JOIN tblUser ON tblPack.creator_id = tblUser.id WHERE tblPackLink.user_id = ?"
            );
            p_statement.setInt(1, user_id);
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }

    public List<Pack> getPublicPacks() throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblPack.id, name, description, front_color, back_color, username FROM tblPack INNER JOIN tblUser ON tblPack.creator_id = tblUser.id WHERE is_public = 1"
            );
            ResultSet result = p_statement.executeQuery();
            return getPackList(result);
        }
    }
}
