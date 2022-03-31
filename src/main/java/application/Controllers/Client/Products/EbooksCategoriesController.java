package application.Controllers.Client.Products;

import application.Controllers.Controller;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class EbooksCategoriesController extends Controller {
    @FXML
    private Pane ebooksCategoryPane;


    @FXML
    private Button fantasy, shotCategoryImage;

    @FXML
    public void initialize() {
        /*

        ImageView shot = setImageFromIconsFolder("/CategoryIcons/science-book.png");
        shotCategoryImage.setLayoutX(400);
        shotCategoryImage.setLayoutY(100);
        shot.setLayoutX(700);
        shot.setLayoutY(100);
        fantasyCategoryImage.setLayoutX(100);
        fantasyCategoryImage.setLayoutY(100);
        fantasyCategoryImage.setVisible(true);
        shotCategoryImage.setVisible(true);
        ebooksCategoryPane.getChildren().addAll(fantasyCategoryImage, shotCategoryImage, shot);

         */
        prepareAllImages();
        setActionsOnclick();
        getCategoryImagesAndPlaceThemInGrid();
    }

    private void prepareAllImages() {
        fantasy = new Button();
        shotCategoryImage = new Button();
        fantasy.setGraphic(setImageFromIconsFolder("/CategoryIcons/fairy-tale.png"));
        shotCategoryImage.setGraphic(setImageFromIconsFolder("/CategoryIcons/weapons.png"));
        fantasy.getStyleClass().add("categoryImages");
        shotCategoryImage.getStyleClass().add("categoryImages");
    }

    private void setActionsOnclick() {
        fantasy.setOnMouseClicked(mouseEvent -> System.out.println("klik fantasy"));

    }

    private void getCategoryImagesAndPlaceThemInGrid() {
        GridPane grid = new GridPane();
        int numberOfImagesInRow = 3;
        double padding = 70;
        grid.setPadding(new Insets(padding));
        grid.setHgap(padding);
        grid.setVgap(padding);
        grid.add(fantasy, 0, 0);
        grid.add(shotCategoryImage, 1, 0);
        ebooksCategoryPane.getChildren().add(grid);
    }
}
