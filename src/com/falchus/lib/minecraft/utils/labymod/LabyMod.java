package com.falchus.lib.minecraft.utils.labymod;

import java.util.Collection;
import java.util.UUID;

import com.falchus.lib.minecraft.utils.labymod.enums.LabyBalanceType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.NonNull;

public class LabyMod {

	/**
	 * Just send this packet to update the value of the balance or to show/hide it
	 */
	public static void updateBalanceDisplay(@NonNull UUID uuid, LabyBalanceType type, boolean visible, int balance) {
	    JsonObject economyObject = new JsonObject();
	    JsonObject cashObject = new JsonObject();

	    // Visibility
	    cashObject.addProperty("visible", visible);

	    // Amount
	    cashObject.addProperty("balance", balance);

	    /*
	    // Icon (Optional)
	    cashObject.addProperty("icon", "<url to image>");

	    // Decimal number (Optional)    
	    JsonObject decimalObject = new JsonObject();
	    decimalObject.addProperty("format", "##.##"); // Decimal format
	    decimalObject.addProperty("divisor", 100); // The value that divides the balance
	    cashObject.add("decimal", decimalObject);
	    */

	    // The display type can be "cash" or "bank".
	    economyObject.add(type.getKey(), cashObject);

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(uuid, "economy", economyObject);
	}
	
	public static void setSubtitle(@NonNull UUID receiver, UUID subtitlePlayer, String value) {
	    // List of all subtitles
	    JsonArray array = new JsonArray();

	    // Add subtitle
	    JsonObject subtitle = new JsonObject();
	    subtitle.addProperty("uuid", subtitlePlayer.toString());

	    // Optional: Size of the subtitle
	    subtitle.addProperty("size", 0.8d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)

	    // no value = remove the subtitle
	    if (value != null) {
	        subtitle.addProperty("value", value);
	    }

	    // If you want to use the new text format in 1.16+
	    // subtitle.add("raw_json_text", textObject);

	    // You can set multiple subtitles in one packet
	    array.add(subtitle);

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(receiver, "account_subtitle", array);
	}
	
	public static void sendFlag(@NonNull UUID receiver, UUID uuid, String countryCode) {
	    JsonObject flagPacket = new JsonObject();

	    // Create array
	    JsonArray users = new JsonArray();

	    // Add user to array
	    JsonObject userObject = new JsonObject();
	    userObject.addProperty("uuid", uuid.toString()); // UUID of the flag player
	    userObject.addProperty("code", countryCode); // The country code (e.g. "us", "de")
	    users.add(userObject);

	    // Add array to flag object packet
	    flagPacket.add("users", users);

	    LabyModProtocol.sendMessage(receiver, "language_flag", flagPacket);
	}
	
	public static void setMiddleClickActions(@NonNull UUID uuid, Collection<JsonObject> entries) {
	    // List of all action menu entries
	    JsonArray array = new JsonArray();

	    // Add entries
	    for (JsonObject entry : entries) {
	    	array.add(entry);
	    }

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(uuid, "user_menu_actions", array);
	}
	
	public static void sendCurrentPlayingGamemode(@NonNull UUID uuid, boolean visible, String gamemodeName) {
	    JsonObject object = new JsonObject();
	    object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
	    object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(uuid, "server_gamemode", object);
	}
	
	/**
	 * @param player The input prompt receiver
	 * @param promptSessionId A unique id for each packet, use a static number and increase it for each prompt request
	 * @param message The message above the text field
	 * @param value The value inside of the text field
	 * @param placeholder A placeholder text inside of the text field if there is no value given
	 * @param maxLength Max amount of characters of the text field value
	 */
	public static void sendInputPrompt(@NonNull UUID uuid, int promptSessionId, String message, String value, String placeholder, int maxLength) {
	    JsonObject object = new JsonObject();
	    object.addProperty("id", promptSessionId);
	    object.addProperty("message", message);
	    object.addProperty("value", value);
	    object.addProperty("placeholder", placeholder);
	    object.addProperty("max_length", maxLength);

	    // If you want to use the new text format in 1.16+
	    // object.add("raw_json_text", textObject);

	    LabyModProtocol.sendMessage(uuid, "input_prompt", object);
	}
	
	public static void sendClientToServer(@NonNull UUID uuid, String title, String address, boolean preview) {
	    JsonObject object = new JsonObject();
	    object.addProperty("title", title); // Title of the warning
	    object.addProperty("address", address); // Destination server address
	    object.addProperty("preview", preview); // Display the server icon, motd and user count

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(uuid, "server_switch", object);
	}
	
	/**
	 * Just send this packet to set the cinescope coverage
	 *  0% - Disabled
	 * 50% - Fully blind
	 */
	public void sendCineScope(@NonNull UUID uuid, int coveragePercent, long duration) {
	    JsonObject object = new JsonObject();

	    // Cinescope height (0% - 50%)
	    object.addProperty("coverage", coveragePercent);

	    // Duration
	    object.addProperty("duration", duration);

	    // Send to LabyMod using the API
	    LabyModProtocol.sendMessage(uuid, "cinescopes", object);
	}
}
