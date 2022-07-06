package application.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import users.Product;
import users.ProductTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllProductsController extends AdminStartSceneController {
    @FXML
    private TableColumn<ProductTable, String> productCategoryColumn, productNameColumn, productSubcategoryColumn, productPriceColumn;
    @FXML
    private TableColumn<ProductTable, Integer> productIdColumn;
    @FXML
    private TableView<ProductTable> productTableView;

    @FXML
    private void initialize() {
        try {
            displayProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void displayProducts() throws SQLException {
        checkConnectionWithDb();
        ResultSet products = Product.getProductsFromDatabase(getConnection());
        assert products != null;
        ObservableList<ProductTable> listOfProducts = ProductTable.getProducts(products);
        fillProductColumnsWithData(listOfProducts);
    }

    private void fillProductColumnsWithData(ObservableList<ProductTable> list) {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        productSubcategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productSubcategory"));
        productTableView.setItems(list);
        prepareTableView(productTableView, productPriceColumn);
    }

    @FXML
    private void addProductButtonClicked() {
        createAndShowConfirmAdminPasswordAlert("to enable menu to add new products", () -> {

            Dialog<ButtonType> addProductDialog = createNewDialog();
            ButtonType saveNewProductButton = new ButtonType("save product");
            addProductDialog.getDialogPane().getButtonTypes().addAll(saveNewProductButton, ButtonType.CANCEL);
            centerButtons(addProductDialog.getDialogPane());

            GridPane gridWithAllFields = new GridPane();
            gridWithAllFields.setVgap(10);
            gridWithAllFields.setHgap(10);
            gridWithAllFields.minWidth(1000);
            gridWithAllFields.setPadding(new Insets(20, 150, 10, 10));
            TextField productName = new TextField();
            productName.setPromptText("product name");

            TextField productPrice = new TextField();
            productPrice.setPromptText("product price");
            Label errorText = new Label("");
            errorText.setStyle("-fx-text-background-color : tomato ");
            productName.setMinWidth(600);
            ChoiceBox<String> allCategories = null;
            try {
                allCategories = getAvailableCategories();
                allCategories.setMinWidth(290);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            gridWithAllFields.add(new Label("Product name:"), 0, 0);
            gridWithAllFields.add(productName, 1, 0);
            gridWithAllFields.add(new Label("Product price:"), 0, 1);
            gridWithAllFields.add(productPrice, 1, 1);
            gridWithAllFields.add(new Label("Product category:"), 0, 2);
            gridWithAllFields.add(allCategories, 1, 2, 2, 1);
            gridWithAllFields.add(errorText, 0, 3, 3, 1);

            gridWithAllFields.setAlignment(Pos.CENTER);
            addProductDialog.getDialogPane().setContent(gridWithAllFields);

            final Button btOk = (Button) addProductDialog.getDialogPane().lookupButton(saveNewProductButton);
            ChoiceBox<String> finalAllCategories = allCategories;
            btOk.addEventFilter(ActionEvent.ACTION, event -> {
                        //validation
                        if (productName.getText().isEmpty() || productPrice.getText().isEmpty() || Objects.requireNonNull(finalAllCategories).getValue() == null) {
                            if (productName.getText().isEmpty()) {
                                errorText.setText("Please provide product name");
                                productName.requestFocus();
                            } else if (productPrice.getText().isEmpty() || !productPrice.getText().matches("^\\d+(,\\d{1,2})?$")) {
                                //https://stackoverflow.com/questions/1547574/regex-for-prices
                                errorText.setText("Please provide correct product price. examples : 1,42  10  102,2");
                                productPrice.requestFocus();
                            } else if (Objects.requireNonNull(finalAllCategories).getValue() == null) {
                                errorText.setText("Please select a product category.");
                                finalAllCategories.requestFocus();
                            }
                            event.consume();// stop dialog from closing
                        } else {
                            System.out.println("nazwa :" + productName.getText() + " " + " product price:" + productPrice.getText() + finalAllCategories.getValue());
                        }
                    }
            );
            addProductDialog.showAndWait();
        });
    }


    private Dialog<ButtonType> createNewDialog() {
        Dialog<ButtonType> addProductDialog = new Dialog<>();
        addProductDialog.setHeaderText("Add new product");
        addProductDialog.setContentText("Available categories :  ");
        setLogoAndCssToCustomDialog(addProductDialog);
        return addProductDialog;
    }

    private ChoiceBox<String> getAvailableCategories() throws SQLException {
        List<String> choices = new ArrayList<>();
        ResultSet allCategories = Product.getAllCategories(getConnection());
        while (Objects.requireNonNull(allCategories).next()) {
            choices.add(allCategories.getString(1));
        }
        choices.sort(null); // now on 0 position is the longest name, and it will nicely fit in window
        ObservableList<String> listOfAllCategories = FXCollections.observableArrayList(choices);
        ChoiceBox<String> allChoices = new ChoiceBox<>(listOfAllCategories);
        allChoices.setTooltip(new Tooltip("Pick product category"));
        return allChoices;
    }
}
