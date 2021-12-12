package application.Controllers;

import dataBase.MySqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public abstract class Controller {
    public final String loginScene = "/application/loginGUI.fxml";
    public final String registrationScene = "/application/registerGUI.fxml";
    public final String adminScene = "/application/adminGUI.fxml";
    public final String clientScene = "/application/loginGUI.fxml";

    public void switchScene(ActionEvent event, String url){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(url));
            Stage window =  (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root);
            window.setScene(newScene);
            window.show();
        }
        catch (IOException e){
            System.out.println("error with switching scene");
        }
    }
    public void checkConnectionWithDataBaseAndDisplayError(){
        MySqlConnection instance = MySqlConnection.createInstance();
        while(instance.connection == null){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Connection to data base failed");
            alert.showAndWait();
            instance = MySqlConnection.createInstance();
        }
    }
}
