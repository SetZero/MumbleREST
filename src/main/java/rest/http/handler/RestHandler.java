package rest.http.handler;

import io.netty.handler.codec.http.HttpRequest;

public interface RestHandler {
    String handleRequest(HttpRequest request);
}
