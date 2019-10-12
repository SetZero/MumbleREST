package rest.http.handler.implementation;

import io.netty.handler.codec.http.HttpRequest;
import rest.http.handler.RestHandler;

public class DefaultRestHandler implements RestHandler {
    @Override
    public String handleRequest(HttpRequest request) {
        return "Hello World!";
    }
}
