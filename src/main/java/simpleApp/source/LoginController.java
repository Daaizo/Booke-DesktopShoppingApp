package simpleApp.source;

import simpleApp.source.databaseconnection.MySqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class LoginController {
    private final String adminSceneUrl = "adminGUI.fxml";
    private final String clientSceneUrl = "clientGUI.fxml";
    private final String registerSceneUrl = "registerGUI.fxml";


    private void switchScene(ActionEvent event, String url){
        Stage stage;
        Parent root;
        Scene newScene;
        try {
            root = FXMLLoader.load(getClass().getResource(url));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();
        }
        catch (IOException e){
            System.out.println("error with switching scene");

        }
    }


    @FXML
    private TextField tfLogin;
    @FXML
    private TextField tfPassword;
    @FXML
    void loginButtonClicked(ActionEvent event) {
        String login = tfLogin.getText();
        String password = tfPassword.getText();
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
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
            switchScene(event, registerSceneUrl);
    }

}
