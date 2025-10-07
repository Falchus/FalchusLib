package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;

import lombok.Getter;

@Getter
public abstract class InventoryOpenAnimation {
	
	protected final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	protected int delayTicks = 1;
	
	/**
	 * Called to implement animation logic.
	 * This will be scheduled automatically after delayTicks.
	 */
	protected abstract void animate(Player player, Inventory inventory);
	
	/**
	 * Plays the animation with automatic scheduling using delayTicks.
	 */
	public final void play(Player player, Inventory inventory) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			inventory.clear();
			animate(player, inventory);
		}, delayTicks);
    }
}
