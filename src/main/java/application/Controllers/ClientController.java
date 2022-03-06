package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
        starNotification = createNotification(new Label("Item added to favourites"));
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
        ebooksStarButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        ebooksStarButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        ebooksCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        ebooksTableView.setItems(list);
        showOnlyRowsWithData(ebooksTableView);

    }


    void displayEbooks() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfEbooks = ProductTable.getProductsFromCategoryAndCheckIfTheyAreInUsersFavourite(products, "ebooks");
        fillEbooksColumnsWithData(listOfEbooks);
    }

    private void fillGamesColumnsWithData(ObservableList<ProductTable> list) {
        gamesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        gamesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        gamesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        gamesStarButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        gamesStarButtonColumn.setCellFactory(productTableStringTableColumn -> createStarButtonInsideTableView());
        gamesCartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        gamesTableView.setItems(list);
        showOnlyRowsWithData(gamesTableView);
    }

    void displayGames() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), CURRENT_USER_LOGIN);
        assert products != null;
        ObservableList<ProductTable> listOfGames = ProductTable.getProductsFromCategoryAndCheckIfTheyAreInUsersFavourite(products, "games");
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
                    button.getRowId().setIsProductFavourite("yes");
                    button.getTableView().refresh();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            turnOfNotifications();
            showNotification(notificationName, durationOfNotification);
        };
    }

    public <T> EventHandler<MouseEvent> deleteFromFavouriteClicked(T rowId) {
        return mouseEvent -> {
            try {
                ProductTable productTableCellId = (ProductTable) rowId;
                String clickedProductName = productTableCellId.getProductName();
                currentUser.deleteItemFromUsersFavourite(clickedProductName, getConnection());
                turnOfNotifications();
                showNotification(yellowStartNotification, durationOfNotification);
                ((ProductTable) rowId).setIsProductFavourite("no");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

    }

    private ButtonInsideTableColumn<ProductTable, String> createStarButtonInsideTableView() {
        ButtonInsideTableColumn<ProductTable, String> starButton = new ButtonInsideTableColumn<>("star.png", "add to favourites");
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
        private final String iconName;
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
        public T getRowId() {
            return rowId;
        }

        public ButtonInsideTableColumn(String iconNameWithExtension, String buttonText) {
            this.iconName = iconNameWithExtension;
            this.button = new Button();
            this.button.setText(buttonText);
            button.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));
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

                button.setOnMouseClicked(eventHandler);
                button.setGraphic(setImageFromIconsFolder(iconName));
                button.setBackground(Background.EMPTY);
                button.setId(cssId);
                button.getStyleClass().add(cssClassId);
                setGraphic(button);

                if (item != null) {
                    if (item.equals("yes")) {
                        setGraphic(setImageFromIconsFolder("yellowStar.png"));
                        setText("");
                        button.fire();
                        setOnMouseClicked(deleteFromFavouriteClicked(getRowId()));
                        setOnMouseEntered(event -> setCursor(Cursor.HAND));
                        setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
                        getStyleClass().add("clientTableviewButtons");
                    }
                }
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
