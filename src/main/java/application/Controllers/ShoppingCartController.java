package application.Controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import users.Client;
import users.Order;
import users.Product;
import users.ProductTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ShoppingCartController extends Controller {

    private String paymentMethod = null;
    private double totalOrderValue;

    @FXML
    private TableColumn<ProductTable, String> cartNameColumn, cartPriceColumn, cartValueColumn, plusButtonColumn, minusButtonColumn, deleteButtonColumn;
    @FXML
    private TableColumn<ProductTable, Integer> cartQuantityColumn;
    @FXML
    private TableView<ProductTable> cartTableView;
    @FXML
    private Label emptyCart, totalValueLabel, titleLabel;
    @FXML
    private ScrollPane paymentMethodsPane;

    @FXML
    public void initialize() {
        prepareScene();
        goBackButton.setVisible(true);
        goBackButton.setOnAction(event -> switchScene(event, clientScene));
        cartTableView.setFixedCellSize(70);
        cartTableView.setMaxHeight(320);
        try {
            displayProducts();
            setTotalValueLabel();
            setPaymentMethods(2, 20);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setTotalValueLabel() throws SQLException {
        checkConnectionWithDb();
        this.totalOrderValue = Client.getTotalValueOfShoppingCart(CURRENT_USER_LOGIN, getConnection());
        totalValueLabel.setText("Total value of products in cart : " + totalOrderValue);
    }

    void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductFromCartAndSetValueBasedOnQuantity(getConnection(), CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromShoppingCart(products);
        if (listOfProducts.isEmpty()) {
            displayLabelWithGivenText(emptyCart, "SHOPPING CART IS EMPTY");
            cartTableView.setVisible(false);
            totalValueLabel.setVisible(false);
            titleLabel.setVisible(false);
        } else {
            cartTableView.setVisible(true);
            fillShoppingCartColumnsWithData(listOfProducts);
            cartTableView.setItems(listOfProducts);
            cartTableView.prefHeightProperty().bind(Bindings.size(cartTableView.getItems()).multiply(cartTableView.getFixedCellSize()).add(50));

        }

    }

    private void fillShoppingCartColumnsWithData(ObservableList<ProductTable> list) {
        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        cartValueColumn.setCellValueFactory(new PropertyValueFactory<>("productTotalValue"));
        plusButtonColumn.setCellFactory(buttonCreation -> plusButtonClicked());
        minusButtonColumn.setCellFactory(buttonCreation -> minusButtonClicked());
        deleteButtonColumn.setCellFactory(buttonCreation -> deleteButtonClicked());
        cartTableView.setItems(list);


    }

    private void reloadTableView(TableView<ProductTable> tableView) {
        tableView.getItems().clear();
        try {
            displayProducts();
            setTotalValueLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void confirmationAlert(String productName, String productQuantity) throws SQLException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("DELETING PRODUCT FROM CART ");
        alert.setContentText("Do you want to delete " + productQuantity + " " + productName + " from cart");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Client.setQuantityOfProduct(CURRENT_USER_LOGIN, productName, "-quantity", getConnection());
            // there is a trigger in database which deletes products from cart when quantity is equal 0
        }
    }

    private ClientController.ButtonInsideTableColumn plusButtonClicked() {
        ClientController.ButtonInsideTableColumn button = new ClientController().new ButtonInsideTableColumn("plus.png", "");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getProductName();
            System.out.println(productName + CURRENT_USER_LOGIN);
            try {
                checkConnectionWithDb();
                Client.setQuantityOfProduct(CURRENT_USER_LOGIN, productName, "+1", getConnection());
                reloadTableView(cartTableView);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        };
        button.setEventHandler(buttonClicked);
        return button;
    }

    private boolean isQuantityEqualZero(String productName, Connection connection) throws SQLException {
        return Client.getQuantityOfProductInCart(CURRENT_USER_LOGIN, productName, connection) == 0;
    }

    private ClientController.ButtonInsideTableColumn minusButtonClicked() {
        ClientController.ButtonInsideTableColumn button = new ClientController().new ButtonInsideTableColumn("minus.png", "");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getProductName();
            try {
                checkConnectionWithDb();
                Client.setQuantityOfProduct(CURRENT_USER_LOGIN, productName, "-1", getConnection());
                if (isQuantityEqualZero(productName, getConnection())) {
                    confirmationAlert(productName, "");
                }

                reloadTableView(cartTableView);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        };
        button.setEventHandler(buttonClicked);
        button.setFxStyle("-fx-alignment : center_left;");
        return button;
    }

    private ClientController.ButtonInsideTableColumn deleteButtonClicked() {
        ClientController.ButtonInsideTableColumn button = new ClientController().new ButtonInsideTableColumn("delete.png", "delete from cart");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getProductName();
            try {
                confirmationAlert(productName, Client.getQuantityOfProductInCart(CURRENT_USER_LOGIN, productName, getConnection()) + "");
                reloadTableView(cartTableView);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        };

        button.setEventHandler(buttonClicked);
        return button;
    }

    private void setPaymentMethods(double numberOfButtonsInLine, double buttonPadding) throws SQLException {
        ResultSet paymentMethods = Product.getPaymentMethods(getConnection());
        ToggleGroup groupOfRadioButtons = new ToggleGroup();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(buttonPadding));
        grid.setHgap(buttonPadding);
        grid.setVgap(buttonPadding);
        int j = 0;
        int i = 0;
        while (paymentMethods.next()) {
            RadioButton button = new RadioButton(paymentMethods.getString(2));
            button.setToggleGroup(groupOfRadioButtons);
            button.setOnAction(event -> this.paymentMethod = button.getText());
            grid.add(button, i, j);
            if (i < numberOfButtonsInLine - 1) {
                i++;
            } else {
                i = 0;
                j++;
            }
        }
        paymentMethodsPane.setContent(grid);
    }


    @FXML
    void placeOrderButtonClicked() {
        if (paymentMethod == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have to choose payment method before placing an order!");
            alert.setHeaderText("Payment method required");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure about placing an order ?");
            alert.setTitle("Placing an order");
            alert.setHeaderText("Total order value : " + totalOrderValue);
            Optional<ButtonType> result = alert.showAndWait();
            if (alertButtonClicked(result, ButtonType.OK)) {
                ButtonType now = new ButtonType("I want to pay now");
                ButtonType later = new ButtonType("I want to pay later");
                Optional<ButtonType> buttonClicked = createAndShowPaymentAlert(now, later);
                if (alertButtonClicked(buttonClicked, now)) {
                    placeOrder("In progress");

                } else if (alertButtonClicked(buttonClicked, later)) {
                    placeOrder("Waiting for payment");
                }
                clearShoppingCart();
                reloadTableView(cartTableView);
            }
        }
    }

    private boolean alertButtonClicked(Optional<ButtonType> alertButton, ButtonType buttonType) {
        return alertButton.isPresent() && alertButton.get() == buttonType;
    }

    private Optional<ButtonType> createAndShowPaymentAlert(ButtonType firstButtonType, ButtonType secondButtonType) {
        Alert payment = new Alert(Alert.AlertType.CONFIRMATION, "", firstButtonType, secondButtonType);
        payment.setTitle("Payment");
        payment.setHeaderText("Do you want to pay now ?");
        return payment.showAndWait();
    }

    private void placeOrder(String orderStatusName) {

        Order order = new Order(paymentMethod, orderStatusName.toLowerCase(), CURRENT_USER_LOGIN);
        try {
            order.createOrder(getConnection());
            order.setOrderId(getConnection());
            order.setOrderProducts(getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearShoppingCart() {
        try {
            Client.deleteWholeCart(CURRENT_USER_LOGIN, getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
