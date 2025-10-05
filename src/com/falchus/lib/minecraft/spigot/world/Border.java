package com.falchus.lib.minecraft.spigot.world;

import org.bukkit.Location;

import lombok.NonNull;

/**
 * Represents a configurable border in a Minecraft world.
 */
public interface Border {

	/**
	 * Gets the current size.
	 */
    double getSize();

    /**
     * Sets the size.
     */
    void setSize(@NonNull Double size);

    /**
     * Gets the center location.
     */
    Location getCenter();

    /**
     * Sets the center location.
     */
    void setCenter(@NonNull Location location);
}
