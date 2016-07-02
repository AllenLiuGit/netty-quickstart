package com.legend.netty.quickstart.server;

import com.legend.netty.quickstart.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * Created by allen on 7/1/16.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    /**
     * 发送错误响应
     * @param channelHandlerContext
     * @param httpResponseStatus
     */
    private void sendError(ChannelHandlerContext channelHandlerContext, HttpResponseStatus httpResponseStatus) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                httpResponseStatus,
                Unpooled.copiedBuffer("Failure: " + httpResponseStatus.toString() + "\r\n", CharsetUtil.UTF_8));
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        channelHandlerContext.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 解析URI并验证
     * @param uri
     * @return
     */
    private String sanitizeUri(String uri) {
        // 解码
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException ex1) {
                throw new Error("Decode uri with error");
            }
        }

        // 必须以url开头,限制访问权限
        if (!uri.startsWith(this.url)) {
            return null;
        }

        if (!uri.startsWith("/")) {
            return null;
        }

        // 替换和操作系统相关的目录分隔符
        uri = uri.replace('/', File.separatorChar);

        // 验证特殊字符
        if (uri.contains(File.separator + ".")
                || uri.contains("." + File.separator)
                || uri.startsWith(".")
                || uri.endsWith(".")
                || Constants.INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        // System.out.println(System.getProperty("user.dir")); // "/Users/allen/Customize/Share/Git/netty-quickstart"
        return System.getProperty("user.dir") + File.separator + uri;
    }

    /**
     * 发送文件列表
     * @param channelHandlerContext
     * @param dir
     */
    private void sendListing(ChannelHandlerContext channelHandlerContext, File dir) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        // 组装响应HTML文本
        StringBuilder content = new StringBuilder();
        String dirPath = dir.getPath();
        content.append("<!DOCTYPE html>\r\n");
        content.append("<html><head><title>");
        content.append(dirPath);
        content.append("Directory: ");
        content.append("</title></head><body>\r\n");
        content.append("<h3>");
        content.append(dirPath);
        content.append(" Directory: ");
        content.append("</h3>\r\n");
        content.append("<ul>");
        content.append("<li>Link: <a href=\"../\">..</a></li>\r\n");
        for (File file : dir.listFiles()) {
            if (file.isHidden() || !file.canRead()) {
                continue;
            }

            String fileName = file.getName();
            if (!Constants.ALLOWED_FILE_NAME.matcher(fileName).matches()) {
                continue;
            }

            content.append("<li>Link: <a href=\"");
            content.append(fileName);
            content.append("\">");
            content.append(fileName);
            content.append("</a></li>\r\n");
        }
        content.append("</ul></body></html>\r\n");

        // 发送响应
        ByteBuf buffer = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        fullHttpResponse.content().writeBytes(buffer);
        buffer.release();
        channelHandlerContext.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 重定向到新的地址
     * @param channelHandlerContext
     * @param newUri
     */
    private void sendRedirect(ChannelHandlerContext channelHandlerContext, String newUri) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        fullHttpResponse.headers().set(HttpHeaderNames.LOCATION, newUri);
        channelHandlerContext.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 设置内容类型
     * @param httpResponse
     * @param file
     */
    private void setContentType(HttpResponse httpResponse, File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file));
    }

    /**
     * 设置内容长度
     * @param httpResponse
     * @param contentLenght
     */
    private void setContentLength(HttpResponse httpResponse, long contentLenght) {
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(contentLenght));
    }

    private boolean isKeepAlive(HttpRequest httpRequest) {
        return HttpHeaderValues.KEEP_ALIVE.equals(httpRequest.headers().get(HttpHeaderNames.CONNECTION));
    }

    /**
     * 接收并处理消息
     * @param channelHandlerContext
     * @param fullHttpRequest
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 解析结果
        if (!fullHttpRequest.decoderResult().isSuccess()) {
            this.sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        // 请求方法
        if (!HttpMethod.GET.equals(fullHttpRequest.method())) {
            this.sendError(channelHandlerContext, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        // URI解析
        final  String uri = fullHttpRequest.uri();
        final String path = sanitizeUri(uri);
        if (path == null) {
            this.sendError(channelHandlerContext, HttpResponseStatus.FORBIDDEN);
            return;
        }

        // 读取文件
        File file = new File(path);

        // 限制不能读取隐藏文件,并且如果文件不存在,也不能读取
        if (file.isHidden() || !file.exists()) {
            this.sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 处理目录
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                this.sendListing(channelHandlerContext, file); // 发送文件列表
            } else {
                this.sendRedirect(channelHandlerContext, uri + "/");
            }
            return;
        }

        // 如果既不是目录,又不是文件,则拒绝访问
        if (!file.isFile()) {
            this.sendError(channelHandlerContext, HttpResponseStatus.FORBIDDEN);
        }

        // 随机访问文件
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r"); // 只读方式打开文件
        } catch (FileNotFoundException ex) {
            this.sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 设置响应
        long fileLength = randomAccessFile.length();
        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.setContentLength(httpResponse, fileLength);
        this.setContentType(httpResponse, file);
        if (this.isKeepAlive(fullHttpRequest)) {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        channelHandlerContext.write(httpResponse);

        // 写入内容
        ChannelFuture sendFileChannelFuture = channelHandlerContext.write(
                new ChunkedFile(randomAccessFile, 0, fileLength, 8192), channelHandlerContext.newProgressivePromise()
        );
        sendFileChannelFuture.addListener(new ChannelProgressiveFutureListener() {
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long progress, long total) throws Exception {
                if (total < 0) { // total unknow
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress: " + progress + "/" + total);
                }
            }

            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                System.out.println("Transfer complete.");
            }
        });

        // 写入结尾内容
        ChannelFuture lastContentChannelFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (this.isKeepAlive(fullHttpRequest)) {
            lastContentChannelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        if (channelHandlerContext.channel().isActive()) {
            this.sendError(channelHandlerContext, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
