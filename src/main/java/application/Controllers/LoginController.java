package application.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import users.Client;

import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController extends Controller {
    @FXML
    public Button registerButton, loginButton, showPasswordButton;
    @FXML
    private TextField tfLogin, tfPassword;
    @FXML
    private Label passwordLabel, loginLabel;
    @FXML
    private ImageView logo;

    @FXML
    public void initialize() {

        setAnchorSizeAndColors();
        createExitButton();
        setPasswordVisibilityButton(showPasswordButton, tfPassword);
    }


    private void loginError() {
        createAndShowAlert(Alert.AlertType.WARNING, "Login or password is incorrect !", "", "Please try again");
        tfPassword.clear();
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
        boolean isInDataBase = false;
        try {
            isInDataBase = Client.isClientInDataBase(getConnection(), login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isInDataBase;
    }

    private String getClientPassword(String username) {
        Client client = new Client(username);
        try {
            ResultSet data = client.getClientData(getConnection());
            client.setClientData(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client.getPassword();
    }

    public void checkPassword(String username, String password, ActionEvent event) {

        if (passwordMatches(getClientPassword(username), password)) {
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
            makeFieldsBorderRed(tfLogin, loginLabel);
            displayLabelWithGivenText(loginLabel, "Filed is empty");

        } else if (isPasswordEmpty()) {
            makeFieldsBorderRed(tfPassword, passwordLabel);
            displayLabelWithGivenText(passwordLabel, "Filed is empty");
        }
        anchor.requestFocus();
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        switchScene(event, registrationScene);
    }


}
