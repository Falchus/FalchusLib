package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

/**
 * Represents a Bossbar.
 */
public class Bossbar extends PlayerElement {

	private final Map<UUID, EntityWither> withers = new HashMap<>();
    
	/**
	 * Constructs a new Bossbar.
	 */
	private Bossbar(@NonNull Player player) {
    	super(player);
    }
	
	/**
	 * Sends a one-time Bossbar message.
	 */
	public void send(@NonNull String message) {
		EntityWither oldWither = withers.remove(player.getUniqueId());
		if (oldWither != null) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(oldWither.getId()));
		}
		
		Location location = player.getLocation().add(player.getLocation().getDirection().multiply(90));
		WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
		
		EntityWither wither = new EntityWither(world);
		wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		wither.setInvisible(true);
		wither.setCustomName(message);
		wither.setCustomNameVisible(true);
		wither.setHealth(wither.getMaxHealth());
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(wither));
		withers.put(player.getUniqueId(), wither);
	}
	
	/**
	 * Sends a Bossbar message repeatedly at a fixed interval.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Supplier<String> messageSupplier) {
		super.sendUpdating(intervalTicks, () -> {
			String message = messageSupplier.get();
			send(message);
		});
	}
	
	/**
	 * Removes the Bossbar, cancelling any ongoing update tasks.
	 */
	@Override
	public void remove() {
		super.remove();
		
		EntityWither wither = withers.remove(player.getUniqueId());
		if (wither != null) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(wither.getId()));
		}
	}
	
	/**
	 * Sets the health/progress of the Bossbar.
	 */
	public void setProgress(@NonNull Double progress) {
        EntityWither wither = withers.get(player.getUniqueId());
        if (wither != null) {
            float max = wither.getMaxHealth();
            float newHealth = (float) Math.max(1, Math.min(max, progress * max));
            wither.setHealth(newHealth);
        }
	}
}
