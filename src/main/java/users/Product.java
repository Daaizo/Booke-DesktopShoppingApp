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
    private String isFavourite;

    private String productTotalValue;


    public Product(int id, String name, String price, String subcategory, String category) {
        this.productId = id;
        this.productCategory = category;
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
    }

    public Product(String name, String price, String subcategory, String isFavourite) {
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
        this.isFavourite = isFavourite;
    }

    public Product(String name, String price, String subcategory, String category, String isFavourite) {
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
        this.productCategory = category;
        this.isFavourite = isFavourite;
    }

    public Product(String name, String price, int quantity, String total) {
        this.productName = name;
        this.productPrice = price;
        this.productInCartQuantity = quantity;
        this.productTotalValue = total;
    }

    public Product(String name, String price, String subcategory) {
        this.productName = name;
        this.productPrice = price;
        this.productSubcategory = subcategory;
    }

    public Product(int id, String name, String price, String isFavourite) {
        this.productId = id;
        this.productName = name;
        this.productPrice = price;
        this.isFavourite = isFavourite;
    }

    public static ResultSet getAllProductsOrderedByUser(Connection connection, String currentUserLogin) throws SQLException {
        String allProducts = """
                select distinct p.productname,p.catalogprice,sc.subcategoryname from orderheader oh
                  inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                  inner join product p on p.productkey = od.productkey
                  inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                  where oh.customerkey = ( select customerkey from customer where customerlogin = ?)
                  order by 1
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(allProducts);
        preparedStatement.setString(1, currentUserLogin);
        return preparedStatement.executeQuery();

    }

    public static ResultSet getAllProductsAndInformationIfProductIsInUsersFavouriteFromDatabase(Connection connection, String currentUserLogin) throws SQLException {
        String sql = """
                select p.productkey "Id", p.productname "Name", p.catalogprice  "Price", sc.subcategoryname "Subcategory", c.categoryname "Category",
                case
                    when (select customerkey from customer where customerlogin  = ?) = f.customerkey  then 'yes'
                    else 'no'
                end as "is favourite"
                from product p
                                    inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                                    inner join category c on c.categorykey = sc.categorykey
                                    left join favourites f on f.productkey = p.productkey and f.customerkey = (select customerkey from customer where customerlogin  = ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, currentUserLogin);
        preparedStatement.setString(2, currentUserLogin);
        return preparedStatement.executeQuery();
    }

    public static ResultSet getProductsFromCategoryAndInformationIfProductIsInUsersFavouriteFromDatabase(Connection connection, String currentUserLogin, String categoryName) throws SQLException {
        String sql = """
                select p.productkey "Id", p.productname "Name", p.catalogprice  "Price", sc.subcategoryname "Subcategory",
                                                case
                                                    when  (select customerkey from customer where customerlogin  = ?) = f.customerkey  then 'yes'
                                                    else 'no'
                                                end as "is favourite"
                                from product p
                                    inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                                    inner join category c on c.categorykey = sc.categorykey
                                    full join favourites f on f.productkey = p.productkey and f.customerkey = (select customerkey from customer where customerlogin  = ?)
                                    where c.categoryname = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, currentUserLogin);
        preparedStatement.setString(2, currentUserLogin);
        preparedStatement.setString(3, categoryName);
        return preparedStatement.executeQuery();
    }

    public static ResultSet getProductsFromSubcategoryAndInformationIfProductIsInUsersFavouriteFromDatabase(Connection connection, String currentUserLogin, String subcategory) throws SQLException {
        String sql = """
                select p.productkey "Id", p.productname "Name", p.catalogprice  "Price",
                                               case
                                                   when (select customerkey from customer where customerlogin  = ?) = f.customerkey  then 'yes'
                                                   else 'no'
                                               end as "is favourite"
                from product p
                    inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                    full join favourites f on f.productkey = p.productkey
                    where sc.subcategoryname = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, currentUserLogin);
        preparedStatement.setString(2, subcategory);
        return preparedStatement.executeQuery();
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
            String query = """
                    select p.productname, p.catalogprice   ,sc.quantity, sum(p.catalogprice * sc.quantity) from shoppingcart  sc
                            inner join customer c on c.customerkey = sc.customerkey
                            inner join product p on p.productkey = sc.productkey
                            where sc.customerkey =
                                                    (    select customerkey from customer     where customerlogin = ?  )
                            group by  p.productname, p.catalogprice ,sc.quantity
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, currentUsername);
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

    public String isFavourite() {
        return isFavourite;
    }

    public Product setFavourite(String favourite) {
        isFavourite = favourite;
        return this;
    }

    public String getProductTotalValue() {
        return this.productTotalValue;
    }

    public void setProductTotalValue(String productTotalValue) {
        this.productTotalValue = productTotalValue;
    }
}
