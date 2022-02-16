package application.Controllers;

import application.Main;
import dataBase.SqlConnection;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public final String shoppingCartScene = "/application/FXML/shoppingCartGUI.fxml";
    public final String absolutePathToIcons = "C:\\Users\\Daaizo\\IdeaProjects\\simple_app\\src\\main\\resources\\application\\Icons\\";
    protected final String currency = " $";

    private SqlConnection instance;
    public HashMap<String, String> loginValues = Main.loginValues; // username - key, password - value
    @FXML
    protected AnchorPane anchor;
    @FXML
    protected Button goBackButton;
    @FXML
    protected StackPane notification;
    @FXML
    protected Label notificationLabel;


    public void prepareScene() {
        AnchorPane mainAnchor = setAnchorSizeAndColors();
        this.goBackButton = createGoBackButton();
        mainAnchor.getChildren().addAll(createExitButton(), createHorizontalLine(), createLogoutButton(), setSmallLogoInCorner(), goBackButton);
    }

    private ImageView setSmallLogoInCorner() {
        ImageView logo = new ImageView();
        logo.setImage(new Image(absolutePathToIcons + "transparentLogo.png"));
        logo.setY(5);
        logo.setX(5);
        return logo;
    }

    protected void showOnlyRowsWithData(TableView<?> tableView, ObservableList list) {

        tableView.setItems(list);
        tableView.setMaxHeight(510);
        tableView.setFixedCellSize(70);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(50));
    }

    private AnchorPane createHorizontalLine() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinSize(1050, 3);
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(52);
        anchorPane.setStyle("-fx-background-color :  #fc766a");
        return anchorPane;
    }

    Button createExitButton() {
        Button closeButton = new Button();
        closeButton.setBackground(Background.EMPTY);
        closeButton.setGraphic(iconPath("close.png"));
        closeButton.setOnAction(actionEvent -> Platform.exit());
        closeButton.setLayoutX(1000);
        closeButton.setLayoutY(10);
        return closeButton;
    }

    private Button createLogoutButton() {
        Button logoutButton = new Button();
        logoutButton.setBackground(Background.EMPTY);
        logoutButton.setGraphic(iconPath("logout.png"));
        logoutButton.setOnAction(actionEvent -> switchScene(actionEvent, loginScene));
        logoutButton.setLayoutX(950);
        logoutButton.setLayoutY(10);
        return logoutButton;
    }

    private Button createGoBackButton() {
        Button goBackButton = new Button();
        goBackButton.setBackground(Background.EMPTY);
        goBackButton.setGraphic(iconPath("back-button.png"));
        goBackButton.setLayoutX(5);
        goBackButton.setLayoutY(65);
        goBackButton.setVisible(false);
        return goBackButton;
    }

    AnchorPane setAnchorSizeAndColors() {
        anchor.setStyle("-fx-border-color :  #fc766a; -fx-border-width : 2px;-fx-background-color : #5B84B1FF ");
        anchor.setMinSize(1050, 694);
        return anchor;
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

    protected void colorField(TextField field, Label label, Color color) {

        DropShadow shadow = new DropShadow();
        shadow.setBlurType(BlurType.THREE_PASS_BOX);
        shadow.setSpread(0.60);
        shadow.setColor(color);
        shadow.setWidth(25);
        shadow.setHeight(25);
        shadow.setRadius(10);
        field.setEffect(shadow);
        field.setOnMouseExited(mouseEvent -> {
            basicTheme(field);
            label.setVisible(false);
        });

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

    protected void basicTheme(TextField field) {
        //resetting theme, used to undo red border on registration and login
        Glow glow = new Glow();
        field.setEffect(glow);
    }

    protected void setImageToButtonAndPlaceItOnXY(Button buttonName, String imageName, double x, double y) {
        buttonName.setGraphic(iconPath(imageName));
        buttonName.setBackground(Background.EMPTY);
        buttonName.setLayoutY(y);
        buttonName.setLayoutX(x);

    }

    protected void createNotification(String text) {

        StackPane pane = new StackPane();
        pane.setPrefWidth(300);
        pane.setPrefHeight(49);
        Label label = new Label(text);
        ImageView image = new ImageView();
        image.setImage(new Image(absolutePathToIcons + "check.png"));
        pane.setId("notification");
        label.setId("notificationLabel");
        pane.getChildren().addAll(image, label);
        label.wrapTextProperty();
        StackPane.setAlignment(label, Pos.CENTER);
        StackPane.setAlignment(image, Pos.CENTER_LEFT);
        pane.setLayoutX(740);
        pane.setLayoutY(84);
        pane.setVisible(false);
        this.notification = pane;
        this.notificationLabel = label;
        anchor.getChildren().add(this.notification);
    }

    protected void showNotification(double timeDuration) {
        this.notification.setVisible(false);
        this.notification.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(timeDuration), this.notification);
        fade.setFromValue(1000);
        fade.setToValue(0);
        fade.setCycleCount(1);
        fade.setAutoReverse(true);
        fade.play();

    }
}
