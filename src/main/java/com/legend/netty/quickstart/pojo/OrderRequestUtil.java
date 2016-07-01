package com.legend.netty.quickstart.pojo;

/**
 * Created by allen on 7/1/16.
 */
public class OrderRequestUtil {
    public static OrderPojo buildOrderRequest(int orderId, String userName) {
        OrderPojo order = new OrderPojo();
        order.setOrderId(orderId);
        order.setUserName(userName);
        order.setProductName("Netty Quick Start");
        order.setPhoneNumber("138xxxxxxxx");
        order.setAddress("北京市朝阳区");

        return order;
    }
}
