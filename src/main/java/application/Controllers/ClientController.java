package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController extends Controller {


    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked,loginScene);
    }
}
