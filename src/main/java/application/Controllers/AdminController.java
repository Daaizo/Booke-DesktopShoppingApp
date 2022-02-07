package application.Controllers;

import application.Main;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Client;
import users.ClientTable;
import users.Product;
import users.ProductTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController extends Controller {
    @FXML
    private TableColumn<ClientTable, String> userFirstNameColumn, userIDColumn, userLoginColumn, userLastNameColumn, userPasswordColumn;
    @FXML
    private TableView<ClientTable> userTableView;

    @FXML
    private TableColumn<ProductTable, String> productCategoryColumn, productNameColumn, productSubcategoryColumn;
    @FXML
    private TableColumn<ProductTable, Integer> productIdColumn;
    @FXML
    private TableColumn<ProductTable, Double> productPriceColumn;

    @FXML
    private TableView<ProductTable> productTableView;


    @FXML
    public void initialize() {
        prepareScene();
        try {
            displayUsers();
            displayProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillUserColumnsWithData(ObservableList<ClientTable> list) {
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTableView.setItems(list);
    }

    void displayUsers() throws SQLException {
        Connection con = Main.connectToDatabase();
        ResultSet users = Client.getUsersFromDataBase(con);
        assert users != null;
        ObservableList<ClientTable> listOfUsers = ClientTable.getUsers(users);
        fillUserColumnsWithData(listOfUsers);
    }

    private void fillProductColumnsWithData(ObservableList<ProductTable> list) {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        productSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        productTableView.setItems(list);
    }

    void displayProducts() throws SQLException {
        Connection con = Main.connectToDatabase();
        ResultSet products = Product.getProductsFromDatabase(con);
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProducts(products);
        fillProductColumnsWithData(listOfProducts);
    }


}
