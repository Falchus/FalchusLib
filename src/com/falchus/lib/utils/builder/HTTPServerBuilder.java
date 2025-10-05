package com.falchus.lib.utils.builder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.falchus.lib.utils.HTTPServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import lombok.NonNull;

/**
 * Builder utility for {@link HTTPServer}.
 */
public class HTTPServerBuilder {
	
	private int port = 8080;
    private final Map<@NonNull String, @NonNull BiConsumer<@NonNull HttpExchange, @NonNull Map<String, String>>> routes = new HashMap<>();
    private BiConsumer<@NonNull HttpExchange, @NonNull Map<String, String>> defaultHandler;
    
    /**
     * Sets the port.
     */
    public HTTPServerBuilder port(@NonNull Integer port) {
    	this.port = port;
    	return this;
    }
    
    /**
     * Adds a route.
     */
    public HTTPServerBuilder route(@NonNull String path, @NonNull BiConsumer<HttpExchange, Map<String, String>> handler) {
        routes.put(path, handler);
        return this;
    }
    
    /**
     * Sets a default handler (used if no routes match).
     */
    @NonNull
    public HTTPServerBuilder defaultHandler(@NonNull BiConsumer<@NonNull HttpExchange, @NonNull Map<String, String>> handler) {
        this.defaultHandler = handler;
        return this;
    }
    
    /**
     * Builds the HTTP server.
     * 
     * @param ip	usually 127.0.0.1
     */
    @NonNull
    public HTTPServer build(@NonNull String ip) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);

            if (routes.isEmpty()) {
                server.createContext("/", exchange -> {
                    Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getRawQuery());
                    if (defaultHandler != null) {
                        defaultHandler.accept(exchange, queryParams);
                    } else {
                        HTTPServer.sendText(exchange, "Server is running", 200);
                    }
                });
            } else {
                for (Map.Entry<String, BiConsumer<HttpExchange, Map<String, String>>> entry : routes.entrySet()) {
                    server.createContext(entry.getKey(), exchange -> {
                        Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getRawQuery());
                        entry.getValue().accept(exchange, queryParams);
                    });
                }
            }

            server.setExecutor(null);
            return new HTTPServer(server, routes);
        } catch (@NonNull IOException e) {
            throw new RuntimeException("Failed to start HTTP server", e);
        }
    }
    
    @NonNull
    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (@NonNull String pair : pairs) {
            String[] kv = pair.split("=", 2);
            try {
                String key = URLDecoder.decode(kv[0], "UTF-8");
                String value = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "";
                map.put(key, value);
            } catch (@NonNull UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}
