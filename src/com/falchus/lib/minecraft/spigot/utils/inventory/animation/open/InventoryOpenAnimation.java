package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
	protected abstract void animate(Player player, Inventory inventory, ItemStack[] items, int tick);
	
	/**
	 * Plays the animation with automatic scheduling using delayTicks.
	 */
	public final void play(Player player, Inventory inventory) {
		ItemStack[] items = inventory.getContents();
		inventory.clear();
		
		new BukkitRunnable() {
			int tick = 0;
			
			@Override
			public void run() {
				if (!player.getOpenInventory().getTopInventory().equals(inventory)) {
					cancel();
					return;
				}
				
				if (tick >= items.length) {
					cancel();
					return;
				}
				
				animate(player, inventory, items, tick);
				tick++;
			}
		}.runTaskTimer(plugin, 0, delayTicks);
    }
}
