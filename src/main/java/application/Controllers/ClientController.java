package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ClientController extends Controller {
    @FXML
    private AnchorPane ebooksAnchorPane, gamesAnchorPane;

    @FXML
    private Button ebooksButton, gamesButton, logoutButton, goBackButton;

    @FXML
    private Pane categoryPickingPane;

    @FXML
    public void initialize() {
        categoryPickingPane.setVisible(true);
        createAnchorAndExitButton();
        setImageToButtonAndPlaceItOnX(logoutButton, "logout.png", 950);
        setImageToButtonAndPlaceItOnX(goBackButton, "back-button.png", 910);
        goBackButton.setVisible(false);
    }

    @FXML
    void logoutButtonClicked(ActionEvent clicked) {
        switchScene(clicked, loginScene);
    }

    @FXML
    void gamesButtonClicked(ActionEvent event) {
        gamesAnchorPane.setVisible(true);
        categoryPickingPane.setVisible(false);
        goBackButton.setVisible(true);

    }

    @FXML
    void ebooksButtonClicked(ActionEvent event) {
        ebooksAnchorPane.setVisible(true);
        categoryPickingPane.setVisible(false);
        goBackButton.setVisible(true);
    }

    @FXML
    void goBackButtonClicked(ActionEvent event) {
        System.out.println("back button clicekd");
        categoryPickingPane.setVisible(true);
        ebooksAnchorPane.setVisible(false);
        gamesAnchorPane.setVisible(false);
        goBackButton.setVisible(false);
    }

    @FXML
    void ebookOnMouseEntered(MouseEvent event) {
        ebooksButton.setStyle("-fx-background-color: #fc766a; -fx-text-fill:  #5B84B1FF;");
    }

    @FXML
    void ebookOnMouseExited(MouseEvent event) {
        ebooksButton.setStyle("-fx-background-color:  #5B84B1FF; -fx-text-fill: #fc766a;-fx-border-color : #fc766a ;");
    }


    @FXML
    void gamesButtonOnMouseEntered(MouseEvent event) {
        gamesButton.setStyle("-fx-background-color: #fc766a; -fx-text-fill:  #5B84B1FF;");

    }

    @FXML
    void gamesButtonOnMouseExited(MouseEvent event) {
        gamesButton.setStyle("-fx-background-color:  #5B84B1FF; -fx-text-fill: #fc766a;-fx-border-color : #fc766a;");

    }


}
