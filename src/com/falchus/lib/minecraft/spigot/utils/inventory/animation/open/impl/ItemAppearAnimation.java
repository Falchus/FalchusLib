package com.falchus.lib.minecraft.spigot.utils.inventory.animation.open.impl;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.falchus.lib.minecraft.spigot.utils.inventory.animation.open.InventoryOpenAnimation;

public class ItemAppearAnimation extends InventoryOpenAnimation {
	
	public ItemAppearAnimation(int delayTicks) {
		this.delayTicks = delayTicks;
	}
	
	@Override
	protected void animate(Player player, Inventory inventory, ItemStack[] items) {
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item != null) {
				inventory.setItem(i, item);
				player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 2);
			}
		}
	}
}
