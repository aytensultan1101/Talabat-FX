import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class OrderDAO {

    public static int insertOrder(int customerId, int driverId, double total) {

        String sql =
            "INSERT INTO orders (customer_id, driver_id, order_date, total) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, customerId);
            ps.setInt(2, driverId);
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps.setDouble(4, total);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
