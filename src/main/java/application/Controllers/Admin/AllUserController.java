package application.Controllers.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Client;
import users.ClientTable;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    }

    void displayUsers() throws SQLException {
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
