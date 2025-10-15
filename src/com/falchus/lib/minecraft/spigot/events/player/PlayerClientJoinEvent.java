package com.falchus.lib.minecraft.spigot.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.falchus.lib.minecraft.spigot.enums.Client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Called when a player's client has been identified after joining.
 */
@AllArgsConstructor
@Getter
public class PlayerClientJoinEvent extends Event {

	private final static HandlerList handlers = new HandlerList();
	
	private final Player player;
	private final Client client;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
