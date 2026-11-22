import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class PaymentDAO {

    private PaymentDAO() {}

    public static void insertPayment(Connection con, int orderId, String method) throws SQLException {

        String sql = "INSERT INTO payments (order_id, method) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, method);
            ps.executeUpdate();
        }
    }
}
