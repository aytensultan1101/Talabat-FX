import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import java.util.Map;

public class MainApp extends Application {

    private VBox root;

    @Override
    public void start(Stage stage) {

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        showRestaurantScreen();

        Scene scene = new Scene(root, 550, 520);
        scene.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        stage.setTitle("Talabat 💗");
        stage.setScene(scene);
        stage.show();
    }

    /* ================= RESTAURANTS SCREEN ================= */

    private void showRestaurantScreen() {

        root.getChildren().clear();

        Label title = new Label("🍽️ Choose a Restaurant");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #c2255c;");

        ListView<HBox> listView = new ListView<>();
        listView.setPrefHeight(420);

        for (String name : RestaurantDAO.getAllRestaurants()) {

            Image image = new Image(
                    getClass().getResourceAsStream(
                            "/resources/restaurants/" + getRestaurantImage(name)
                    )
            );

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Label label = new Label(name);
            label.setStyle("-fx-font-size: 18px;");

            HBox row = new HBox(25, imageView, label);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(15));
            row.setCursor(Cursor.HAND);

            row.setOnMouseClicked(e -> showMenuScreen(name));

            listView.getItems().add(row);
        }

        root.getChildren().addAll(title, listView);
    }

    /* ================= MENU SCREEN ================= */

    private void showMenuScreen(String restaurantName) {

        root.getChildren().clear();

        Label title = new Label("🍴 " + restaurantName + " Menu");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #c2255c;");

        ListView<HBox> menuList = new ListView<>();
        menuList.setPrefHeight(360);

        int restaurantId = RestaurantDAO.getRestaurantIdByName(restaurantName);

        for (MenuItem item : MenuItemDAO.getMenuItemsByRestaurant(restaurantName)) {

            Image img = new Image(
                    getClass().getResourceAsStream(
                            "/resources/menu/" + getMenuImage(item.getName())
                    )
            );

            ImageView iv = new ImageView(img);
            iv.setFitWidth(70);
            iv.setFitHeight(70);
            iv.setPreserveRatio(true);

            Label nameLabel = new Label(item.getName());
            Label priceLabel = new Label(item.getPrice() + " LE");

            Button addBtn = new Button("💗 Add");
            addBtn.setOnAction(e -> {
                boolean added = Cart.addItem(item, restaurantId);
                if (!added) {
                    System.out.println("❌ Cannot order from two restaurants");
                }
            });

            HBox row = new HBox(20, iv, nameLabel, priceLabel, addBtn);
            row.setPadding(new Insets(15));
            row.setAlignment(Pos.CENTER_LEFT);

            menuList.getItems().add(row);
        }

        Button cartBtn = new Button("🛒 View Cart");
        cartBtn.setOnAction(e -> showCartScreen());

        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> showRestaurantScreen());

        root.getChildren().addAll(title, menuList, cartBtn, backBtn);
    }

    /* ================= CART + CHECKOUT SCREEN ================= */

    private void showCartScreen() {

        root.getChildren().clear();

        Label title = new Label("🛒 Your Cart");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #c2255c;");

        ListView<HBox> cartList = new ListView<>();
        cartList.setPrefHeight(220);

        for (Map.Entry<MenuItem, Integer> entry : Cart.getItems().entrySet()) {

            MenuItem item = entry.getKey();
            int qty = entry.getValue();

            Label name = new Label(item.getName() + " x" + qty);
            Label price = new Label((item.getPrice() * qty) + " LE");

            HBox row = new HBox(20, name, price);
            row.setPadding(new Insets(10));

            cartList.getItems().add(row);
        }

        Label total = new Label("Total: " + Cart.getTotal() + " LE");

        TextField nameField = new TextField();
        nameField.setPromptText("Your name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        ToggleGroup paymentGroup = new ToggleGroup();

        RadioButton cashBtn = new RadioButton("💵 Cash");
        RadioButton cardBtn = new RadioButton("💳 Card");

        cashBtn.setToggleGroup(paymentGroup);
        cardBtn.setToggleGroup(paymentGroup);
        cashBtn.setSelected(true);
        Button checkoutBtn = new Button("✅ Checkout");

        checkoutBtn.setOnAction(e -> {

            String paymentMethod;
            if (cashBtn.isSelected()) {
                paymentMethod = "Cash";
            } else {
                paymentMethod = "Card";
            }

            String driverName = CheckoutService.checkout(
                nameField.getText(),
                emailField.getText(),
                addressField.getText(),
                paymentMethod
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Placed");
            alert.setHeaderText("✔ Order placed successfully!");
            alert.setContentText(
                "🚴 Delivered in 20 minutes\n" +
                "👤 Driver: " + driverName
            );
            alert.showAndWait();

            showRestaurantScreen();
        });



        Button clearBtn = new Button("🧹 Clear Cart");
        clearBtn.setOnAction(e -> {
            Cart.clear();
            showCartScreen();
        });

        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> showRestaurantScreen());
        root.getChildren().addAll(
        	    title,
        	    cartList,
        	    total,
        	    nameField,
        	    emailField,
        	    addressField,
        	    cashBtn,
        	    cardBtn,
        	    checkoutBtn,
        	    clearBtn,
        	    backBtn
        	);

    }

    /* ================= IMAGE HELPERS ================= */

    private String getRestaurantImage(String name) {
        name = name.toLowerCase();
        if (name.contains("toto")) return "toto.png";
        if (name.contains("menna")) return "menna.png";
        return "aya.png";
    }

    private String getMenuImage(String item) {
        item = item.toLowerCase();
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
