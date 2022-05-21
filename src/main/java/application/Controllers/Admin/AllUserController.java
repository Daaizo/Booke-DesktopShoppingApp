package application.Controllers.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import users.Client;
import users.ClientTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class AllUserController extends AdminStartSceneController {
    @FXML
    private TableColumn<ClientTable, String> userFirstNameColumn, userLoginColumn, userLastNameColumn, userPasswordColumn;
    @FXML
    private TableColumn<ClientTable, Integer> userIDColumn;
    @FXML
    private TableView<ClientTable> userTableView;

    @FXML
    private void initialize() {
        try {
            displayUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createButtons();
    }

    private void createButtons() {
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
                try {
                    displayUsers();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        anchor.getChildren().add(gridPane);
        gridPane.setHgap(30);
        gridPane.add(fieldArrayList.get(2), 0, 0);
        gridPane.add(fieldArrayList.get(0), 1, 0);
        gridPane.add(fieldArrayList.get(1), 2, 0);
        gridPane.add(fieldArrayList.get(3), 3, 0);
        gridPane.add(addNewUserButton, 4, 0);
    }

    private void displayUsers() throws SQLException {
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
        userTableView.setItems(list);
        prepareTableView(userTableView, null);
    }
}
