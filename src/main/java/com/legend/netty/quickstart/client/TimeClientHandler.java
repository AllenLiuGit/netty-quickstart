package com.legend.netty.quickstart.client;

import com.legend.netty.quickstart.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by allen on 6/30/16.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    // private final ByteBuf firstMessage;
    private int counter;
    private byte[] req;

    public TimeClientHandler() {
        req = (Constants.CORRECT_QUERY_TIME_ORDER + System.getProperty("line.separator")).getBytes();
        // firstMessage = Unpooled.buffer(req.length);
        // firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        // channelHandlerContext.writeAndFlush(firstMessage);
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            channelHandlerContext.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // ByteBuf req = (ByteBuf)msg;
        // byte[] reqBytes = new byte[req.readableBytes()];
        // req.readBytes(reqBytes);
        // String body = new String(reqBytes, "UTF-8");
        String body = (String)msg;
        System.out.println("Now is: " + body + ", The counter is: " + (++counter));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
