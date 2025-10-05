package com.falchus.lib.minecraft.spigot.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.enums.Client;

public class ClientManager {

	private static final Map<Player, Client> clients = new HashMap<>();
	
	public static Client get(Player player) {
		return clients.getOrDefault(player, Client.OTHER);
	}
	
	public void set(Player player, Client client) {
		clients.put(player, client);
	}
}
