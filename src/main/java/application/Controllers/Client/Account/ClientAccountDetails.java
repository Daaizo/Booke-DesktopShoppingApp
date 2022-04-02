package application.Controllers.Client.Account;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import users.Client;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClientAccountDetails extends ClientAccountStartSceneController {
    @FXML
    private Pane accountSettingsPane;
    @FXML
    private Label nameLabel, loginLabel, lastNameLabel, noOrdersLabel, valueOfOrdersLabel,
            noCanceledOrdersLabel, noInProgressOrdersLabel, noUnpaidOrdersLabel, noFinishedOrdersLabel;
    @FXML
    private TextField tfLogin, tfName, tfLastName;

    @FXML
    public void initialize() {
        checkConnectionWithDb();
        setDetailsOfOrdersLabels();
        try {
            ResultSet data = currentUser.getClientData(getConnection());
            currentUser.setClientData(data);
            data.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setAccountDetailsLabels();
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

    @FXML
    private void confirmChangesButtonClicked() {
        if (isAnyDataChanged()) {
            if (!isAndFieldEmpty()) {
                try {
                    if (updateChangesInDatabase()) {
                        showNotification("Data successfully changed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            createAndShowAlert(Alert.AlertType.INFORMATION, "", "Account", "There are no changes to save !");
        }
        accountSettingsPane.requestFocus();
    }

    @FXML
    private void changePasswordButtonClicked() {
        Optional<String> result = createAndShowConfirmPasswordAlert();
        result.ifPresent(s -> {
            if (result.get().equals(currentUser.getPassword())) {
                Optional<String> newAlert = enterNewPasswordAlert();
                newAlert.ifPresent(newPassword -> {
                    if (checkPasswordRegex(newPassword)) {
                        try {
                            currentUser.updateClientPassword(getConnection(), newPassword);
                            showNotification("Password changed");
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
                    if (res.get() == tryAgain) changePasswordButtonClicked();
                });
            }
        });
        accountSettingsPane.requestFocus();
    }

    @FXML
    private void allOrderedProductsButtonClicked() {
        Dialog<ButtonType> dialog = new Dialog<>();
        VBox content = new VBox();

        TableView<ProductTable> orderedProductTable = createAllOrderedProductTable();
        try {
            displayOrderedProducts(orderedProductTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        orderedProductTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        content.setSpacing(10);
        if (orderedProductTable.getItems().isEmpty()) {
            content.getChildren().add(new Label("There are no ordered products"));
        } else {
            content.getChildren().add(orderedProductTable);

        }
        dialog.getDialogPane().setContent(content);
        dialog.setHeaderText("All ordered products");
        setLogoAndCssToCustomDialog(dialog);
        dialog.getDialogPane().setMinWidth(650);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
        accountSettingsPane.requestFocus();
    }

    private TableView<ProductTable> createAllOrderedProductTable() {
        TableView<ProductTable> orderedProductTable = new TableView<>();
        orderedProductTable.setPlaceholder(new Label("There are no ordered products "));
        orderedProductTable.setPrefWidth(500);
        TableColumn<ProductTable, String> productName = new TableColumn<>("name");
        productName.setMinWidth(200);
        TableColumn<ProductTable, String> productPrice = new TableColumn<>("price");
        productPrice.setPrefWidth(200);
        TableColumn<ProductTable, String> productSubcategory = new TableColumn<>("subcategory");
        productSubcategory.setPrefWidth(230);
        orderedProductTable.getColumns().addAll(productName, productPrice, productSubcategory);
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productSubcategory.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        return orderedProductTable;
    }

    private void displayOrderedProducts(TableView<ProductTable> tableView) throws SQLException {
        ResultSet products = Product.getAllProductsOrderedByUser(getConnection(), CURRENT_USER_LOGIN);
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsBasicInfo(products);
        tableView.setItems(listOfProducts);
        prepareTableView(tableView, (TableColumn<?, String>) tableView.getColumns().get(1));
        tableView.setMaxHeight(250);
    }

    private void setAccountDetailsLabels() {
        tfLogin.setText(currentUser.getLogin());
        tfLastName.setText(currentUser.getLastName());
        tfName.setText(currentUser.getName());
    }

    private void setDetailsOfOrdersLabels() {
        try {
            noOrdersLabel.setText(currentUser.getNumberOfOrders(getConnection()) + "");
            valueOfOrdersLabel.setText(currentUser.getTotalValueOfAllOrders(getConnection()) + CURRENCY);
            noCanceledOrdersLabel.setText(currentUser.getNumbersOfOrdersWithStatus("Canceled", getConnection()) + "");
            noInProgressOrdersLabel.setText(currentUser.getNumbersOfOrdersWithStatus("In progress", getConnection()) + "");
            noUnpaidOrdersLabel.setText(currentUser.getNumbersOfOrdersWithStatus("Waiting for payment", getConnection()) + "");
            noFinishedOrdersLabel.setText(currentUser.getNumbersOfOrdersWithStatus("Finished", getConnection()) + "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> enterNewPasswordAlert() {
        PasswordField passwordField = new PasswordField();
        Dialog<String> dialog = createCustomEnterPasswordAlert(passwordField);
        dialog.setTitle("New password");
        dialog.setHeaderText("A password can only be saved if the following conditions are met :\n6-20 characters, one number, one uppercase letter, one lowercase letter, one special character ");
        ButtonType savePass = new ButtonType("Save new password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(savePass);
        Node savaPasswordButton = dialog.getDialogPane().lookupButton(savePass);
        dialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK);
        savaPasswordButton.setDisable(true);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == savePass) {
                return passwordField.getText();
            }
            return null;
        });
        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> savaPasswordButton.setDisable(!checkPasswordRegex(newValue)));
        return dialog.showAndWait();
    }


    private Dialog<String> createCustomEnterPasswordAlert(PasswordField passwordField) {
        Button button = new Button();
        setPasswordVisibilityButton(button, passwordField);
        Dialog<String> dialog = new Dialog<>();
        passwordField.setPrefSize(600, 30);
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Enter password here :"), passwordField, button);
        dialog.getDialogPane().setContent(content);
        setLogoAndCssToCustomDialog(dialog);
        dialog.getDialogPane().setMinWidth(650);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return dialog;
    }


    private Optional<String> createAndShowConfirmPasswordAlert() {
        PasswordField passwordField = new PasswordField();
        Dialog<String> dialog = createCustomEnterPasswordAlert(passwordField);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
        dialog.setTitle("Password");
        dialog.setHeaderText("Confirm that it is you ");
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

    private boolean isLoginUnique(String login) throws SQLException {
        if (Client.isClientInDataBase(getConnection(), login)) {
            createAndShowAlert(Alert.AlertType.WARNING, "A user with this login already exists.", "Login", "Please choose a new login and try again.");
            return false;
        } else return true;

    }
    private boolean updateChangesInDatabase() throws SQLException {
        String login = tfLogin.getText();
        String name = tfName.getText();
        String lastName = tfLastName.getText();
        if (!checkRegex(login, name, lastName)) {
            return false;
        }
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

    private boolean isAndFieldEmpty() {
        return isTextFieldEmpty(tfLogin, loginLabel) || isTextFieldEmpty(tfLastName, lastNameLabel) || isTextFieldEmpty(tfName, nameLabel);
    }

    private boolean isTextFieldEmpty(TextField tf, Label label) {
        if (tf.getText().trim().isEmpty()) {
            makeFieldsBorderRed(tf, label);
            label.setVisible(true);
            return true;
        } else {
            basicTheme(tf, label);
            return false;
        }
    }
}
