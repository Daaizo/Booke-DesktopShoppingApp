package application.Controllers.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Order;
import users.OrderTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllOrdersController extends AdminStartSceneController {
    @FXML
    private TableColumn<OrderTable, String> ordersAcceptColumn, ordersDateColumn, ordersPriceColumn,
            ordersDeliveryDateColumn, ordersDetailButtonColumn, ordersPaymentMethodColumn, ordersStatusColumn;
    @FXML
    private TableColumn<OrderTable, Integer> ordersIdColumn;
    @FXML
    private TableView<OrderTable> ordersTableView;

    @FXML
    private void initialize() {
        try {
            displayOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void displayOrders() throws SQLException {
        checkConnectionWithDb();
        ResultSet allOrdersFromDb = Order.getAllOrders(getConnection());
        ObservableList<OrderTable> orders = OrderTable.getAllOrdersFromDb(allOrdersFromDb);
        fillOrdersColumnsWithData(orders);
    }

    private void fillOrdersColumnsWithData(ObservableList<OrderTable> list) {
        ordersIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordersDeliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDeliveryDate"));
        ordersPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        ordersPaymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("orderPaymentMethodName"));
        ordersStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatusName"));

        ordersAcceptColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        ordersDetailButtonColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        ordersTableView.setItems(list);
        prepareTableView(ordersTableView, ordersPriceColumn);
    }
}
