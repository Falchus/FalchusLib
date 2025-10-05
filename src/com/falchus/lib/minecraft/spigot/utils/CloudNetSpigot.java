package com.falchus.lib.minecraft.spigot.utils;

import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.utils.CloudNet;

import eu.cloudnetservice.modules.bridge.player.executor.ServerSelectorType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for Spigot-specific CloudNet operations.
 */
@UtilityClass
public class CloudNetSpigot {

	/**
	 * Sets the "extra" field for the current service.
	 */
	public static void setExtra(@NonNull String newExtra) {
		CloudNet.bridgeServiceHelper.extra().set(newExtra);
	}
	
	/**
	 * Sets the MOTD for the current service.
	 */
	public static void setMotd(@NonNull String newMotd) {
		CloudNet.bridgeServiceHelper.motd().set(newMotd);
	}
	
	/**
	 * Changes the service state to "ingame" and publishes the update.
	 */
	public static void changeToIngame() {
		CloudNet.bridgeServiceHelper.changeToIngame();
        CloudNet.publishServiceInfoUpdate();
	}
	
	/**
	 * Connects a player to a specified task using the given selector type.
	 */
	public static void connectPlayerToTask(@NonNull Player player, @NonNull String task, @NonNull ServerSelectorType serverSelectorType) {
		CloudNet.playerManager.playerExecutor(player.getUniqueId()).connectToTask(task, serverSelectorType);
	}
}
