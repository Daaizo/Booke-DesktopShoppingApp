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
    private Pane menuPane, mainPane, startPane;
    @FXML
    private Button ordersButton, accountSettingsButton, favouritesButton;
    @FXML
    public static Button goBackButton;
    @FXML
    public static Label emptyTableViewLabel;

    protected final String pathToFxml = "/ClientSceneFXML/ClientAccountFXML/";

    @FXML
    private void initialize() {
        prepareScene();
        goBackButton = createGoBackButton(event -> {
            clearPane(mainPane);
            switchScene(event, clientScene);
        });
        createLightingEffect();
        createEmptyTableViewLabel();
    }


    @FXML
    private void allOrdersButtonClicked() {
        displayOrderScene(initializeController());
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

    private FXMLLoader initializeController() {
        ClientOrders clientOrderDetailsController = new ClientOrders(false, CURRENT_USER_LOGIN, "All orders");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/FXML/ClientSceneFXML/ClientAccountFXML/clientOrdersGUI.fxml"));
        loader.setController(clientOrderDetailsController);
        return loader;
    }

    private void displayOrderScene(FXMLLoader loader) {
        clearPane(mainPane);
        try {
            mainPane.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuPane.setVisible(true);
        startPane.setVisible(false);
    }

    private void loadScene(String fxmlName) {
        loadFXMLAndInitializeController(pathToFxml + fxmlName, mainPane);
        menuPane.setVisible(true);
        startPane.setVisible(false);
    }

    private void createLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setColor(Color.LIGHTPINK);
        lighting.setLight(light);
    }

    void createEmptyTableViewLabel() {
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

    protected void makePaneVisible(Pane pane) {
        pane.setVisible(true);
        emptyTableViewLabel.setVisible(false);
    }

    protected void makeLabelVisible(Pane pane) {
        pane.setVisible(false);
        emptyTableViewLabel.setVisible(true);
    }

    protected void setLogoAndCssToCustomDialog(Dialog<?> dialog) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(setImage("Logo", "transparentLogo"));
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("alert");
    }


}
