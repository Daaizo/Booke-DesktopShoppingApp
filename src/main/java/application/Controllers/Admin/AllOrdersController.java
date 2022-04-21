package application.Controllers.Admin;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Client.Account.ClientOrderDetails;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        createGoBackButton();
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

    private void createGoBackButton() {
        goBackButton = createGoBackButton(event -> {
            orderDetailsPane.setVisible(false);
            ordersPane.setVisible(true);
            goBackButton.setVisible(false);
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
        ordersTableView.setItems(list);
        prepareTableView(ordersTableView, ordersPriceColumn);
    }


    EventHandler<MouseEvent> buttonInsideTableViewClicked(ButtonInsideOrdersTableView button) {
        return mouseEvent -> {
            int orderNumber = button.getRowId().getOrderNumber();
            checkConnectionWithDb();
            System.out.println(orderNumber);

        };
    }

    ButtonInsideOrdersTableView createStatusButtons() {
        Button acceptButton = new Button("approve");
        Button cancelButton = new Button("cancel");
        cancelButton.setOnMouseClicked(mouseEvent -> System.out.println("tralala"));
        return new ButtonInsideOrdersTableView(acceptButton, cancelButton);
    }

    ButtonInsideTableColumn<OrderTable, String> createOrderIdButton() {
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
    }

    static class ButtonInsideOrdersTableView extends ButtonInsideTableColumn<OrderTable, String> {
        private Button approveButton;
        private Button cancelButton;

        public ButtonInsideOrdersTableView(Button approveButton, Button cancelButton) {
            this.cancelButton = cancelButton;
            this.approveButton = approveButton;
        }

        public ButtonInsideOrdersTableView(String buttonText) {
            button.fire();
            button.setText(buttonText);
            setGraphic(button);
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
                            //   setOnMouseClicked(deleteFromFavouriteClicked(getRowId()));
                            setOnMouseEntered(event -> setCursor(Cursor.HAND));
                            setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

                        }
                        case "Canceled" -> {
                            setGraphic(setIconFromAdminIconsFolder("rejected.png"));
                        }
                        case "Sent" -> {
                            setGraphic(setIconFromAdminIconsFolder("approved.png"));
                        }
                        default -> {
                            button.setText("-");
                        }
                    }
                    approveButton.getStyleClass().add("clientTableviewButtons");
                    cancelButton.getStyleClass().add("clientTableviewButtons");
                }
            }
        }

    }
}
