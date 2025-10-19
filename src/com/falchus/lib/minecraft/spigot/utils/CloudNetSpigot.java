package com.falchus.lib.minecraft.spigot.utils;

import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.utils.CloudNet;

import eu.cloudnetservice.modules.bridge.player.executor.ServerSelectorType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @deprecated since 1.1.4, use {@link CloudNet} instead!
 * Utility class for Spigot-specific CloudNet operations.
 */
@UtilityClass
@Deprecated(since = "1.1.4")
public class CloudNetSpigot {

	/**
	 * Sets the "extra" field for the current service.
	 */
	public static void setExtra(@NonNull String newExtra) {
		CloudNet.setExtra(newExtra);
	}
	
	/**
	 * Sets the MOTD for the current service.
	 */
	public static void setMotd(@NonNull String newMotd) {
		CloudNet.setMotd(newMotd);
	}
	
	/**
	 * Changes the service state to "ingame" and publishes the update.
	 */
	public static void changeToIngame() {
		CloudNet.changeToIngame();
	}
	
	/**
	 * Connects a player to a specified task using the given selector type.
	 */
	public static void connectPlayerToTask(@NonNull Player player, @NonNull String task, @NonNull ServerSelectorType serverSelectorType) {
		CloudNet.connectPlayerToTask(player.getUniqueId(), task, serverSelectorType);
	}
	
	/**
	 * Connects a player to a specified service.
	 */
	public static void connectPlayerToService(@NonNull Player player, @NonNull String service) {
		CloudNet.connectPlayerToService(player.getUniqueId(), service);
	}
}
