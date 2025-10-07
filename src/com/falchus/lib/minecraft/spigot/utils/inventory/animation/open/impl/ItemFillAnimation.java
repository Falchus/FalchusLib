package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open.impl;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.falchus.lib.minecraft.spigot.utils.inventory.animation.open.InventoryOpenAnimation;

/**
 * Fills the items in order.
 */
public class ItemFillAnimation extends InventoryOpenAnimation {
	
	public ItemFillAnimation(int delayTicks) {
		this.delayTicks = delayTicks;
	}
	
	@Override
	protected void animate(Player player, Inventory inventory, ItemStack[] items, int tick) {
		ItemStack item = items[tick];
		if (item != null) {
			inventory.setItem(tick, item);
			player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 2);
		}
	}
}
