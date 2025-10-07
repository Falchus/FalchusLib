package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open;

import java.util.ArrayList;
import java.util.List;

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
	protected List<ItemStack> excludedItems = new ArrayList<>();
	
	/**
	 * Called to implement animation logic.
	 * This will be scheduled automatically after delayTicks.
	 */
	protected abstract void animate(Player player, Inventory inventory, ItemStack[] items, int tick);
	
	/**
	 * Plays the animation with automatic scheduling using delayTicks.
	 */
	public final void play(Player player, Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		inventory.clear();
		
		List<ItemStack> includedItems = new ArrayList<>();
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if (excludedItems.contains(item)) {
				inventory.setItem(i, item);
			} else {
				includedItems.add(item);
			}
		}
		ItemStack[] items = includedItems.toArray(new ItemStack[0]);
		
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
