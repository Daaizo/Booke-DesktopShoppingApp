package users;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // ex. 2022-02-15 10:57:43
    private final String paymentMethodName;
    private final String orderStatusName;
    private final String customerLogin;
    private int orderId;


    public Order(String paymentMethod, String orderStatus, String customerLogin) {
        this.orderStatusName = orderStatus;
        this.paymentMethodName = paymentMethod;
        this.customerLogin = customerLogin;
    }


    public void createOrder(Connection connection) throws SQLException {
        String currentDate = dateFormat.format(new Date());
        String placeOrder = "INSERT INTO orderheader (orderdate, customerkey, paymentmethodkey, orderstatuskey) " +
                "    VALUES " +
                "    (" +
                "       TO_DATE('" + currentDate + "', 'YYYY-MM-DD HH24:MI:SS')," + // date format specified in oracle sql developer
                "        (select cu.customerkey from customer cu where cu.customerlogin = '" + customerLogin + "')," +
                "        (select p.paymentmethodkey from paymentmethod p where p.paymentmethodname = '" + this.paymentMethodName + "')," +
                "        (select os.orderstatuskey from orderstatus os where lower(os.orderstatusname) = '" + this.orderStatusName + "')" +
                "    )";
        Statement sqlStatement = connection.createStatement();
        sqlStatement.executeQuery(placeOrder);
    }

    public void setOrderId(Connection connection) throws SQLException {
        String lastOrderId = "select max(orderheaderkey) from orderheader where customerkey = " +
                "(select customerkey from customer where customerlogin = ? )";
        PreparedStatement preparedStatement = connection.prepareStatement(lastOrderId);
        preparedStatement.setString(1, customerLogin);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        this.orderId = resultSet.getInt(1);
    }

    public void setOrderProducts(Connection connection) throws SQLException {
        String insert = "insert into orderdetail(orderheaderkey,productkey,quantity) (select ?, productkey,quantity from shoppingcart where customerkey = " +
                "(select customerkey from customer where customerlogin = ? ))";
        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setInt(1, this.orderId);
        preparedStatement.setString(2, customerLogin);
        preparedStatement.execute();
    }
}
