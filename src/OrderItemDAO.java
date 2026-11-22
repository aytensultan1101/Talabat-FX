import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class OrderItemDAO {

    private OrderItemDAO() {}

    public static void insertOrderItem(Connection con, int orderId, int menuItemId, int quantity)
            throws SQLException {

        String sql =
            "INSERT INTO order_items (order_id, menu_item_id, quantity) " +
            "VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }
}
