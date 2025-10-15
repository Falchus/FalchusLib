package com.falchus.lib.minecraft.spigot.utils.lunar;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.google.common.collect.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LunarJsonPacketUtil {

	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	public static void sendPacket(Player player, JsonObject message) {
	    player.sendPluginMessage(plugin, "apollo:json", message.toString().getBytes());
	}
	
	public static void broadcastPacket(JsonObject message) {
	    byte[] data = message.toString().getBytes();
	 
	    Bukkit.getOnlinePlayers().forEach(player ->
	        player.sendPluginMessage(plugin, "apollo:json", data));
	}
	
	public static JsonObject createEnableModuleObject(@NonNull String module, Map<String, Object> properties) {
	    JsonObject enableModuleObject = new JsonObject();
	    enableModuleObject.addProperty("apollo_module", module);
	    enableModuleObject.addProperty("enable", true);
	 
	    if (properties != null) {
	        JsonObject propertiesObject = new JsonObject();
	        for (Map.Entry<String, Object> entry : properties.entrySet()) {
	            propertiesObject.add(entry.getKey(), convertToJsonElement(entry.getValue()));
	        }
	 
	        enableModuleObject.add("properties", propertiesObject);
	    }
	 
	    return enableModuleObject;
	}
	 
	private static JsonElement convertToJsonElement(Object value) {
	    if (value == null) {
	        return JsonNull.INSTANCE;
	    } else if (value instanceof String) {
	        return new JsonPrimitive((String) value);
	    } else if (value instanceof Number) {
	        return new JsonPrimitive((Number) value);
	    } else if (value instanceof Boolean) {
	        return new JsonPrimitive((Boolean) value);
	    } else if (value instanceof List) {
	        JsonArray jsonArray = new JsonArray();
	        for (Object item : (List<?>) value) {
	            jsonArray.add(convertToJsonElement(item));
	        }
	        return jsonArray;
	    }
	 
	    throw new RuntimeException("Unable to wrap value of type '" + value.getClass().getSimpleName() + "'!");
	}
	
	public static void enableModules(Player player, List<String> modules, Table<String, String, Object> moduleProperties) {
	    JsonArray settings = modules.stream()
	        .map(module -> createEnableModuleObject(module, moduleProperties.row(module)))
	        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
	 
	    JsonObject message = new JsonObject();
	    message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.configurable.v1.OverrideConfigurableSettingsMessage");
	    message.add("configurable_settings", settings);
	 
	    sendPacket(player, message);
	}
}
