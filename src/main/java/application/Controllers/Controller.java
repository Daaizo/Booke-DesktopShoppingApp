package application.Controllers;

import application.Main;
import dataBase.MySqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

public abstract class Controller {
    public final String loginScene = "/application/loginGUI.fxml";
    public final String registrationScene = "/application/registerGUI.fxml";
    public final String adminScene = "/application/adminGUI.fxml";
    public final String clientScene = "/application/clientGUI.fxml";
    private MySqlConnection instance;
    public HashMap<String, String> loginValues = Main.loginValues; // username - key, password - value

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

    public void checkConnectionWithDataBaseAndDisplayError() {
        instance = MySqlConnection.createInstance();
        while (instance.getConnection() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection to data base failed. Reconnect and try again.");
            alert.showAndWait();
            instance = MySqlConnection.createInstance();
        }
    }

    public Connection getConnection() {
        return this.instance.getConnection();
    }

    public void updateHashMapWithLoginValues(String login, String pass) {
        loginValues.put(login, pass);
    }

    protected void colorField(TextField field, Color color) {
        InnerShadow shadow = new InnerShadow();
        shadow.setBlurType(BlurType.ONE_PASS_BOX);
        shadow.setColor(color);
        shadow.setWidth(26);
        shadow.setHeight(36);
        shadow.setRadius(16);
        field.setEffect(shadow);
    }

    protected void basicTheme(TextField field) {

        //TODO some nice theme to input fields
        Glow glow = new Glow();
        field.setEffect(glow);

    }

}
