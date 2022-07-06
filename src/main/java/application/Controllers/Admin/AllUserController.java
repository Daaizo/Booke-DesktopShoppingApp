package application.Controllers.Admin;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Client.Account.ClientOrders;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import users.Client;
import users.ClientTable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class AllUserController extends AdminStartSceneController {
    @FXML
    private TableColumn<ClientTable, String> userFirstNameColumn, userLoginColumn, userLastNameColumn, userPasswordColumn, userDeleteColumn, userDetailsColumn, userShowPasswordColumn;
    @FXML
    private TableColumn<ClientTable, Integer> userIDColumn;
    @FXML
    private TableView<ClientTable> userTableView;
    @FXML
    private Pane allUsersPane, userOrdersPane;

    @FXML
    private void initialize() {
        displayUsers();
        createAddUserSection();
        setSorting(userTableView);
        createGoBackButton();

    }

    private void createGoBackButton() {
        goBackButton = createGoBackButton(event -> {
            sortingButtonsBox.setVisible(true);
            userOrdersPane.setVisible(false);
            allUsersPane.setVisible(true);
            goBackButton.setVisible(false);
        });
        goBackButton.setLayoutY(goBackButton.getLayoutY() - 50);
        goBackButton.setLayoutX(goBackButton.getLayoutX() + 20);
        goBackButton.fire();
    }

    private void createAddUserSection() {
        Button addNewUserButton = new Button("Add new user");
        addNewUserButton.getStyleClass().add("OrangeButtons");
        ArrayList<TextField> listOfTextFields = createTextFields();
        createAndFillGridPane(addNewUserButton, listOfTextFields);
        addNewUserButton.setOnAction(event -> {
            String login = listOfTextFields.get(2).getText();
            if (isAnyFieldEmpty(listOfTextFields) && checkIfLoginIsUnique(login)) {
                displayAcceptAlert(listOfTextFields);
            }

            anchor.requestFocus();
        });
    }

    private void clearAllFields(ArrayList<TextField> listOfTextFields) {
        for (var i : listOfTextFields) {
            i.clear();
        }
    }

    private boolean isAnyFieldEmpty(ArrayList<TextField> listOfTextFields) {
        for (var i : listOfTextFields) {
            if (i.getText().isEmpty()) {
                createAndShowAlert(Alert.AlertType.WARNING, "Any field cannot be empty.", "New user", "Please fill all blanks and try again.");
                return false;
            }
        }
        return true;
    }

    private void displayAcceptAlert(ArrayList<TextField> listOfTextFields) {
        String name = listOfTextFields.get(0).getText();
        String lastName = listOfTextFields.get(1).getText();
        String login = listOfTextFields.get(2).getText();
        String pass = listOfTextFields.get(3).getText();
        String alertText = "User data \nname : " + name + "\nlast name : " + lastName + "\nlogin : " + login + "\npassword : " + pass;

        Optional<ButtonType> button = createAndShowAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to add a new user?", "New user", alertText);
        if (button.isPresent() && button.get() == ButtonType.OK) {
            createAndShowConfirmAdminPasswordAlert("add new user", () -> {
                Client client = new Client(login, name, lastName, pass);
                client.addUserToDatabase(getConnection());
                showNotification("User added successfully");
                clearAllFields(listOfTextFields);
                displayUsers();
            });
        }
    }

    private boolean checkIfLoginIsUnique(String login) {
        try {
            if (Client.isClientInDataBase(getConnection(), login)) {
                createAndShowAlert(Alert.AlertType.WARNING, "A user with this login already exists", "Login", "Please choose a new login and try again");
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<TextField> createTextFields() {
        ArrayList<TextField> listOfAllFields = new ArrayList<>();
        TextField userName = new TextField();
        userName.setPromptText("name");
        TextField userLastName = new TextField();
        userLastName.setPromptText("last name");
        TextField userLogin = new TextField();
        userLogin.setPromptText("login");
        TextField userPassword = new TextField();
        userPassword.setPromptText("password");
        listOfAllFields.add(userName);
        listOfAllFields.add(userLastName);
        listOfAllFields.add(userLogin);
        listOfAllFields.add(userPassword);
        return listOfAllFields;
    }

    private void createAndFillGridPane(Button addNewUserButton, ArrayList<TextField> fieldArrayList) {
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(92);
        gridPane.setLayoutY(565);
        allUsersPane.getChildren().add(gridPane);
        gridPane.setHgap(30);
        gridPane.add(fieldArrayList.get(2), 0, 0);
        gridPane.add(fieldArrayList.get(0), 1, 0);
        gridPane.add(fieldArrayList.get(1), 2, 0);
        gridPane.add(fieldArrayList.get(3), 3, 0);
        gridPane.add(addNewUserButton, 4, 0);
    }

    private void displayUsers() {
        checkConnectionWithDb();
        ResultSet users = Client.getUsersFromDataBase(getConnection());
        assert users != null;
        ObservableList<ClientTable> listOfUsers = ClientTable.getUsers(users);
        fillUserColumnsWithData(listOfUsers);
    }

    private void fillUserColumnsWithData(ObservableList<ClientTable> list) {
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userPasswordColumn.setVisible(false);
        userShowPasswordColumn.setCellFactory(clientTableStringCellDataFeature -> createShowPasswordButton());
        userDeleteColumn.setCellFactory(clientTableStringTableColumn -> createDeleteUserButton());
        userDetailsColumn.setCellFactory(clientTableStringTableColumn -> createUserOrdersButton());
        userTableView.setItems(list);
        userTableView.getStyleClass().add("adminSceneTableview");
        prepareTableView(userTableView, null);
    }

    ButtonInsideTableColumn<ClientTable, String> createShowPasswordButton() {
        ButtonInsideTableColumn<ClientTable, String> showPasswordButton = new ButtonInsideTableColumn<>("PasswordIcons/hiddenPassword.png", "Display password");
        showPasswordButton.setBackground(Background.EMPTY);
        showPasswordButton.setCssClassId("clientTableviewButtons");
        showPasswordButton.setEventHandler(mouseEvent -> createAndShowConfirmAdminPasswordAlert("to display password of user '" + showPasswordButton.getRowId().getLogin() + "'",
                () -> showUserPasswordEvent(showPasswordButton)
        ));
        return showPasswordButton;

    }

    private void showUserPasswordEvent(ButtonInsideTableColumn<ClientTable, String> showPasswordButton) {

        ButtonType editPass = new ButtonType("Edit user's password");

        Optional<ButtonType> result = createAndShowAlert(editPass, ButtonType.OK,
                "Password of user " + showPasswordButton.getRowId().getLogin() + " is \" " + showPasswordButton.getRowId().getPassword() + "\"",
                "Password");
        result.ifPresent(s -> {
            if (result.get() == editPass) {
                Optional<String> newAlert = enterNewPasswordAlert();
                newAlert.ifPresent(newPassword -> {
                    if (checkPasswordRegex(newPassword)) {
                        try {
                            Client.updateClientPasswordByLogin(getConnection(), newPassword, showPasswordButton.getRowId().getLogin());
                            showNotification("Password changed");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        displayUsers();
                    }
                });
            }

        });
    }


    private ButtonInsideTableColumn<ClientTable, String> createDeleteUserButton() {
        ButtonInsideTableColumn<ClientTable, String> button = new ButtonInsideTableColumn<>("Others/delete.png", "delete user");
        button.setEventHandler(mouseEvent -> createAndShowConfirmAdminPasswordAlert("delete user number " + button.getRowId().getId(), () -> {
            try {
                Client.deleteClient(getConnection(), button.getRowId().getLogin());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            showNotification("User deleted");
            displayUsers();
        }));
        button.setId("adminSceneDeleteOrderButton");
        return button;
    }

    ButtonInsideTableColumn<ClientTable, String> createUserOrdersButton() {
        ButtonInsideTableColumn<ClientTable, String> button = new ButtonInsideTableColumn<>("", "orders");
        button.setEventHandler(openUserOrders(button));
        button.setCssId("orderDetailsButton");
        return button;
    }

    private EventHandler<MouseEvent> openUserOrders(ButtonInsideTableColumn<ClientTable, String> button) {
        return mouseEvent -> {
            String userLogin = button.getRowId().getLogin();
            ClientOrders clientOrderController = new ClientOrders(true, userLogin, "All orders of user '" + userLogin + "'");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/clientOrdersGUI.fxml"));
            loader.setController(clientOrderController);

            try {
                userOrdersPane.getChildren().clear();
                userOrdersPane.getChildren().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            userOrdersPane.setVisible(true);
            allUsersPane.setVisible(false);
            sortingButtonsBox.setVisible(false);
            goBackButton.setVisible(true);
        };
    }


    private void setSorting(TableView<ClientTable> tableView) {
        createSortingButtons();
        sortingButtonsBox.setLayoutY(70);
        sortingButtonsBox.getItems().addAll("Id", "Login", "Name", "Last Name");
        sortingButtonsBox.setValue("Choose sorting type :");
        sortingButtonsBox.setVisible(true);
        sortingButtonsBox.valueProperty().addListener((observableValue, s, selectedValue) -> {
            tableView.getSortOrder().clear();
            if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(0))) {
                setSortingType(tableView, 1, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(1))) {
                setSortingType(tableView, 2, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(2))) {
                setSortingType(tableView, 3, TableColumn.SortType.ASCENDING);
            } else if (Objects.equals(selectedValue, sortingButtonsBox.getItems().get(3))) {
                setSortingType(tableView, 4, TableColumn.SortType.ASCENDING);
            }
            tableView.requestFocus();
        });
    }

}
