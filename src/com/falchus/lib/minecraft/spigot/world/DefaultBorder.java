package com.falchus.lib.minecraft.spigot.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Default implementation of {@link Border} using a Bukkit {@link World}'s
 * built-in {@link WorldBorder}.
 */
@AllArgsConstructor
public class DefaultBorder implements Border {

    @NonNull private final World world;

    /**
     * Gets the current size.
     */
    @Override
    public double getSize() {
        return world.getWorldBorder().getSize();
    }

    /**
     * Sets the size.
     */
    @Override
    public void setSize(@NonNull Double size) {
        world.getWorldBorder().setSize(size);
    }

    /**
     * Gets the center location.
     */
    @Override
    public Location getCenter() {
        return world.getWorldBorder().getCenter();
    }

    /**
     * Sets the center location.
     */
    @Override
    public void setCenter(@NonNull Location location) {
        world.getWorldBorder().setCenter(location);
    }
}
