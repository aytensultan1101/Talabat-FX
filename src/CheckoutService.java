public class CheckoutService {

    public static String checkout(
            String name,
            String email,
            String address,
            String paymentMethod
    ) {

        try {
            // 1️⃣ Save user
            int userId = UserDAO.insertUser(name, email, "customer");

            // 2️⃣ Save customer
            CustomerDAO.insertCustomer(userId, address);

            // 3️⃣ Assign driver
            int driverId = DriverDAO.getRandomDriverId();
            String driverName = DriverDAO.getDriverName(driverId);

            // 4️⃣ Save order
            double total = Cart.getTotal();
            int orderId = OrderDAO.insertOrder(userId, driverId, total);

            // 5️⃣ Save order items (with quantity)
            for (MenuItem item : Cart.getItems().keySet()) {
                int qty = Cart.getItems().get(item);
                OrderItemDAO.insertOrderItem(
                        orderId,
                        item.getId(),
                        qty
                );
            }

            // 6️⃣ Save payment
            PaymentDAO.insertPayment(orderId, paymentMethod);

            // 7️⃣ Clear cart
            Cart.clear();

            // ✅ RETURN DRIVER NAME (THIS IS IMPORTANT)
            return driverName;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown driver";
        }
    }
}
