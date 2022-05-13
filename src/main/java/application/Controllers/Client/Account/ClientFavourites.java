package application.Controllers.Client.Account;

import application.Controllers.ButtonInsideTableColumn;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClientFavourites extends ClientAccountStartSceneController {
    @FXML
    private Pane favouritesPane;
    @FXML
    private TableView<ProductTable> favouritesTableView;
    @FXML
    private TableColumn<ProductTable, String> favouritesNameColumn, favouritesPriceColumn, favouritesSubcategoryColumn, favouritesButtonColumn;

    @FXML
    private void initialize() {
        try {
            displayFavourites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addAllFavouritesToCartButtonClicked() {
        ObservableList<ProductTable> list = favouritesTableView.getItems();
        for (ProductTable product : list) {
            try {
                currentUser.addItemToUsersCart(product.getProductName(), getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        showNotification("Items successfully added to cart");
        favouritesPane.requestFocus();
    }

    @FXML
    private void deleteAllFavouritesButtonClicked() throws SQLException {
        ObservableList<ProductTable> list = favouritesTableView.getItems();
        for (ProductTable product : list) {
            try {
                currentUser.deleteItemFromUsersFavourite(product.getProductName(), getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        showNotification("Items successfully deleted");
        displayFavourites();
        favouritesPane.requestFocus();

    }


    private void setEmptyTableViewLabel() {
        anchor.getChildren().add(emptyTableViewLabel);
        emptyTableViewLabel.setLayoutX(200);
        emptyTableViewLabel.setLayoutY(200);

    }

    private void showOnlyRowsWithData() {
        prepareTableView(favouritesTableView, favouritesPriceColumn);
        favouritesTableView.setMaxHeight(340);//365
    }

    private void displayFavourites() throws SQLException {
        checkConnectionWithDb();
        ResultSet favourites = currentUser.getFavouriteProducts(getConnection());
        ObservableList<ProductTable> listOfFavourites = ProductTable.getProductsBasicInfo(favourites);
        if (listOfFavourites.isEmpty()) {
            displayLabelWithGivenText(emptyTableViewLabel, "List of favourites is empty");
            setEmptyTableViewLabel();
            makeLabelVisible(favouritesPane);

        } else {
            fillFavouritesColumns(listOfFavourites);
            showOnlyRowsWithData();
            makePaneVisible(favouritesPane);
        }
            favourites.close();
    }

    private void fillFavouritesColumns(ObservableList<ProductTable> listOfOrders) {
        favouritesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        favouritesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        favouritesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        favouritesButtonColumn.setCellFactory(cell -> createDeleteFromFavouritesButton());
        favouritesTableView.setItems(listOfOrders);
    }

    private ButtonInsideTableColumn<ProductTable, String> createDeleteFromFavouritesButton() {
        ButtonInsideTableColumn<ProductTable, String> deleteFromFavouritesButton = new ButtonInsideTableColumn<>("Others/delete.png", "delete from favourites");
        deleteFromFavouritesButton.setEventHandler(mouseEvent -> {
            String productName = deleteFromFavouritesButton.getRowId().getProductName();
            Optional<ButtonType> result = deleteProductAlert(productName, "1", "favourites");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    currentUser.deleteItemFromUsersFavourite(productName, getConnection());
                    showNotification("Item removed from favourites");
                    displayFavourites();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteFromFavouritesButton.setCssClassId("clientTableviewButtons");

        return deleteFromFavouritesButton;
    }

}
