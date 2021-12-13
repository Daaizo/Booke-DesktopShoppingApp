package application.Controllers;

import application.Main;
import dataBase.MySqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.HashMap;


public class LoginController extends Controller {
    private final HashMap<String,String> loginValues = Main.loginValues; // username - key, password - value

    @FXML
    private TextField tfLogin,tfPassword;

    //instead of error message inside the window ?
    private void loginError(){
        Alert unsuccessfulLogin= new Alert(Alert.AlertType.INFORMATION, "login or password is incorrect");
        unsuccessfulLogin.showAndWait();
        tfPassword.clear();
    }
    private void checkLogin(String username, String password, ActionEvent event)  {
        if(loginValues.get(username).compareTo(password) == 0)
        {
            if(username.compareTo("admin")  == 0){
                switchScene(event, adminScene);
            }
            else{
                switchScene(event, clientScene);
            }
        }
        else{
            loginError();
        }
    }
    @FXML
    private void loginButtonClicked(ActionEvent event) {

        checkConnectionWithDataBaseAndDisplayError();
        String login = tfLogin.getText();
        String password = tfPassword.getText();
        checkLogin(login, password, event);


    }



    @FXML
    void registerButtonClicked(ActionEvent event) {
            switchScene(event, registrationScene);
    }

}
