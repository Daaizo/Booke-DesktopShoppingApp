package application.Controllers;

import application.Main;
import dataBase.SqlConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Objects;

public abstract class Controller {
    public static String CURRENT_USER_LOGIN;
    public final String loginScene = "/application/FXML/loginGUI.fxml";
    public final String registrationScene = "/application/FXML/registerGUI.fxml";
    public final String adminScene = "/application/FXML/adminGUI.fxml";
    public final String clientScene = "/application/FXML/clientGUI.fxml";
    public final String absolutePathToIcons = "C:\\Users\\Daaizo\\IdeaProjects\\simple_app\\src\\main\\resources\\application\\Icons\\";
    private SqlConnection instance;
    public HashMap<String, String> loginValues = Main.loginValues; // username - key, password - value
    @FXML
    private AnchorPane anchor;


    public void createAnchorAndExitButton() {
        Button closeButton = new Button();
        closeButton.setBackground(Background.EMPTY);
        closeButton.setGraphic(iconPath("close.png"));
        closeButton.setOnAction(actionEvent -> Platform.exit());
        closeButton.setLayoutX(1000);
        closeButton.setLayoutY(10);
        anchor.getChildren().add(closeButton);
        anchor.setStyle("-fx-border-color :  #fc766a; -fx-border-width : 2px;-fx-background-color : #5B84B1FF ");
        anchor.setMinSize(1050, 694);
    }

    public ImageView iconPath(String iconName) {
        return new ImageView(absolutePathToIcons + iconName);
    }

    @FXML
    void Dragging(MouseEvent event) {
        Stage stage = (Stage) anchor.getScene().getWindow();
        anchor.setOnMousePressed(pressEvent -> anchor.setOnMouseDragged(dragEvent -> {
            stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
    }

    public void switchScene(ActionEvent event, String url) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(url)));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root);
            window.setScene(newScene);
            window.show();
        } catch (IOException e) {
            System.out.println("error with switching scene");
            e.printStackTrace();
        }
    }

    private void showConnectionAlertAndWait() {
        while (instance.getConnection() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection to data base failed. Reconnect and try again.");
            alert.showAndWait();
            instance = SqlConnection.createInstance();
        }
    }

    public void checkConnectionWithDb() {
        instance = SqlConnection.createInstance();
        if (instance == null) showConnectionAlertAndWait();
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

    protected void displayLabelWithGivenText(Label label, String text) {
        label.setText(text);
        label.setVisible(true);
    }

    protected void basicTheme(TextField field, Label label) {
        //resetting theme, used to undo red border on registration and login
        Glow glow = new Glow();
        field.setEffect(glow);
        label.setVisible(false);
    }

    protected void setImageToButtonAndPlaceItOnXY(Button buttonName, String imageName, double x, double y) {
        buttonName.setGraphic(iconPath(imageName));
        buttonName.setBackground(Background.EMPTY);
        buttonName.setLayoutY(y);
        buttonName.setLayoutX(x);

    }


}
