package com.legend.netty.quickstart.client;

import com.legend.netty.quickstart.pojo.OrderRequestUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by allen on 7/1/16.
 */
public class OrderClientHandler extends ChannelHandlerAdapter {
    public OrderClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        for (int i = 0; i < 10; i++) {
            channelHandlerContext.write(OrderRequestUtil.buildOrderRequest(i, "Allen"));
        }

        channelHandlerContext.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("Received from server: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
