package com.falchus.lib.minecraft.spigot.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.enums.GameRule;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for Spigot world-related operations.
 */
@UtilityClass
public class WorldUtils {
	
	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	public static final List<Chunk> loadedChunks = new ArrayList<>();

	/**
	 * Sets a game rule for the given world.
	 */
	public static void setGameRule(@NonNull World world, @NonNull GameRule gameRule, @NonNull String value) {
		world.setGameRuleValue(gameRule.getKey(), value);
	}
	
	/**
	 * Loads chunks in the given world for the given radius.
	 */
	public static void loadChunks(@NonNull World world, int radius, boolean store) {
        int centerX = world.getSpawnLocation().getBlockX();
        int centerZ = world.getSpawnLocation().getBlockZ();

        new BukkitRunnable() {
            int x = centerX - radius;
            int z = centerZ - radius;

            @Override
            public void run() {
                if (x > centerX + radius) {
                    cancel();
                    return;
                }

                Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
                if (!chunk.isLoaded()) {
                	chunk.load(true);
                }

                if (store) {
                	loadedChunks.add(chunk);
                }

                z += 16;
                if (z > centerZ + radius) {
                    z = centerZ - radius;
                    x += 16;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
	}
}
