import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RestaurantDAO {

    // 1️⃣ Get all restaurant names
    public static ArrayList<String> getAllRestaurants() {

        ArrayList<String> restaurants = new ArrayList<>();
        String sql = "SELECT name FROM restaurants";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                restaurants.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return restaurants;
    }

    // 2️⃣ Get restaurant ID by name (IMPORTANT)
    public static int getRestaurantIdByName(String name) {

        String sql = "SELECT id FROM restaurants WHERE name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // not found
    }
}
