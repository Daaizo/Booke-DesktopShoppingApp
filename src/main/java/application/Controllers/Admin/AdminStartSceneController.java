package application.Controllers.Admin;

import application.Controllers.Client.Account.ClientAccountDetails;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import users.Client;

import java.sql.SQLException;
import java.util.Optional;

public class AdminStartSceneController extends ClientAccountDetails {

    private final Lighting lighting = new Lighting();
    @FXML
    private Button ordersButton, productsButton, usersButton;
    @FXML
    private Pane mainPane, startPane, topMenuPane;
    private Label title;
    protected ComboBox<String> sortingButtonsBox;

    @FXML
    private void initialize() {
        prepareScene();
        createLightingEffect();
        createTitle();
        createNotification();
    }

    @FXML
    private void ordersButtonClicked() {
        loadScene("allOrdersGUI.fxml");
        title.setText("All orders");
        setButtonLightingEffect(ordersButton);
    }

    @FXML
    private void productsButtonClicked() {
        loadScene("allProductsGUI.fxml");
        title.setText("All products");
        setButtonLightingEffect(productsButton);
    }

    @FXML
    private void usersButtonClicked() {
        loadScene("allUsersGUI.fxml");
        title.setText("All users");
        setButtonLightingEffect(usersButton);
    }


    interface InterfaceToRunMethod {
        void myMethod();
    }

    private String getAdminPassword() {
        String adminPassword = null;
        try {
            adminPassword = Client.getClientPassword(getConnection(), "admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminPassword;
    }

    void createAndShowConfirmAdminPasswordAlert(String alertText, InterfaceToRunMethod methodsInterface) {
        Optional<String> result = createAndShowConfirmPasswordAlert("Re enter admin password to " + alertText);
        result.ifPresent(s -> {
            if (result.get().equals(getAdminPassword())) {
                methodsInterface.myMethod();
            } else {
                ButtonType tryAgain = new ButtonType("Try again");
                ButtonType cancel = new ButtonType("Cancel");
                Optional<ButtonType> res = createAndShowAlert(tryAgain, cancel, "Incorrect password, please try again", "Wrong password");
                res.ifPresent(buttonType -> {
                    if (res.get() == tryAgain) {
                        createAndShowConfirmAdminPasswordAlert(alertText, methodsInterface);
                    }
                });
            }
        });
    }

    private void createLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setColor(Color.LIGHTPINK);
        lighting.setLight(light);
    }

    private void setButtonLightingEffect(Button button) {
        ordersButton.setEffect(null);
        usersButton.setEffect(null);
        productsButton.setEffect(null);
        if (button == ordersButton) ordersButton.setEffect(lighting);
        else if (button == usersButton) usersButton.setEffect(lighting);
        else if (button == productsButton) productsButton.setEffect(lighting);
    }

    private void loadScene(String fxmlName) {
        String pathToFxml = "/AdminSceneFXML/";
        loadFXMLAndInitializeController(pathToFxml + fxmlName, mainPane);
        topMenuPane.setVisible(true);
        startPane.setVisible(false);
    }

    private void createTitle() {
        title = new Label();
        setTittlePosition();
        title.setId("titleLabel");
        topMenuPane.getChildren().add(title);
        title.toBack();
    }

    private void setTittlePosition() {
        title.setLayoutY(65);
        title.setLayoutX(0);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setPrefHeight(68);
        title.setPrefWidth(1000);
    }


}
