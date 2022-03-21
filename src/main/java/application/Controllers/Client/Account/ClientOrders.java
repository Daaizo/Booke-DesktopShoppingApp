package application.Controllers.Client.Account;

import application.Controllers.ButtonInsideTableColumn;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import users.Order;
import users.OrderTable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientOrders extends ClientAccountStartSceneController {
    @FXML
    private Pane ordersPane, orderDetailsPane;
    @FXML
    private TableColumn<OrderTable, String> ordersDateColumn, ordersDeliveryDateColumn, ordersPaymentColumn, ordersStatusColumn, ordersIdColumn, ordersButtonColumn;
    @FXML
    private TableColumn<OrderTable, Double> ordersTotalValueColumn;
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

    private void displayOrders() throws SQLException {
        ObservableList<OrderTable> listOfOrders = getOrdersFromDb();
        if (listOfOrders.isEmpty()) {
            displayLabelWithGivenText(emptyTableViewLabel, "There are no orders !");
            makeLabelVisible(ordersPane);
        } else {
            displayTableWithOrders(listOfOrders);
            makePaneVisible(ordersPane);
        }
    }

    private void displayTableWithOrders(ObservableList<OrderTable> listOfOrders) {
        fillOrdersColumnsWithData();
        ordersTableView.setItems(listOfOrders);
        showOnlyRowsWithData();
    }

    private ObservableList<OrderTable> getOrdersFromDb() throws SQLException {
        checkConnectionWithDb();
        Order order = new Order(currentUser.getLogin());
        ResultSet orders = order.getOrdersFromCustomer(getConnection());
        ObservableList<OrderTable> listOfOrders = OrderTable.getOrders(orders);
        orders.close();
        return listOfOrders;
    }

    private void showOnlyRowsWithData() {
        showOnlyRowsWithData(ordersTableView);
        ordersTableView.setMaxHeight(530);
    }

    private void fillOrdersColumnsWithData() {
        ordersIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        ordersButtonColumn.setCellFactory(orderTableStringTableColumn -> createOrderIdButton());
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordersDeliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDeliveryDate"));
        ordersTotalValueColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        ordersStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatusName"));
        ordersPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("orderPaymentMethodName"));
    }

    private ButtonInsideTableColumn<OrderTable, String> createOrderIdButton() {
        ButtonInsideTableColumn<OrderTable, String> button = new ButtonInsideTableColumn<>("", "details");
        button.setEventHandler(createActionOnClick(button));
        button.setCssId("orderDetailsButton");
        return button;
    }

    private EventHandler<MouseEvent> createActionOnClick(ButtonInsideTableColumn<OrderTable, String> button) {
        return mouseEvent -> {
            FXMLLoader loader = createLoaderWithCustomController(button);
            try {
                displayOrderDetails(loader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private FXMLLoader createLoaderWithCustomController(ButtonInsideTableColumn<OrderTable, String> button) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/clientOrderDetailsGUI.fxml"));
        ClientOrderDetails clientOrderDetailsController = new ClientOrderDetails();
        clientOrderDetailsController.setSpecificRowId(button.getRowId());
        clientOrderDetailsController.setAllOrdersPane(ordersPane);
        loader.setController(clientOrderDetailsController);
        return loader;
    }

    private void displayOrderDetails(FXMLLoader loader) throws IOException {
        ordersPane.getChildren().clear();
        ordersPane.setVisible(false);

        orderDetailsPane.getChildren().add(loader.load());
    }
}
