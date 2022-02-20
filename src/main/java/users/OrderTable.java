package users;

import javafx.beans.property.SimpleDoubleProperty;
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

    private final SimpleDoubleProperty productPrice;
    private final SimpleIntegerProperty productQuantity;
    private final SimpleDoubleProperty orderTotalValue;
    private final SimpleIntegerProperty orderNumber;

    public OrderTable(Order order) {
        this.orderNumber = new SimpleIntegerProperty(order.getOrderNumber());
        this.orderDate = new SimpleStringProperty(order.getOrderDate());
        this.orderPaymentMethodName = new SimpleStringProperty(order.getPaymentMethodName());
        this.orderStatusName = new SimpleStringProperty(order.getOrderStatusName());
        this.orderDeliveryDate = new SimpleStringProperty(order.getOrderDeliveryDate());
        this.orderProduct = new SimpleStringProperty(order.getOrderProduct());
        this.productPrice = new SimpleDoubleProperty(order.getProductPrice());
        this.productQuantity = new SimpleIntegerProperty(order.getProductQuantity());
        this.orderTotalValue = new SimpleDoubleProperty(order.getOrderTotalValue());
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
                double totalValue = order.getDouble(4);
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

    public static ObservableList<OrderTable> getProductsFromOrder(ResultSet order) {
        ObservableList<OrderTable> listOfProducts = FXCollections.observableArrayList();
        try {
            while (order.next()) {
                int id = order.getInt(1);
                String date = order.getString(2);
                String deliveryDate = order.getString(3);
                double totalValue = order.getDouble(4);
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

    public double getProductPrice() {
        return productPrice.get();
    }


    public void setProductPrice(double productPrice) {
        this.productPrice.set(productPrice);
    }

    public int getProductQuantity() {
        return productQuantity.get();
    }


    public void setProductQuantity(int productQuantity) {
        this.productQuantity.set(productQuantity);
    }

    public double getOrderTotalValue() {
        return orderTotalValue.get();
    }


    public void setOrderTotalValue(double orderTotalValue) {
        this.orderTotalValue.set(orderTotalValue);
    }

    public int getOrderNumber() {
        return orderNumber.get();
    }


    public void setOrderNumber(int orderNumber) {
        this.orderNumber.set(orderNumber);
    }
}
