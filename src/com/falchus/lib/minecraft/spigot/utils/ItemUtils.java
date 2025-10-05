package com.falchus.lib.minecraft.spigot.utils;

import java.util.*;
import java.util.function.Consumer;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.falchus.lib.interfaces.consumer.TriConsumer;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

/**
 * Utility class for items.
 */
@UtilityClass
public class ItemUtils {

	public static final Map<UUID, Consumer<Player>> itemActions = new HashMap<>();
    public static final Map<UUID, TriConsumer<Player, ItemStack, InventoryClickEvent>> itemActionsInventory = new HashMap<>();
    public static final Map<Inventory, TriConsumer<Player, ItemStack, InventoryClickEvent>> inventoryCallbacks = new HashMap<>();

    public static Consumer<PlayerInteractEvent> globalInteractCallback;
    public static Consumer<InventoryClickEvent> globalInventoryCallback;

    /**
     * Sets a UUID on the given item via NBT.
     */
    @SuppressWarnings("deprecation")
	public static ItemStack setUUID(@NonNull ItemStack item, @NonNull UUID uuid) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null) {
            nmsItem = new net.minecraft.server.v1_8_R3.ItemStack(net.minecraft.server.v1_8_R3.Item.getById(item.getType().getId()));
        }

        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        tag.setString("UUID", uuid.toString());
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    /**
     * Retrieves the UUID stores on the given item.
     */
    public static UUID getUUID(@NonNull ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null) return null;

        if (nmsItem.hasTag() && nmsItem.getTag().hasKey("UUID")) {
            return UUID.fromString(nmsItem.getTag().getString("UUID"));
        }
        return null;
    }

    /**
     * Represents an item in an inventory.
     */
    public static class InventoryItem {
        public final int slot;
        public final ItemStack item;
        public final Consumer<Player> onInventoryClick;

        /**
         * Constructs a new InventoryItem.
         */
        public InventoryItem(@NonNull Integer slot, @NonNull ItemStack item, Consumer<Player> onInventoryClick) {
            this.slot = slot;
            this.item = item;
            this.onInventoryClick = onInventoryClick;
        }
    }
}