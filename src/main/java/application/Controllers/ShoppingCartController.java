package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
    private Label emptyCart, totalValueLabel, titleLabel;
    @FXML
    private ScrollPane paymentMethodsPane;

    @FXML
    public void initialize() {
        prepareScene();
        goBackButton.setVisible(true);
        goBackButton.setOnAction(event -> switchScene(event, clientScene));
        try {
            displayProducts();
            setTotalValueLabel();
            setPaymentMethods();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setTotalValueLabel() throws SQLException {
        checkConnectionWithDb();
        double totalValue = Client.getTotalValueOfShoppingCart(CURRENT_USER_LOGIN, getConnection());
        totalValueLabel.setText("Total value of products in cart : " + totalValue);
    }

    void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductFromCartAndSetValueBasedOnQuantity(getConnection(), CURRENT_USER_LOGIN);
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromShoppingCart(products);
        if (listOfProducts.isEmpty()) {
            displayLabelWithGivenText(emptyCart, "SHOPPING CART IS EMPTY");
            cartTableView.setVisible(false);
            totalValueLabel.setVisible(false);
            titleLabel.setVisible(false);
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
        setTotalValueLabel();
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

    private void setPaymentMethods() throws SQLException {
        ResultSet paymentMethods = Product.getPaymentMethods(getConnection());
        ToggleGroup groupOfRadioButtons = new ToggleGroup();
        int x = (int) paymentMethodsPane.getLayoutX();
        int y = (int) paymentMethodsPane.getLayoutY();
        GridPane grid = new GridPane();
        grid.setHgap(1);
        while (paymentMethods.next()) {
            RadioButton button = new RadioButton(paymentMethods.getString(2));
            y += 10;
            button.setLayoutX(x);
            button.setLayoutY(y);
            button.setPadding(new Insets(10));
            button.setToggleGroup(groupOfRadioButtons);
            button.setOnAction(event -> {
                RadioButton selectedButton = (RadioButton) groupOfRadioButtons.getSelectedToggle();
                System.out.println(selectedButton.getText());
            });
            grid.add(button, x, y);
        }
        paymentMethodsPane.setContent(grid);
    }
//TODO udpate diagrams from db

}
