package application.Controllers;

import application.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.Client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController extends Controller {
    @FXML
    private TableColumn<Client, String> userFirstNameColumn;

    @FXML
    private TableColumn<Client, String> userIDColumn;
    @FXML
    private TableColumn<Client, String> userLoginColumn;

    @FXML
    private TableColumn<Client, String> userLastNameColumn;

    @FXML

    private TableView<Client> userTableView;


    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked, loginScene);
    }


    void displayUsers() throws SQLException {
        Connection con = Main.connectToDatabase();

        ResultSet users = Client.getDataFromDataBase(con);
        ObservableList<Client> listOfUsers = Client.getUsers(users);
        userIDColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("id"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("login"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("lastName"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("name"));
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
