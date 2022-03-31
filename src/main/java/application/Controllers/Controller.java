package application.Controllers;

import dataBase.SqlConnection;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Objects;
import java.util.Optional;

public abstract class Controller {

    @FXML
    protected AnchorPane anchor;

    public static final String CURRENCY = " $";
    protected static final String PASSWORDS_REGEX = "^(?=.*[A-Z])(?=.*[!@#$&%^*()_+])(?=.*[0-9])(?=.*[a-z]).{6,20}$";
    public static String CURRENT_USER_LOGIN;
    //Regex meaning/  at least: 1 uppercase letter,  one special sign ( basically all numbers + shift ), 1 number,1 lowercase letter, 6-20 characters
    //Rubular link : https://rubular.com/r/gEmHAEm9wKr1Tj    <- regex checker
    protected final String loginScene = "/application/FXML/loginGUI.fxml";
    protected final String registrationScene = "/application/FXML/registerGUI.fxml";
    protected final String adminScene = "/application/FXML/AdminSceneFXML/adminStartingSceneGUI.fxml";
    protected final String clientScene = "/application/FXML/ClientSceneFXML/clientStartingSceneGUI.fxml";
    protected final String shoppingCartScene = "/application/FXML/ClientSceneFXML/shoppingCartGUI.fxml";
    protected final String clientAccountScene = "/application/FXML/ClientSceneFXML/ClientAccountFXML/clientAccountStartSceneGUI.fxml";
    protected final URL iconsUrl = getClass().getResource("/application/Icons/");
    protected String password;
    protected final URL cssUrl = getClass().getResource("/application/style.css");
    private SqlConnection instance;
    protected StackPane notification;

    @FXML
    private void enableDraggingWholeWindow() {
        Stage stage = (Stage) anchor.getScene().getWindow();
        anchor.setOnMousePressed(pressEvent -> anchor.setOnMouseDragged(dragEvent -> {
            stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
    }

    private ImageView setSmallLogoInCorner() {
        ImageView logo = setImageFromIconsFolder("transparentLogo.png");
        logo.setY(5);
        logo.setX(5);
        return logo;
    }

    private void disableAllActionOnTableViewColumns(TableView<?> tableView) {
        tableView.getColumns().forEach(tableColumn -> {
            tableColumn.setReorderable(false);
            tableColumn.setResizable(false);
            tableColumn.setEditable(false);
        });
    }

    private void showOnlyRowsWithData(TableView<?> tableView) {
        tableView.setMaxHeight(480);
        tableView.setFixedCellSize(55);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(40));
    }


    private int comparatorForPriceColumn(String a, String b) {
        String onlyNumberA = a.replace(CURRENCY, "").trim();
        String onlyNumberB = b.replace(CURRENCY, "").trim();
        double numberA = Double.parseDouble(onlyNumberA);
        double numberB = Double.parseDouble(onlyNumberB);
        return Double.compare(numberA, numberB);
    }

    private AnchorPane createHorizontalLine() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinSize(1050, 3);
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(52);
        anchorPane.setStyle("-fx-background-color :  #fc766a");
        return anchorPane;
    }

    private void showConnectionAlertAndWait() {
        while (instance.getConnection() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection to data base failed. Reconnect and try again.");
            alert.showAndWait();
            instance = SqlConnection.createInstance();
        }
    }

    protected void clearPane(Pane pane) {
        pane.getChildren().removeAll();
        pane.getChildren().clear();
    }

    protected void loadFXMLAndInitializeController(String fxmlPathFromFXMLFolder, Pane pane) {
        clearPane(pane);
        try {
            pane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/application/FXML" + fxmlPathFromFXMLFolder))));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void prepareScene() {
        AnchorPane mainAnchor = setAnchorSizeAndColors();
        mainAnchor.getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        mainAnchor.getChildren().addAll(createHorizontalLine(), setSmallLogoInCorner());
        createExitButton();
        createLogoutButton();
    }

    protected void prepareSceneWithoutLogoutButton() {
        AnchorPane mainAnchor = setAnchorSizeAndColors();
        mainAnchor.getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        mainAnchor.getChildren().addAll(createHorizontalLine(), setSmallLogoInCorner());
        createExitButton();
    }


    protected void prepareTableView(TableView<?> tableView, TableColumn<?, String> columnWithPrice) {
        showOnlyRowsWithData(tableView);
        disableAllActionOnTableViewColumns(tableView);
        columnWithPrice.setComparator(this::comparatorForPriceColumn);

    }

    protected Button createButton(String imageName, int x, int y) {
        int sideLength = 49;
        Button button = new Button();
        button.getStyleClass().add("topButtons");
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setMaxSize(sideLength + 2, sideLength);
        button.setPrefSize(sideLength + 2, sideLength);
        button.setGraphic(setImageFromIconsFolder(imageName));
        button.setOnMouseClicked(mouseEvent -> anchor.requestFocus());
        button.setOnAction(event -> anchor.requestFocus());
        anchor.getChildren().add(button);
        return button;
    }

    protected Button createGoBackButton(EventHandler<ActionEvent> event) {
        Button goBackButton = createButton("back-button.png", 5, 65);
        goBackButton.setOnAction(event);
        return goBackButton;
    }

    protected void createExitButton() {
        Button closeButton = createButton("close.png", 1000, 2);
        closeButton.setId("exitButton");
        closeButton.setOnAction(actionEvent -> Platform.exit());
    }

    protected void createLogoutButton() {
        Button logoutButton = createButton("logout.png", 948, 2);
        logoutButton.setOnAction(actionEvent -> switchScene(actionEvent, loginScene));
    }


    protected AnchorPane setAnchorSizeAndColors() {
        anchor.setStyle("-fx-border-color :  #fc766a; -fx-border-width : 2px;-fx-background-color : #5B84B1FF ");
        anchor.setMinSize(1050, 694);
        anchor.setMaxSize(1050, 694);
        return anchor;
    }

    protected ImageView setImageFromIconsFolder(String iconName) {
        return new ImageView(iconsUrl + iconName);
    }


    protected void switchScene(ActionEvent event, String url) {
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


    protected void checkConnectionWithDb() {
        instance = SqlConnection.createInstance();
        if (instance == null) showConnectionAlertAndWait();
    }

    protected Connection getConnection() {
        return this.instance.getConnection();
    }

    protected void makeFieldsBorderRed(TextField field, Label label) {
        DropShadow shadow = new DropShadow();
        shadow.setBlurType(BlurType.THREE_PASS_BOX);
        shadow.setSpread(0.60);
        shadow.setColor(Color.RED);
        shadow.setWidth(25);
        shadow.setHeight(25);
        shadow.setRadius(10);
        field.setEffect(shadow);
        field.setOnMouseClicked(mouseEvent -> {
            basicTheme(field);
            label.setVisible(false);
        });
    }


    protected void displayLabelWithGivenText(Label label, String text) {
        label.setText(text);
        label.setVisible(true);
    }

    protected void basicTheme(TextField field, Label label) {
        Glow glow = new Glow();
        field.setEffect(glow);
        label.setVisible(false);
    }

    protected void basicTheme(TextField field) {
        Glow glow = new Glow();
        field.setEffect(glow);
    }


    protected void createNotification() {
        notification = new StackPane();
        Label notificationLabel = new Label();
        ImageView image = setImageFromIconsFolder("check.png");
        notification.setId("notification");
        notificationLabel.setId("notificationLabel");
        notificationLabel.setPadding(new Insets(0, 0, 0, 47));
        notification.getChildren().addAll(notificationLabel, image);
        notificationLabel.setWrapText(true);
        StackPane.setAlignment(notificationLabel, Pos.CENTER);
        StackPane.setAlignment(image, Pos.CENTER_LEFT);
        notification.setMinWidth(305);

        notification.setLayoutX(720);
        notification.setLayoutY(7);
        notification.setVisible(false);
        anchor.getChildren().add(notification);
    }

    protected void showNotification(String notificationText) {
        if (notification == null) {
            createNotification();
        }
        double timeDuration = 3000;
        Label notificationLabel = (Label) notification.getChildren().get(0);
        notificationLabel.setText(notificationText);
        notification.setVisible(false);
        notification.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(timeDuration), notification);
        fade.setFromValue(1000);
        fade.setToValue(0);
        fade.setCycleCount(1);
        fade.setAutoReverse(true);
        fade.play();
    }

    protected Optional<ButtonType> createAndShowAlert(Alert.AlertType type, String headerText, String title, String contextText) {
        Alert alert = new Alert(type, contextText);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(iconsUrl + "transparentLogo.png"));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        dialogPane.getStyleClass().add("alert");
        return alert.showAndWait();
    }

    protected Optional<ButtonType> createAndShowAlert(ButtonType buttonType1, ButtonType buttonType2, String headerText, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", buttonType1, buttonType2);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(iconsUrl + "transparentLogo.png"));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        dialogPane.getStyleClass().add("alert");
        return alert.showAndWait();
    }

    protected Optional<ButtonType> deleteProductAlert(String productName, String productQuantity, String fromWhere) {
        Optional<ButtonType> buttonClicked;
        if (productQuantity.compareTo("1") == 0) {
            buttonClicked = createAndShowAlert(Alert.AlertType.CONFIRMATION, "DELETING PRODUCT FROM " + fromWhere.toUpperCase(),
                    "Confirmation",
                    "Do you want to delete '" + productName + "' from " + fromWhere);
        } else {
            buttonClicked = createAndShowAlert(Alert.AlertType.CONFIRMATION, "DELETING PRODUCT FROM " + fromWhere.toUpperCase(),
                    "Confirmation",
                    "Do you want to delete " + productQuantity + " pieces of '" + productName + "' from " + fromWhere);
        }
        return buttonClicked;
    }



    protected void setPasswordVisibilityButton(Button showPasswordButton, TextField passwordTextField) {
        String hiddenPassIconName = "hiddenPassword.png";
        String showPassIconName = "showPassword.png";
        showPasswordButton.setGraphic(setImageFromIconsFolder("hiddenPassword.png"));
        showPasswordButton.setBackground(Background.EMPTY);
        showPasswordButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> showPasswordButtonPressed(passwordTextField, showPasswordButton, setImageFromIconsFolder(showPassIconName)));
        showPasswordButton.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> showPasswordButtonReleased(passwordTextField, showPasswordButton, setImageFromIconsFolder(hiddenPassIconName)));
    }

    protected void showPasswordButtonPressed(TextField field, Button button, ImageView graphic) {
        button.setGraphic(graphic);
        password = field.getText();
        field.clear();
        field.setPromptText(password);
    }

    protected void showPasswordButtonReleased(TextField field, Button button, ImageView graphic) {
        button.setGraphic(graphic);
        field.setText(password);
    }
}
