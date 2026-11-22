import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CustomerDAO {

    private CustomerDAO() {}

    public static void insertCustomer(Connection con, int userId, String address) throws SQLException {

        String sql = "INSERT INTO customers (user_id, address) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, address);
            ps.executeUpdate();
        }
    }
}
