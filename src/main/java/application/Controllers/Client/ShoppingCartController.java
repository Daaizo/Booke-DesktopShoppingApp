package application.Controllers.Client;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Client.Account.ClientOrderDetails;
import application.Controllers.Controller;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import users.Client;
import users.Order;
import users.Product;
import users.ProductTable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class ShoppingCartController extends Controller {

    private String paymentMethod = null;
    private double totalOrderValue;
    private final Client currentUser = new Client(CURRENT_USER_LOGIN);
    private Order order = null;
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
    private Button clearCartButton, orderJustPlaceButton;
    @FXML
    private Pane orderPlacedPane;

    @FXML
    private void initialize() {
        orderPlacedPane.setVisible(false);
        prepareScene();
        createGoBackButton(event -> switchScene(event, clientScene));
        createClearCartButton();
        try {
            displayProducts();
            setTotalValueLabel();
            setPaymentMethods();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fixNotificationPlacement();
    }

    @FXML
    private void detailsOfJustPlaceOrderButtonClicked() {
        displayPostPlacingOrderInformation(order.getOrderNumber());
        orderJustPlaceButton.setVisible(false);
    }

    private void fixNotificationPlacement() {
        createNotification();
        notification.setLayoutY(notification.getLayoutY() + 70);
    }

    private void createClearCartButton() {
        clearCartButton = createButton("", 800, 80);
        clearCartButton.setText("Delete whole cart");
        clearCartButton.getStyleClass().add("DeleteButtons");
        clearCartButton.setMinWidth(200);
        clearCartButton.setOnAction(event -> {
            Optional<ButtonType> buttonCLicked = createAndShowAlert(Alert.AlertType.CONFIRMATION, "", "Delete", "Do you want to delete whole cart ?");
            if (alertButtonClicked(buttonCLicked, ButtonType.OK)) {
                clearShoppingCart();
                reloadTableView(cartTableView);
            }
        });
    }

    private void prepareTableView() {
        super.prepareTableView(cartTableView, cartPriceColumn);
        cartTableView.setMaxHeight(320);
    }

    private void setTotalValueLabel() throws SQLException {
        checkConnectionWithDb();
        this.totalOrderValue = currentUser.getTotalValueOfShoppingCart(getConnection());
        totalValueLabel.setText(totalOrderValue + CURRENCY);
        totalValueLabel.setId("displayLabel");
    }

    private void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductFromCartAndSetValueBasedOnQuantity(getConnection(), CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromShoppingCart(products);
        if (listOfProducts.isEmpty()) {
            displayLabelWithGivenText(emptyCart, "SHOPPING CART IS EMPTY");
            cartTableView.setVisible(false);
            totalValueLabel.setVisible(false);
            titleLabel.setVisible(false);
            clearCartButton.setVisible(false);
        } else {
            cartTableView.setVisible(true);
            clearCartButton.setVisible(true);
            fillShoppingCartColumnsWithData(listOfProducts);
            cartTableView.setItems(listOfProducts);
            prepareTableView();
            cartValueColumn.setSortType(cartPriceColumn.getSortType());
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
        Optional<ButtonType> buttonClicked = deleteProductAlert(productName, productQuantity, "cart");
        if (alertButtonClicked(buttonClicked, ButtonType.OK)) {
            currentUser.setQuantityOfProductInCart(productName, "-quantity", getConnection());
            // there is a trigger in database which deletes products from cart when quantity is equal 0
            showNotification("Item successfully deleted");

        }
    }


    private ButtonInsideTableColumn<ProductTable, String> plusButtonClicked() {
        ButtonInsideTableColumn<ProductTable, String> button = new ButtonInsideTableColumn<>("plus.png", "");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getRowId().getProductName();
            try {
                checkConnectionWithDb();
                currentUser.setQuantityOfProductInCart(productName, "+1", getConnection());
                reloadTableView(cartTableView);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        };
        button.setEventHandler(buttonClicked);
        button.setCssClassId("clientTableviewButtons");
        button.setStyle("-fx-alignment : center-right;");
        return button;
    }

    private boolean isQuantityEqualOne(String productName, Connection connection) throws SQLException {
        return currentUser.getQuantityOfProductInCart(productName, connection) == 1;
    }


    private ButtonInsideTableColumn<ProductTable, String> minusButtonClicked() {
        ButtonInsideTableColumn<ProductTable, String> button = new ButtonInsideTableColumn<>("minus.png", "");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getRowId().getProductName();
            try {
                checkConnectionWithDb();
                if (isQuantityEqualOne(productName, getConnection())) {
                    confirmationAlert(productName, "1");
                } else {
                    currentUser.setQuantityOfProductInCart(productName, "-1", getConnection());
                }
                reloadTableView(cartTableView);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        };
        button.setEventHandler(buttonClicked);
        button.setCssClassId("clientTableviewButtons");
        button.setStyle("-fx-alignment : center-right;");
        return button;
    }

    private ButtonInsideTableColumn<ProductTable, String> deleteButtonClicked() {
        ButtonInsideTableColumn<ProductTable, String> button = new ButtonInsideTableColumn<>("delete.png", "delete from cart");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            String productName = button.getRowId().getProductName();
            try {
                confirmationAlert(productName, currentUser.getQuantityOfProductInCart(productName, getConnection()) + "");
                reloadTableView(cartTableView);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


        };
        button.setCssClassId("clientTableviewButtons");
        button.setEventHandler(buttonClicked);
        return button;
    }

    private void setPaymentMethods() throws SQLException {
        int numberOfButtonsInLine = 4;
        double buttonPadding = 20;
        ResultSet paymentMethods = Product.getPaymentMethods(getConnection());
        ToggleGroup groupOfRadioButtons = new ToggleGroup();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(buttonPadding));
        grid.setHgap(buttonPadding);
        grid.setVgap(buttonPadding);
        int j = 0;
        int i = 0;
        while (Objects.requireNonNull(paymentMethods).next()) {
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
    private void placeOrderButtonClicked() {
        if (paymentMethod == null) {
            createAndShowAlert(Alert.AlertType.WARNING,
                    "Payment method required",
                    "Payment", "You have to choose payment method before placing an order!");

        } else {
            Optional<ButtonType> buttonClicked = createAndShowAlert(Alert.AlertType.CONFIRMATION,
                    "Are you sure about placing an order ?",
                    "Placing an order",
                    "Total order value : " + totalOrderValue + CURRENCY);
            if (alertButtonClicked(buttonClicked, ButtonType.OK)) {

                ButtonType now = new ButtonType("I want to pay now");
                ButtonType later = new ButtonType("I want to pay later");
                Optional<ButtonType> buttonTypeClicked = createAndShowAlert(
                        now, later, "Do you want to pay now ?", "Payment");
                if (alertButtonClicked(buttonTypeClicked, now)) {
                    placeOrder("In progress");

                } else if (alertButtonClicked(buttonTypeClicked, later)) {
                    placeOrder("Waiting for payment");
                }
                showNotification("Order successfully placed");
                clearShoppingCart();
                reloadTableView(cartTableView);
            }
        }
        anchor.requestFocus();
    }

    private boolean alertButtonClicked(Optional<ButtonType> alertButton, ButtonType buttonType) {
        return alertButton.isPresent() && alertButton.get() == buttonType;
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
        this.order = order;
        orderPlacedPane.setVisible(true);
        orderJustPlaceButton.setVisible(true);

    }

    private void displayPostPlacingOrderInformation(int orderNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/clientOrderDetailsGUI.fxml"));
            ClientOrderDetails clientOrderDetailsController = new ClientOrderDetails();
            clientOrderDetailsController.setOrderNumber(orderNumber);
            clientOrderDetailsController.setAllOrdersPane(orderPlacedPane);
            loader.setController(clientOrderDetailsController);

            orderPlacedPane.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clearShoppingCart() {
        try {
            currentUser.deleteWholeCart(getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
