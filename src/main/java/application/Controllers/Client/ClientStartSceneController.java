package application.Controllers.Client;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Controller;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import users.Client;
import users.ProductTable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ClientStartSceneController extends Controller {

    private final int cartLabelXPosition = 60;
    private final int cartLabelYPosition = 2;
    private final double durationOfNotification = 3000;
    protected final String sortingBoxDefaultText = "Choose sorting type :";
    protected final Client currentUser = new Client(CURRENT_USER_LOGIN);
    @FXML
    private Pane mainPane, categoryPickingPane;
    @FXML
    private Button goBackButton, shoppingCartButton;

    @FXML
    public static Label cartQuantityLabel;
    @FXML
    public static StackPane cartNotification, starNotification, yellowStartNotification;
    @FXML
    protected ComboBox<String> sortingButtonsBox;

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
        goBackButton.setVisible(false);
        createSortingButtons();
    }

    protected void createSortingButtons() {
        sortingButtonsBox = new ComboBox<>();
        sortingButtonsBox.getItems().addAll("Price (High -> Low)", "Price (Low -> High)", "Number of orders", "Name", "Favourites first");
        sortingButtonsBox.setLayoutX(815);
        sortingButtonsBox.setLayoutY(82);
        sortingButtonsBox.getStyleClass().add("OrangeButtons");
        sortingButtonsBox.setId("sortingButtons");
        anchor.getChildren().add(sortingButtonsBox);
        sortingButtonsBox.setVisible(false);
    }

    protected void prepareSortingButtons(TableView<ProductTable> tableView, TableColumn<ProductTable, String> numberOfOrdersColumn, TableColumn<ProductTable, String> columnWithStar) {
        sortingButtonsBox.setValue(sortingBoxDefaultText);
        sortingButtonsBox.setVisible(true);
        sortingButtonsBox.valueProperty().addListener((observableValue, s, selectedValue) -> {
            tableView.getSortOrder().clear();
            if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(0))) {
                tableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
                tableView.getSortOrder().add(tableView.getColumns().get(1));
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(1))) {
                tableView.getColumns().get(1).setSortType(TableColumn.SortType.ASCENDING);
                tableView.getSortOrder().add(tableView.getColumns().get(1));
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(2))) {
                numberOfOrdersColumn.setSortType(TableColumn.SortType.DESCENDING);
                tableView.getSortOrder().add(numberOfOrdersColumn);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(3))) {
                tableView.getColumns().get(0).setSortType(TableColumn.SortType.ASCENDING);
                tableView.getSortOrder().add(tableView.getColumns().get(0));
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(4))) {
                columnWithStar.setSortType(TableColumn.SortType.DESCENDING);
                tableView.getSortOrder().add(columnWithStar);
            }
            tableView.requestFocus();
        });

    }

    @FXML
    private void allProductsButtonClicked() {
        sortingButtonsBox.setValue(sortingBoxDefaultText);
        loadPane("allProductsPaneGUI.fxml");

    }

    @FXML
    private void gamesButtonClicked() {
        loadPane("allGamesPaneGUI.fxml");
    }

    @FXML
    private void ebooksButtonClicked() {
        loadPane("ebooksCategoriesGUI.fxml");
    }

    private void loadPane(String fxmlName) {
        try {
            mainPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/application/FXML/ClientSceneFXML/ProductsFXML/" + fxmlName))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        goBackButton.setVisible(true);
        categoryPickingPane.setVisible(false);

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
        goBackButton = super.createGoBackButton(event -> {
            categoryPickingPane.setVisible(true);
            mainPane.getChildren().removeAll();
            mainPane.getChildren().clear();
            goBackButton.setVisible(false);
            sortingButtonsBox.setVisible(false);
            sortingButtonsBox.setValue(sortingBoxDefaultText);

        });


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

    protected EventHandler<MouseEvent> buttonInsideTableViewClicked(ButtonInsideProductTableView button, StackPane notificationName) {
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

    protected ButtonInsideProductTableView createStarButtonInsideTableView() {
        ButtonInsideProductTableView starButton = new ButtonInsideProductTableView("star.png", "add to favourites");
        starButton.setEventHandler(buttonInsideTableViewClicked(starButton, starNotification));
        starButton.setCssClassId("clientTableviewButtons");

        return starButton;
    }

    protected ButtonInsideProductTableView createCartButtonInsideTableView() {
        ButtonInsideProductTableView button = new ButtonInsideProductTableView("add_cart.png", "add to cart");
        button.setCssClassId("clientTableviewButtons");
        button.setEventHandler(buttonInsideTableViewClicked(button, cartNotification));
        return button;
    }


    class ButtonInsideProductTableView extends ButtonInsideTableColumn<ProductTable, String> {

        public ButtonInsideProductTableView(String iconNameWithExtension, String buttonText) {
            super(iconNameWithExtension, buttonText);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                button.setOnMouseClicked(eventHandler);
                button.setGraphic(new ImageView(getClass().getResource("/application/Icons/") + iconName));
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
