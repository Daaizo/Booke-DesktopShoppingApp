package users;

import java.sql.*;

public class Client extends User {

    public Client(String un, String ln, String n, String pas) {
        this.login = un;
        this.name = n;
        this.lastName = ln;
        this.password = pas;

    }

    public Client(Integer d, String un, String ln, String n, String p) {
        this.id = d;
        this.login = un;
        this.name = n;
        this.lastName = ln;
        this.password = p;
    }


    public static void addItemToUsersCart(String productName, String CURRENT_USERNAME, Connection connection) throws SQLException {
        // since product name is always unique this is fine
        try {
            String addItemToCart = "insert into shoppingcart (customerkey,productkey)  (" +
                    "    select customerkey, " +
                    "(select productkey from product where productname = '" + productName + "')  from customer where customerlogin = '" + CURRENT_USERNAME + "' )";
            Statement stm = connection.createStatement();
            stm.execute(addItemToCart);
        } catch (SQLIntegrityConstraintViolationException exception) {
            // only 1 way to do that - by adding product that is already in cart
            incrementQuantity("shoppingcart", CURRENT_USERNAME, productName, connection);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void incrementQuantity(String where, String CURRENT_USERNAME, String productName, Connection con) throws SQLException {
        String incrementQuantity = "update  " + where +
                " Set quantity = quantity + 1" +
                " where customerkey = ( select customerkey from customer where customerlogin = '" + CURRENT_USERNAME + "' )" +
                " and productkey = (select productkey from product where productname = '" + productName + "')";
        Statement stm = con.createStatement();
        stm.execute(incrementQuantity);
    }

    public static int getQuantityOfProductsInCart(String CURRENT_USERNAME, Connection connection) throws SQLException {
        String incrementQuantity = "select sum(quantity) from shoppingcart sc" +
                " inner join customer cu on cu.customerkey = sc.customerkey" +
                " where customerlogin = '" + CURRENT_USERNAME + "' ";
        Statement stm = connection.createStatement();
        ResultSet set = stm.executeQuery(incrementQuantity);
        set.next();
        return set.getInt(1);
    }

    public void addUserToDatabase(Connection connection) {
        try {
            String userToDb = "insert into CUSTOMER (customerlogin,customername,customerlastname,customerpassword) values ('"
                    + this.login + "','" + this.name + "', '" + this.lastName + "','" + this.password + "')";
            Statement stm = connection.createStatement();
            stm.execute(userToDb);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void changeData() {

    }
}
