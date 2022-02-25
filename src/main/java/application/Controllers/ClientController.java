package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import users.Client;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientController extends Controller {

    private final int cartLabelXPosition = 60;
    private final int cartLabelYPosition = 2;
    private final double durationOfNotification = 3000;
    private final Client currentUser = new Client(CURRENT_USER_LOGIN);
    @FXML
    private AnchorPane ebooksAnchorPane, gamesAnchorPane;
    @FXML
    private Button ebooksButton, gamesButton, goBackButton;
    @FXML
    private Label cartQuantityLabel;
    @FXML
    private StackPane cartNotification, starNotification, yellowStartNotification;
    @FXML
    private Pane categoryPickingPane;
    @FXML
    private TableColumn<ProductTable, String> gamesNameColumn, gamesSubcategoryColumn, ebooksNameColumn, ebooksSubcategoryColumn;
    @FXML
    private TableColumn<ProductTable, Double> gamesPriceColumn, ebooksPriceColumn;
    @FXML
    private TableColumn<ProductTable, String> gamesIsFavouriteColumn;
    @FXML
    private TableColumn<ProductTable, String> ebooksStarButtonColumn, ebooksCartButtonColumn, gamesCartButtonColumn, gamesStarButtonColumn;
    @FXML
    private TableView<ProductTable> gamesTableView, ebooksTableView;

    @FXML
    public void initialize() {
        prepareScene();
        createNotifications();
        createButtons();
        try {
            displayEbooks();
            displayGames();
            setQuantityLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNotifications() {
        starNotification = createNotification(new Label("Item added to favourite"));
        cartNotification = createNotification(new Label("Item added to cart"));
        yellowStartNotification = createNotification(new Label("Item removed from favourites"));
    }

    private void turnOfNotifications() {
        starNotification.setVisible(false);
        cartNotification.setVisible(false);
        yellowStartNotification.setVisible(false);
    }

    private void createButtons() {
        createAccountButton();
        createGoBackButton();
        createCartButton();
    }

    @FXML
    void createAccountButton() {
        Button userAccountInformationButton = createButton("user.png", cartLabelXPosition + 60, cartLabelYPosition);
        userAccountInformationButton.setOnAction(event -> switchScene(event, clientAccountScene));
    }

    void createCartButton() {
        Button shoppingCartButton = createButton("cart.png", cartLabelXPosition, cartLabelYPosition);
        shoppingCartButton.setOnAction(event -> switchScene(event, shoppingCartScene));

    }

    private void createGoBackButton() {
        goBackButton = super.createGoBackButton(event -> {
            categoryPickingPane.setVisible(true);
            ebooksAnchorPane.setVisible(false);
            gamesAnchorPane.setVisible(false);
            goBackButton.setVisible(false);
        });
        goBackButton.fire();
        goBackButton.setVisible(false);
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
        cartQuantityLabel.setLayoutX(cartLabelXPosition + 20);
        int quantity = currentUser.getQuantityOfProductsInCart(getConnection());
        if (quantity > 9)
            displayLabelWithGivenText(cartQuantityLabel, "9+");
        else displayLabelWithGivenText(cartQuantityLabel, " " + quantity);

    }

    private void fillEbooksColumnsWithData(ObservableList<ProductTable> list) {
        ebooksNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        ebooksPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        ebooksSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        ebooksStarButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        ebooksCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        ebooksTableView.setItems(list);
        showOnlyRowsWithData(ebooksTableView);

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
        gamesIsFavouriteColumn.setCellValueFactory(new PropertyValueFactory<>("isProductFavourite"));
        gamesStarButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        gamesCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        gamesTableView.setItems(list);
        showOnlyRowsWithData(gamesTableView);
    }

    void displayGames() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfGames = ProductTable.getProducts(products);
        fillGamesColumnsWithData(listOfGames);
    }

    private EventHandler<MouseEvent> buttonInsideTableViewClicked(ButtonInsideTableColumn<ProductTable, String> button, StackPane notificationName) {
        return mouseEvent -> {
            String productName = button.getRowId().getProductName();

            checkConnectionWithDb();
            try {
                if (notificationName.equals(cartNotification)) {
                    currentUser.addItemToUsersCart(productName, getConnection());
                    setQuantityLabel();
                } else {
                    currentUser.addItemToUsersFavourite(productName, getConnection());
                    gamesTableView.refresh();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            turnOfNotifications();
            showNotification(notificationName, durationOfNotification);

        };
    }

    private EventHandler<MouseEvent> yellowStarClicked(ButtonInsideTableColumn<ProductTable, String> button, String productName) {
        return mouseEvent -> {
            try {
                currentUser.deleteItemFromUsersFavourite(productName, getConnection());
                turnOfNotifications();
                showNotification(yellowStartNotification, durationOfNotification);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //      button.setOnMouseClicked(buttonInsideTableViewClicked(button,starNotification));
        };

    }

    private ButtonInsideTableColumn<ProductTable, String> createStarButtonInsideTableView() {
        ButtonInsideTableColumn<ProductTable, String> starButton = new ButtonInsideTableColumn<>("star.png", "add to favourite");
        starButton.setEventHandler(buttonInsideTableViewClicked(starButton, starNotification));
        starButton.setCssClassId("clientTableviewButtons");

        return starButton;
    }

    private ButtonInsideTableColumn<ProductTable, String> createCartButtonInsideTableView() {
        ButtonInsideTableColumn<ProductTable, String> button = new ButtonInsideTableColumn<>("add_cart.png", "add to cart");
        button.setCssClassId("clientTableviewButtons");
        button.setEventHandler(buttonInsideTableViewClicked(button, cartNotification));
        return button;
    }


    public class ButtonInsideTableColumn<T, V> extends TableCell<T, V> {

        private final Button button;
        private String iconName;
        private EventHandler<MouseEvent> eventHandler;
        private T rowId;
        private String cssId;
        private String cssClassId;

        public void setCssId(String cssId) {
            this.cssId = cssId;
        }

        public void setCssClassId(String classId) {
            this.cssClassId = classId;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }

        public T getRowId() {
            return rowId;
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
        protected void updateItem(V item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {

                button.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));
                button.setOnMouseClicked(eventHandler);
                button.setGraphic(setImageFromIconsFolder(iconName));
                button.setBackground(Background.EMPTY);
                button.setId(cssId);
                button.getStyleClass().add(cssClassId);
                setGraphic(button);
            }
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
