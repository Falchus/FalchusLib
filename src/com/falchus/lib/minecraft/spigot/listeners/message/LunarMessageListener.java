package com.falchus.lib.minecraft.spigot.listeners.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.Messenger;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.enums.Client;
import com.falchus.lib.minecraft.spigot.events.player.PlayerClientJoinEvent;
import com.falchus.lib.minecraft.spigot.utils.lunar.LunarJsonPacketUtil;
import com.google.gson.JsonObject;

public class LunarMessageListener implements Listener {
	
	private final FalchusLibMinecraftSpigot plugin = FalchusLibMinecraftSpigot.getInstance();
	
	public LunarMessageListener() {
		Messenger messenger = Bukkit.getServer().getMessenger();
        messenger.registerIncomingPluginChannel(plugin, "lunar:apollo", (s, player, bytes) -> { });
        messenger.registerIncomingPluginChannel(plugin, "apollo:json", (s, player, bytes) -> { });
        messenger.registerOutgoingPluginChannel(plugin, "apollo:json");
 
        Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    @EventHandler
    private void onRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equalsIgnoreCase("lunar:apollo")) return;
 
        Player player = event.getPlayer();
 
        // Sending the player's world name to the client is required for some modules
        LunarJsonPacketUtil.sendPacket(player, createUpdatePlayerWorldMessage(player));
 
        Client client = Client.LUNAR;
        plugin.getContexts().getClientManager().set(player, client);
        Bukkit.getPluginManager().callEvent(new PlayerClientJoinEvent(player, client));
    }
    
    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
 
        // Sending the player's world name to the client is required for some modules
        LunarJsonPacketUtil.sendPacket(player, this.createUpdatePlayerWorldMessage(player));
    }
 
    private JsonObject createUpdatePlayerWorldMessage(Player player) {
        JsonObject message = new JsonObject();
        message.addProperty("@type", "type.googleapis.com/lunarclient.apollo.player.v1.UpdatePlayerWorldMessage");
        message.addProperty("world", player.getWorld().getName());
        return message;
    }
}
