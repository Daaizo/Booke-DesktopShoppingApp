package application.Controllers;

import application.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import users.Product;
import users.ProductTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientController extends Controller {
    @FXML
    private AnchorPane ebooksAnchorPane, gamesAnchorPane;

    @FXML
    private Button ebooksButton, gamesButton, logoutButton, goBackButton;

    @FXML
    private Pane categoryPickingPane;

    @FXML
    private TableColumn<ProductTable, String> gamesNameColumn, gamesSubcategoryColumn, ebooksNameColumn, ebooksSubcategoryColumn;
    @FXML
    private TableColumn<ProductTable, Double> gamesPriceColumn, ebooksPriceColumn;
    @FXML
    private TableView<ProductTable> gamesTableView, ebooksTableView;


    @FXML
    public void initialize() {
        try {
            displayEbooks();
            displayGames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryPickingPane.setVisible(true);
        ebooksAnchorPane.setVisible(false);
        gamesAnchorPane.setVisible(false);
        goBackButton.setVisible(false);
        createAnchorAndExitButton();
        setImageToButtonAndPlaceItOnX(logoutButton, "logout.png", 950);
        setImageToButtonAndPlaceItOnX(goBackButton, "back-button.png", 910);
    }

    private void fillEbooksColumnsWithData(ObservableList<ProductTable> list) {
        ebooksNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        ebooksPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        ebooksSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        ebooksTableView.setItems(list);
    }

    void displayEbooks() throws SQLException {
        Connection con = Main.connectToDatabase();
        ResultSet products = Product.getProductsFromDatabase(con);
        assert products != null;
        ObservableList<ProductTable> listOfEbooks = ProductTable.getProductsFromCategory(products, "ebooks");
        fillEbooksColumnsWithData(listOfEbooks);
    }

    private void fillGamesColumnsWithData(ObservableList<ProductTable> list) {
        gamesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        gamesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        gamesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        gamesTableView.setItems(list);
    }

    void displayGames() throws SQLException {
        Connection con = Main.connectToDatabase();
        ResultSet products = Product.getProductsFromDatabase(con);
        assert products != null;
        ObservableList<ProductTable> listOfGames = ProductTable.getProductsFromCategory(products, "games");
        fillGamesColumnsWithData(listOfGames);
    }


    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked, loginScene);
    }

    @FXML
    void gamesButtonClicked() {
        gamesAnchorPane.setVisible(true);
        categoryPickingPane.setVisible(false);
        goBackButton.setVisible(true);

    }

    @FXML
    void ebooksButtonClicked() {
        ebooksAnchorPane.setVisible(true);
        categoryPickingPane.setVisible(false);
        goBackButton.setVisible(true);
    }

    @FXML
    void goBackButtonClicked() {
        categoryPickingPane.setVisible(true);
        ebooksAnchorPane.setVisible(false);
        gamesAnchorPane.setVisible(false);
        goBackButton.setVisible(false);
    }

    //simple hover effects
    @FXML
    void ebookOnMouseEntered() {
        ebooksButton.setStyle("-fx-background-color: #fc766a; -fx-text-fill:  #5B84B1FF;");
    }

    @FXML
    void ebookOnMouseExited() {
        ebooksButton.setStyle("-fx-background-color:  #5B84B1FF; -fx-text-fill: #fc766a;-fx-border-color : #fc766a ;");
    }


    @FXML
    void gamesButtonOnMouseEntered() {
        gamesButton.setStyle("-fx-background-color: #fc766a; -fx-text-fill:  #5B84B1FF;");

    }

    @FXML
    void gamesButtonOnMouseExited() {
        gamesButton.setStyle("-fx-background-color:  #5B84B1FF; -fx-text-fill: #fc766a;-fx-border-color : #fc766a;");

    }

//


}
