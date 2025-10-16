package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;

/**
 * Represents a per-player chat.
 */
public class Chat extends PlayerElement implements Listener {

	private final Map<UUID, String> prefix = new ConcurrentHashMap<>();
	private boolean registered = false;
	
	/**
	 * Constructs a Chat element.
	 */
	private Chat(@NonNull Player player) {
		super(player);
	}
	
	/**
	 * Sets one-time.
	 */
	public void send(@NonNull String prefix) {
		this.prefix.put(player.getUniqueId(), prefix);
		
		if (!registered) {
			Bukkit.getPluginManager().registerEvents(this, plugin);
			registered = true;
		}
	}
	
	/**
	 * Updates periodically.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Supplier<String> prefixSupplier) {
		super.sendUpdating(intervalTicks, () -> {
			String prefix = prefixSupplier.get();
			send(prefix);
		});
	}
	
	/**
	 * Removes the chat element.
	 */
	public void remove() {
		super.remove();
		
		prefix.remove(player.getUniqueId());
		
		if (prefix.isEmpty() && registered) {
			HandlerList.unregisterAll(this);
			registered = false;
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String prefix = this.prefix.get(player.getUniqueId());
		
		if (prefix == null) return;
		
		event.setCancelled(true);
	    Bukkit.broadcastMessage(prefix + event.getMessage());
	}
}
