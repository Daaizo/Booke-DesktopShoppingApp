package application.Controllers;

import application.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Client;
import users.ClientTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController extends Controller {
    @FXML
    private TableColumn<ClientTable, String> userFirstNameColumn;

    @FXML
    private TableColumn<ClientTable, String> userIDColumn;
    @FXML
    private TableColumn<ClientTable, String> userLoginColumn;

    @FXML
    private TableColumn<ClientTable, String> userLastNameColumn;

    @FXML

    private TableView<ClientTable> userTableView;


    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked, loginScene);
    }


    void displayUsers() throws SQLException {
        Connection con = Main.connectToDatabase();

        ResultSet users = Client.getDataFromDataBase(con);
        ObservableList<ClientTable> listOfUsers = ClientTable.getUsers(users);
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userTableView.setItems(listOfUsers);
    }

    @FXML
    public void initialize() {
        try {
            displayUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
