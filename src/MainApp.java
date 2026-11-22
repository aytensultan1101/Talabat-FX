import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/** JavaFX entry point. Screens: restaurants → menu → cart and checkout. */
public class MainApp extends Application {

    private static final String APP_TITLE = "Talabat 💗";
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final String DB_ERROR =
            "⚠ Couldn't reach the database.\nMake sure MySQL is running and db.properties is correct.";

    private final Map<String, Image> imageCache = new HashMap<>();
    private Stage stage;
    private VBox root;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 600, 640);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());

        stage.setTitle(APP_TITLE);
        stage.setMinWidth(480);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();

        showRestaurantScreen();
    }

    /* ================= RESTAURANTS SCREEN ================= */

    private void showRestaurantScreen() {

        ListView<HBox> list = growingList("No restaurants yet 🥲");

        try {
            for (String name : RestaurantDAO.getAllRestaurants()) {

                HBox row = new HBox(25,
                        imageView("/resources/restaurants/" + getRestaurantImage(name), 90),
                        new Label(name));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(15));
                row.setCursor(Cursor.HAND);
                row.setOnMouseClicked(e -> showMenuScreen(name));

                list.getItems().add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            list.setPlaceholder(databaseErrorPlaceholder(this::showRestaurantScreen));
        }

        root.getChildren().setAll(title("🍽️ Choose a Restaurant"), list);
    }

    /* ================= MENU SCREEN ================= */

    private void showMenuScreen(String restaurantName) {

        ListView<HBox> list = growingList("No dishes here yet 🥲");

        Button cartBtn = new Button();
        updateCartButton(cartBtn);
        cartBtn.setOnAction(e -> showCartScreen());

        try {
            int restaurantId = RestaurantDAO.getRestaurantIdByName(restaurantName);

            for (MenuItem item : MenuItemDAO.getMenuItemsByRestaurant(restaurantName)) {

                Button addBtn = new Button("💗 Add");
                addBtn.setOnAction(e -> {
                    if (Cart.addItem(item, restaurantId)) {
                        updateCartButton(cartBtn);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "One restaurant at a time",
                                "Your cart already has items from another restaurant.\n"
                                        + "Check out or clear your cart first.");
                    }
                });

                HBox row = new HBox(20,
                        imageView("/resources/menu/" + getMenuImage(item.getName()), 70),
                        new Label(item.getName()),
                        new Label(formatPrice(item.getPrice())),
                        addBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(15));

                list.getItems().add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            list.setPlaceholder(databaseErrorPlaceholder(() -> showMenuScreen(restaurantName)));
        }

        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> showRestaurantScreen());

        root.getChildren().setAll(
                title("🍴 " + restaurantName + " Menu"),
                list,
                buttonRow(backBtn, cartBtn));
    }

    /* ================= CART + CHECKOUT SCREEN ================= */

    private void showCartScreen() {

        ListView<HBox> list = growingList("Your cart is empty 🛒");

        for (Map.Entry<MenuItem, Integer> entry : Cart.getItems().entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();

            HBox row = new HBox(20,
                    new Label(item.getName() + " x" + qty),
                    new Label(formatPrice(item.getPrice() * qty)));
            row.setPadding(new Insets(10));

            list.getItems().add(row);
        }

        Label total = new Label("Total: " + formatPrice(Cart.getTotal()));

        TextField nameField = textField("Your name");
        TextField emailField = textField("Email");
        TextField addressField = textField("Address");

        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cashBtn = new RadioButton("💵 Cash");
        RadioButton cardBtn = new RadioButton("💳 Card");
        cashBtn.setToggleGroup(paymentGroup);
        cardBtn.setToggleGroup(paymentGroup);
        cashBtn.setSelected(true);

        HBox paymentRow = new HBox(25, cashBtn, cardBtn);
        paymentRow.setAlignment(Pos.CENTER);

        Button checkoutBtn = new Button("✅ Checkout");
        checkoutBtn.setDisable(Cart.isEmpty());
        checkoutBtn.setOnAction(e -> {

            String name = nameField.getText().strip();
            String email = emailField.getText().strip();
            String address = addressField.getText().strip();

            String problem = validateCheckout(name, email, address);
            if (problem != null) {
                showAlert(Alert.AlertType.WARNING, "Almost there!", problem);
                return;
            }

            String paymentMethod = cashBtn.isSelected() ? "Cash" : "Card";

            try {
                String driverName = CheckoutService.checkout(name, email, address, paymentMethod);
                showAlert(Alert.AlertType.INFORMATION, "✔ Order placed successfully!",
                        "🚴 Delivered in 20 minutes\n👤 Driver: " + driverName);
                showRestaurantScreen();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Couldn't place your order",
                        "Nothing was saved and your cart is unchanged.\n\n" + ex.getMessage());
            }
        });

        Button clearBtn = new Button("🧹 Clear Cart");
        clearBtn.setDisable(Cart.isEmpty());
        clearBtn.setOnAction(e -> {
            Cart.clear();
            showCartScreen();
        });

        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> showRestaurantScreen());

        root.getChildren().setAll(
                title("🛒 Your Cart"),
                list,
                total,
                nameField,
                emailField,
                addressField,
                paymentRow,
                buttonRow(backBtn, clearBtn, checkoutBtn));
    }

    /** Returns a message describing the first invalid field, or {@code null} if everything is valid. */
    private static String validateCheckout(String name, String email, String address) {
        if (name.isEmpty()) return "Please enter your name.";
        if (!EMAIL.matcher(email).matches()) return "Please enter a valid email address.";
        if (address.isEmpty()) return "Please enter a delivery address.";
        return null;
    }

    /* ================= UI HELPERS ================= */

    private static Label title(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("title");
        return label;
    }

    private static ListView<HBox> growingList(String emptyMessage) {
        ListView<HBox> list = new ListView<>();
        list.setPlaceholder(new Label(emptyMessage));
        VBox.setVgrow(list, Priority.ALWAYS);
        return list;
    }

    private static Node databaseErrorPlaceholder(Runnable retry) {
        Label message = new Label(DB_ERROR);
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);

        Button retryBtn = new Button("🔄 Retry");
        retryBtn.setOnAction(e -> retry.run());

        VBox box = new VBox(15, message, retryBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    private static HBox buttonRow(Button... buttons) {
        HBox row = new HBox(15, buttons);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private static TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private static void updateCartButton(Button cartBtn) {
        int count = Cart.getItemCount();
        cartBtn.setText(count == 0 ? "🛒 View Cart" : "🛒 View Cart (" + count + ")");
    }

    private static String formatPrice(double amount) {
        return String.format(Locale.ROOT, "%.2f LE", amount);
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setTitle(APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /* ================= IMAGE HELPERS ================= */

    /** Loads a classpath image scaled down to {@code size} pixels, caching it for reuse. */
    private ImageView imageView(String path, double size) {
        Image image = imageCache.computeIfAbsent(path + "@" + size, key -> {
            URL url = getClass().getResource(path);
            return url == null ? null : new Image(url.toExternalForm(), size, size, true, true);
        });

        ImageView view = new ImageView(image);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        return view;
    }

    private static String getRestaurantImage(String name) {
        name = name.toLowerCase(Locale.ROOT);
        if (name.contains("toto")) return "toto.png";
        if (name.contains("menna")) return "menna.png";
        return "aya.png";
    }

    private static String getMenuImage(String item) {
        item = item.toLowerCase(Locale.ROOT);
        if (item.contains("burger")) return "burger.png";
        if (item.contains("pizza")) return "pizza.png";
        if (item.contains("falafel")) return "falafelsandwich.png";
        if (item.contains("fol")) return "folsandwich.png";
        if (item.contains("cola")) return "cola.png";
        if (item.contains("tea")) return "tea.png";
        if (item.contains("orange")) return "orangejuice.png";
        if (item.contains("fruit")) return "fruitsalad.png";
        return "burger.png";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
