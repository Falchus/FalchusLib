package com.falchus.lib.minecraft.spigot.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public class EntityPlayerListener implements Listener {

	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	public final Map<UUID, EntityPlayer> players = new HashMap<>();
	public final Map<UUID, Consumer<Player>> actions = new HashMap<>();
	
	public EntityPlayerListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof EntityPlayer)) return;
		
		EntityPlayer clicked = (EntityPlayer) event.getRightClicked();
		UUID uuid = clicked.getUniqueID();
		
		if (players.containsKey(uuid)) {
			Consumer<Player> action = actions.get(uuid);
			if (action != null) {
				event.setCancelled(true);
				action.accept(event.getPlayer());
			}
		}
	}
}
