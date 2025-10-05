package com.falchus.lib.minecraft.spigot.player.elements;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Abstract base class for player-related elements.
 */
@AllArgsConstructor
public abstract class PlayerElement {

	protected final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	@NonNull protected final Player player;
	
    private static final Map<Class<? extends PlayerElement>, Map<UUID, PlayerElement>> INSTANCES = new HashMap<>();
    private static final Map<Class<? extends PlayerElement>, Map<UUID, BukkitTask>> TASKS = new HashMap<>();
	
    /**
     * Sends the element to the player.
     */
	public void send() {}
	
	/**
	 * Sends the element to the player repeatedly with a fixed interval.
	 * If a previous repeating task exists for this element, it will be canceled.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Runnable runnable) {
	    Map<UUID, BukkitTask> map = TASKS.computeIfAbsent(this.getClass(), c -> new HashMap<>());
	    
		BukkitTask oldTask = map.get(player.getUniqueId());
		if (oldTask != null) {
			remove();
		}
		
		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				if (!player.isOnline()) {
					remove();
					return;
				}
				runnable.run();
			}
		}.runTaskTimer(plugin, 0, intervalTicks);
		
		map.put(player.getUniqueId(), task);
	}
	
	/**
	 * Removes this element and cancels any schedules repeating tasks.
	 */
	public void remove() {
		Map<UUID, BukkitTask> map = TASKS.get(this.getClass());
		if (map != null) {
	        BukkitTask task = map.remove(player.getUniqueId());
	        if (task != null) {
	            task.cancel();
	        }
		}
		
		Map<UUID, PlayerElement> isntMap = INSTANCES.get(this.getClass());
		if (isntMap != null) {
			isntMap.remove(player.getUniqueId());
		}
	}
	
	/**
	 * Retrieves a singleton instance of a PlayerElement subclass for a given player.
	 * If it does not exist, it is created via reflection using a constructor that
	 * accepts a Player parameter.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PlayerElement> T get(@NonNull Class<T> clazz, @NonNull Player player) {
		if (!PlayerElement.class.isAssignableFrom(clazz)) return null;
		
		Map<UUID, PlayerElement> map = INSTANCES.computeIfAbsent(clazz, c -> new HashMap<>());
		
		return (T) map.computeIfAbsent(player.getUniqueId(), id -> {
			try {
				Constructor<T> ctor = clazz.getDeclaredConstructor(Player.class);
	            ctor.setAccessible(true);
	            return ctor.newInstance(player);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}
}
