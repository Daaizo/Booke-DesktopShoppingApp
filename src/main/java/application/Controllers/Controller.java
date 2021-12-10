package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
