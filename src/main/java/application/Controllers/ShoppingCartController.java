package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import users.Client;
import users.Product;
import users.ProductTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ShoppingCartController extends Controller {

    @FXML
    private TableColumn<ProductTable, String> cartNameColumn, cartPriceColumn, cartValueColumn, plusButtonColumn, minusButtonColumn, deleteButtonColumn;
    @FXML
    private TableColumn<ProductTable, Integer> cartQuantityColumn;
    @FXML
    private TableView<ProductTable> cartTableView;
    @FXML
    private Label emptyCart;

    @FXML
    public void initialize() {
        prepareScene();
        goBackButton.setVisible(true);
        goBackButton.setOnAction(event -> switchScene(event, clientScene));
        try {
            displayProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductFromCart(getConnection(), CURRENT_USER_LOGIN);
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromShoppingCart(products);
        if (listOfProducts.isEmpty()) {
            displayLabelWithGivenText(emptyCart, "SHOPPING CART IS EMPTY");
            cartTableView.setVisible(false);
        } else {
            cartTableView.setVisible(true);
            fillShoppingCartColumnsWithData(listOfProducts);
            showOnlyRowsWithData(cartTableView, listOfProducts);
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
        showOnlyRowsWithData(cartTableView, list);


    }

    private void reloadTableView(TableView<ProductTable> tableView) throws SQLException {
        tableView.getItems().clear();
        displayProducts();
    }

    private void confirmationAlert(String productName, String productQuantity) throws SQLException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("DELETING PRODUCT FROM CART ");
        alert.setContentText("Do you want to delete " + productQuantity + " " + productName + " from cart");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
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


}
