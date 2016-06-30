package com.legend.netty.quickstart.server;

import com.legend.netty.quickstart.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


/**
 * Created by allen on 6/30/16.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 处理请求消息
        ByteBuf req = (ByteBuf)msg;
        byte[] reqByteArray = new byte[req.readableBytes()];
        req.readBytes(reqByteArray);

        String body = new String(reqByteArray, "UTF-8");
        System.out.println("Time server receive order: " + body);

        // 处理响应消息
        String currentTime = Constants.CORRECT_QUERY_TIME_ORDER.equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : Constants.BAD_QUERY_TIME_ORDER;
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        channelHandlerContext.write(resp); // 异步发送应答消息给客户端
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        channelHandlerContext.close();
    }
}
