package com.falchus.lib.minecraft.spigot.listeners;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.falchus.lib.interfaces.consumer.TriConsumer;
import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.utils.ItemUtils;

public class ItemListener implements Listener {

	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	public ItemListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	ItemStack item = event.getItem();
    	if (item == null) return;
    	
        UUID uuid = ItemUtils.getUUID(item);
        if (uuid != null) {
            Consumer<Player> action = ItemUtils.itemActions.get(uuid);
            if (action != null) {
                event.setCancelled(true);
                action.accept(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        
        UUID uuid = ItemUtils.getUUID(item);
        if (uuid != null) {
            TriConsumer<Player, ItemStack, InventoryClickEvent> action = ItemUtils.itemActionsInventory.get(uuid);
            if (action != null) {
                event.setCancelled(true);
                action.accept((Player) event.getWhoClicked(), item, event);
                return;
            }
        }

        TriConsumer<Player, ItemStack, InventoryClickEvent> invCallback = ItemUtils.inventoryCallbacks.get(event.getInventory());
        if (invCallback != null) {
            event.setCancelled(true);
            invCallback.accept((Player) event.getWhoClicked(), item, event);
        }
    }
}
