package com.falchus.lib.minecraft.spigot.utils.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for backing up and restoring a player's inventory and armor.
 */
@UtilityClass
public class InventoryBackup {

    private static final Map<UUID, BackupData> playerInventories = new HashMap<>();

    /**
     * Internal class to hold data.
     */
    @AllArgsConstructor
    private static class BackupData {
        ItemStack[] inventory;
        ItemStack[] armor;
    }

    /**
     * Backs up the inventory and armor of the given player.
     * If a backup already exists, it is overwritten.
     */
    public static void backupInventory(@NonNull Player player) {
        playerInventories.remove(player.getUniqueId());

        ItemStack[] inventory = player.getInventory().getContents().clone();
        ItemStack[] armor = player.getInventory().getArmorContents().clone();

        playerInventories.put(player.getUniqueId(), new BackupData(inventory, armor));
    }

    /**
     * Restores the inventory and armor of the given player from the backup.
     * The backup is removed after restoration.
     */
    public static void loadInventory(@NonNull Player player) {
        if (playerInventories.containsKey(player.getUniqueId())) {
            BackupData backup = playerInventories.get(player.getUniqueId());

            player.getInventory().setContents(backup.inventory);
            player.getInventory().setArmorContents(backup.armor);

            playerInventories.remove(player.getUniqueId());
        }
    }

    /**
     * Checks if a backup exists for the given player.
     */
    public static boolean hasBackup(@NonNull Player player) {
        return playerInventories.containsKey(player.getUniqueId());
    }
}