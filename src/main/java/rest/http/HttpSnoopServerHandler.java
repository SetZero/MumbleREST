package rest.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import rest.http.handler.RestHandler;
import rest.http.handler.implementation.DefaultRestHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {
    private ChannelHandlerContext channelHandlerContext;
    private HttpRequest request;
    private RestHandler handler = new DefaultRestHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.channelHandlerContext = ctx;
            this.request = (HttpRequest) msg;
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            send(handler.handleRequest(request));
        }
    }
    private void sendResponse(CharSequence responseStr, HttpResponseStatus httpResponseStatus, String contentType) {
        HttpResponse responseHeaders = new DefaultHttpResponse(request.protocolVersion(), httpResponseStatus);
        responseHeaders.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        ByteBuf responseContent = Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8);
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            responseHeaders.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, responseContent.readableBytes());
            responseHeaders.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        channelHandlerContext.write(responseHeaders);
        channelHandlerContext.write(responseContent);
        ChannelFuture future = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    private void send(String html) {
        final int helloDelay = 1;
        channelHandlerContext.executor().schedule(() -> sendResponse(html, HttpResponseStatus.OK, "text/html; charset=UTF-8"),
                helloDelay, TimeUnit.MILLISECONDS);
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logger.getGlobal().warning("Exception occurred during processing HTTP request/response");
        cause.printStackTrace();
        ctx.close();
    }

}
