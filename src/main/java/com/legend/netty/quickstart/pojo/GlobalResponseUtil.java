package com.legend.netty.quickstart.pojo;

import com.legend.netty.quickstart.pojo.GlobalResponsePojo;
import com.legend.netty.quickstart.pojo.OrderPojo;

/**
 * Created by allen on 7/1/16.
 */
public class GlobalResponseUtil {
    public static final int GLB_SUCCESS_RESP_CODE = 0;
    public static final int GLB_FAILED_RESP_CODE = 1;
    public static final String GLB_SUCCESS_RESP_MSG = "We have received your order, will be soon sent to the designated address.";
    public static final String GLB_FAILED_RESP_MSG = "Failed to process your order, because of not correct user.";

    public static GlobalResponsePojo buildGlobalResponse(OrderPojo order, int responseCode, String responseDesc) {
        GlobalResponsePojo response = new GlobalResponsePojo();
        response.setOrderId(order.getOrderId());
        response.setResponseCode(responseCode);
        response.setResponseDesc(responseDesc);

        return response;
    }
}
