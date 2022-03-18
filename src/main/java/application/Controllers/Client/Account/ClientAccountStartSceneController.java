package application.Controllers.Client.Account;

import application.Controllers.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import users.Client;

import java.io.IOException;
import java.util.Objects;


public class ClientAccountStartSceneController extends Controller {

    private final Lighting lighting = new Lighting();
    protected final Client currentUser = new Client(CURRENT_USER_LOGIN);

    @FXML
    public static Label emptyTableViewLabel;
    @FXML
    private Pane menuPane, mainPane, startPane;
    @FXML
    private Button ordersButton, accountSettingsButton, favouritesButton;

    //TODO database DIAGRAMS update needed !

    @FXML
    private void initialize() {
        prepareScene();
        createGoBackButton(event -> {
            switchScene(event, clientScene);
            mainPane.getChildren().removeAll();
            mainPane.getChildren().clear();
        });
        createLightingEffect();
        createEmptyTableViewLabel();

    }


    @FXML
    private void allOrdersButtonClicked() {
        loadScene("clientOrdersGUI.fxml");
        setButtonLightingEffect(ordersButton);
    }

    @FXML
    private void favouritesButtonClicked() {
        loadScene("clientFavouritesGUI.fxml");
        setButtonLightingEffect(favouritesButton);
    }

    @FXML
    private void accountSettingsButtonClicked() {
        loadScene("clientAccountDetailsGUI.fxml");
        setButtonLightingEffect(accountSettingsButton);
    }

    private void loadScene(String fxmlName) {
        try {
            mainPane.getChildren().removeAll();
            mainPane.getChildren().clear();
            mainPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/" + fxmlName))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuPane.setVisible(true);
        startPane.setVisible(false);
    }

    private void createLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setColor(Color.LIGHTPINK);
        lighting.setLight(light);
    }

    private void createEmptyTableViewLabel() {
        emptyTableViewLabel = new Label();
        emptyTableViewLabel.setId("titleLabel");
        emptyTableViewLabel.setVisible(false);
        emptyTableViewLabel.setAlignment(Pos.CENTER);
        emptyTableViewLabel.setTextAlignment(TextAlignment.CENTER);


    }


    private void setButtonLightingEffect(Button button) {
        ordersButton.setEffect(null);
        favouritesButton.setEffect(null);
        accountSettingsButton.setEffect(null);
        if (button == ordersButton) ordersButton.setEffect(lighting);
        else if (button == favouritesButton) favouritesButton.setEffect(lighting);
        else if (button == accountSettingsButton) accountSettingsButton.setEffect(lighting);
    }

    private void makePaneVisible(Pane pane) {
        emptyTableViewLabel.setVisible(false);
    }

    protected void setLogoAndCssToCustomDialog(Dialog<?> dialog) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(iconsUrl + "transparentLogo.png"));
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("alert");
    }


}
