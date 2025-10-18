package com.falchus.lib.minecraft.spigot.utils;

import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import com.falchus.lib.minecraft.spigot.enums.GameRule;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.WorldChunkManager;
import net.minecraft.server.v1_8_R3.WorldServer;

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
	
	/**
	 * Gets world biomes.
	 */
	public static BiomeBase[] getWorldBiomes(World world) {
		try {
			WorldServer nmsWorld = ((CraftWorld) world).getHandle();
			WorldChunkManager worldChunkManager = nmsWorld.getWorldChunkManager();
			
			Field biomesField = WorldChunkManager.class.getDeclaredField("biomes");
			biomesField.setAccessible(true);
			
			return (BiomeBase[]) biomesField.get(worldChunkManager);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
