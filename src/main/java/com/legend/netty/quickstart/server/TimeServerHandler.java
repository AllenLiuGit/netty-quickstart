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
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 处理请求消息
        // ByteBuf req = (ByteBuf)msg;
        // byte[] reqByteArray = new byte[req.readableBytes()];
        // req.readBytes(reqByteArray);

        // String body = new String(reqByteArray, "UTF-8").substring(0, reqByteArray.length - System.getProperty("line.separator").length());
        String body = (String)msg;
        System.out.println("Time server receive order: " + body + ", the counter is: " + (++counter));

        // 处理响应消息
        String currentTime = Constants.CORRECT_QUERY_TIME_ORDER.equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : Constants.BAD_QUERY_TIME_ORDER;
        // 由于LineBasedFrameDecoder自动删除了回车换行符,因此,如果此处不添加回车换行符,那么,客户端将会得不到任何数据输出
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        channelHandlerContext.writeAndFlush(resp); // 异步发送应答消息给客户端
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
