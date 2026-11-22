import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentDAO {

    public static void insertPayment(int orderId, String method) {

        String sql = "INSERT INTO payments (order_id, method) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, method);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
