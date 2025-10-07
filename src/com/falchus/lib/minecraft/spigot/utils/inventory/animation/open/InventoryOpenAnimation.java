package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
	protected abstract void animate(Player player, Inventory inventory, ItemStack[] items);
	
	/**
	 * Plays the animation with automatic scheduling using delayTicks.
	 */
	public final void play(Player player, Inventory inventory) {
		ItemStack[] items = inventory.getContents();
		inventory.clear();
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			animate(player, inventory, items);
		}, delayTicks);
    }
}
