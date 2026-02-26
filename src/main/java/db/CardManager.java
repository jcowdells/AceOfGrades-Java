package db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CardManager {
    private final DataSource data_source;

    public CardManager(DataSource data_source) throws SQLException {
        this.data_source = data_source;
        try (Connection connection = data_source.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tblCard (id INTEGER PRIMARY KEY, pack_id INTEGER, front TEXT, back TEXT, front_color TEXT, back_color TEXT, FOREIGN KEY(pack_id) REFERENCES tblPack(id));"
            );
        }
    }

    public void createCard(int pack_id, String front, String back, String front_color, String back_color) throws SQLException {
        try (Connection connection = data_source.getConnection()) {
            PreparedStatement p_statement = connection.prepareStatement(
                    "INSERT INTO tblCard(pack_id, front, back, front_color, back_color) VALUES(?, ?, ?, ?, ?);"
            );
            p_statement.setInt(1, pack_id);
            p_statement.setString(2, front);
            p_statement.setString(3, back);
            p_statement.setString(4, front_color);
            p_statement.setString(5, back_color);
            p_statement.execute();
        }
    }
}
