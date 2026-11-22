import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MenuItemDAO {

    private MenuItemDAO() {}

    public static List<MenuItem> getMenuItemsByRestaurant(String restaurantName) throws SQLException {

        List<MenuItem> list = new ArrayList<>();

        String sql =
            "SELECT m.id, m.name, m.price " +
            "FROM menu_items m " +
            "JOIN restaurants r ON m.restaurant_id = r.id " +
            "WHERE r.name = ? " +
            "ORDER BY m.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, restaurantName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MenuItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price")));
                }
            }
        }
        return list;
    }
}
