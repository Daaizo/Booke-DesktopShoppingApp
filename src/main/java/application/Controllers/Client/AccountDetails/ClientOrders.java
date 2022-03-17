package application.Controllers.Client.AccountDetails;

import application.Controllers.Client.ClientAccountController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import users.Order;
import users.OrderTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientOrders extends ClientAccountController {
    @FXML
    private Pane ordersPane;
    @FXML
    private TableColumn<OrderTable, String> ordersDateColumn, ordersDeliveryDateColumn, ordersPaymentColumn, ordersStatusColumn, ordersIdColumn, ordersButtonColumn;
    @FXML
    private TableColumn<OrderTable, Double> ordersTotalValueColumn;
    @FXML
    private TableView<OrderTable> ordersTableView;


    private void displayOrders() {
        checkConnectionWithDb();
        Order order = new Order(currentUser.getLogin());
        try {
            ResultSet orders = order.getOrdersFromCustomer(getConnection());
            ObservableList<OrderTable> listOfOrders = OrderTable.getOrders(orders);
            if (listOfOrders.isEmpty()) {
                displayLabelWithGivenText(emptyTableViewLabel, "There are no orders !");
                ordersPane.setVisible(false);
            } else {
                ordersPane.setVisible(true);
                emptyTableViewLabel.setVisible(false);
                fillOrdersColumnsWithData(listOfOrders);

            }
            ordersTableView.setMaxHeight(530);
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillOrdersColumnsWithData(ObservableList<OrderTable> list) {
        ordersIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        //ordersButtonColumn.setCellFactory(orderTableStringTableColumn -> orderIdButton());
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordersDeliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDeliveryDate"));
        ordersTotalValueColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        ordersStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatusName"));
        ordersPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("orderPaymentMethodName"));
        ordersTableView.setItems(list);
        showOnlyRowsWithData(ordersTableView);
    }

}
