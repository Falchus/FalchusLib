package com.falchus.lib.minecraft.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for Minecraft-related API calls.
 */
@UtilityClass
public class APIUtils {

	/**
	 * Retrieves the UUID of a Minecraft player.
	 * 
	 * @param callback	a consumer that receives the {@link UUID} or {@code null} if not found
	 */
    public static UUID getUUID(@NonNull String username) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                String json = sb.toString();

                int index = json.indexOf("\"id\":\"");
                if (index == -1) return null;

                int start = index + 6;
                int end = json.indexOf("\"", start);
                if (end == -1) return null;

                String rawId = json.substring(start, end);
                if (rawId.length() == 32) {
                    String formatted = rawId.replaceFirst(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5"
                    );
                    return UUID.fromString(formatted);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
            	connection.disconnect();
            }
        }
        return null;
    }
    
    /**
     * Retrieves the name of a Minecraft player by UUID.
     * 
	 * @param callback	a consumer that receives the {@link String} or {@code null} if not found
	 */
    public static String getName(@NonNull String uuid) {
        HttpURLConnection connection = null;
        try {
        	uuid = uuid.replace("-", "");
        	
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                String json = sb.toString();

                int index = json.indexOf("\"name\":\"");
                if (index == -1) return null;

                int start = index + 8;
                int end = json.indexOf("\"", start);
                if (end == -1) return null;

                return json.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
            	connection.disconnect();
            }
        }
        return null;
    }
}