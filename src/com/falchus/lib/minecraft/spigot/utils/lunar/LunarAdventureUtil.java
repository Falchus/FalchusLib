package com.falchus.lib.minecraft.spigot.utils.lunar;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class LunarAdventureUtil {

	public static String toJson(@NonNull Component component) {
	    return GsonComponentSerializer.gson().serialize(component);
	}
}
