package users;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // ex. 2022-02-15 10:57:43
    private String paymentMethodName;
    private String orderStatusName;
    private String customerLogin;
    private String orderDate;
    private String orderTotalValue;
    private String orderDeliveryDate;
    private String productName;
    private String orderProduct;
    private String productPrice;
    private int productQuantity;
    private int orderNumber;
    private int customerId;

    public Order(int id, int customerId, String date, String deliveryDate, String totalValue, String paymentMethod, String status) {
        this.orderNumber = id;
        this.customerId = customerId;
        this.orderDeliveryDate = deliveryDate;
        this.orderDate = date;
        this.orderTotalValue = totalValue;
        this.paymentMethodName = paymentMethod;
        this.orderStatusName = status;
    }

    public Order(int id, String date, String deliveryDate, String totalValue, String paymentMethod, String status) {
        this.orderNumber = id;
        this.orderDeliveryDate = deliveryDate;
        this.orderDate = date;
        this.orderTotalValue = totalValue;
        this.paymentMethodName = paymentMethod;
        this.orderStatusName = status;
    }

    public Order(String productName, int quantity, String price, String totalValue) {
        this.productName = productName;
        this.productQuantity = quantity;
        this.productPrice = price;
        this.orderTotalValue = totalValue;
    }

    public Order(int orderNumber, String totalValue, String paymentMethodName, String orderStatusName) {
        this.orderNumber = orderNumber;
        this.orderTotalValue = totalValue;
        this.paymentMethodName = paymentMethodName;
        this.orderStatusName = orderStatusName;
    }

    public Order(String paymentMethod, String orderStatus, String customerLogin) {
        this.orderStatusName = orderStatus;
        this.paymentMethodName = paymentMethod;
        this.customerLogin = customerLogin;
    }

    public Order(String customerLogin) {
        this.customerLogin = customerLogin;
    }

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public static ResultSet getAllOrders(Connection connection) throws SQLException {
        String getAllOrdersBasicInformation = """
                SELECT oh.orderheaderkey, customerkey, orderdate, deliverydate,os.orderstatusname,p.paymentmethodname,sum(od.quantity * pr.catalogprice)from orderheader oh
                    inner join orderstatus os on os.orderstatuskey = oh.orderstatuskey
                    inner join paymentmethod p on p.paymentmethodkey = oh.paymentmethodkey
                    inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                    inner join product pr on pr.productkey = od.productkey
                    group by oh.orderheaderkey,customerkey, orderdate, deliverydate,os.orderstatusname,p.paymentmethodname
                """;
        Statement sqlStatement = connection.createStatement();
        return sqlStatement.executeQuery(getAllOrdersBasicInformation);

    }

    public void createOrder(Connection connection) throws SQLException {
        String currentDate = dateFormat.format(new Date());
        String placeOrder = "INSERT INTO orderheader (orderdate, customerkey, paymentmethodkey, orderstatuskey) " +
                "    VALUES " +
                "    (" +
                "       TO_DATE('" + currentDate + "', 'YYYY-MM-DD HH24:MI:SS')," + // date format specified in oracle sql developer
                "        (select cu.customerkey from customer cu where cu.customerlogin = '" + customerLogin + "')," +
                "        (select p.paymentmethodkey from paymentmethod p where p.paymentmethodname = '" + paymentMethodName + "')," +
                "        (select os.orderstatuskey from orderstatus os where lower(os.orderstatusname) = '" + orderStatusName + "')" +
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
        orderNumber = resultSet.getInt(1);
        resultSet.close();
    }

    public void setOrderProducts(Connection connection) throws SQLException {
        String insert = "insert into orderdetail(orderheaderkey,productkey,quantity) (select ?, productkey,quantity from shoppingcart where customerkey = " +
                "(select customerkey from customer where customerlogin = ? ))";
        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setInt(1, orderNumber);
        preparedStatement.setString(2, customerLogin);
        preparedStatement.execute();
    }

    public ResultSet getOrdersFromCustomer(Connection connection) throws SQLException {
        String getOrders = """
                select oh.orderheaderkey "Order number",oh.orderdate "Order Time",oh.deliverydate "Delivery date",
                    sum(p.catalogprice * od.quantity) "Total value", pm.paymentmethodname "Payment method", os.orderstatusname "Order status"
                     from orderheader oh
                    inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                    inner join product p on p.productkey = od.productkey
                    inner join paymentmethod pm on pm.paymentmethodkey = oh.paymentmethodkey
                    inner join orderstatus os on os.orderstatuskey = oh.orderstatuskey
                    where oh.customerkey =
                                            (select customerkey from customer where customerlogin = ?)
                    group by oh.orderheaderkey ,oh.orderdate ,oh.deliverydate,pm.paymentmethodname , os.orderstatusname
                    order by 1 desc
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(getOrders);
        preparedStatement.setString(1, customerLogin);
        return preparedStatement.executeQuery();
    }

    public ResultSet getOrderDetailedInformation(Connection connection, int orderNumber) throws SQLException {
        String getOrders = """
                select oh.orderheaderkey "Order number",sum(p.catalogprice * od.quantity) "Total value",
                                   pm.paymentmethodname "Payment method", os.orderstatusname "Order status"
                                    from orderheader oh
                                   inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                                   inner join product p on p.productkey = od.productkey
                                   inner join paymentmethod pm on pm.paymentmethodkey = oh.paymentmethodkey
                                   inner join orderstatus os on os.orderstatuskey = oh.orderstatuskey
                                   where oh.customerkey =
                                                           (select customerkey from customer where customerlogin = ?)
                                                           and oh.orderheaderkey = ?
                                   group by oh.orderheaderkey ,oh.orderdate ,oh.deliverydate,pm.paymentmethodname , os.orderstatusname
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(getOrders);
        preparedStatement.setString(1, customerLogin);
        preparedStatement.setInt(2, orderNumber);
        return preparedStatement.executeQuery();
    }

    public ResultSet getOrderProducts(Connection connection, int orderNumber) throws SQLException {
        String getOrder = """
                select  p.productname "Product",p.catalogprice "Price", od.quantity "Quantity",(p.catalogprice * od.quantity) "Total value"
                    from orderheader oh
                    inner join orderdetail od on od.orderheaderkey = oh.orderheaderkey
                    inner join product p on p.productkey = od.productkey
                    where oh.orderheaderkey = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(getOrder);
        preparedStatement.setInt(1, orderNumber);
        return preparedStatement.executeQuery();

    }

    public void setOrderStatus(Connection connection, String orderStatus) throws SQLException {
        String changeStatus = """
                update orderheader
                    set orderstatuskey = ( select orderstatuskey from orderstatus where orderstatusname = ?)
                    where orderheaderkey = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(changeStatus);
        preparedStatement.setString(1, orderStatus);
        preparedStatement.setInt(2, orderNumber);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void setPaymentMethod(Connection connection, String paymentMethodName) throws SQLException {
        String changeStatus = """
                update orderheader
                    set paymentmethodkey = ( select paymentmethodkey from paymentmethod where paymentmethodname = ?)
                    where orderheaderkey = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(changeStatus);
        preparedStatement.setString(1, paymentMethodName);
        preparedStatement.setInt(2, orderNumber);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void deleteOrder(Connection connection, int orderNumber) throws SQLException {
        String deleteOrder = """
                delete orderheader where orderheaderkey = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(deleteOrder);
        preparedStatement.setInt(1, orderNumber);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    public String getCustomerLogin() {
        return customerLogin;
    }

    public void setCustomerLogin(String customerLogin) {
        this.customerLogin = customerLogin;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderDeliveryDate() {
        return orderDeliveryDate;
    }

    public void setOrderDeliveryDate(String orderDeliveryDate) {
        this.orderDeliveryDate = orderDeliveryDate;
    }

    public String getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(String orderProduct) {
        this.orderProduct = orderProduct;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getOrderTotalValue() {
        return orderTotalValue;
    }

    public void setOrderTotalValue(String orderTotalValue) {
        this.orderTotalValue = orderTotalValue;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductName() {
        return productName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Order setCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }
}

