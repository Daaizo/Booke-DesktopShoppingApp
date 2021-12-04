package com.example.projectshop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {
    private Stage stage;
    private Scene loginScene;
    private Parent root;

    @FXML
    void logoutButtonClicked(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("loginGUI.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            loginScene = new Scene(root);
            stage.setScene(loginScene);
            stage.show();
        }
        catch (IOException e){
            System.out.println("error with switching scene to admin");

        }
    }
}
