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

public class EbooksCategoriesController extends ClientStartSceneController {
    @FXML
    private Pane ebooksCategoryPane, chosenCategoryPane;
    @FXML
    private Button fantasy, science, sciFi, crime, allEbooks;
    @FXML
    private TableView<ProductTable> productsTableView;

    @FXML
    private void initialize() {
        initializeButtons();
        prepareAllImages();
        setButtonsActions();
        getCategoryButtonsAndPlaceThemInGrid();
        displayPane("ebooksCategoryPane");
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

    private void prepareNewPane(String subcategoryName, String sceneTitle) {
        clearPane(chosenCategoryPane);
        displayPane("chosenCategoryPane");
        goBackButton.setOnAction(event -> {
            displayPane("ebooksCategoryPane");
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
        allEbooks.setOnAction(event -> {
            loadFXMLAndInitializeController("/ClientSceneFXML/ProductsFXML/allEbooksPaneGUI.fxml", chosenCategoryPane);
            displayPane("chosenCategoryPane");
            goBackButton.setOnAction(e -> displayPane("ebooksCategoryPane"));
        });
        fantasy.setOnMouseClicked(mouseEvent -> prepareNewPane("fantasy e-books", "FANTASY E-BOOKS"));
        crime.setOnMouseClicked(mouseEvent -> prepareNewPane("crime e-books", "CRIME E-BOOKS"));
        sciFi.setOnMouseClicked(mouseEvent -> prepareNewPane("sc-fi e-books", "SC-FI E-BOOKS"));
        science.setOnMouseClicked(mouseEvent -> prepareNewPane("science e-books", "SCIENCE E-BOOKS"));
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
            ebooksCategoryPane.setVisible(false);
        } else {
            goBackButton.setOnAction(event -> switchScene(event, clientScene));
            chosenCategoryPane.setVisible(false);
            ebooksCategoryPane.setVisible(true);
        }
    }
}
