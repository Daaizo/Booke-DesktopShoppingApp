package application.Controllers.Client;

import application.Controllers.Controller;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllProductsTableController extends ClientSceneController {
    @FXML
    private TableColumn<ProductTable, String> allProductsNameColumn, allProductsSubcategoryColumn, allProductsPriceColumn, allProductsCategoryColumn;
    @FXML
    private TableColumn<ProductTable, String> allProductsCartButtonColumn, allProductsStarButtonColumn;
    @FXML
    private TableView<ProductTable> allProductsTableView;

    @FXML
    public void initialize() {
        try {
            displayAllProducts();
            setSoringTypeToColumns(allProductsPriceColumn, allProductsCartButtonColumn, allProductsStarButtonColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillAllProductsColumnsWithData(ObservableList<ProductTable> list) {
        allProductsNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        allProductsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        allProductsSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        allProductsStarButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        allProductsStarButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        allProductsCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        allProductsCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        allProductsTableView.setItems(list);
        showOnlyRowsWithData(allProductsTableView);
    }

    private void displayAllProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), Controller.CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getAllProductsWithInformationIfTheyAreInUsersFavourite(products);
        fillAllProductsColumnsWithData(listOfProducts);
    }


}
