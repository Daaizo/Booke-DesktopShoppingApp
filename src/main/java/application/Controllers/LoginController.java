package application.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class LoginController extends Controller {

    @FXML
    private TextField tfLogin, tfPassword;


    private void loginError() {
        //TODO instead of error message inside the window
        Alert unsuccessfulLogin = new Alert(Alert.AlertType.INFORMATION, "login or password is incorrect");
        unsuccessfulLogin.showAndWait();
        tfPassword.clear();
    }

    private boolean passwordMatches(String password, String login) {
        return password.compareTo(login) == 0 ? true : false;
    }

    private boolean isAdmin(String username) {
        return username.compareTo("admin") == 0 ? true : false;
    }

    private void checkLogin(String username, String password, ActionEvent event) {
        String pass = loginValues.get(username);
        if (passwordMatches(pass, password)) {
            if (isAdmin(username)) {
                switchScene(event, adminScene);
            } else {
                switchScene(event, clientScene);
            }
        } else {
            loginError();
        }
    }
    @FXML
    private void loginButtonClicked(ActionEvent event) {
        if (!tfLogin.getText().isEmpty() || !tfPassword.getText().isEmpty()) {
            checkConnectionWithDataBaseAndDisplayError();
            String login = tfLogin.getText();
            String password = tfPassword.getText();
            checkLogin(login, password, event);
        }

    }



    @FXML
    void registerButtonClicked(ActionEvent event) {
            switchScene(event, registrationScene);
    }

}
