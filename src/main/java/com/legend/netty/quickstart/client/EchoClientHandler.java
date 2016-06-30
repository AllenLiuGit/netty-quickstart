package com.legend.netty.quickstart.client;

import com.legend.netty.quickstart.common.Constants;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by allen on 6/30/16.
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int counter;
    private static final String ECHO_REQUEST = "Hi, I'm allen." + Constants.DOLLAR_DELIMITER;

    public EchoClientHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        for (int i = 0; i < 10; i++) {
            channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQUEST.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("This is " + (++counter) + " times received from server: [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
