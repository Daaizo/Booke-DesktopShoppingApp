package application.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


public class LoginController extends Controller {


    @FXML
    public Button registerButton, loginButton;

    @FXML
    private TextField tfLogin, tfPassword;

    @FXML
    private Label passwordLabel, loginLabel;


    @FXML
    public void initialize() {
        AnchorPane mainAnchor = setAnchorSizeAndColors();
        createExitButton();
    }


    private void loginError() {
        createAndShowAlert(Alert.AlertType.CONFIRMATION, "", "", "login or password is incorrect");

    }

    private boolean passwordMatches(String password, String login) {
        return password.compareTo(login) == 0;
    }

    private boolean areFieldsNotEmpty() {
        return !isLoginEmpty() && !isPasswordEmpty();
    }

    private boolean isLoginEmpty() {
        return tfLogin.getText().isEmpty();
    }

    private boolean isPasswordEmpty() {
        return tfPassword.getText().isEmpty();
    }


    private boolean isAdmin(String username) {
        return username.compareTo("admin") == 0;
    }

    private boolean isLoginInDatabase(String login) {
        return loginValues.get(login) != null;
    }


    private void checkPassword(String username, String password, ActionEvent event) {

        String pass = loginValues.get(username);
        if (passwordMatches(pass, password)) {
            CURRENT_USER_LOGIN = username;
            if (isAdmin(username)) {
                switchScene(event, adminScene);
            } else {
                switchScene(event, clientScene);
            }
        } else {
            loginError();
            tfPassword.clear();
        }
    }
    @FXML
    private void loginButtonClicked(ActionEvent event) {
        basicTheme(tfPassword, passwordLabel);
        basicTheme(tfLogin, loginLabel);
        if (areFieldsNotEmpty()) {
            checkConnectionWithDb();
            String login = tfLogin.getText();
            if (isLoginInDatabase(login)) {
                String password = tfPassword.getText();
                checkPassword(login, password, event);
            } else {
                loginError();
            }

        } else if (isLoginEmpty()) {
            colorField(tfLogin, loginLabel, Color.RED);
            displayLabelWithGivenText(loginLabel, "Filed is empty");

        } else if (isPasswordEmpty()) {
            colorField(tfPassword, passwordLabel, Color.RED);
            displayLabelWithGivenText(passwordLabel, "Filed is empty");
        }

    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        switchScene(event, registrationScene);
    }

}
