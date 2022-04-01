package application.Controllers.Client.Products;

import application.Controllers.Client.ClientStartSceneController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EbooksCategoriesController extends ClientStartSceneController {
    @FXML
    private Pane ebooksCategoryPane;

    @FXML
    private Button fantasy, science, sciFi, crime, allEbooks;
    @FXML
    private TableView<ProductTable> productsTableView;

    @FXML
    public void initialize() {
        initializeButtons();
        prepareAllImages();
        setButtonsActions();
        getCategoryButtonsAndPlaceThemInGrid();
    }

    private void prepareAllImages() {
        fantasy.setGraphic(setImageFromIconsFolder("/CategoryIcons/fantasy.png"));
        science.setGraphic(setImageFromIconsFolder("/CategoryIcons/science.png"));
        sciFi.setGraphic(setImageFromIconsFolder("/CategoryIcons/sci-fi.png"));
        crime.setGraphic(setImageFromIconsFolder("/CategoryIcons/crime.png"));

    }

    private void initializeButtons() {
        String cssClassName = "categoryImages";
        fantasy = new Button();
        science = new Button();
        sciFi = new Button();
        crime = new Button();
        fantasy.getStyleClass().add(cssClassName);
        science.getStyleClass().add(cssClassName);
        sciFi.getStyleClass().add(cssClassName);
        crime.getStyleClass().add(cssClassName);
        createAllEbooksButton();
    }

    private void createAllEbooksButton() {
        allEbooks = new Button("All e-books");
        allEbooks.getStyleClass().add("CategoryPickingButtons");
        allEbooks.setMaxWidth(1000);
        allEbooks.setPrefHeight(100);
    }

    private void setButtonsActions() {
        allEbooks.setOnAction(event -> loadPane("allEbooksPaneGUI.fxml"));
        fantasy.setOnMouseClicked(mouseEvent -> {
            prepareNewPane("fantasy books");
        });

    }

    private void getCategoryButtonsAndPlaceThemInGrid() {
        GridPane grid = new GridPane();
        double padding = 70;
        grid.setPadding(new Insets(padding));
        grid.setHgap(padding);
        grid.setVgap(padding);
        grid.add(fantasy, 0, 0);
        grid.add(science, 1, 0);
        grid.add(sciFi, 2, 0);
        grid.add(crime, 3, 0);
        grid.add(allEbooks, 0, 1, 4, 1);
        grid.setLayoutY(30);
        grid.setLayoutX(20);
        ebooksCategoryPane.getChildren().add(grid);
        GridPane.setFillWidth(allEbooks, true);

    }

    private void createSubSceneTitle(String text) {
        Label title = new Label();
        title.setId("titleLabel");
        title.setText(text);
        title.setLayoutY(anchor.getWidth() / 2);
        title.setLayoutX(10);
    }

    private void createTableView() {
        productsTableView = new TableView<>();
        productsTableView.setPlaceholder(new Label("There are no ordered products "));
        productsTableView.setPrefWidth(900);
        TableColumn<ProductTable, String> productName = new TableColumn<>("name");
        productName.setMinWidth(200);
        TableColumn<ProductTable, String> productPrice = new TableColumn<>("price");
        productPrice.setPrefWidth(200);
        TableColumn<ProductTable, String> cartButtonColumn = new TableColumn<>();
        TableColumn<ProductTable, String> starButtonColumn = new TableColumn<>();
        starButtonColumn.setPrefWidth(230);
        cartButtonColumn.setPrefWidth(230);
        productsTableView.getColumns().addAll(productName, productPrice, cartButtonColumn, starButtonColumn);
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        starButtonColumn.setCellValueFactory(buttonInsideCell -> buttonInsideCell.getValue().isProductFavouriteProperty());
        starButtonColumn.setCellFactory(buttonInsideCell -> createStarButtonInsideTableView());
        cartButtonColumn.setCellFactory(buttonInsideCell -> createCartButtonInsideTableView());
        ebooksCategoryPane.getChildren().add(productsTableView);
    }

    private void displayProductsFromCategory(String subcategoryName) throws SQLException {
        ResultSet products = Product.getProductsFromSubcategoryAndInformationIfProductIsInUsersFavouriteFromDatabase(getConnection(), CURRENT_USER_LOGIN, subcategoryName);
        ObservableList<ProductTable> listOfProducts = ProductTable.getProductsFromSubcategoryWithInformationIfTheyAreInUsersFavourite(products);
        productsTableView.setItems(listOfProducts);
        prepareTableView(productsTableView, (TableColumn<?, String>) productsTableView.getColumns().get(1));
        productsTableView.setMaxHeight(250);
    }

    private void prepareNewPane(String subcategoryName) {
        clearPane(ebooksCategoryPane);
        goBackButton.setOnAction(event -> loadPane("ebooksCategoriesGUI.fxml"));
        getProductsFromDbIntoTableView(subcategoryName);
    }

    private void getProductsFromDbIntoTableView(String subcategoryName) {
        checkConnectionWithDb();
        createTableView();
        try {
            displayProductsFromCategory("fantasy books");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
