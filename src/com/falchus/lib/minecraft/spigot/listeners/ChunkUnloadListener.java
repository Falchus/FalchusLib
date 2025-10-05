package com.falchus.lib.minecraft.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.utils.WorldUtils;

public class ChunkUnloadListener implements Listener {
	
	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	public ChunkUnloadListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (WorldUtils.loadedChunks.contains(event.getChunk())) {
			event.setCancelled(true);
		}
	}
}
