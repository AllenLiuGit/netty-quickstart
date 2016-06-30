package com.legend.netty.quickstart.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by allen on 6/30/16.
 */
public class TimeServer {
    public void bind(int port) throws Exception {
        // 配置服务端NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 用于服务器端接受客户端连接请求的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于进行SocketChannel的网络读写的线程组

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // Netty用于启动NIO服务端的辅助启动类
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            // 绑定监听端口,并同步等待绑定操作成功完成
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            // 等待服务端监听端口关闭,也即服务器端链路关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅退出,释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length >0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                // 采用默认值
            }
        }

        new TimeServer().bind(port);
    }
}
