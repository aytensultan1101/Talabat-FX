import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DriverDAO {

    public record Driver(int id, String name) {}

    private DriverDAO() {}

    /**
     * Picks a random driver to deliver an order.
     *
     * @throws SQLException if there are no drivers in the database
     */
    public static Driver findRandomDriver(Connection con) throws SQLException {

        String sql =
            "SELECT u.id, u.name " +
            "FROM drivers d " +
            "JOIN users u ON u.id = d.user_id " +
            "ORDER BY RAND() LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Driver(rs.getInt("id"), rs.getString("name"));
            }
        }
        throw new SQLException("No drivers available to deliver the order");
    }
}
