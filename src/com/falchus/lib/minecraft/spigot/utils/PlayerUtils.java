package com.falchus.lib.minecraft.spigot.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.utils.builder.GameProfileBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

/**
 * Utility class for players.
 */
@UtilityClass
public class PlayerUtils {
	
	private static final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	private static final Map<UUID, Property> skins = new HashMap<>();
	private static final Map<UUID, String> names = new HashMap<>();

	/**
	 * Sends a raw NMS packet to a player.
	 */
	public static void sendPacket(@NonNull Player player, @NonNull Packet<?> packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	/**
	 * Sends a title and/or subtitle to a player.
	 */
	public static void sendTitle(@NonNull Player player, String title, String subtitle) {
		IChatBaseComponent titleComponent = title != null && !title.isEmpty() ? new ChatComponentText(title) : null;
		IChatBaseComponent subtitleComponent = subtitle != null && !subtitle.isEmpty() ? new ChatComponentText(subtitle) : null;
		
		if (titleComponent != null) {
			PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleComponent);
			sendPacket(player, titlePacket);
		}
		
		if (subtitleComponent != null) {
			PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleComponent);
			sendPacket(player, subtitlePacket);
		}
	}
	
	/**
	 * Freezes a player.
	 */
	public static void freeze(@NonNull Player player) {
		plugin.getContexts().getFreezeListener().players.add(player.getUniqueId());
	}
	
	/**
	 * Unfreezes a player.
	 */
	public static void unfreeze(@NonNull Player player) {
		plugin.getContexts().getFreezeListener().players.remove(player.getUniqueId());
	}
	
	/**
	 * Retrieves the LuckPerms rank prefix of a player.
	 */
	public static String getLuckPermsRankPrefix(@NonNull Player player) {
		if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) return "";
		
		net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
		net.luckperms.api.cacheddata.CachedMetaData metaData = user.getCachedData().getMetaData();
		String prefix = metaData.getPrefix();
		return prefix != null ? prefix : "";
	}
	
	/**
	 * Sends the end credits screen to a player.
	 */
	public static void sendEndCredits(@NonNull Player player) {
		PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(4, 0.0F);
		sendPacket(player, packet);
	}
	
	/**
	 * Vanishes a player.
	 */
	public static void vanish(@NonNull Player player) {
		if (plugin.getContexts().getVanishListener().players.add(player.getUniqueId())) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				onlinePlayer.hidePlayer(player);
			}
		}
	}
	
	/**
	 * Unvanishes a player.
	 */
	public static void unvanish(@NonNull Player player) {
		if (plugin.getContexts().getVanishListener().players.remove(player.getUniqueId())) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				onlinePlayer.showPlayer(player);
			}
		}
	}
	
	/**
	 * Sets a custom skin.
	 */
	public static void setSkin(@NonNull Player player, @NonNull UUID uuid) {
		try {
			EntityPlayer ep = ((CraftPlayer) player).getHandle();
			GameProfile profile = ep.getProfile();
			
	        if (!skins.containsKey(player.getUniqueId()) && !profile.getProperties().get("textures").isEmpty()) {
	            Property original = profile.getProperties().get("textures").iterator().next();
	            skins.put(player.getUniqueId(), original);
	        }
			
			GameProfile skinProfile = GameProfileBuilder.fetch(uuid);
			
			profile.getProperties().removeAll("textures");
			for (Property property : skinProfile.getProperties().get("textures")) {
				profile.getProperties().put("textures", property);
			}
			
			refresh(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the skin back to the original.
	 */
	public static void resetSkin(@NonNull Player player) {
	    try {
	        EntityPlayer ep = ((CraftPlayer) player).getHandle();
	        GameProfile profile = ep.getProfile();

	        Property original = skins.get(player.getUniqueId());
	        if (original != null) {
	            profile.getProperties().removeAll("textures");
	            profile.getProperties().put("textures", original);

	            refresh(player);

	            skins.remove(player.getUniqueId());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sets a custom name.
	 */
	public static void setName(@NonNull Player player, @NonNull String name) {
	    try {
	        EntityPlayer ep = ((CraftPlayer) player).getHandle();
	        GameProfile profile = ep.getProfile();

	        names.put(player.getUniqueId(), profile.getName());

	        player.setCustomName(name);
	        player.setCustomNameVisible(true);
	        player.setDisplayName(name);
	        
	        Field nameField = GameProfile.class.getDeclaredField("name");
	        nameField.setAccessible(true);
	        nameField.set(profile, name);

	        refresh(player);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Resets the name back to the original.
	 */
	public static void resetName(@NonNull Player player) {
	    try {
	        String original = names.get(player.getUniqueId());
	        if (original == null) return;

	        EntityPlayer ep = ((CraftPlayer) player).getHandle();
	        GameProfile profile = ep.getProfile();
	        
	        player.setCustomName(original);
	        player.setCustomNameVisible(true);
	        player.setDisplayName(original);

	        Field nameField = GameProfile.class.getDeclaredField("name");
	        nameField.setAccessible(true);
	        nameField.set(profile, original);

	        refresh(player);

	        names.remove(player.getUniqueId());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Forces clients to reload the player's GameProfile.
	 */
	public static void refresh(@NonNull Player player) {
	    EntityPlayer ep = ((CraftPlayer) player).getHandle();

	    PacketPlayOutPlayerInfo update = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ep);
	    for (Player online : Bukkit.getOnlinePlayers()) {
	        sendPacket(online, update);
	    }
	}
	
	public static void addEntityPlayer(@NonNull Player player, @NonNull EntityPlayer entityPlayer) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
			EnumPlayerInfoAction.ADD_PLAYER,
			entityPlayer
		);
		sendPacket(player, packet);
	}
	
	public static void removeEntityPlayer(@NonNull Player player, @NonNull EntityPlayer entityPlayer) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
			EnumPlayerInfoAction.REMOVE_PLAYER,
			entityPlayer
		);
		sendPacket(player, packet);
	}
}