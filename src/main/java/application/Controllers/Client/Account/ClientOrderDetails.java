package application.Controllers.Client.Account;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import users.Client;
import users.Order;
import users.OrderTable;
import users.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClientOrderDetails extends ClientAccountStartSceneController {
    private final HashMap<String, String> orderStatusHashMap = createHashMapWithOrderStatuses();

    @FXML
    private Pane detailsPane, allOrdersPane;
    @FXML
    private Label orderIdLabel, totalValueLabel, paymentMethodLabel, orderStatusLabel, informationLabel;
    @FXML
    private TableColumn<OrderTable, Integer> orderDetailsQuantityColumn;
    @FXML
    private TableColumn<OrderTable, String> orderDetailsProductColumn, orderDetailsTotalValueColumn, orderDetailsValueColumn;
    @FXML
    private TableView<OrderTable> orderDetailsTableView;
    @FXML
    private Button payOrderButton, changePaymentMethodButton, cancelOrderButton;
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------
    //all fields are assigned in FXML but controller is set on run time
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------

    private boolean isLunchedByAdmin;
    private int orderNumber;
    private OrderTable orderTable;

    public ClientOrderDetails(boolean isLunchedByAdmin) {
        this.isLunchedByAdmin = isLunchedByAdmin;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOrder(OrderTable orderTable) {
        this.orderTable = orderTable;
    }

    public void setAllOrdersPane(Pane pane) {
        this.allOrdersPane = pane;
    }

    public void setGoBackButton(Button button) {
        goBackButton = button;
    }

    public void isLunchedByAdmin(boolean isLunchedByAdmin) {
        this.isLunchedByAdmin = isLunchedByAdmin;
    }

    public void initialize() {
        detailsPane.requestFocus();
        displayOrderDetails(orderNumber);
        if (orderTable == null) {
            orderTable = getOrdersInformationFromDataBase();
            if (goBackButton != null) goBackButton.setVisible(false);
        }
        if (isLunchedByAdmin) {
            hideAllButtons();
            setClientDataToGridPane();
        } else {
            setButtons();
            makeProperButtonsVisible(orderStatusLabel.getText());
        }
        fillOrderDetailLabels(orderTable);
        createInformationImageAndAttachItToLabel();
        setInformationAboutOrderStatus(orderStatusHashMap);
    }

    private void hideAllButtons() {
        payOrderButton.setVisible(false);
        cancelOrderButton.setVisible(false);
        changePaymentMethodButton.setVisible(false);
    }

    private ResultSet getClientData() throws SQLException {
        return Client.getUserInformationById(getConnection(), orderTable.getCustomerId());
    }

    private void setClientDataToGridPane() {
        System.out.println("tworzenie grid pane");
        GridPane gridPane = createGridPane();
        try {
            fillGridPaneWithData(getClientData(), gridPane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        double padding = 10;
        grid.setPadding(new Insets(padding));
        grid.setHgap(padding);
        grid.setVgap(padding);
        grid.setLayoutY(470);
        grid.setLayoutX(20);
        grid.setId("adminSceneUserInformationGridPane");
        detailsPane.getChildren().add(grid);
        return grid;
    }

    private void fillGridPaneWithData(ResultSet data, GridPane gridPane) throws SQLException {
        if (data.next()) {
            Label key = new Label(data.getString(1));
            Label login = new Label(data.getString(2));
            Label name = new Label(data.getString(3));
            Label lastName = new Label(data.getString(4));
            key.getStyleClass().add("importantDataLabels");
            login.getStyleClass().add("importantDataLabels");
            name.getStyleClass().add("importantDataLabels");
            lastName.getStyleClass().add("importantDataLabels");

            gridPane.add(new Label("Data of the orderer :"), 0, 0, 4, 1);
            gridPane.add(new Label("Customer key :"), 1, 1);
            gridPane.add(key, 2, 1);
            gridPane.add(new Label("Login :"), 3, 1);
            gridPane.add(login, 4, 1);
            gridPane.add(new Label("Name :"), 1, 2);
            gridPane.add(name, 2, 2);
            gridPane.add(new Label("Last Name :"), 3, 2);
            gridPane.add(lastName, 4, 2);
            gridPane.setVgap(10);
            gridPane.setHgap(30);
            data.close();
        }
    }

    private OrderTable getOrdersInformationFromDataBase() {
        try {
            Order order = new Order(currentUser.getLogin());
            ResultSet orderInformation = order.getOrderDetailedInformation(getConnection(), orderNumber);
            ObservableList<OrderTable> listOfOrders = OrderTable.getOrderBasicInformation(orderInformation);
            orderInformation.close();
            return listOfOrders.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void changeGoBackButtonAction(String fxmlPath, String fxmlNameToDisplay) {
        if (goBackButton == null) {
            goBackButton = createGoBackButton(null);
        }
        EventHandler<ActionEvent> oldEvent = goBackButton.getOnAction();
        goBackButton.setOnAction(event -> {
            detailsPane.getChildren().clear();
            loadFXMLAndInitializeController(fxmlPath + fxmlNameToDisplay, allOrdersPane);
            allOrdersPane.setVisible(true);
            goBackButton.setOnAction(oldEvent);
        });
    }

    private void setButtons() {
        setCancelOrderButtonAction();
        setPayOrderButtonAction();
        setChangePaymentMethodButtonAction();
    }

    private void setPayOrderButtonAction() {
        payOrderButton.setOnAction(event -> {
            changeOrderStatusAndDisplayProperButtons("In progress");
            showNotification("order successfully paid");
        });

    }

    private void setCancelOrderButtonAction() {
        cancelOrderButton.setOnAction(event -> {
            Optional<ButtonType> buttonClicked = createAndShowAlert(Alert.AlertType.CONFIRMATION, "", "Canceling", "Are you sure about canceling the order ?");
            if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
                changeOrderStatusAndDisplayProperButtons("Canceled");
                setDisableToAllLabels(true);
            }
            anchor.requestFocus();
        });
    }

    private void setChangePaymentMethodButtonAction() {
        changePaymentMethodButton.setOnAction(event -> {
            try {
                Optional<String> buttonInsideAlertText = setAvailablePaymentMethodsToAlert(paymentMethodLabel.getText());
                if (buttonInsideAlertText.isPresent() && !buttonInsideAlertText.get().equals(ButtonType.CANCEL.getText())) {
                    buttonInsideAlertText.ifPresent(this::changePaymentMethod);
                    showNotification("payment method changed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            anchor.requestFocus();
        });
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

    private void fillOrderDetailColumnsWithData(ObservableList<OrderTable> list) {
        orderDetailsProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderDetailsQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        orderDetailsTotalValueColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        orderDetailsValueColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        orderDetailsTableView.setItems(list);
        prepareTableView(orderDetailsTableView, orderDetailsTotalValueColumn);
        orderDetailsTableView.setMaxHeight(230);
    }

    private Optional<String> setAvailablePaymentMethodsToAlert(String paymentMethod) throws SQLException {
        List<String> choices = new ArrayList<>();
        ResultSet paymentMethods = Product.getPaymentMethods(getConnection());

        while (Objects.requireNonNull(paymentMethods).next()) {
            if (!paymentMethods.getString(2).equals(paymentMethod)) {
                choices.add(paymentMethods.getString(2));
            }
        }
        choices.sort(null); // now on 0 position is the longest name, and it will nicely fit in window
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setHeaderText("Payment method change");
        dialog.setContentText("Available payment methods :  ");
        setLogoAndCssToCustomDialog(dialog);
        return dialog.showAndWait();
    }

    private void displayOrderDetails(int orderNumber) {
        checkConnectionWithDb();
        Order order = new Order(CURRENT_USER_LOGIN);
        try {
            ResultSet orders = order.getOrderProducts(getConnection(), orderNumber);
            ObservableList<OrderTable> listOfOrders = OrderTable.getProductsFromOrder(orders);
            fillOrderDetailColumnsWithData(listOfOrders);
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillOrderDetailLabels(OrderTable order) {
        orderIdLabel.setText(order.getOrderNumber() + "");
        totalValueLabel.setText(order.getOrderTotalValue() + "");
        paymentMethodLabel.setText(order.getOrderPaymentMethodName());
        orderStatusLabel.setText(order.getOrderStatusName());
        setDisableToAllLabels(orderStatusLabel.getText().equals("Canceled"));
    }

    private void setDisableToAllLabels(boolean disable) {
        orderIdLabel.setDisable(disable);
        totalValueLabel.setDisable(disable);
        paymentMethodLabel.setDisable(disable);
    }


    private HashMap<String, String> createHashMapWithOrderStatuses() {
        HashMap<String, String> orderStatus = new HashMap<>();
        orderStatus.put("Canceled", "This order has been cancelled and the payment will be refunded");
        orderStatus.put("In progress", "This order has been paid and is awaiting approval");
        orderStatus.put("Sent", "Order has been sent to the email assigned to this account");
        orderStatus.put("Waiting for payment", "The order has not been paid");
        return orderStatus;
    }

    private void setInformationAboutOrderStatus(HashMap<String, String> orderStatusHashMap) {
        informationLabel.setWrapText(true);
        informationLabel.setText(orderStatusHashMap.get(orderStatusLabel.getText()));
        informationLabel.setVisible(false);
    }

    private void changeOrderStatusAndDisplayProperButtons(String status) {
        try {
            Order order = new Order(Integer.parseInt(orderIdLabel.getText()));
            order.setOrderStatus(getConnection(), status);
            orderStatusLabel.setText(status);

            setInformationAboutOrderStatus(orderStatusHashMap);
            makeProperButtonsVisible(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changePaymentMethod(String paymentMethod) {
        try {
            Order order = new Order(Integer.parseInt(orderIdLabel.getText()));
            order.setPaymentMethod(getConnection(), paymentMethod);
            paymentMethodLabel.setText(paymentMethod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createInformationImageAndAttachItToLabel() {
        ImageView informationImage = setImageFromIconsFolder("Others", "information");
        informationImage.setLayoutX(725);
        informationImage.setLayoutY(91);
        orderStatusLabel.setGraphic(informationImage);
        orderStatusLabel.setContentDisplay(ContentDisplay.RIGHT);
        orderStatusLabel.setOnMouseEntered(mouseEvent -> informationLabel.setVisible(true));
        orderStatusLabel.setOnMouseExited(mouseEvent -> informationLabel.setVisible(false));
    }
}
