import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class RestaurantDAO {

    private RestaurantDAO() {}

    /** Returns the names of all restaurants. */
    public static List<String> getAllRestaurants() throws SQLException {

        List<String> restaurants = new ArrayList<>();
        String sql = "SELECT name FROM restaurants ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                restaurants.add(rs.getString("name"));
            }
        }
        return restaurants;
    }

    /** Returns the ID of the restaurant with the given name, or -1 if there is none. */
    public static int getRestaurantIdByName(String name) throws SQLException {

        String sql = "SELECT id FROM restaurants WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
}
