package com.falchus.lib.minecraft.spigot.utils.lunar;

import java.awt.Color;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.google.gson.JsonObject;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LunarJsonObjectUtil {

	public static JsonObject createEnableModuleObjectWithType(@NonNull String module, Map<String, Object> properties) {
	    JsonObject enableModuleObject = LunarJsonPacketUtil.createEnableModuleObject(module, properties);
	    enableModuleObject.addProperty("@type", "type.googleapis.com/lunarclient.apollo.configurable.v1.ConfigurableSettings");
	    return enableModuleObject;
	}
	 
	public static JsonObject createUuidObject(@NonNull UUID uuid) {
	    JsonObject uuidObject = new JsonObject();
	    uuidObject.addProperty("high64", Long.toUnsignedString(uuid.getMostSignificantBits()));
	    uuidObject.addProperty("low64", Long.toUnsignedString(uuid.getLeastSignificantBits()));
	    return uuidObject;
	}
	 
	public static JsonObject createColorObject(@NonNull Color color) {
	    JsonObject colorObject = new JsonObject();
	    colorObject.addProperty("color", color.getRGB());
	    return colorObject;
	}
	 
	public static String createDurationObject(@NonNull Duration duration) {
	    long seconds = duration.getSeconds();
	    int nanos = duration.getNano();
	 
	    String durationString;
	    if (nanos == 0) {
	        durationString = seconds + "s";
	    } else {
	        durationString = String.format("%d.%09ds", seconds, nanos)
	            .replaceAll("0+$", "")
	            .replaceAll("\\.$", "");
	    }
	 
	    return durationString;
	}
	 
	public static JsonObject createCuboid2DObject(double minX, double minZ, double maxX, double maxZ) {
	    JsonObject cuboid2DObject = new JsonObject();
	    cuboid2DObject.addProperty("min_x", minX);
	    cuboid2DObject.addProperty("min_z", minZ);
	    cuboid2DObject.addProperty("max_x", maxX);
	    cuboid2DObject.addProperty("max_z", maxZ);
	    return cuboid2DObject;
	}
	 
	public static JsonObject createEntityIdObject(@NonNull Entity entity) {
	    JsonObject entityIdObject = new JsonObject();
	    entityIdObject.addProperty("entity_id", entity.getEntityId());
	    entityIdObject.add("entity_uuid", createUuidObject(entity.getUniqueId()));
	    return entityIdObject;
	}
}
