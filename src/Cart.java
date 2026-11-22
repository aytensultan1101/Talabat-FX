import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** The in-memory shopping cart. A cart can only hold items from one restaurant at a time. */
public final class Cart {

    /** Item to quantity, in the order items were first added. */
    private static final Map<MenuItem, Integer> items = new LinkedHashMap<>();
    private static Integer restaurantId = null;

    private Cart() {}

    /**
     * Adds one unit of {@code item}.
     *
     * @return {@code false} if the cart already holds items from a different restaurant
     */
    public static boolean addItem(MenuItem item, int restId) {
        if (restaurantId != null && restaurantId != restId) {
            return false;
        }
        restaurantId = restId;
        items.merge(item, 1, Integer::sum);
        return true;
    }

    public static Map<MenuItem, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public static int getItemCount() {
        int count = 0;
        for (int qty : items.values()) {
            count += qty;
        }
        return count;
    }

    public static boolean isEmpty() {
        return items.isEmpty();
    }

    public static double getTotal() {
        double total = 0;
        for (Map.Entry<MenuItem, Integer> e : items.entrySet()) {
            total += e.getKey().getPrice() * e.getValue();
        }
        return total;
    }

    public static void clear() {
        items.clear();
        restaurantId = null;
    }
}
