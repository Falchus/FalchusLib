package com.falchus.lib.minecraft.spigot.utils;

import org.bukkit.World;

import com.falchus.lib.minecraft.spigot.enums.GameRule;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for Spigot world-related operations.
 */
@UtilityClass
public class WorldUtils {

	/**
	 * Sets a game rule for the given world.
	 */
	public static void setGameRule(@NonNull World world, @NonNull GameRule gameRule, @NonNull String value) {
		world.setGameRuleValue(gameRule.getKey(), value);
	}
}
