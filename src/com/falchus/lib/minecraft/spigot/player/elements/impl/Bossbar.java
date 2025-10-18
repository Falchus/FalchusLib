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
    private final Map<UUID, Float> lastProgress = new HashMap<>();
    
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
        Location eye = player.getEyeLocation().clone();
        Location location = eye.add(eye.getDirection().multiply(45));

        float yaw = eye.getYaw();
        float pitch = Math.max(-15, Math.min(15, eye.getPitch()));

	    EntityWither wither = withers.get(uuid);
	    if (wither == null) {
            WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
            wither = new EntityWither(world);
            wither.setInvisible(true);
            wither.setHealth(wither.getMaxHealth());
            withers.put(uuid, wither);

	        wither.setLocation(location.getX(), location.getY(), location.getZ(), yaw, pitch);
	        PlayerUtils.sendPacket(player, new PacketPlayOutSpawnEntityLiving(wither));
	    } else {
            Location last = lastLocations.get(uuid);
            boolean moved = last == null || !last.getWorld().equals(location.getWorld()) || location.distanceSquared(last) > 4;
            boolean rotated = last != null && (Math.abs(yaw - last.getYaw()) > 2 || Math.abs(pitch - last.getPitch()) > 2);

            if (moved || rotated) {
                wither.setLocation(location.getX(), location.getY(), location.getZ(), yaw, pitch);
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
		
        UUID uuid = player.getUniqueId();
        EntityWither wither = withers.remove(uuid);
        lastLocations.remove(uuid);
        lastMessages.remove(uuid);
        lastProgress.remove(uuid);
        if (wither != null) {
            PlayerUtils.sendPacket(player, new PacketPlayOutEntityDestroy(wither.getId()));
        }
	}
	
	/**
	 * Sets the health/progress of the Bossbar.
	 */
	public void setProgress(@NonNull Double progress) {
        EntityWither wither = withers.get(player.getUniqueId());
        if (wither == null) return;

        float max = wither.getMaxHealth();
        float newHealth = (float) Math.max(1, Math.min(max, progress * max));
        Float last = lastProgress.get(player.getUniqueId());
        if (last == null || Math.abs(last - newHealth) > 0.01f) {
            wither.setHealth(newHealth);
            PlayerUtils.sendPacket(player, new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true));
            lastProgress.put(player.getUniqueId(), newHealth);
        }
	}
}
