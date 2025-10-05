package com.falchus.lib.minecraft.spigot.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;

public class VanishListener implements Listener {

	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	public final Set<UUID> players = new HashSet<>();
	
	public VanishListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (players.contains(onlinePlayer.getUniqueId())) {
				player.hidePlayer(onlinePlayer);
			} else {
				player.showPlayer(onlinePlayer);
			}
		}
	}
}
