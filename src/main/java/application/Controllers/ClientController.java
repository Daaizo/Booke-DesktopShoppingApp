package application.Controllers;

import application.Main;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
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
    private TableColumn<ProductTable, String> ebooksStarButtonColumn, ebooksCartButtonColumn, gamesCartButtonColumn, gamesStarButtonColumn;
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


    private Callback createButtonInTableView(EventHandler eventHandler, String buttonIconName, String buttonText) {
        return new Callback<TableColumn<ProductTable, String>, TableCell<ProductTable, String>>() {
            @Override
            public TableCell call(final TableColumn<ProductTable, String> param) {
                return new TableCell<ProductTable, String>() {

                    final Button button = new Button(buttonText);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        button.setGraphic(iconPath(buttonIconName));
                        button.setBackground(Background.EMPTY);
                        button.setOnAction(eventHandler);
                        setGraphic(button);
                    }
                };
            }
        };
    }

    private void fillEbooksColumnsWithData(ObservableList<ProductTable> list) {
        ebooksNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        ebooksPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        ebooksSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        ebooksStarButtonColumn.setCellFactory(createButtonInTableView(starOnAction(), "star.png", "add to favourite"));
        ebooksCartButtonColumn.setCellFactory(createButtonInTableView(cartOnAction(), "add_cart.png", "add to cart"));
        ebooksCartButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        ebooksStarButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        ebooksTableView.setItems(list);
        ebooksTableView.setFixedCellSize(70);
        ebooksTableView.prefHeightProperty().bind(Bindings.size(ebooksTableView.getItems()).multiply(ebooksTableView.getFixedCellSize()).add(50));

    }

    private EventHandler starOnAction() {
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                System.out.println("kliknieto gwiazdke");
            }
        };
        return eventHandler;
    }

    private EventHandler cartOnAction() {
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                System.out.println("kliknieto cart");
            }
        };
        return eventHandler;
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
        gamesStarButtonColumn.setCellFactory(createButtonInTableView(starOnAction(), "star.png", "add to favourite"));
        gamesCartButtonColumn.setCellFactory(createButtonInTableView(cartOnAction(), "add_cart.png", "add to cart"));
        gamesCartButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        gamesStarButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        gamesTableView.setItems(list);
        gamesTableView.setFixedCellSize(70);
        gamesTableView.prefHeightProperty().bind(Bindings.size(gamesTableView.getItems()).multiply(gamesTableView.getFixedCellSize()).add(50));

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
