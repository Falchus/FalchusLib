package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.util.function.Supplier;

import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;
import com.falchus.lib.minecraft.spigot.utils.PlayerUtils;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

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
		IChatBaseComponent chatMessage = new ChatComponentText(message);
		PacketPlayOutChat packet = new PacketPlayOutChat(chatMessage, (byte) 2);
		PlayerUtils.sendPacket(player, packet);
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
