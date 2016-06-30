package com.legend.netty.quickstart.server;

import com.legend.netty.quickstart.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by allen on 6/30/16.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    int counter;

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 处理请求
        String body = (String)msg;
        System.out.println("This is " + (++counter) + " times received from client: [" + body + "]");

        // 处理响应
        body += Constants.DOLLAR_DELIMITER;
        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
        channelHandlerContext.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
