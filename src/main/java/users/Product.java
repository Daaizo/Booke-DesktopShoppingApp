package users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Product {
    private int productId;
    private String productName;
    private String productPrice;
    private String productSubcategory;
    private String productCategory;

    public Product(int id, String name, String price, String subcategory, String category) {
        this.productId = id;
        this.productCategory = category;
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
    }

    public static ResultSet getProductsFromDatabase(Connection connection) {
        try {
            String query = "select p.productkey \"Id\", p.productname \"Name\", p.catalogprice || ' $' \"Price\", sc.subcategoryname \"Subcategory\", c.categoryname \"Category\" from product p \n" +
                    "inner join subcategory sc on sc.subcategorykey = p.subcategorykey\n" +
                    "inner join category c on c.categorykey = sc.categorykey\n";
            Statement stm = connection.createStatement();
            ResultSet allProducts = stm.executeQuery(query);
            return allProducts;
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
            e.printStackTrace();
        }
        return null;
    }

    public String getProductSubcategory() {
        return productSubcategory;
    }

    public void setProductSubcategory(String productSubcategory) {
        this.productSubcategory = productSubcategory;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
