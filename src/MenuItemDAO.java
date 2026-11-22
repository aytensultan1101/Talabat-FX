import java.sql.*;
import java.util.ArrayList;

public class MenuItemDAO {

    public static ArrayList<MenuItem> getMenuItemsByRestaurant(String restaurantName) {

        ArrayList<MenuItem> list = new ArrayList<>();

        String sql =
            "SELECT m.id, m.name, m.price " +
            "FROM menu_items m " +
            "JOIN restaurants r ON m.restaurant_id = r.id " +
            "WHERE r.name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, restaurantName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(
                    new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                    )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
