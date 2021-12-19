package application.Controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.HashMap;


public class LoginController extends Controller {
    private final HashMap<String,String> loginValues = Main.loginValues; // username - key, password - value

    @FXML
    private TextField tfLogin,tfPassword;

    //TODO instead of error message inside the window
    private void loginError(){
        Alert unsuccessfulLogin= new Alert(Alert.AlertType.INFORMATION, "login or password is incorrect");
        unsuccessfulLogin.showAndWait();
        tfPassword.clear();
    }
    private void checkLogin(String username, String password, ActionEvent event) {
        String pass = loginValues.get(username);
        if (pass != null && pass.compareTo(password) == 0) {

            if (username.compareTo("admin") == 0) {
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
