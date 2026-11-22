/** A dish or drink on a restaurant's menu. Two items are equal when they share the same database ID. */
public class MenuItem {

    private final int id;
    private final String name;
    private final double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        MenuItem m = (MenuItem) o;
        return id == m.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
