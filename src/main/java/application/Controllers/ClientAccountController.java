package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import users.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ClientAccountController extends Controller {

    private final Lighting lighting = new Lighting();
    private final Client currentUser = new Client(CURRENT_USER_LOGIN);

    @FXML
    private Pane ordersPane, favouritesPane, detailsPane;
    @FXML
    private TableColumn<OrderTable, String> ordersDateColumn, ordersDeliveryDateColumn, ordersPaymentColumn, ordersStatusColumn, ordersIdColumn, ordersButtonColumn, orderDetailsProductColumn, orderDetailsTotalValueColumn, orderDetailsValueColumn;
    @FXML
    private TableColumn<OrderTable, Double> ordersTotalValueColumn;
    @FXML
    private TableColumn<OrderTable, Integer> orderDetailsQuantityColumn;
    @FXML
    private TableView<OrderTable> ordersTableView, orderDetailsTableView;
    @FXML
    TableColumn<ProductTable, String> favouritesNameColumn, favouritesPriceColumn, favouritesSubcategoryColumn, favouritesButtonColumn;


    @FXML
    private TableView<ProductTable> favouritesTableView;
    @FXML
    private Label orderIdLabel, totalValueLabel, paymentMethodLabel, orderStatusLabel, informationLabel, emptyTableViewLabel;
    @FXML
    private Button ordersButton, accountButton, favouritesButton, settingsButton, payOrderButton, changePaymentMethodButton, cancelOrderButton;


    @FXML
    public void initialize() {
        prepareScene();
        createGoBackButton();
        createLightingEffect();
        createInformationImageAndAttachItToLabel();
        createEmptyTableViewLabel();
        ordersButton.fire();

    }

    @FXML
    void ordersButtonClicked() {
        makePaneVisible(ordersPane);
        displayOrders();
        setButtonLightingEffect(ordersButton);
    }

    @FXML
    void favouritesButtonClicked() {
        makePaneVisible(favouritesPane);
        displayFavourites();
        setButtonLightingEffect(favouritesButton);
    }

    private void createLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setColor(Color.LIGHTPINK);
        lighting.setLight(light);
    }

    private void createGoBackButton() {
        Button button = super.createGoBackButton(event -> switchScene(event, clientScene));
        button.setLayoutX(60);
        button.setLayoutY(2);
    }

    private void createEmptyTableViewLabel() {
        emptyTableViewLabel.setId("titleLabel");
        emptyTableViewLabel.setVisible(false);
        emptyTableViewLabel.setAlignment(Pos.CENTER);
    }

    private void createInformationImageAndAttachItToLabel() {
        ImageView informationImage = setImageFromIconsFolder("information.png");
        informationImage.setLayoutX(725);
        informationImage.setLayoutY(91);
        orderStatusLabel.setGraphic(informationImage);
        orderStatusLabel.setContentDisplay(ContentDisplay.RIGHT);
        orderStatusLabel.setOnMouseEntered(mouseEvent -> informationLabel.setVisible(true));
        orderStatusLabel.setOnMouseExited(mouseEvent -> informationLabel.setVisible(false));
    }

    private void displayInformationAboutOrderStatus() {
        HashMap<String, String> orderStatus = new HashMap<>();
        orderStatus.put("Canceled", "Your order has been cancelled and your payment will be refunded");
        orderStatus.put("In progress", "Your order has been paid and is awaiting approval");
        orderStatus.put("Finished", "Order has been sent to the email assigned to your account");
        orderStatus.put("Waiting for payment", "The order has not been paid");
        informationLabel.setWrapText(true);
        informationLabel.setText(orderStatus.get(orderStatusLabel.getText()));
        StackPane.setAlignment(informationLabel, Pos.CENTER);
    }

    private void setButtonLightingEffect(Button button) {
        ordersButton.setEffect(null);
        favouritesButton.setEffect(null);
        settingsButton.setEffect(null);
        accountButton.setEffect(null);
        if (button == ordersButton) ordersButton.setEffect(lighting);
        else if (button == favouritesButton) favouritesButton.setEffect(lighting);
        else if (button == settingsButton) settingsButton.setEffect(lighting);
        else if (button == accountButton) accountButton.setEffect(lighting);
    }

    private void makePaneVisible(Pane pane) {
        ordersPane.setVisible(false);
        detailsPane.setVisible(false);
        favouritesPane.setVisible(false);
        emptyTableViewLabel.setVisible(false);
        if (pane == ordersPane) {
            ordersPane.setVisible(true);
        } else if (pane == detailsPane) {
            detailsPane.setVisible(true);
        } else if (pane == favouritesPane) {
            favouritesPane.setVisible(true);
        }
    }

    private void makeProperButtonsVisible(String orderStatus) {
        payOrderButton.setDisable(true);
        cancelOrderButton.setDisable(true);
        changePaymentMethodButton.setDisable(true);

        switch (orderStatus) {
            case "Waiting for payment" -> {
                payOrderButton.setDisable(false);
                changePaymentMethodButton.setDisable(false);
                cancelOrderButton.setDisable(false);
            }
            case "In progress" -> cancelOrderButton.setDisable(false);
        }
    }
    private void fillOrdersColumnsWithData(ObservableList<OrderTable> list) {
        ordersIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        ordersButtonColumn.setCellFactory(orderTableStringTableColumn -> orderIdButton());
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordersDeliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDeliveryDate"));
        ordersTotalValueColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        ordersStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatusName"));
        ordersPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("orderPaymentMethodName"));
        ordersTableView.setItems(list);
        showOnlyRowsWithData(ordersTableView);
    }

    private void fillOrderDetailColumnsWithData(ObservableList<OrderTable> list) {
        orderDetailsProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderDetailsQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        orderDetailsTotalValueColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        orderDetailsValueColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        orderDetailsTableView.setItems(list);
        showOnlyRowsWithData(orderDetailsTableView);
        orderDetailsTableView.setMaxHeight(250);
    }


    private ClientController.ButtonInsideTableColumn<OrderTable, String> orderIdButton() {
        ClientController.ButtonInsideTableColumn<OrderTable, String> button = new ClientController().new ButtonInsideTableColumn<>("", "details");
        EventHandler<MouseEvent> buttonClicked = mouseEvent -> {
            makePaneVisible(detailsPane);
            displayOrderDetails(button.getRowId().getOrderNumber());
            fillOrderDetailLabels(button);
            makeProperButtonsVisible(orderStatusLabel.getText());
            displayInformationAboutOrderStatus();
        };
        button.setEventHandler(buttonClicked);
        button.setCssId("orderDetailsButton");
        return button;
    }

    private void displayOrderDetails(int orderNumber) {
        checkConnectionWithDb();
        Order order = new Order(CURRENT_USER_LOGIN);
        try {
            ResultSet orders = order.getOrderDetailedInformation(getConnection(), orderNumber);
            ObservableList<OrderTable> listOfOrders = OrderTable.getProductsFromOrder(orders);
            fillOrderDetailColumnsWithData(listOfOrders);
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillOrderDetailLabels(ClientController.ButtonInsideTableColumn<OrderTable, String> button) {
        orderIdLabel.setText(button.getRowId().getOrderNumber() + "");
        totalValueLabel.setText(button.getRowId().getOrderTotalValue() + "");
        paymentMethodLabel.setText(button.getRowId().getOrderPaymentMethodName());
        orderStatusLabel.setText(button.getRowId().getOrderStatusName());
        setDisableToAllLabels(orderStatusLabel.getText().equals("Canceled"));
    }

    private void setDisableToAllLabels(boolean disable) {
        orderIdLabel.setDisable(disable);
        totalValueLabel.setDisable(disable);
        paymentMethodLabel.setDisable(disable);
    }

    private void displayOrders() {
        checkConnectionWithDb();
        Order order = new Order(CURRENT_USER_LOGIN);
        try {
            ResultSet orders = order.getOrdersFromCustomer(getConnection());
            ObservableList<OrderTable> listOfOrders = OrderTable.getOrders(orders);
            if (listOfOrders.isEmpty()) {
                displayLabelWithGivenText(emptyTableViewLabel, "There are no orders !");
                ordersTableView.setVisible(false);
            } else {
                ordersTableView.setVisible(true);
                emptyTableViewLabel.setVisible(false);
                fillOrdersColumnsWithData(listOfOrders);

            }

            ordersTableView.setMaxHeight(530);
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillFavouritesColumns(ObservableList<ProductTable> listOfOrders) {
        favouritesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        favouritesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        favouritesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        favouritesButtonColumn.setCellFactory(cell -> createDeleteFromFavouritesButton());
        favouritesTableView.setItems(listOfOrders);
    }

    private ClientController.ButtonInsideTableColumn<ProductTable, String> createDeleteFromFavouritesButton() {
        ClientController.ButtonInsideTableColumn<ProductTable, String> deleteFromFavouritesButton = new ClientController().new ButtonInsideTableColumn<>("delete.png", "delete from favourites");
        deleteFromFavouritesButton.setEventHandler(mouseEvent -> {
            String productName = deleteFromFavouritesButton.getRowId().getProductName();
            try {
                currentUser.deleteItemFromUsersFavourite(productName, getConnection());
                showNotification(createNotification(new Label("Item removed from favourites")), 2500);
                displayFavourites();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        deleteFromFavouritesButton.setCssClassId("clientTableviewButtons");
        return deleteFromFavouritesButton;
    }

    private void displayFavourites() {
        checkConnectionWithDb();
        try {
            ResultSet favourites = currentUser.getFavouriteProducts(getConnection());
            ObservableList<ProductTable> listOfFavourites = ProductTable.getProductFromFavourites(favourites);
            if (listOfFavourites.isEmpty()) {
                displayLabelWithGivenText(emptyTableViewLabel, "List of favourites is empty");
                favouritesTableView.setVisible(false);
            } else {
                fillFavouritesColumns(listOfFavourites);
                emptyTableViewLabel.setVisible(false);
                favouritesTableView.setVisible(true);
                showOnlyRowsWithData(favouritesTableView);
            }

            favourites.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reloadTableView(TableView<?> tableView) {
        tableView.getItems().clear();
        displayOrders();
    }

    private void changeOrderStatusAndDisplayProperButtons(String status) {
        try {
            Order order = new Order(Integer.parseInt(orderIdLabel.getText()));
            order.setOrderStatus(getConnection(), status);
            reloadTableView(ordersTableView);
            orderStatusLabel.setText(status);
            displayInformationAboutOrderStatus();
            makeProperButtonsVisible(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changePaymentMethod(String paymentMethod) {
        try {
            Order order = new Order(Integer.parseInt(orderIdLabel.getText()));
            order.setPaymentMethod(getConnection(), paymentMethod);
            reloadTableView(ordersTableView);
            paymentMethodLabel.setText(paymentMethod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void payOrderButtonClicked() {
        changeOrderStatusAndDisplayProperButtons("In progress");
        showNotification(createNotification(new Label("order successfully paid")), 4000);
    }

    @FXML
    void cancelOrderButtonClicked() {
        Optional<ButtonType> buttonClicked = createAndShowAlert(Alert.AlertType.WARNING, "", "Canceling", "Are you sure about canceling the order ?");
        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            changeOrderStatusAndDisplayProperButtons("Canceled");
            setDisableToAllLabels(true);
        }
    }

    @FXML
    void changePaymentMethodButtonClicked() {
        try {
            Optional<String> buttonInsideAlertText = setAvailablePaymentMethodsToAlert(paymentMethodLabel.getText());
            buttonInsideAlertText.ifPresent(this::changePaymentMethod);
            showNotification(createNotification(new Label("payment method changed")), 4000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> setAvailablePaymentMethodsToAlert(String paymentMethod) throws SQLException {
        List<String> choices = new ArrayList<>();
        ResultSet paymentMethods = Product.getPaymentMethods(getConnection());

        while (Objects.requireNonNull(paymentMethods).next()) {
            if (!paymentMethods.getString(2).equals(paymentMethod)) {
                choices.add(paymentMethods.getString(2));
            }
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setHeaderText("Payment method change");
        dialog.setContentText("Available payment methods : ");
        return dialog.showAndWait();
    }


}
