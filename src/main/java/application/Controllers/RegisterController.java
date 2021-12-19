package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import users.Client;

import java.sql.Connection;

public class RegisterController extends Controller {

    @FXML
    private TextField tfLastName, tfLogin, tfPassword, tfName, tfRepeatPassword;


    @FXML
    void goBackButtonClicked(ActionEvent event) {
        switchScene(event, loginScene);
    }

    @FXML
    void clearButtonClicked(ActionEvent event) {
        tfLogin.clear();
        tfName.clear();
        tfLastName.clear();
        tfPassword.clear();
        tfRepeatPassword.clear();
    }


    private Client newUser() {
        String login = tfLogin.getText().trim();
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String password = tfPassword.getText().trim();
        Client client = new Client(login, name, lastName, password);
        return client;
    }

    @FXML
    void saveButtonClicked(ActionEvent event) {
        Client client = newUser();
        checkConnectionWithDataBaseAndDisplayError();
        Connection connection = getConnection();
        boolean isUserAdded = client.addUserToDatabase(connection);
        if (isUserAdded) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "User created");
            alert.showAndWait();
            switchScene(event, loginScene);
        }
    }

}
