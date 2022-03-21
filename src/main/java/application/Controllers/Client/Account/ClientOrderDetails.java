package application.Controllers.Client.Account;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

    private OrderTable rowId;

    public void setSpecificRowId(OrderTable rowId) {
        this.rowId = rowId;
    }

    public void setAllOrdersPane(Pane pane) {
        this.allOrdersPane = pane;
    }

    public void initialize() {
        detailsPane.requestFocus();
        setButtons();
        displayOrderDetails(rowId.getOrderNumber());
        fillOrderDetailLabels(rowId);
        makeProperButtonsVisible(orderStatusLabel.getText());
        createInformationImageAndAttachItToLabel();
        setInformationAboutOrderStatus(orderStatusHashMap);

    }

    private void changeGoBuckButtonAction() {
        EventHandler<ActionEvent> oldEvent = goBackButton.getOnAction();
        goBackButton.setOnAction(event -> {
            detailsPane.getChildren().clear();
            loadFXMLAndInitializeController(pathToFxml + "clientOrdersGUI.fxml", allOrdersPane);
            allOrdersPane.setVisible(true);
            goBackButton.setOnAction(oldEvent);
        });

    }

    private void setButtons() {
        setCancelOrderButtonAction();
        setPayOrderButtonAction();
        setChangePaymentMethodButtonAction();
        changeGoBuckButtonAction();

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
        showOnlyRowsWithData(orderDetailsTableView);
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
            ResultSet orders = order.getOrderDetailedInformation(getConnection(), orderNumber);
            ObservableList<OrderTable> listOfOrders = OrderTable.getProductsFromOrder(orders);
            fillOrderDetailColumnsWithData(listOfOrders);
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillOrderDetailLabels(OrderTable rowId) {
        orderIdLabel.setText(rowId.getOrderNumber() + "");
        totalValueLabel.setText(rowId.getOrderTotalValue() + "");
        paymentMethodLabel.setText(rowId.getOrderPaymentMethodName());
        orderStatusLabel.setText(rowId.getOrderStatusName());
        setDisableToAllLabels(orderStatusLabel.getText().equals("Canceled"));
    }

    private void setDisableToAllLabels(boolean disable) {
        orderIdLabel.setDisable(disable);
        totalValueLabel.setDisable(disable);
        paymentMethodLabel.setDisable(disable);
    }


    private HashMap<String, String> createHashMapWithOrderStatuses() {
        HashMap<String, String> orderStatus = new HashMap<>();
        orderStatus.put("Canceled", "Your order has been cancelled and your payment will be refunded");
        orderStatus.put("In progress", "Your order has been paid and is awaiting approval");
        orderStatus.put("Finished", "Order has been sent to the email assigned to your account");
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
        ImageView informationImage = setImageFromIconsFolder("information.png");
        informationImage.setLayoutX(725);
        informationImage.setLayoutY(91);
        orderStatusLabel.setGraphic(informationImage);
        orderStatusLabel.setContentDisplay(ContentDisplay.RIGHT);
        orderStatusLabel.setOnMouseEntered(mouseEvent -> informationLabel.setVisible(true));
        orderStatusLabel.setOnMouseExited(mouseEvent -> informationLabel.setVisible(false));
    }
}
