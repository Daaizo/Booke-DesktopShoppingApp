package application.Controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingCartController extends Controller {

    @FXML
    private TableColumn<ProductTable, String> cartNameColumn, cartPriceColumn, cartValueColumn;
    @FXML
    private TableColumn<ProductTable, Integer> cartQuantityColumn;
    @FXML
    private TableView<ProductTable> cartTableView;

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

        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromShoppingCart(products);
        fillShoppingCartColumnsWithData(listOfProducts);
    }

    private void fillShoppingCartColumnsWithData(ObservableList<ProductTable> list) {
        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        cartValueColumn.setCellValueFactory(new PropertyValueFactory<>("productTotalValue"));
        showOnlyRowsWithData(cartTableView, list);

    }


}
