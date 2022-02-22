package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import users.Order;
import users.OrderTable;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ClientAccountController extends Controller {

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
    private Label orderID, totalValue, paymentMethod;

    @FXML
    public void initialize() {
        prepareScene();
        displayOrders();
        makePaneVisible(ordersPane);
        createGoBackButton(event -> {
            if (detailsPane.isVisible()) {
                makePaneVisible(ordersPane);
            } else switchScene(event, clientScene);


        });
    }

    @FXML
    void ordersButtonClicked() {
        makePaneVisible(ordersPane);

    }

    @FXML
    void favouritesButtonClicked() {
        makePaneVisible(favouritesPane);
    }

    private void makePaneVisible(Pane pane) {
        ordersPane.setVisible(false);
        detailsPane.setVisible(false);
        favouritesPane.setVisible(false);
        if (pane == ordersPane) {
            ordersPane.setVisible(true);
        } else if (pane == detailsPane) {
            detailsPane.setVisible(true);
        } else if (pane == favouritesPane) {
            favouritesPane.setVisible(true);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillOrderDetailLabels(ClientController.ButtonInsideTableColumn<OrderTable, String> button) {
        orderID.setText("Order ID : " + button.getRowId().getOrderNumber());
        totalValue.setText("Total order value : " + button.getRowId().getOrderTotalValue());
        paymentMethod.setText("Payment method: " + button.getRowId().getOrderPaymentMethodName());
    }

    private void displayOrders() {
        checkConnectionWithDb();
        Order order = new Order(CURRENT_USER_LOGIN);
        try {
            ResultSet orders = order.getOrdersFromCustomer(getConnection());
            ObservableList<OrderTable> listOfOrders = OrderTable.getOrders(orders);
            fillOrdersColumnsWithData(listOfOrders);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
