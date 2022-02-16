package users;

import application.Controllers.Controller;

import java.sql.*;

public class Product {
    private int productId;
    private String productName;
    private String productPrice;
    private String productSubcategory;
    private String productCategory;
    private int productInCartQuantity;


    private String productTotalValue;


    public Product(int id, String name, String price, String subcategory, String category) {
        this.productId = id;
        this.productCategory = category;
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
    }

    public Product(String name, String price, String subcategory) {
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
    }

    public Product(String name, String price, int quantity, String total) {
        this.productName = name;
        this.productPrice = price;
        this.productInCartQuantity = quantity;
        this.productTotalValue = total;
    }


    public static ResultSet getProductsFromDatabase(Connection connection) {
        try {
            String query = """
                    select p.productkey "Id", p.productname "Name", p.catalogprice || ? "Price", sc.subcategoryname "Subcategory", c.categoryname "Category" from product p\s
                    inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                    inner join category c on c.categorykey = sc.categorykey
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Controller.CURRENCY);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet getProductFromCartAndSetValueBasedOnQuantity(Connection connection, String currentUsername) {
        try {
            String query = "select p.productname, p.catalogprice || ? \"PRICE\" ,sc.quantity, sum(p.catalogprice * sc.quantity) ||  ? \"TOTAL\" from shoppingcart  sc\n" +
                    "inner join customer c on c.customerkey = sc.customerkey\n" +
                    "inner join product p on p.productkey = sc.productkey\n" +
                    "where sc.customerkey = \n" +
                    "                        (\n" +
                    "                        select customerkey from customer \n" +
                    "                        where customerlogin = '" + currentUsername + "'\n" +
                    "                        )\n" +
                    "group by  p.productname, p.catalogprice ,sc.quantity";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Controller.CURRENCY);
            preparedStatement.setString(2, Controller.CURRENCY);

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet getPaymentMethods(Connection connection) {
        String paymentMethods = "select * from paymentmethod";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(paymentMethods);
        } catch (SQLException e) {
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


    public int getProductInCartQuantity() {
        return productInCartQuantity;
    }

    public void setProductInCartQuantity(int productInCartQuantity) {
        this.productInCartQuantity = productInCartQuantity;
    }

    public String getProductTotalValue() {
        return this.productTotalValue;
    }

    public void setProductTotalValue(String productTotalValue) {
        this.productTotalValue = productTotalValue;
    }
}
