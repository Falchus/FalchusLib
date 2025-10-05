package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.function.Supplier;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;

/**
 * Represents a Actionbar.
 */
public class Actionbar extends PlayerElement {

	/**
	 * Constructs a new Actionbar.
	 */
	private Actionbar(@NonNull Player player) {
		super(player);
	}
	
	/**
	 * Sends a one-time action bar message.
	 */
	public void send(@NonNull String message) {
		IChatBaseComponent chatMessage = ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(chatMessage, (byte) 2);
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(packet);
	}
	
	/**
	 * Sends an action bar message repeatedly at a fixed interval.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Supplier<String> messageSupplier) {
		super.sendUpdating(intervalTicks, () -> {
			String message = messageSupplier.get();
			send(message);
		});
	}
}
