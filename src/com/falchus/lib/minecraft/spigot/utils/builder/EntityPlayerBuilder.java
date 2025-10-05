package com.falchus.lib.minecraft.spigot.utils.builder;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

/**
 * Builder for creating and managing {@link EntityPlayer}s.
 */
@Getter
public class EntityPlayerBuilder {
	
	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	private String name = "";
	private UUID uuid = UUID.randomUUID();
	private String skinValue;
	private String skinSignature;
	private boolean invisible = false;
	private Location location;
	private boolean lookAtPlayer = false;
	
	/**
	 * Sets the name.
	 */
	public EntityPlayerBuilder setName(@NonNull String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * Sets the UUID.
	 */
	public EntityPlayerBuilder setUUID(@NonNull UUID uuid) {
		this.uuid = uuid;
		return this;
	}
	
	/**
	 * Sets the skin with the skinValue & skinSignature.
	 */
	public EntityPlayerBuilder setSkin(@NonNull String skinValue, @NonNull String skinSignature) {
		this.skinValue = skinValue;
		this.skinSignature = skinSignature;
		return this;
	}
	
	/**
	 * Makes the EntityPlayer invisible.
	 */
	public EntityPlayerBuilder setInvisible(boolean invisible) {
		this.invisible = invisible;
		return this;
	}
	
	/**
	 * Sets the location of the EntityPlayer.
	 */
	public EntityPlayerBuilder setLocation(@NonNull Location location) {
		this.location = location;
		return this;
	}
	
	/**
	 * Registers a callback to be executed when a player interacts with this EntityPlayer.
	 */
	public EntityPlayerBuilder withInteractListener(@NonNull Consumer<Player> onPlayerInteract) {
		plugin.getContexts().getEntityPlayerListener().actions.put(uuid, onPlayerInteract);
		return this;
	}
	
	/**
	 * Makes the EntityPlayer look at the player.
	 */
	public EntityPlayerBuilder lookAtPlayer(boolean lookAtPlayer) {
		this.lookAtPlayer = lookAtPlayer;
		return this;
	}
	
	/**
	 * Builds and returns the final {@link EntityPlayer}.
	 */
	public EntityPlayer build() {
        WorldServer world = ((CraftWorld) (location != null ? location.getWorld() : Bukkit.getWorlds().get(0))).getHandle();
		
		GameProfile profile = new GameProfile(uuid, name);
		if (skinValue != null && skinSignature != null) {
			profile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));
		}
		
		EntityPlayer entityPlayer = new EntityPlayer(
			((CraftServer) Bukkit.getServer()).getServer(), 
			world, 
			profile, 
			new PlayerInteractManager(world)
		);
		
		if (location != null) {
			entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		}
		entityPlayer.setInvisible(invisible);
		
		plugin.getContexts().getEntityPlayerListener().players.put(uuid, entityPlayer);
		
		if (lookAtPlayer) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (entityPlayer.dead) {
						cancel();
						return;
					}
					
					Player nearest = null;
					double nearestDist = Double.MAX_VALUE;
					
					for (Player player : entityPlayer.getBukkitEntity().getWorld().getPlayers()) {
					    double dist = player.getLocation().distanceSquared(entityPlayer.getBukkitEntity().getLocation());
					    if (dist < nearestDist) {
					        nearestDist = dist;
					        nearest = player;
					    }
					}
					
					if (nearest != null) {
						Location from = entityPlayer.getBukkitEntity().getLocation().add(0, 1.6, 0);
						Location to = nearest.getLocation().add(0, 1.6, 0);
						
						double dx = to.getX() - from.getX();
						double dy = to.getY() - from.getY();
						double dz = to.getZ() - from.getZ();
						
						double distanceXZ = Math.sqrt(dx * dx + dz * dz);
						
						entityPlayer.yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
						entityPlayer.pitch = (float) -Math.toDegrees(Math.atan2(dy, distanceXZ));
					}
				}
			}.runTaskTimer(plugin, 0, 1);
		}
		return entityPlayer;
	}
}
