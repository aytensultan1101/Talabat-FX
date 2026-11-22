import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerDAO {

    public static void insertCustomer(int userId, String address) {

        String sql = "INSERT INTO customers (user_id, address) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, address);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
