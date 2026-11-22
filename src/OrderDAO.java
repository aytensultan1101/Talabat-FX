import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class OrderDAO {

    private OrderDAO() {}

    /** Inserts an order dated today and returns its generated ID. */
    public static int insertOrder(Connection con, int customerId, int driverId, double total) throws SQLException {

        String sql =
            "INSERT INTO orders (customer_id, driver_id, order_date, total) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, driverId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDouble(4, total);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Inserting order failed: no ID was generated");
    }
}
