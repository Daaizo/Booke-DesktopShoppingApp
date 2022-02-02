package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ClientController extends Controller {


    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked, loginScene);
    }

    @FXML
    public void initialize() {
        createAnchorAndExitButton();
    }
}
