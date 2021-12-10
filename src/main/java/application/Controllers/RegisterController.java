package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController extends Controller{

    @FXML
    private TextField t;

    @FXML
    void goBackButtonClicked(ActionEvent event) {
        switchScene(event,loginScene );
    }
    @FXML
    void clearButtonClicked(ActionEvent event) {

    }



    @FXML
    void saveButtonClicked(ActionEvent event) {

    }

}
