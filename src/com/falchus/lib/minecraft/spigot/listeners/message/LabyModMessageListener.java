package com.falchus.lib.minecraft.spigot.listeners.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.enums.Client;
import com.falchus.lib.minecraft.spigot.events.player.PlayerClientJoinEvent;
import com.falchus.lib.minecraft.utils.labymod.LabyModProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LabyModMessageListener implements PluginMessageListener {
	
	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	public LabyModMessageListener() {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "labymod3:main", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("labymod3:main")) return;
		
		ByteBuf buf = Unpooled.wrappedBuffer(message);
		String key = LabyModProtocol.readString(buf, Short.MAX_VALUE);
		
		// LabyMod user joins the server
		if (key.equals("INFO")) {
			Client client = Client.LABYMOD;
			plugin.getContexts().getClientManager().set(player, client);
			Bukkit.getPluginManager().callEvent(new PlayerClientJoinEvent(player, Client.LABYMOD));
		}
	}
}
