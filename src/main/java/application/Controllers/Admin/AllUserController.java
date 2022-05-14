package application.Controllers.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import users.Client;
import users.ClientTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
        ArrayList<TextField> listOfTextFields = new ArrayList<>();
        createTextFields(listOfTextFields);
        createAndFillGridPane(addNewUserButton, listOfTextFields);
        addNewUserButton.setOnAction(event -> {

        });

    }

    private void createTextFields(ArrayList<TextField> listOfAllFields) {
        listOfAllFields = new ArrayList<>();
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
    }

    private void createAndFillGridPane(Button addNewUserButton, ArrayList<TextField> fieldArrayList) {
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(100);
        gridPane.setLayoutY(500);
        anchor.getChildren().add(gridPane);


        gridPane.setHgap(30);
        gridPane.add(fieldArrayList.get(0), 0, 0);
        gridPane.add(fieldArrayList.get(1), 1, 0);
        gridPane.add(fieldArrayList.get(2), 2, 0);
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
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTableView.setItems(list);
        prepareTableView(userTableView, null);
    }
}
