package com.legend.netty.quickstart.server;

import com.legend.netty.quickstart.common.Constants;
import com.legend.netty.quickstart.pojo.GlobalResponsePojo;
import com.legend.netty.quickstart.pojo.GlobalResponseUtil;
import com.legend.netty.quickstart.pojo.OrderPojo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by allen on 7/1/16.
 */
public class OrderServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        OrderPojo order = (OrderPojo)msg;
        GlobalResponsePojo globalResponse;
        if (Constants.ACCEPTED_USER_NAME.equalsIgnoreCase(order.getUserName())) {
            System.out.println("Server accepted client order: " + order.toString());
            globalResponse = GlobalResponseUtil.buildGlobalResponse(order,
                    GlobalResponseUtil.GLB_SUCCESS_RESP_CODE,
                    GlobalResponseUtil.GLB_SUCCESS_RESP_MSG);

        } else {
            globalResponse = GlobalResponseUtil.buildGlobalResponse(order,
                    GlobalResponseUtil.GLB_FAILED_RESP_CODE,
                    GlobalResponseUtil.GLB_FAILED_RESP_MSG);
        }

        ctx.writeAndFlush(globalResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
