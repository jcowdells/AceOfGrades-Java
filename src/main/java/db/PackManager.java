package db;

import core.Pair;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackManager {
    private final DataSource data_source;

    public PackManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblPack (id INTEGER PRIMARY KEY AUTOINCREMENT, creator_id INTEGER, name TEXT, description TEXT, front_color TEXT, back_color TEXT, is_public INTEGER, FOREIGN KEY(creator_id) REFERENCES tblUser(id));"
            );
        }
    }

    public void createPack(int creator_id, String name, String description, String front_color, String back_color, boolean is_public) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblPack(creator_id, name, description, front_color, back_color, is_public) VALUES(?, ?, ?, ?, ?, ?);"
            );
            p_statement.setInt(1, creator_id);
            p_statement.setString(2, name);
            p_statement.setString(3, description);
            p_statement.setString(4, front_color);
            p_statement.setString(5, back_color);
            p_statement.setInt(6, is_public ? 1 : 0);
            p_statement.execute();
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

    public boolean hasPack(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT 1 FROM tblPack WHERE id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();
            return result.next();
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

    public boolean canAccessPack(int pack_id, int user_id) throws SQLException {
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

    public List<Map<String, Object>> getPackCards(int pack_id) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "SELECT tblCard.front, tblCard.back, tblCard.front_color, tblCard.back_color FROM tblCard INNER JOIN tblCardLink ON tblCardLink.card_id = tblCard.id WHERE tblCardLink.pack_id = ?"
            );
            p_statement.setInt(1, pack_id);
            ResultSet result = p_statement.executeQuery();

            List<Map<String, Object>> card_list = new ArrayList<>();
            while (result.next()) {
                String front = result.getString(1);
                String back = result.getString(2);
                String front_color = result.getString(3);
                String back_color = result.getString(4);

                Map<String, Object> card_data = new HashMap<>();
                card_data.put("front", front);
                card_data.put("back", back);
                card_data.put("front_color", front_color);
                card_data.put("back_color", back_color);
                card_list.add(card_data);
            }
            return card_list;
        }
    }
}
