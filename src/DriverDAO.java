import java.sql.*;

public class DriverDAO {

    public static int getRandomDriverId() throws Exception {

        String sql = "SELECT user_id FROM drivers ORDER BY RAND() LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1; // ❌ no driver found
    }

    public static String getDriverName(int driverId) throws Exception {

        String sql = "SELECT name FROM users WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return "Unknown driver";
    }
}
