import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/** Turns the current cart into a saved order. */
public final class CheckoutService {

    private CheckoutService() {}

    /**
     * Saves the customer, order, order items and payment, then clears the cart.
     *
     * <p>All writes happen in a single transaction: if any step fails, nothing is
     * saved and the cart is left untouched so the user can try again.
     *
     * @return the name of the driver assigned to the order
     * @throws SQLException if the database is unreachable, no driver is available, or any insert fails
     */
    public static String checkout(String name, String email, String address, String paymentMethod)
            throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId = UserDAO.insertUser(con, name, email, "customer");
                CustomerDAO.insertCustomer(con, userId, address);

                DriverDAO.Driver driver = DriverDAO.findRandomDriver(con);

                int orderId = OrderDAO.insertOrder(con, userId, driver.id(), Cart.getTotal());
                for (Map.Entry<MenuItem, Integer> entry : Cart.getItems().entrySet()) {
                    OrderItemDAO.insertOrderItem(con, orderId, entry.getKey().getId(), entry.getValue());
                }

                PaymentDAO.insertPayment(con, orderId, paymentMethod);

                con.commit();
                Cart.clear();
                return driver.name();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
