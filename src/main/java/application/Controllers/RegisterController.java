package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import users.Client;

import java.sql.Connection;

public class RegisterController extends Controller {

    @FXML
    private TextField tfLastName, tfLogin, tfPassword, tfName, tfPasswordRepeat;
    @FXML
    private Label tfPassInfo, tfLoginEmpty, tfPassEmpty, tfPassRepEmpty;

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
        tfPasswordRepeat.clear();
    }

    private boolean isPasswordEmpty() {
        tfPassInfo.setVisible(false);
        if (tfPassword.getText().isEmpty()) {
            tfPassEmpty.setVisible(true);
            colorField(tfPassword, Color.RED);
            return true;
        } else {
            basicTheme(tfPasswordRepeat);
            tfPassEmpty.setVisible(false);

        }
        if (tfPasswordRepeat.getText().isEmpty()) {
            tfPassRepEmpty.setVisible(true);
            colorField(tfPasswordRepeat, Color.RED);
            return true;
        } else {
            basicTheme(tfPasswordRepeat);
            tfPassRepEmpty.setVisible(false);
        }
        return false;


    }

    private boolean passFieldMatches() {


        if (tfPassword.getText().compareTo(tfPasswordRepeat.getText()) != 0) {
            colorField(tfPassword, Color.RED);
            colorField(tfPasswordRepeat, Color.RED);
            tfPassInfo.setVisible(true);
            return false;
        } else {
            basicTheme(tfPasswordRepeat);
            basicTheme(tfPassword);
            tfPassInfo.setVisible(false);
            return true;
        }


    }

    private boolean isLoginEmpty() {
        if (tfLogin.getText().isEmpty()) {
            colorField(tfLogin, Color.RED);
            tfLoginEmpty.setVisible(true);
            return true;
        } else {
            //TODO nice effect
            Glow glow = new Glow();
            tfLogin.setEffect(glow);
            tfLoginEmpty.setVisible(false);

            return false;
        }
    }

    private Client newUser() {
        String login = tfLogin.getText().trim();
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String password = tfPassword.getText().trim();
        Client newUser = new Client(login, name, lastName, password);
        return newUser;
    }

    @FXML
    void saveButtonClicked(ActionEvent event) {
        if (!isLoginEmpty() && !isPasswordEmpty() && passFieldMatches()) {

            Client newUser = newUser();
            checkConnectionWithDataBaseAndDisplayError();
            Connection connection = getConnection();
            newUser.addUserToDatabase(connection);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "User created");
            alert.showAndWait();
            updateHashMapWithLoginValues(newUser.getLogin(), newUser.getPassword());
            switchScene(event, loginScene);
        }

    }

    private void addUserToDb() {

    }

}
