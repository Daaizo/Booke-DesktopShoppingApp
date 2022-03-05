package application.Controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import users.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;


public class ClientAccountController extends Controller {

    private final Lighting lighting = new Lighting();
    private final Client currentUser = new Client(CURRENT_USER_LOGIN);

    @FXML
    private Pane ordersPane, favouritesPane, detailsPane, accountSettingsPane;
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
    private TextField tfLogin, tfName, tfLastName;
    @FXML
    private TableView<ProductTable> favouritesTableView;
    @FXML
    private Label orderIdLabel, totalValueLabel, paymentMethodLabel, orderStatusLabel, informationLabel, emptyTableViewLabel, nameLabel, loginLabel, lastNameLabel;
    @FXML
    private Button ordersButton, accountSettingsButton, favouritesButton, payOrderButton, changePaymentMethodButton, cancelOrderButton, deleteAccountButton, changePasswordButton;

    //TODO database DIAGRAMS update needed !
    @FXML
    public void initialize() {
        prepareScene();
        createGoBackButton(event -> switchScene(event, clientScene));
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

    @FXML
    void accountSettingsButtonClicked() {
        makePaneVisible(accountSettingsPane);
        setButtonLightingEffect(accountSettingsButton);
        try {
            ResultSet data = currentUser.getClientData(getConnection());
            currentUser.setClientData(data);
            data.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setLabels();
    }

    private void setLabels() {
        tfLogin.setText(currentUser.getLogin());
        tfLastName.setText(currentUser.getLastName());
        tfName.setText(currentUser.getName());
    }

    @FXML
    void confirmChangesButtonClicked() {
        if (isAnyDataChanged()) {
            if (!isAndFieldEmpty()) {
                try {
                    if (updateChangesInDatabase()) {
                        showNotification(createNotification(new Label("Data successfully changed")), 3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            createAndShowAlert(Alert.AlertType.INFORMATION, "", "Account", "There are no changes to save !");
        }
        anchor.requestFocus();
    }

    private boolean isAndFieldEmpty() {
        return isTextFieldEmpty(tfLogin, loginLabel) || isTextFieldEmpty(tfLastName, lastNameLabel) || isTextFieldEmpty(tfName, nameLabel);
    }

    private boolean isTextFieldEmpty(TextField tf, Label label) {
        if (tf.getText().trim().isEmpty()) {
            colorField(tf, label, Color.RED);
            label.setVisible(true);
            return true;
        } else {
            basicTheme(tf, label);
            return false;
        }
    }

    private boolean updateChangesInDatabase() throws SQLException {

        String login = tfLogin.getText();
        String name = tfName.getText();
        String lastName = tfLastName.getText();
        if (!login.equals(currentUser.getLogin())) {
            if (isLoginUnique(login)) {
                currentUser.updateClientLogin(getConnection(), login);
                CURRENT_USER_LOGIN = login;
                return true;
            }

        }
        if (!name.equals(currentUser.getName())) {
            currentUser.updateClientName(getConnection(), name);
            return true;
        }
        if (!lastName.equals(currentUser.getLastName())) {
            currentUser.updateClientLastName(getConnection(), lastName);
            return true;
        }
        return false;

    }

    private boolean isAnyDataChanged() {
        return !(tfLogin.getText().equals(currentUser.getLogin()) && tfName.getText().equals(currentUser.getName()) && tfLastName.getText().equals(currentUser.getLastName()));
    }

    private boolean isLoginUnique(String login) throws SQLException {
        if (Client.isClientInDataBase(getConnection(), login)) {
            createAndShowAlert(Alert.AlertType.WARNING, "A user with this login already exists.", "Login", "Please choose a new login and try again.");
            return false;
        } else return true;

    }

    @FXML
    void changePasswordButtonClicked(ActionEvent event) {
        Optional<String> result = createAndShowConfirmPasswordAlert();
        result.ifPresent(s -> {
            if (result.get().equals(currentUser.getPassword())) {
                Optional<String> newAlert = enterNewPasswordAlert();
                newAlert.ifPresent(newPassword -> {
                    System.out.println(newPassword);
                    if (Pattern.matches(PASSWORDS_REGEX, newPassword)) {
                        try {
                            currentUser.updateClientPassword(getConnection(), newPassword);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                ButtonType tryAgain = new ButtonType("Try again");
                ButtonType cancel = new ButtonType("Cancel");
                Optional<ButtonType> res = createAndShowAlert(tryAgain, cancel, "Incorrect password, please try again", "Wrong password");
                res.ifPresent(buttonType -> {
                    if (res.get() == tryAgain) changePasswordButtonClicked(event);
                });
            }
        });
    }

    private Optional<String> enterNewPasswordAlert() {
        Dialog<String> dialog = new Dialog<>();
        PasswordField passwordField = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Enter password here :"), passwordField);
        dialog.getDialogPane().setContent(content);
        dialog.setTitle("New password");
        dialog.setHeaderText("A password can only be saved if the following conditions are met :\n 6-20 characters, one number, one uppercase letter, one lowercase letter, one special character ");
        setLogoAndCssToCustomDialog(dialog);
        dialog.getDialogPane().setMinWidth(650);
        ButtonType savePass = new ButtonType("Save new password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(savePass, ButtonType.CANCEL);
        Node savaPasswordButton = dialog.getDialogPane().lookupButton(savePass);
        dialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK);
        savaPasswordButton.setDisable(true);
        dialog.getDialogPane().requestFocus();
        dialog.setResultConverter(buttonType -> {
            if (buttonType == savePass) {
                return passwordField.getText();
            }
            return null;
        });
        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> savaPasswordButton.setDisable(!Pattern.matches(PASSWORDS_REGEX, newValue)));
        return dialog.showAndWait();
    }

    private Optional<String> createAndShowConfirmPasswordAlert() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Password");
        dialog.setHeaderText("Confirm that it is you ");
        dialog.setContentText("Type your password here : ");
        setLogoAndCssToCustomDialog(dialog);
        dialog.getDialogPane().setMinWidth(650);
        return dialog.showAndWait();
    }

    private void deleteConfirmation(ActionEvent event) {
        Optional<ButtonType> buttonAlert = createAndShowAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete your account?", "DELETE ACCOUNT"
                , "Account deletion is permanent and cannot be undone.\nAll your orders will be closed and deleted.");
        if (buttonAlert.isPresent() && buttonAlert.get() == ButtonType.OK) {
            try {
                checkConnectionWithDb();
                currentUser.deleteClient(getConnection());
                createAndShowAlert(Alert.AlertType.WARNING, "", "", "The account has been successfully deleted.");
                switchScene(event, loginScene);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void deleteAccountButtonClicked(ActionEvent event) {
        Optional<String> result = createAndShowConfirmPasswordAlert();
        result.ifPresent(s -> {
            if (result.get().equals(currentUser.getPassword())) {
                deleteConfirmation(event);
            } else {
                ButtonType tryAgain = new ButtonType("Try again");
                ButtonType cancel = new ButtonType("Cancel");
                Optional<ButtonType> res = createAndShowAlert(tryAgain, cancel, "Incorrect password, please try again", "Wrong password");
                res.ifPresent(buttonType -> {
                    if (res.get() == tryAgain) deleteAccountButtonClicked(event);
                });
            }
        });

    }

    private void createLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setColor(Color.LIGHTPINK);
        lighting.setLight(light);
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
        accountSettingsButton.setEffect(null);
        if (button == ordersButton) ordersButton.setEffect(lighting);
        else if (button == favouritesButton) favouritesButton.setEffect(lighting);
        else if (button == accountSettingsButton) accountSettingsButton.setEffect(lighting);
    }

    private void makePaneVisible(Pane pane) {
        ordersPane.setVisible(false);
        detailsPane.setVisible(false);
        favouritesPane.setVisible(false);
        accountSettingsPane.setVisible(false);
        emptyTableViewLabel.setVisible(false);
        if (pane == ordersPane) {
            ordersPane.setVisible(true);
        } else if (pane == detailsPane) {
            detailsPane.setVisible(true);
        } else if (pane == favouritesPane) {
            favouritesPane.setVisible(true);
        } else if (pane == accountSettingsPane) {
            accountSettingsPane.setVisible(true);
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
            setButtonLightingEffect(null);
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
        Order order = new Order(currentUser.getLogin());
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
            Optional<ButtonType> result = deleteProductAlert(productName, "1", "favourites");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    currentUser.deleteItemFromUsersFavourite(productName, getConnection());
                    showNotification(createNotification(new Label("Item removed from favourites")), 2500);
                    displayFavourites();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        Optional<ButtonType> buttonClicked = createAndShowAlert(Alert.AlertType.CONFIRMATION, "", "Canceling", "Are you sure about canceling the order ?");
        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            changeOrderStatusAndDisplayProperButtons("Canceled");
            setDisableToAllLabels(true);
        }
        anchor.requestFocus();
    }

    @FXML
    void changePaymentMethodButtonClicked() {
        try {
            Optional<String> buttonInsideAlertText = setAvailablePaymentMethodsToAlert(paymentMethodLabel.getText());
            if (buttonInsideAlertText.isPresent() && !buttonInsideAlertText.get().equals(ButtonType.CANCEL.getText())) {
                buttonInsideAlertText.ifPresent(this::changePaymentMethod);
                showNotification(createNotification(new Label("payment method changed")), 4000);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        anchor.requestFocus();

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

    private void setLogoAndCssToCustomDialog(Dialog<?> dialog) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(iconsUrl + "transparentLogo.png"));
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("alert");
    }


}
