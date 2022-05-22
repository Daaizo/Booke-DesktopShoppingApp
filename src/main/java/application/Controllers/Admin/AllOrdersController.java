package application.Controllers.Admin;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Client.Account.ClientOrderDetails;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import users.Order;
import users.OrderTable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AllOrdersController extends AdminStartSceneController {
    @FXML
    private Pane ordersPane, orderDetailsPane;
    @FXML
    private TableColumn<OrderTable, String> ordersAcceptColumn, ordersDateColumn, ordersPriceColumn,
            ordersDeliveryDateColumn, ordersDetailButtonColumn, deleteButtonColumn, ordersStatusColumn;
    @FXML
    private TableColumn<OrderTable, Integer> ordersIdColumn;
    @FXML
    private TableView<OrderTable> ordersTableView;
    private Button goBackButton;

    @FXML
    private void initialize() {
        try {
            displayOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createSortingButtons();
        prepareSortingButtons(ordersTableView);
        ordersAcceptColumn.setComparator((o1, o2) -> {
            if (o1.equals("In progress")) return 1;
            else return 0;
        });
        createGoBackButton();

    }



    protected void prepareSortingButtons(TableView<OrderTable> tableView) {
        sortingButtonsBox.getItems().addAll("Id", "Total value (High -> Low)", "Total value (Low -> High)", "Status", "To approve first");
        sortingButtonsBox.setValue("Choose sorting type :");
        sortingButtonsBox.setVisible(true);
        sortingButtonsBox.valueProperty().addListener((observableValue, s, selectedValue) -> {
            tableView.getSortOrder().clear();
            if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(0))) {
                setSortingType(tableView, 1, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(1))) {
                setSortingType(tableView, 4, TableColumn.SortType.DESCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(2))) {
                setSortingType(tableView, 4, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(3))) {
                setSortingType(tableView, 5, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(4))) {
                setSortingType(tableView, 6, TableColumn.SortType.DESCENDING);
            }
            tableView.requestFocus();
        });
    }


    void displayOrders() throws SQLException {
        checkConnectionWithDb();
        ResultSet allOrdersFromDb = Order.getAllOrders(getConnection());
        ObservableList<OrderTable> orders = OrderTable.getAllOrdersFromDb(allOrdersFromDb);
        fillOrdersColumnsWithData(orders);
    }

    private void createGoBackButton() {
        goBackButton = createGoBackButton(event -> {
            orderDetailsPane.setVisible(false);
            ordersPane.setVisible(true);
            goBackButton.setVisible(false);
            sortingButtonsBox.setVisible(true);
        });
        goBackButton.fire();
        fixGoBackButtonPosition();
    }

    private void fixGoBackButtonPosition() {
        goBackButton.setLayoutY(goBackButton.getLayoutY() - 35);
    }

    private void fillOrdersColumnsWithData(ObservableList<OrderTable> list) {
        ordersIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        ordersDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordersDeliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDeliveryDate"));
        ordersPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotalValue"));
        ordersStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatusName"));
        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        ordersAcceptColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().orderStatusNameProperty());
        ordersAcceptColumn.setCellFactory(orderTableStringTableColumn -> createStatusButtons());
        ordersDetailButtonColumn.setCellFactory(orderTableStringTableColumn -> createOrderIdButton());
        deleteButtonColumn.setCellFactory(orderTableStringCellDataFeatures -> createDeleteOrderButton());
        ordersTableView.setItems(list);
        prepareTableView(ordersTableView, ordersPriceColumn);

    }

    private void setOrderStatus(int orderNumber, String orderName) {
        checkConnectionWithDb();
        Order order = new Order(orderNumber);
        try {
            order.setOrderStatus(getConnection(), orderName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeButtonsIcon(ButtonInsideOrdersTableView button, String orderName) {
        button.getRowId().setOrderStatusName(orderName);
        showNotification("Order status changed");
    }

    EventHandler<MouseEvent> cancelOrderButtonClicked(ButtonInsideOrdersTableView button) {
        return mouseEvent -> createAndShowConfirmAdminPasswordAlert("change order status", () -> {
            int orderNumber = button.getRowId().getOrderNumber();
            String orderName = "Canceled";
            setOrderStatus(orderNumber, orderName);
            changeButtonsIcon(button, orderName);
        });
    }

    EventHandler<MouseEvent> approveOrderButtonClicked(ButtonInsideOrdersTableView button) {
        return mouseEvent -> {
            int orderNumber = button.getRowId().getOrderNumber();
            createAndShowConfirmAdminPasswordAlert("change order status", () -> {
                String orderName = "Sent";
                setOrderStatus(orderNumber, orderName);
                changeButtonsIcon(button, orderName);
            });
        };
    }

    ButtonInsideOrdersTableView createStatusButtons() {
        Button acceptButton = new Button("approve");
        Button cancelButton = new Button("cancel");
        ButtonInsideOrdersTableView button = new ButtonInsideOrdersTableView(acceptButton, cancelButton);
        cancelButton.setOnMouseClicked(cancelOrderButtonClicked(button));
        acceptButton.setOnMouseClicked(approveOrderButtonClicked(button));
        return button;
    }

    ButtonInsideTableColumn<OrderTable, String> createOrderIdButton() {
        ButtonInsideTableColumn<OrderTable, String> button = new ButtonInsideTableColumn<>("", "details");
        button.setEventHandler(openOrderDetails(button));
        button.setCssId("orderDetailsButton");
        return button;
    }

    ButtonInsideTableColumn<OrderTable, String> createDeleteOrderButton() {
        ButtonInsideTableColumn<OrderTable, String> button = new ButtonInsideTableColumn<>("Others/delete.png", "delete order");
        button.setEventHandler(deleteOrder(button));
        button.setId("adminSceneDeleteOrderButton");
        return button;
    }


    private EventHandler<MouseEvent> openOrderDetails(ButtonInsideTableColumn<OrderTable, String> button) {
        return mouseEvent -> {
            FXMLLoader loader = createLoaderWithCustomController(button);
            try {
                displayOrderDetails(loader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private EventHandler<MouseEvent> deleteOrder(ButtonInsideTableColumn<OrderTable, String> button) {
        return mouseEvent -> createAndShowConfirmAdminPasswordAlert("delete order", () -> {
            try {
                Order.deleteOrder(getConnection(), button.getRowId().getOrderNumber());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            showNotification("Order " + button.getRowId().getOrderNumber() + " successfully deleted");
            try {
                displayOrders();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    private FXMLLoader createLoaderWithCustomController(ButtonInsideTableColumn<OrderTable, String> button) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/clientOrderDetailsGUI.fxml"));
        ClientOrderDetails clientOrderDetailsController = initializeController(button);
        loader.setController(clientOrderDetailsController);
        return loader;
    }

    private ClientOrderDetails initializeController(ButtonInsideTableColumn<OrderTable, String> button) {
        ClientOrderDetails clientOrderDetailsController = new ClientOrderDetails();
        clientOrderDetailsController.setOrder(button.getRowId());
        clientOrderDetailsController.setOrderNumber(button.getRowId().getOrderNumber());
        clientOrderDetailsController.setAllOrdersPane(ordersPane);
        clientOrderDetailsController.setGoBackButton(goBackButton);
        clientOrderDetailsController.isLunchedByAdmin(true);
        return clientOrderDetailsController;
    }

    private void displayOrderDetails(FXMLLoader loader) throws IOException {
        orderDetailsPane.getChildren().clear();
        ordersPane.setVisible(false);
        orderDetailsPane.getChildren().add(loader.load());
        goBackButton.setVisible(true);
        orderDetailsPane.setVisible(true);
        sortingButtonsBox.setVisible(false);
    }

    static class ButtonInsideOrdersTableView extends ButtonInsideTableColumn<OrderTable, String> {
        private final Button approveButton;
        private final Button cancelButton;

        public ButtonInsideOrdersTableView(Button approveButton, Button cancelButton) {
            this.cancelButton = cancelButton;
            this.approveButton = approveButton;
            approveButton.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));
            cancelButton.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));

        }

        protected ImageView setIconFromAdminIconsFolder(String iconName) {
            return new ImageView(getClass().getResource("/application/Icons/AdminIcons/") + iconName);
        }

        private void setButtonsBackground() {
            cancelButton.setBackground(Background.EMPTY);
            approveButton.setBackground(Background.EMPTY);
        }

        private void setButtonImages() {
            approveButton.setGraphic(setIconFromAdminIconsFolder("approve.png"));
            cancelButton.setGraphic(setIconFromAdminIconsFolder("cancel.png"));
        }


        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {

                if (item != null) {
                    switch (item) {
                        case "In progress" -> {
                            setButtonsBackground();
                            setButtonImages();
                            HBox pane = new HBox(approveButton, cancelButton);
                            setGraphic(pane);
                            setOnMouseEntered(event -> setCursor(Cursor.HAND));
                            setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
                            pane.setMaxHeight(30);
                            pane.setAlignment(Pos.CENTER);
                        }
                        case "Canceled" -> setGraphic(setIconFromAdminIconsFolder("rejected.png"));
                        case "Sent" -> setGraphic(setIconFromAdminIconsFolder("approved.png"));
                        default -> button.setText("-");
                    }
                    approveButton.getStyleClass().add("clientTableviewButtons");
                    cancelButton.getStyleClass().add("clientTableviewButtons");
                }
            }
        }

    }
}
