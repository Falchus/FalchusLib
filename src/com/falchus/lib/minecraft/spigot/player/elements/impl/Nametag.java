package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

/**
 * Represents a Nametag.
 */
public class Nametag extends PlayerElement {
	
	private final Scoreboard scoreboard;
	private Team team;
	
	/**
	 * Constructs a Nametag.
	 */
	private Nametag(@NonNull Player player) {
		super(player);
		this.scoreboard = player.getScoreboard() != null ? player.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
		
		this.team = scoreboard.getTeam(player.getName());
		if (team == null) {
			this.team = scoreboard.registerNewTeam(player.getName());
		}
		
		if (!team.hasEntry(player.getName())) {
			team.addEntry(player.getName());
		}
		
		player.setScoreboard(scoreboard);
	}

	/**
	 * Sets a one-time prefix and suffix.
	 */
	public void send(@NonNull String prefix, @NonNull String suffix) {
		update(prefix, suffix);
	}
	
	/**
	 * Updates prefix and suffiy periodically.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Supplier<String> prefixSupplier, @NonNull Supplier<String> suffixSupplier) {
		super.sendUpdating(intervalTicks, () -> {
			String prefix = prefixSupplier.get();
			String suffix = suffixSupplier.get();
			update(prefix, suffix);
		});
	}
	
	private void update(@NonNull String prefix, @NonNull String suffix) {
		String teamName = player.getName();
		
		try {
            Class<?> teamClass = PacketPlayOutScoreboardTeam.class;
            
            Field nameField = teamClass.getDeclaredField("a");
            nameField.setAccessible(true);
        	
            Field displayNameField = teamClass.getDeclaredField("b");
            displayNameField.setAccessible(true);
            
            Field prefixField = teamClass.getDeclaredField("c");
            prefixField.setAccessible(true);
            
            Field suffixField = teamClass.getDeclaredField("d");
            suffixField.setAccessible(true);
            
            Field playersField = teamClass.getDeclaredField("g");
            playersField.setAccessible(true);
            
            Field modeField = teamClass.getDeclaredField("h");
            modeField.setAccessible(true);
            
            PacketPlayOutScoreboardTeam create = new PacketPlayOutScoreboardTeam();
            nameField.set(create, teamName);
            displayNameField.set(create, teamName);
            prefixField.set(create, prefix);
            suffixField.set(create, suffix);
            playersField.set(create, new HashSet<>(Collections.singletonList(player.getName())));
            modeField.set(create, 0);

            PacketPlayOutScoreboardTeam update = new PacketPlayOutScoreboardTeam();
            nameField.set(update, teamName);
            displayNameField.set(update, teamName);
            prefixField.set(update, prefix);
            suffixField.set(update, suffix);
            playersField.set(update, new HashSet<>(Collections.singletonList(player.getName())));
            modeField.set(update, 2);
            
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(create);
                ((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(update);
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	public void remove() {
		super.remove();
		
		if (team != null && team.hasEntry(player.getName())) {
			team.removeEntry(player.getName());
		}
		
		try {
			Class<?> teamClass = PacketPlayOutScoreboardTeam.class;
			
	        Field nameField = teamClass.getDeclaredField("a");
	        nameField.setAccessible(true);
	        
	        Field playersField = teamClass.getDeclaredField("g");
	        playersField.setAccessible(true);
	        
	        Field modeField = teamClass.getDeclaredField("h");
	        modeField.setAccessible(true);
	        
	        PacketPlayOutScoreboardTeam removePacket = new PacketPlayOutScoreboardTeam();
	        nameField.set(removePacket, team.getName());
	        playersField.set(removePacket, new HashSet<>(Collections.singletonList(player.getName())));
	        modeField.set(removePacket, 4);
	        
	        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	        	((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(removePacket);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}