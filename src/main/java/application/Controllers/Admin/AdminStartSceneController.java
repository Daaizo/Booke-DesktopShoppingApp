package application.Controllers.Admin;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Controller;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import users.OrderTable;

public class AdminStartSceneController extends Controller {

    private final Lighting lighting = new Lighting();
    @FXML
    private Button ordersButton, productsButton, usersButton;
    @FXML
    private Pane mainPane, startPane, topMenuPane;
    private Label title;

    @FXML
    private void initialize() {
        prepareScene();
        createLightingEffect();
        createTitle();
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
    }

    private void setTittlePosition() {
        title.setLayoutY(65);
        title.setLayoutX(0);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setPrefHeight(68);
        title.setPrefWidth(1000);
    }

    protected EventHandler<MouseEvent> buttonInsideTableViewClicked(ButtonInsideOrdersTableView button) {
        return mouseEvent -> {
            int orderNumber = button.getRowId().getOrderNumber();
            checkConnectionWithDb();
            System.out.println(orderNumber);

        };
    }

    protected ButtonInsideOrdersTableView createStatusButtons() {
        Button acceptButton = new Button("approve");
        Button cancelButton = new Button("cancel");
        cancelButton.setOnMouseClicked(mouseEvent -> System.out.println("tralala"));

        return new ButtonInsideOrdersTableView(acceptButton, cancelButton);
    }

    static class ButtonInsideOrdersTableView extends ButtonInsideTableColumn<OrderTable, String> {
        private final Button approveButton;
        private final Button cancelButton;

        public ButtonInsideOrdersTableView(Button approveButton, Button cancelButton) {
            this.cancelButton = cancelButton;
            this.approveButton = approveButton;
        }

        protected ImageView setIconFromAdminIconsFolder(String iconName) {
            return new ImageView(getClass().getResource("/application/Icons/AdminIcons/") + iconName);
        }

        private void setButtonsBackground() {
            cancelButton.setBackground(Background.EMPTY);
            approveButton.setBackground(Background.EMPTY);
        }

        private void setButtonImages() {
            approveButton.setGraphic(setIconFromAdminIconsFolder("approve.png"));
            cancelButton.setGraphic(setIconFromAdminIconsFolder("cancel.png"));
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                button.setText("-");
                button.setBackground(Background.EMPTY);
                button.setId(cssId);
                button.getStyleClass().add(cssClassId);
                if (item != null) {
                    switch (item) {
                        case "In progress" -> {
                            setButtonsBackground();
                            setButtonImages();
                            HBox pane = new HBox(approveButton, cancelButton);
                            approveButton.setOnMouseClicked(ButtonInsideOrdersTableView -> System.out.println("approve"));
                            setGraphic(pane);
                            button.fire();
                            //   setOnMouseClicked(deleteFromFavouriteClicked(getRowId()));
                            setOnMouseEntered(event -> setCursor(Cursor.HAND));
                            setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
                            getStyleClass().add("clientTableviewButtons");
                        }
                        case "Canceled" -> {
                            setGraphic(setIconFromAdminIconsFolder("rejected.png"));
                            getStyleClass().add("clientTableviewButtons");
                        }
                        case "Sent" -> {
                            setGraphic(setIconFromAdminIconsFolder("approved.png"));
                            getStyleClass().add("clientTableviewButtons");
                        }
                    }
                }
            }
        }

    }

}
