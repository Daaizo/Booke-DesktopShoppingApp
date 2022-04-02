package application.Controllers.Client.Products;

import application.Controllers.Client.ClientStartSceneController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GamesCategoriesController extends ClientStartSceneController {
    @FXML
    private Pane gamesCategoryPane, chosenCategoryPane;
    @FXML
    private Button adventure, shooters, sport, mmorpg, allGames;
    @FXML
    private TableView<ProductTable> productsTableView;

    @FXML
    private void initialize() {
        initializeButtons();
        prepareAllImages();
        setButtonsActions();
        getCategoryButtonsAndPlaceThemInGrid();
        displayPane("gamesCategoryPane");
    }

    private void prepareAllImages() {
        adventure.setGraphic(setImageFromIconsFolder("/CategoryIcons/adventure.png"));
        shooters.setGraphic(setImageFromIconsFolder("/CategoryIcons/shooters.png"));
        sport.setGraphic(setImageFromIconsFolder("/CategoryIcons/sport.png"));
        mmorpg.setGraphic(setImageFromIconsFolder("/CategoryIcons/mmorpg.png"));
    }

    private void initializeButtons() {
        String cssClassName = "categoryImages";
        adventure = new Button();
        shooters = new Button();
        sport = new Button();
        mmorpg = new Button();
        adventure.getStyleClass().add(cssClassName);
        shooters.getStyleClass().add(cssClassName);
        sport.getStyleClass().add(cssClassName);
        mmorpg.getStyleClass().add(cssClassName);
        createAllEbooksButton();
    }

    private void createAllEbooksButton() {
        allGames = new Button("All games");
        allGames.getStyleClass().add("CategoryPickingButtons");
        allGames.setMaxWidth(1000);
        allGames.setPrefHeight(100);
    }

    private void prepareNewPane(String subcategoryName, String sceneTitle) {
        clearPane(chosenCategoryPane);
        displayPane("chosenCategoryPane");
        goBackButton.setOnAction(event -> {
            displayPane("gamesCategoryPane");
            sortingButtonsBox.setVisible(false);
        });
        getProductsFromDbIntoTableView(subcategoryName);
        createSubSceneTitle(sceneTitle);
        createSortingButtons();
        prepareSortingButtons(productsTableView, (TableColumn<ProductTable, String>) productsTableView.getColumns().get(4),
                (TableColumn<ProductTable, String>) productsTableView.getColumns().get(3));
        fixSortingButtonPosition();
    }

    private void setButtonsActions() {
        allGames.setOnAction(event -> {
            loadFXMLAndInitializeController("/ClientSceneFXML/ProductsFXML/allGamesPaneGUI.fxml", chosenCategoryPane);
            displayPane("chosenCategoryPane");
            goBackButton.setOnAction(e -> displayPane("gamesCategoryPane"));
        });
        adventure.setOnMouseClicked(mouseEvent -> prepareNewPane("adventure games", "ADVENTURE GAMES"));
        sport.setOnMouseClicked(mouseEvent -> prepareNewPane("sport games", "SPORT GAMES"));
        mmorpg.setOnMouseClicked(mouseEvent -> prepareNewPane("mmorpg games", "MMORPG GAMES"));
        shooters.setOnMouseClicked(mouseEvent -> prepareNewPane("fps games", "FPS GAMES"));
    }

    private void getCategoryButtonsAndPlaceThemInGrid() {
        GridPane grid = new GridPane();
        double padding = 70;
        grid.setPadding(new Insets(padding));
        grid.setHgap(padding);
        grid.setVgap(padding);
        grid.add(adventure, 0, 0);
        grid.add(shooters, 1, 0);
        grid.add(sport, 2, 0);
        grid.add(mmorpg, 3, 0);
        grid.add(allGames, 0, 1, 4, 1);
        grid.setLayoutY(30);
        grid.setLayoutX(20);
        gamesCategoryPane.getChildren().add(grid);
        GridPane.setFillWidth(allGames, true);
    }

    private void createSubSceneTitle(String text) {
        Label title = new Label();
        title.setLayoutY(14);
        title.setLayoutX(0);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setPrefHeight(68);
        title.setPrefWidth(1000);
        title.setId("titleLabel");
        title.setText(text);
        chosenCategoryPane.getChildren().add(title);
    }

    private void createTableView() {
        productsTableView = new TableView<>();
        productsTableView.setLayoutX(100);
        productsTableView.setLayoutY(150);
        productsTableView.setPlaceholder(new Label("There are no products in this category"));
        TableColumn<ProductTable, String> productName = new TableColumn<>("name");
        productName.setPrefWidth(244);
        TableColumn<ProductTable, String> productPrice = new TableColumn<>("price");
        productPrice.setPrefWidth(91);
        TableColumn<ProductTable, String> cartButtonColumn = new TableColumn<>();
        TableColumn<ProductTable, String> starButtonColumn = new TableColumn<>();
        TableColumn<ProductTable, String> numberOfOrdersColumn = new TableColumn<>();
        numberOfOrdersColumn.setVisible(false);
        starButtonColumn.setPrefWidth(243);
        cartButtonColumn.setPrefWidth(230);
        productsTableView.getColumns().addAll(productName, productPrice, cartButtonColumn, starButtonColumn, numberOfOrdersColumn);
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        starButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        starButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        cartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        numberOfOrdersColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfOrdersPerProduct"));
        chosenCategoryPane.getChildren().add(productsTableView);
    }

    private void displayProductsFromCategory(String subcategoryName) throws SQLException {
        ResultSet products = Product.getProductsFromSubcategoryAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), CURRENT_USER_LOGIN, subcategoryName);
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromSubcategoryWithInformationIfTheyAreInUsersFavourite(products);
        productsTableView.setItems(listOfProducts);
        prepareTableView(productsTableView, (TableColumn<?, String>) productsTableView.getColumns().get(1));
        productsTableView.setMaxHeight(250);
    }


    private void fixSortingButtonPosition() {
        sortingButtonsBox.setLayoutY(142);
        sortingButtonsBox.setLayoutX(715);
    }

    private void getProductsFromDbIntoTableView(String subcategoryName) {
        checkConnectionWithDb();
        createTableView();
        try {
            displayProductsFromCategory(subcategoryName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayPane(String paneName) {
        if (paneName.equals("chosenCategoryPane")) {
            chosenCategoryPane.setVisible(true);
            gamesCategoryPane.setVisible(false);
        } else {
            goBackButton.setOnAction(event -> switchScene(event, clientScene));
            chosenCategoryPane.setVisible(false);
            gamesCategoryPane.setVisible(true);
        }
    }
}
