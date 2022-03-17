package application.Controllers.Client.AccountDetails;

import application.Controllers.ButtonInsideTableColumn;
import application.Controllers.Client.ClientAccountController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClientFavourites extends ClientAccountController {
    @FXML
    private Pane favouritesPane;
    @FXML
    private TableView<ProductTable> favouritesTableView;
    @FXML
    private TableColumn<ProductTable, String> favouritesNameColumn, favouritesPriceColumn, favouritesSubcategoryColumn, favouritesButtonColumn;

    @FXML
    void addAllFavouritesToCartButtonClicked() {
        ObservableList<ProductTable> list = favouritesTableView.getItems();
        for (ProductTable product : list) {
            try {
                currentUser.addItemToUsersCart(product.getProductName(), getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        showNotification(createNotification(new Label("Items successfully added to cart")), 2500);
        anchor.requestFocus();
    }

    @FXML
    void deleteAllFavouritesButtonClicked() {
        ObservableList<ProductTable> list = favouritesTableView.getItems();
        for (ProductTable product : list) {
            try {
                currentUser.deleteItemFromUsersFavourite(product.getProductName(), getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        showNotification(createNotification(new Label("Items successfully deleted")), 2500);
        displayFavourites();
        anchor.requestFocus();

    }

    private void displayFavourites() {
        checkConnectionWithDb();
        try {
            ResultSet favourites = currentUser.getFavouriteProducts(getConnection());
            ObservableList<ProductTable> listOfFavourites = ProductTable.getProductsBasicInfo(favourites);
            if (listOfFavourites.isEmpty()) {
                displayLabelWithGivenText(emptyTableViewLabel, "List of favourites is empty");
                favouritesPane.setVisible(false);

            } else {
                fillFavouritesColumns(listOfFavourites);
                emptyTableViewLabel.setVisible(false);
                favouritesPane.setVisible(true);
                favouritesPane.setVisible(true);
                showOnlyRowsWithData(favouritesTableView);
                favouritesTableView.setMaxHeight(365);
            }
            favourites.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillFavouritesColumns(ObservableList<ProductTable> listOfOrders) {
        favouritesNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        favouritesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        favouritesSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        favouritesButtonColumn.setCellFactory(cell -> createDeleteFromFavouritesButton());
        favouritesTableView.setItems(listOfOrders);
    }

    private ButtonInsideTableColumn<ProductTable, String> createDeleteFromFavouritesButton() {
        ButtonInsideTableColumn<ProductTable, String> deleteFromFavouritesButton = new ButtonInsideTableColumn<>("delete.png", "delete from favourites");
        deleteFromFavouritesButton.setEventHandler(mouseEvent -> {
            String productName = deleteFromFavouritesButton.getRowId().getProductName();
            Optional<ButtonType> result = deleteProductAlert(productName, "1", "favourites");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    currentUser.deleteItemFromUsersFavourite(productName, getConnection());
                    showNotification(createNotification(new Label("Item removed from favourites")), 2500);
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
