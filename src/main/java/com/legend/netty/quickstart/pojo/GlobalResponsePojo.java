package com.legend.netty.quickstart.pojo;

import java.io.Serializable;

/**
 * Created by allen on 7/1/16.
 */
public class GlobalResponsePojo implements Serializable {
    private int orderId;
    private int responseCode;
    private String responseDesc;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    @Override
    public String toString() {
        return "GlobalResponsePojo{" +
                "orderId=" + orderId +
                ", responseCode=" + responseCode +
                ", responseDesc='" + responseDesc + '\'' +
                '}';
    }
}
