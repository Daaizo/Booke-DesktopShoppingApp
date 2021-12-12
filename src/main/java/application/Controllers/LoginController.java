package application.Controllers;

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
import javafx.stage.Stage;
import users.Admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class LoginController extends Controller {

    @FXML
    private Button registerButton;
    @FXML
    private TextField tfLogin,tfPassword;

    private MySqlConnection sqlConnection;


    private void loginError(){
        Alert unsucesfullLogin= new Alert(Alert.AlertType.INFORMATION, "login or password is incorrect");
        unsucesfullLogin.showAndWait();
        tfPassword.clear();
    }
    private void checkLogin(String username, String password, ActionEvent event) throws SQLException {
        Admin admin = Admin.createAdmin();
        ResultSet dataFromDatabase = admin.getDataFromDataBase(sqlConnection.connection);
        // connection is and Connection object used to control SQL queries
        HashMap<String,String> loginValues = new HashMap<>();
        while(dataFromDatabase.next()){
            loginValues.put(dataFromDatabase.getString("username"), dataFromDatabase.getString("password"));
        }
        ///searching in hashMap
        for(String i : loginValues.keySet()){
            if(username != i){
                loginError();
            }
            else {
                for(String j : loginValues.values()){
                    if(password != j) {
                        loginError();
                    }
                    else switchScene(event, adminScene);

                }
            }

        }
    }
    @FXML
    private void loginButtonClicked(ActionEvent event) {
        try{


        this.sqlConnection = MySqlConnection.createInstance();
        checkConnectionWithDataBaseAndDisplayError();
        String login = tfLogin.getText();
        String password = tfPassword.getText();
        checkLogin(login, password, event);
        }
        catch (SQLException e){
            e.getMessage();
        }
        /*


        try {

            String query = "select username from shop.user where username='" + login +"' and password = '" +password +"'";
            Connection con = MySqlConnection.connectToDatabase();
            Statement stm = con.createStatement();
            ResultSet data = stm.executeQuery(query);
            if(data.next() ){
                System.out.println("success");
                if(login.compareTo("admin") == 0){
                    switchScene(event, adminSceneUrl);
                }
                else{
                    switchScene(event, clientSceneUrl);
                }
            }
            else {
                System.out.println("login failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }



    @FXML
    void registerButtonClicked(ActionEvent event) {
            switchScene(event, registrationScene);
    }

}
