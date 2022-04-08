package application.Controllers.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllProductsController extends AdminStartSceneController {
    @FXML
    private TableColumn<ProductTable, String> productCategoryColumn, productNameColumn, productSubcategoryColumn, productPriceColumn;
    @FXML
    private TableColumn<ProductTable, Integer> productIdColumn;

    @FXML
    private TableView<ProductTable> productTableView;

    @FXML
    private void initialize() {
        try {
            displayProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductsFromDatabase(getConnection());
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProducts(products);
        fillProductColumnsWithData(listOfProducts);
    }

    private void fillProductColumnsWithData(ObservableList<ProductTable> list) {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        productSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        productTableView.setItems(list);
        prepareTableView(productTableView, productPriceColumn);
    }


}
