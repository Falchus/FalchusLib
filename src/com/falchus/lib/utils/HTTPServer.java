package com.falchus.lib.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.BiConsumer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Utility class to manage a minimal HTTP server.
 */
@AllArgsConstructor
public class HTTPServer {

	@NonNull private final HttpServer server;
	@NonNull private final Map<String, BiConsumer<HttpExchange, Map<String, String>>> routes;
	
	/**
	 * Starts the server.
	 */
	public void start() {
		server.start();
	}
	
	/**
	 * Stops the server after a given delay.
	 */
	public void stop(@NonNull Integer delay) {
		server.stop(delay);
	}

	/**
	 * Sends a plain text response to the client.
	 */
    public static void sendText(@NonNull HttpExchange exchange, @NonNull String text, int statusCode) {
        sendResponse(exchange, text, statusCode, "text/plain; charset=UTF-8");
    }
    
    /**
     * Sends a JSON response to the client.
     */
    public static void sendJson(@NonNull HttpExchange exchange, @NonNull String json, int statusCode) {
        sendResponse(exchange, json, statusCode, "application/json; charset=UTF-8");
    }
    
    private static void sendResponse(@NonNull HttpExchange exchange, @NonNull String content, int statusCode, @NonNull String contentType) {
        try {
            byte[] bytes = content.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
