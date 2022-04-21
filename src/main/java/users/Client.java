package users;

import java.sql.*;

public class Client {
    private String login;
    private String password;
    private String name;
    private String lastName;
    private Integer id;

    public static ResultSet getUsersFromDataBase(Connection connection) {
        try {
            String query = "select * from customer";
            Statement stm = connection.createStatement();
            return stm.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
        }
        return null;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


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

    public Client(String login) {
        this.login = login;
    }

    public static boolean isClientInDataBase(Connection connection, String login) throws SQLException {
        String query = """
                select * from customer where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        boolean isClientInDatabase = preparedStatement.executeQuery().next();
        preparedStatement.close();
        return isClientInDatabase;
    }

    public ResultSet getClientData(Connection connection) throws SQLException {
        String data = """
                select customerlogin, customername, customerlastname,customerpassword from customer where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(data);
        preparedStatement.setString(1, login);
        return preparedStatement.executeQuery();
    }

    public void setClientData(ResultSet data) {
        try {
            if (data.next()) {
                this.login = data.getString(1);
                this.name = data.getString(2);
                this.lastName = data.getString(3);
                this.password = data.getString(4);
                data.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClientName(Connection connection, String newName) throws SQLException {
        String setData = """
                update customer
                    set customername = ?
                    where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(setData);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, login);
        preparedStatement.executeQuery();
        preparedStatement.close();
    }

    public void updateClientLogin(Connection connection, String newLogin) throws SQLException {
        String setData = """
                update customer
                    set customerlogin = ?
                    where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(setData);
        preparedStatement.setString(1, newLogin);
        preparedStatement.setString(2, login);
        preparedStatement.executeQuery();
        preparedStatement.close();
        this.login = newLogin;
    }

    public void updateClientLastName(Connection connection, String newLastName) throws SQLException {
        String setData = """
                update customer
                    set customerLastName = ?
                    where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(setData);
        preparedStatement.setString(1, newLastName);
        preparedStatement.setString(2, login);
        preparedStatement.executeQuery();
        preparedStatement.close();
    }

    public void updateClientPassword(Connection connection, String newPassword) throws SQLException {
        String setData = """
                update customer
                    set customerPassword = ?
                    where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(setData);
        preparedStatement.setString(1, newPassword);
        preparedStatement.setString(2, login);
        preparedStatement.executeQuery();
        preparedStatement.close();
    }

    public void deleteClient(Connection connection) throws SQLException {
        String deleteUser = """
                delete from customer
                     where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(deleteUser);
        preparedStatement.setString(1, login);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deleteWholeCart(Connection connection) throws SQLException {
        String delete = "delete from shoppingcart where customerkey = (select customerkey from customer where customerlogin = ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(delete);
        preparedStatement.setString(1, login);
        preparedStatement.execute();
    }

    public ResultSet getFavouriteProducts(Connection connection) throws SQLException {
        String getItems = """
                select p.productname "Name", p.catalogprice  "Price", sc.subcategoryname "Subcategory" from product p
                    inner join subcategory sc on sc.subcategorykey = p.subcategorykey
                    inner join favourites f on f.productkey = p.productkey
                    inner join customer c on c.customerkey = f.customerkey
                    where c.customerkey = (select customerkey from customer where customerlogin = ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(getItems);
        preparedStatement.setString(1, login);
        return preparedStatement.executeQuery();
    }

    public void addItemToUsersFavourite(String productName, Connection connection) throws SQLException {
        String addItem = """
                insert into favourites (customerkey,productkey)
                    select customerkey,
                    (select productkey from product where productname = ?)
                    from customer
                    where customerlogin = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(addItem);
        preparedStatement.setString(1, productName);
        preparedStatement.setString(2, login);
        preparedStatement.executeQuery();
        preparedStatement.close();
    }

    public void deleteItemFromUsersFavourite(String productName, Connection connection) throws SQLException {
        String deleteItem = """
                delete from favourites
                    where customerkey = ( select customerkey from customer where customerlogin = ?)
                        and productkey = (select productkey from product where productname = ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(deleteItem);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, productName);
        preparedStatement.executeQuery();
        preparedStatement.close();
    }

    public void addItemToUsersCart(String productName, Connection connection) throws SQLException {
        // since product name is always unique this is fine
        try {
            String addItemToCart = "insert into shoppingcart (customerkey,productkey)  (" +
                    "    select customerkey, " +
                    "(select productkey from product where productname = '" + productName + "')  from customer where customerlogin = '" + login + "' )";
            Statement stm = connection.createStatement();
            stm.execute(addItemToCart);
        } catch (SQLIntegrityConstraintViolationException exception) {
            // only 1 way to do that - by adding product that is already in cart
            setQuantityOfProductInCart(productName, "+1", connection);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setQuantityOfProductInCart(String productName, String quantityWithSign, Connection con) throws SQLException {
        String incrementQuantity = "update shoppingcart" +
                " Set quantity = quantity " + quantityWithSign + "" +
                " where customerkey = ( select customerkey from customer where customerlogin = '" + login + "' )" +
                " and productkey = (select productkey from product where productname = '" + productName + "')";
        Statement stm = con.createStatement();
        stm.execute(incrementQuantity);
    }

    public int getQuantityOfProductsInCart(Connection connection) throws SQLException {
        String productsQuantity = "select sum(quantity) from shoppingcart sc" +
                " inner join customer cu on cu.customerkey = sc.customerkey" +
                " where customerlogin = '" + login + "' ";
        Statement stm = connection.createStatement();
        ResultSet set = stm.executeQuery(productsQuantity);
        set.next();
        return set.getInt(1);
    }

    public int getNumberOfOrders(Connection connection) throws SQLException {
        String query = """
                select count( distinct oh.orderheaderkey) from orderheader oh
                where oh.customerkey = ( select customerkey from customer where customerlogin = ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int numberOfOrders = resultSet.getInt(1);
        preparedStatement.close();
        return numberOfOrders;
    }

    public double getTotalValueOfAllOrders(Connection connection) throws SQLException {
        //excluding canceled
        String query = """
                select sum(sq.price) "Total value of orders" from(
                                select oh.orderheaderkey, sum(od.quantity*p.catalogprice) Price,os.orderstatusname  from orderheader oh
                                inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                                inner join product p on p.productkey = od.productkey
                                inner join orderstatus os on os.orderstatuskey = oh.orderstatuskey
                                where oh.customerkey = ( select customerkey from customer where customerlogin = ?)
                                group by oh.orderheaderkey,os.orderstatusname
                                ) sq
                where lower(sq.orderstatusname) != 'canceled'
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        double valueOfAllOrders = resultSet.getDouble(1);
        preparedStatement.close();
        return valueOfAllOrders;
    }

    public int getNumbersOfOrdersWithStatus(String status, Connection connection) throws SQLException {
        String query = """
                select count( distinct oh.orderheaderkey) from orderheader oh
                inner join orderstatus os on os.orderstatuskey = oh.orderstatuskey
                where oh.customerkey = ( select customerkey from customer where customerlogin = ?) and os.orderstatusname = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, status);
        ResultSet resultSet = preparedStatement.executeQuery();
        int numberOfOrders = 0;
        if (resultSet.next()) numberOfOrders = resultSet.getInt(1);
        preparedStatement.close();
        return numberOfOrders;
    }

    public int getQuantityOfProductInCart(String productName, Connection connection) throws SQLException {
        String productQuantity = "select sum(quantity) from shoppingcart sc" +
                " inner join customer cu on cu.customerkey = sc.customerkey" +
                " where customerlogin = '" + login + "' and productkey = (select productkey from product where productname = '" + productName + "')";
        Statement stm = connection.createStatement();
        ResultSet set = stm.executeQuery(productQuantity);
        set.next();
        return set.getInt(1);
    }

    public double getTotalValueOfShoppingCart(Connection connection) throws SQLException {
        String totalValue = """
                    select sum(sc.quantity*p.catalogprice)
                            from shoppingcart sc
                            inner join product p on p.productkey = sc.productkey
                            where sc.customerkey = ( select customerkey from customer where customerlogin = ? )
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(totalValue);
        preparedStatement.setString(1, login);
        ResultSet set = preparedStatement.executeQuery();
        set.next();
        return set.getDouble(1);
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

    public static ResultSet getUserInformationById(Connection connection, int userId) throws SQLException {
        String totalValue = """
                    select * from customer
                        where customerkey =?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(totalValue);
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeQuery();
    }


}
