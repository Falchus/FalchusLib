package com.falchus.lib.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for checking if an IP is a VPN.
 */
@UtilityClass
public class VPNApi {

	/**
	 * Checks if the given IP is a VPN.
	 * 
	 * @param apiUrl	if null or empty, defaults to "https://vpnapi.io/api/{ip}"
	 * @param callback	a consumer that receives {@code true} if the IP is a VPN
	 */
	public static void isVPN(@NonNull String ip, String apiUrl, String apiKey, @NonNull Consumer<Boolean> callback) {
	    String requestUrl = apiUrl;
	    if (requestUrl == null || requestUrl.isEmpty()) {
	        requestUrl = "https://vpnapi.io/api/" + ip;
	        if (apiKey != null && !apiKey.isEmpty()) {
	            requestUrl += "?key=" + apiKey;
	        }
	    }

	    final String finalRequestUrl = requestUrl;

	    Thread.runAsync(() -> {
	        boolean result = false;
	        try {
	            HttpURLConnection connection = (HttpURLConnection) new URL(finalRequestUrl).openConnection();
	            connection.setRequestMethod("GET");
	            connection.setConnectTimeout(5000);
	            connection.setReadTimeout(5000);

	            StringBuilder response = new StringBuilder();
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	                String inputLine;
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	            }

	            String json = response.toString();

	            int idx = json.indexOf("\"vpn\":");
	            String vpnValue = null;
	            if (idx != -1) {
	                int start = idx + 6;
	                int end = json.indexOf(",", start);
	                if (end == -1) {
	                    end = json.indexOf("}", start);
	                }
	                if (end != -1) {
	                    vpnValue = json.substring(start, end).trim();
	                }
	            }
	            result = "true".equals(vpnValue);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        callback.accept(result);
	    });
	}
}
