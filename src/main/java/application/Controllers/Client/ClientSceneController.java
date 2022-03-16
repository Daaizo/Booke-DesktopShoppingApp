package application.Controllers.Client;

import application.Controllers.Controller;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import users.Client;
import users.ProductTable;

import java.sql.SQLException;

public class ClientSceneController extends Controller {

    @FXML
    public static Label cartQuantityLabel;
    private final int cartLabelXPosition = 60;
    private final int cartLabelYPosition = 2;
    protected final Client currentUser = new Client(CURRENT_USER_LOGIN);
    private final double durationOfNotification = 3000;
    @FXML
    protected StackPane cartNotification, starNotification, yellowStartNotification;
    @FXML
    private Pane categoryPickingPane, ebooksPane, gamesPane, allProductsPane, ebooksCategoryPane;
    @FXML
    private Button goBackButton, shoppingCartButton;

    @FXML
    private void initialize() {
        prepareScene();
        createNotifications();
        createButtons();
        createQuantityLabel();
        try {
            setQuantityLabel(cartQuantityLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void allProductsButtonClicked() {
        makePaneAndGoBackButtonVisible(allProductsPane);
    }

    @FXML
    private void gamesButtonClicked() {
        makePaneAndGoBackButtonVisible(gamesPane);
    }

    @FXML
    private void ebooksButtonClicked() {
        makePaneAndGoBackButtonVisible(ebooksCategoryPane);
    }

    private void hideAllPanes() {
        ebooksCategoryPane.setVisible(false);
        gamesPane.setVisible(false);
        categoryPickingPane.setVisible(false);
        ebooksPane.setVisible(false);
        allProductsPane.setVisible(false);
    }

    private void ifPaneMatchesMakeItVisible(Pane paneA, Pane paneB) {
        if (paneA.equals(paneB)) paneB.setVisible(true);
    }

    private void makePaneAndGoBackButtonVisible(Pane pane) {
        goBackButton.setVisible(true);
        hideAllPanes();
        ifPaneMatchesMakeItVisible(pane, categoryPickingPane);
        ifPaneMatchesMakeItVisible(pane, gamesPane);
        ifPaneMatchesMakeItVisible(pane, ebooksPane);
        ifPaneMatchesMakeItVisible(pane, allProductsPane);
        ifPaneMatchesMakeItVisible(pane, ebooksCategoryPane);

    }



    private void createButtons() {
        createAccountButton();
        createGoBackButton();
        createCartButton();
    }


    private void createAccountButton() {
        Button userAccountInformationButton = createButton("user.png", cartLabelXPosition + 60, cartLabelYPosition);
        userAccountInformationButton.setOnAction(event -> switchScene(event, clientAccountScene));
    }

    private void createCartButton() {
        shoppingCartButton = createButton("cart.png", cartLabelXPosition, cartLabelYPosition);
        shoppingCartButton.setOnAction(event -> switchScene(event, shoppingCartScene));

    }

    private void createGoBackButton() {
        goBackButton = super.createGoBackButton(event -> makePaneAndGoBackButtonVisible(categoryPickingPane));
        goBackButton.fire();
        goBackButton.setVisible(false);
    }

    private void createQuantityLabel() {
        cartQuantityLabel = new Label();
        cartQuantityLabel.setId("cartQuantityLabel");
        cartQuantityLabel.setLayoutX(cartLabelXPosition + 20);
        cartQuantityLabel.setLayoutY(cartLabelYPosition + 12);
        cartQuantityLabel.setOnMouseClicked(mouseEvent -> shoppingCartButton.fire());
        anchor.getChildren().add(cartQuantityLabel);
    }

    private void setQuantityLabel(Label cartQuantityLabel) throws SQLException {
        checkConnectionWithDb();
        int quantity = currentUser.getQuantityOfProductsInCart(getConnection());
        if (quantity > 9)
            displayLabelWithGivenText(cartQuantityLabel, "9+");
        else displayLabelWithGivenText(cartQuantityLabel, " " + quantity);

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

    protected EventHandler<MouseEvent> buttonInsideTableViewClicked(ButtonInsideTableColumn<ProductTable, String> button, StackPane notificationName) {
        return mouseEvent -> {
            String productName = button.getRowId().getProductName();

            checkConnectionWithDb();
            try {
                if (notificationName.equals(cartNotification)) {
                    currentUser.addItemToUsersCart(productName, getConnection());
                    setQuantityLabel(cartQuantityLabel);
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

    protected <T> EventHandler<MouseEvent> deleteFromFavouriteClicked(T rowId) {
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

    protected ButtonInsideTableColumn<ProductTable, String> createStarButtonInsideTableView() {
        ButtonInsideTableColumn<ProductTable, String> starButton = new ButtonInsideTableColumn<>("star.png", "add to favourites");
        starButton.setEventHandler(buttonInsideTableViewClicked(starButton, starNotification));
        starButton.setCssClassId("clientTableviewButtons");

        return starButton;
    }

    protected ButtonInsideTableColumn<ProductTable, String> createCartButtonInsideTableView() {
        ButtonInsideTableColumn<ProductTable, String> button = new ButtonInsideTableColumn<>("add_cart.png", "add to cart");
        button.setCssClassId("clientTableviewButtons");
        button.setEventHandler(buttonInsideTableViewClicked(button, cartNotification));
        return button;
    }


    protected class ButtonInsideTableColumn<T, V> extends TableCell<T, V> {

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



}
