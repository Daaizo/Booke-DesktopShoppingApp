package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminController extends  Controller{

    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked,loginScene);
    }

}
