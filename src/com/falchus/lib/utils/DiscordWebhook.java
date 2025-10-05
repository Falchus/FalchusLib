package com.falchus.lib.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for sending messages to Discord webhooks.
 */
@UtilityClass
public class DiscordWebhook {

	/**
	 * Sends a plain text message.
	 */
	public static void sendMessage(@NonNull String message, @NonNull String webhookUrl) {
	    Thread.runAsync(() -> {
		    try {
		        URL url = new URL(webhookUrl);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	
		        connection.setRequestMethod("POST");
		        connection.setRequestProperty("Content-Type", "application/json");
		        connection.setRequestProperty("User-Agent", "Java-DiscordWebhook/1.0");
		        connection.setDoOutput(true);
	
		        String payload = "{\"content\":\"" + escapeJson(message) + "\"}";
	
		        try (OutputStream outputStream = connection.getOutputStream()) {
		            outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
		            outputStream.flush();
		        }
	
		        int responseCode = connection.getResponseCode();
		        if (responseCode != 204) {
		            System.err.println(responseCode);
		            System.err.println(connection.getResponseMessage());
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	    });
	}

	/**
	 * Sends an embed message.
	 */
    public static void sendEmbedMessage(
			String title, 
			String description, 
			int red, 
			int green, 
			int blue, 
			String footerText, 
			String footerImageUrl, 
			String thumbnailUrl, 
			@NonNull String webhookUrl
    ) {
	    Thread.runAsync(() -> {
	        try {
	            URL url = new URL(webhookUrl);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	
	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	            connection.setRequestProperty("User-Agent", "Java-DiscordWebhook/1.0");
	            connection.setDoOutput(true);
	
	            int color = (red << 16) + (green << 8) + blue;
	
	            StringBuilder payload = new StringBuilder();
	            payload.append("{\"embeds\":[{");
	
	            if (title != null) {
	                payload.append("\"title\":\"").append(escapeJson(title)).append("\",");
	            }
	            if (description != null) {
	                payload.append("\"description\":\"").append(escapeJson(description)).append("\",");
	            }
	            payload.append("\"color\":").append(color);
	
	            if (footerText != null) {
	                payload.append(",\"footer\":{\"text\":\"").append(escapeJson(footerText)).append("\"");
	                if (footerImageUrl != null) {
	                    payload.append(",\"icon_url\":\"").append(footerImageUrl).append("\"");
	                }
	                payload.append("}");
	            }
	
	            if (thumbnailUrl != null) {
	                payload.append(",\"thumbnail\":{\"url\":\"").append(thumbnailUrl).append("\"}");
	            }
	
	            payload.append("}]}");
	
	            try (OutputStream outputStream = connection.getOutputStream()) {
	                outputStream.write(payload.toString().getBytes(StandardCharsets.UTF_8));
	                outputStream.flush();
	            }
	
	            int responseCode = connection.getResponseCode();
	            if (responseCode != 204) {
	                System.err.println(responseCode);
	                System.err.println(connection.getResponseMessage());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
    }

    private static String escapeJson(@NonNull String text) {
        return text.replace("\\", "\\\\")
        		.replace("\"", "\\\"")
        		.replace("\n", "\\n");
    }
}