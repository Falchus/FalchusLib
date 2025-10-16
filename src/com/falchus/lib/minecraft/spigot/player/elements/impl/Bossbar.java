package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;
import com.falchus.lib.minecraft.spigot.utils.PlayerUtils;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

/**
 * Represents a Bossbar.
 */
public class Bossbar extends PlayerElement {

	private final Map<UUID, EntityWither> withers = new HashMap<>();
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final Map<UUID, String> lastMessages = new HashMap<>();
    
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
	    UUID uuid = player.getUniqueId();
	    EntityWither wither = withers.get(uuid);
	    Location location = player.getLocation().add(player.getLocation().getDirection().multiply(90));

	    if (wither == null) {
	        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
	        wither = new EntityWither(world);
	        wither.setInvisible(true);
	        wither.setHealth(wither.getMaxHealth());
	        withers.put(uuid, wither);

	        wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	        PlayerUtils.sendPacket(player, new PacketPlayOutSpawnEntityLiving(wither));
	    } else {
	        Location lastLocation = lastLocations.get(uuid);
	        if (lastLocation == null || location.distanceSquared(lastLocation) > 0.01) {
	            wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	            PlayerUtils.sendPacket(player, new PacketPlayOutEntityTeleport(wither));
	        }
	    }

	    String lastMessage = lastMessages.get(uuid);
	    if (!message.equals(lastMessage)) {
	        wither.setCustomName(message);
	        wither.setCustomNameVisible(true);
	        PlayerUtils.sendPacket(player, new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true));
	    }

	    lastLocations.put(uuid, location);
	    lastMessages.put(uuid, message);
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
        lastLocations.remove(player.getUniqueId());
        lastMessages.remove(player.getUniqueId());
		if (wither != null) {
			PlayerUtils.sendPacket(player, new PacketPlayOutEntityDestroy(wither.getId()));
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
            if (newHealth != wither.getHealth()) {
                wither.setHealth(newHealth);
                PlayerUtils.sendPacket(player, new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true));
            }
        }
	}
}
