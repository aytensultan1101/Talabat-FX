import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class UserDAO {

    private UserDAO() {}

    /** Inserts a user and returns its generated ID. */
    public static int insertUser(Connection con, String name, String email, String role) throws SQLException {

        String sql = "INSERT INTO users (name, email, role) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, role);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Inserting user failed: no ID was generated");
    }
}
