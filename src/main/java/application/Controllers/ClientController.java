package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
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
        goBackButton.fire(); // fire action to hide all not necessary panes
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

    private void fillEbooksColumnsWithData(ObservableList<ProductTable> list) {
        ebooksNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        ebooksPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        ebooksSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        ebooksStarButtonColumn.setCellFactory(
                buttonInsideCell -> new ButtonInsideTableColumn("star.png", "add to favourite", starButtonClicked()));
        ebooksCartButtonColumn.setCellFactory(
                buttonInsideCell -> cartButtonClicked());
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
        gamesStarButtonColumn.setCellFactory(
                buttonInsideCell -> new ButtonInsideTableColumn("star.png", "add to favourite", starButtonClicked()));
        gamesCartButtonColumn.setCellFactory(
                buttonInsideCell -> cartButtonClicked());
        showOnlyRowsWithData(gamesTableView, list);
    }

    void displayGames() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductsFromDatabase(getConnection());
        assert products != null;
        ObservableList<ProductTable> listOfGames = ProductTable.getProductsFromCategory(products, "games");
        fillGamesColumnsWithData(listOfGames);
    }

    private EventHandler<MouseEvent> starButtonClicked() {
        return event -> System.out.println("start clicked // to be continued");
    }


    private ButtonInsideTableColumn cartButtonClicked() {
        ButtonInsideTableColumn button = new ButtonInsideTableColumn("add_cart.png", "add to cart");
        EventHandler<MouseEvent> eventHandler = event -> {
            String productName = button.productName;
            try {
                checkConnectionWithDb();
                System.out.println("cos nie dziala");
                Client.addItemToUsersCart(productName, CURRENT_USER_LOGIN, getConnection());
                System.out.println(productName);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    setQuantityLabel();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        button.setEventHandler(eventHandler);
        return button;
    }


    public class ButtonInsideTableColumn extends TableCell<ProductTable, String> {
        private final Button button;
        private final String iconName;
        private EventHandler<MouseEvent> eventHandler;
        private String productName;

        public ButtonInsideTableColumn(String iconNameWithExtension, String buttonText, EventHandler<MouseEvent> eventHandler) {
            this.iconName = iconNameWithExtension;
            this.button = new Button(buttonText);
            this.eventHandler = eventHandler;
        }

        public ButtonInsideTableColumn(String iconNameWithExtension, String buttonText) {
            this.iconName = iconNameWithExtension;
            this.button = new Button(buttonText);
            button.fire();
        }

        public void setEventHandler(EventHandler<MouseEvent> eventHandler) {
            this.eventHandler = eventHandler;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            button.setOnAction(mouseEvent -> {
                ProductTable product = getTableView().getItems().get(getIndex());
                System.out.println("produkt +" + product.getProductName());
                this.productName = product.getProductName();

            });
            button.setOnMouseClicked(eventHandler);
            button.setGraphic(iconPath(iconName));
            button.setBackground(Background.EMPTY);
            button.setOnMouseEntered(mouseEvent -> button.setStyle("-fx-border-color : #fc766a ;"));
            button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: transparent"));
            setGraphic(button);
        }
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
