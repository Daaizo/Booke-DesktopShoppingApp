package users;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductTable {
    private SimpleIntegerProperty productId;
    private SimpleStringProperty productName;
    private SimpleStringProperty productCategory;
    private SimpleDoubleProperty productPrice;


    public int getProductId() {
        return productId.get();
    }


    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getProductCategory() {
        return productCategory.get();
    }


    public void setProductCategory(String productCategory) {
        this.productCategory.set(productCategory);
    }

    public double getProductPrice() {
        return productPrice.get();
    }


    public void setProductPrice(double productPrice) {
        this.productPrice.set(productPrice);
    }


}
