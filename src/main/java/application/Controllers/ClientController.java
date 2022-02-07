package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import users.Client;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientController extends Controller {
    @FXML
    private AnchorPane ebooksAnchorPane, gamesAnchorPane;
    @FXML
    private Button ebooksButton, gamesButton, shoppingCartButton;
    @FXML
    private Label cartQuantityLabel;
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
    void shoppingCartButtonClicked(ActionEvent event) {
        switchScene(event, shoppingCartScene);
    }


    @FXML
    public void initialize() {

        prepareScene();
        goBackButton.setOnAction(event -> {
            categoryPickingPane.setVisible(true);
            ebooksAnchorPane.setVisible(false);
            gamesAnchorPane.setVisible(false);
            goBackButton.setVisible(false);

        });
        categoryPickingPane.setVisible(true);
        ebooksAnchorPane.setVisible(false);
        gamesAnchorPane.setVisible(false);
        goBackButton.setVisible(false);

        //  setImageToButtonAndPlaceItOnXY(goBackButton, "back-button.png", -10, 10);
        setImageToButtonAndPlaceItOnXY(shoppingCartButton, "cart.png", 30, 3);
        try {
            displayEbooks();
            displayGames();
            setQuantityLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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


    private void setQuantityLabel() throws SQLException {
        checkConnectionWithDb();
        int quantity = Client.getQuantityOfProductsInCart(CURRENT_USER_LOGIN, getConnection());
        if (quantity > 9)
            displayLabelWithGivenText(cartQuantityLabel, "9+");
        else displayLabelWithGivenText(cartQuantityLabel, " " + quantity);

    }

    private Callback createButtonInTableView(String buttonIconName, String buttonText) {
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
                        button.setOnAction(actionEvent -> {
                            ProductTable product = getTableView().getItems().get(getIndex());
                            if (buttonIconName.compareTo("star.png") == 0) {
                                System.out.print(" gwiazdka ");

                            } else {
                                System.out.print(" koszyk ");
                                try {
                                    checkConnectionWithDb();
                                    Client.addItemToUsersCart(product.getProductName(), CURRENT_USER_LOGIN, getConnection());
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                } finally {
                                    try {
                                        setQuantityLabel();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println(product.getProductName() + product.getProductPrice() + CURRENT_USER_LOGIN);
                            }
                        });
                        button.setOnMouseEntered(mouseEvent -> button.setStyle("-fx-border-color : #fc766a ;")
                        );
                        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: transparent"));
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
        ebooksStarButtonColumn.setCellFactory(createButtonInTableView("star.png", "add to favourite"));
        ebooksCartButtonColumn.setCellFactory(createButtonInTableView("add_cart.png", "add to cart"));
        ebooksCartButtonColumn.setStyle("  -fx-padding: 15 5 5 5px;");
        ebooksStarButtonColumn.setStyle("  -fx-padding: 15 5 5 5px;");

        showOnlyRowsWithData(ebooksTableView, list);

    }


    void displayEbooks() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductsFromDatabase(getConnection());
        assert products != null;
        ObservableList<ProductTable> listOfEbooks = ProductTable.getProductsFromCategory(products, "ebooks");

        fillEbooksColumnsWithData(listOfEbooks);
    }

    private void fillGamesColumnsWithData(ObservableList<ProductTable> list) {
        gamesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        gamesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        gamesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        gamesStarButtonColumn.setCellFactory(createButtonInTableView("star.png", "add to favourite"));
        gamesCartButtonColumn.setCellFactory(createButtonInTableView("add_cart.png", "add to cart"));
        gamesCartButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        gamesStarButtonColumn.setStyle("  -fx-padding: 18 5 5 5px;");
        showOnlyRowsWithData(gamesTableView, list);

    }

    void displayGames() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductsFromDatabase(getConnection());
        assert products != null;
        ObservableList<ProductTable> listOfGames = ProductTable.getProductsFromCategory(products, "games");
        fillGamesColumnsWithData(listOfGames);
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
