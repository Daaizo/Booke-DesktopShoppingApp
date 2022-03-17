package application.Controllers.Client.Products;

import application.Controllers.Controller;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class EbooksCategoriesController extends Controller {
    @FXML
    private Pane ebooksCategoryPane;


    @FXML
    private ImageView fantasyCategoryImage;

    @FXML
    private ImageView shotCategoryImage;

    @FXML
    public void initialize() {
        fantasyCategoryImage = setImageFromIconsFolder("/CategoryIcons/weapons.png");
        shotCategoryImage = setImageFromIconsFolder("/CategoryIcons/fairy-tale.png");
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

    }
}
