package application.Controllers.Client.Products;

import application.Controllers.Client.ClientStartSceneController;
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

public class AllProductsTableController extends ClientStartSceneController {
    @FXML
    private TableColumn<ProductTable, String> allProductsNameColumn, allProductsSubcategoryColumn, allProductsPriceColumn, allProductsCategoryColumn;
    @FXML
    private TableColumn<ProductTable, String> allProductsCartButtonColumn, allProductsStarButtonColumn;
    @FXML
    private TableColumn<ProductTable, String> numberOfOrdersColumn;
    @FXML
    private TableView<ProductTable> allProductsTableView;

    @FXML
    private void initialize() {
        try {
            displayAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createSortingButtons();
        prepareSortingButtons(allProductsTableView, numberOfOrdersColumn, allProductsStarButtonColumn);
    }


    private void fillAllProductsColumnsWithData(ObservableList<ProductTable> list) {
        allProductsNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        allProductsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        allProductsSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        allProductsStarButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        allProductsStarButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        allProductsCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        allProductsCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        numberOfOrdersColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfOrdersPerProduct"));
        allProductsTableView.setItems(list);
        prepareTableView(allProductsTableView, allProductsPriceColumn);

    }

    private void displayAllProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), Controller.CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getAllProductsWithInformationIfTheyAreInUsersFavourite(products);
        fillAllProductsColumnsWithData(listOfProducts);
    }


}
