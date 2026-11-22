import java.util.HashMap;
import java.util.Map;

public class Cart {

    private static HashMap<MenuItem, Integer> items = new HashMap<>();
    private static Integer restaurantId = null;

    public static boolean addItem(MenuItem item, int restId) {

        if (restaurantId == null)
            restaurantId = restId;

        if (!restaurantId.equals(restId))
            return false; // ❌ different restaurant

        items.put(item, items.getOrDefault(item, 0) + 1);
        return true;
    }

    public static HashMap<MenuItem, Integer> getItems() {
        return items;
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
