package com.example.projectshop;

import com.example.projectshop.databaseconnection.MySqlConnection;
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

    private Stage stage;
    private Scene adminScene,clientScene;
    private Parent root;
    private void adminLogin(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("adminGUI.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            adminScene = new Scene(root);
            stage.setScene(adminScene);
            stage.show();
        }
        catch (IOException e){
            System.out.println("error with switching scene to admin");

        }
    }
    private void clientLogin(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("clientGUI.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            clientScene = new Scene(root);
            stage.setScene(clientScene);
            stage.show();
        }
        catch (Exception e){
            System.out.println("error with switching scene to client");

        }
    }

    @FXML
    private Button registerButton;
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
                System.out.println("success");  //handling successful login
                if(login.compareTo("admin") == 0){
                    adminLogin(event);
                }
                else{
                    clientLogin(event);
                }



            }
            else {
                System.out.println(data.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void registerButtonClicked(MouseEvent event) {

    }

}
