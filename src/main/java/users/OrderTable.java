package users;

import application.Controllers.Controller;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderTable {
    private final SimpleStringProperty orderPaymentMethodName;
    private final SimpleStringProperty orderStatusName;
    private final SimpleStringProperty orderDate;
    private final SimpleStringProperty orderDeliveryDate;
    private final SimpleStringProperty orderProduct;
    private final SimpleStringProperty orderTotalValue;
    private final SimpleStringProperty productPrice;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty productQuantity;
    private final SimpleIntegerProperty orderNumber;
    private final SimpleIntegerProperty customerId;

    public OrderTable(Order order) {
        this.orderNumber = new SimpleIntegerProperty(order.getOrderNumber());
        this.customerId = new SimpleIntegerProperty(order.getCustomerId());
        this.orderDate = new SimpleStringProperty(order.getOrderDate());
        this.orderPaymentMethodName = new SimpleStringProperty(order.getPaymentMethodName());
        this.orderStatusName = new SimpleStringProperty(order.getOrderStatusName());
        this.orderDeliveryDate = new SimpleStringProperty(order.getOrderDeliveryDate());
        this.orderProduct = new SimpleStringProperty(order.getOrderProduct());
        this.productPrice = new SimpleStringProperty(order.getProductPrice());
        this.productQuantity = new SimpleIntegerProperty(order.getProductQuantity());
        this.orderTotalValue = new SimpleStringProperty(order.getOrderTotalValue());
        this.productName = new SimpleStringProperty(order.getProductName());
    }

    public static ObservableList<OrderTable> getAllOrdersFromDb(ResultSet order) {
        ObservableList<OrderTable> listOfProducts = FXCollections.observableArrayList();
        try {
            while (order.next()) {
                int id = order.getInt(1);
                int customerId = order.getInt(2);
                String date = order.getString(3);
                String deliveryDate;
                if (order.getString(4) == null) {
                    deliveryDate = "-";
                } else {
                    deliveryDate = order.getString(4);
                }
                String orderStatus = order.getString(5);
                String paymentMethod = order.getString(6);
                String totalValue = order.getDouble(7) + Controller.CURRENCY;
                listOfProducts.add(new OrderTable(new Order(id, customerId, date, deliveryDate, totalValue, paymentMethod, orderStatus)));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        return listOfProducts;
    }

    public static ObservableList<OrderTable> getOrders(ResultSet order) {
        ObservableList<OrderTable> listOfProducts = FXCollections.observableArrayList();
        try {
            while (order.next()) {
                int id = order.getInt(1);
                String date = order.getString(2);
                String deliveryDate;
                if (order.getString(3) == null) {
                    deliveryDate = "-";
                } else {
                    deliveryDate = order.getString(3);
                }
                String totalValue = order.getDouble(4) + Controller.CURRENCY;
                String paymentMethod = order.getString(5);
                String orderStatus = order.getString(6);
                listOfProducts.add(new OrderTable(new Order(id, date, deliveryDate, totalValue, paymentMethod, orderStatus)));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        return listOfProducts;
    }

    public static ObservableList<OrderTable> getOrderBasicInformation(ResultSet order) {
        ObservableList<OrderTable> listOfProducts = FXCollections.observableArrayList();
        try {
            while (order.next()) {
                int id = order.getInt(1);
                String totalValue = order.getDouble(2) + Controller.CURRENCY;
                String paymentMethod = order.getString(3);
                String orderStatus = order.getString(4);
                listOfProducts.add(new OrderTable(new Order(id, totalValue, paymentMethod, orderStatus)));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        return listOfProducts;
    }

    public static ObservableList<OrderTable> getProductsFromOrder(ResultSet order) {
        ObservableList<OrderTable> listOfProducts = FXCollections.observableArrayList();
        try {
            while (order.next()) {
                String productName = order.getString(1);
                String price = order.getString(2) + Controller.CURRENCY;
                int quantity = order.getInt(3);
                String totalValue = order.getString(4) + Controller.CURRENCY;
                listOfProducts.add(new OrderTable(new Order(productName, quantity, price, totalValue)));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return listOfProducts;
    }

    public SimpleStringProperty orderStatusNameProperty() {
        return orderStatusName;
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public String getOrderPaymentMethodName() {
        return orderPaymentMethodName.get();
    }

    public void setOrderPaymentMethodName(String orderPaymentMethodName) {
        this.orderPaymentMethodName.set(orderPaymentMethodName);
    }

    public String getOrderStatusName() {
        return orderStatusName.get();
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName.set(orderStatusName);
    }

    public String getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(String orderDate) {
        this.orderDate.set(orderDate);
    }

    public String getOrderDeliveryDate() {
        return orderDeliveryDate.get();
    }

    public void setOrderDeliveryDate(String orderDeliveryDate) {
        this.orderDeliveryDate.set(orderDeliveryDate);
    }

    public String getOrderProduct() {
        return orderProduct.get();
    }

    public void setOrderProduct(String orderProduct) {
        this.orderProduct.set(orderProduct);
    }

    public String getProductPrice() {
        return productPrice.get();
    }


    public void setProductPrice(String productPrice) {
        this.productPrice.set(productPrice);
    }

    public int getProductQuantity() {
        return productQuantity.get();
    }


    public void setProductQuantity(int productQuantity) {
        this.productQuantity.set(productQuantity);
    }

    public String getOrderTotalValue() {
        return orderTotalValue.get();
    }


    public void setOrderTotalValue(String orderTotalValue) {
        this.orderTotalValue.set(orderTotalValue);
    }

    public int getOrderNumber() {
        return orderNumber.get();
    }


    public void setOrderNumber(int orderNumber) {
        this.orderNumber.set(orderNumber);
    }

    public String getProductName() {
        return productName.get();
    }


}
