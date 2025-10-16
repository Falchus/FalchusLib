package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;
import com.falchus.lib.minecraft.spigot.utils.PlayerUtils;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

/**
 * Represents a Nametag.
 */
public class Nametag extends PlayerElement {
	
	private final Scoreboard scoreboard;
	private Team team;
	
	private Field nameField;
	private Field displayNameField;
	private Field prefixField;
	private Field suffixField;
	private Field playersField;
	private Field modeField;

	private PacketPlayOutScoreboardTeam create;
	private PacketPlayOutScoreboardTeam update;
	private PacketPlayOutScoreboardTeam remove;
	
	/**
	 * Constructs a Nametag.
	 */
	private Nametag(@NonNull Player player) {
		super(player);
		this.scoreboard = player.getScoreboard() != null ? player.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
		
		Team team = scoreboard.getTeam(player.getName());
		if (team == null) {
			team = scoreboard.registerNewTeam(player.getName());
		}
		if (!team.hasEntry(player.getName())) {
			team.addEntry(player.getName());
		}
		this.team = team;
		
		player.setScoreboard(scoreboard);
		
		try {
            Class<?> teamClass = PacketPlayOutScoreboardTeam.class;
            
            nameField = teamClass.getDeclaredField("a");
            nameField.setAccessible(true);
            
            displayNameField = teamClass.getDeclaredField("b");
            displayNameField.setAccessible(true);
            
            prefixField = teamClass.getDeclaredField("c");
            prefixField.setAccessible(true);
            
            suffixField = teamClass.getDeclaredField("d");
            suffixField.setAccessible(true);
            
            playersField = teamClass.getDeclaredField("g");
            playersField.setAccessible(true);
            
            modeField = teamClass.getDeclaredField("h");
            modeField.setAccessible(true);
            
            HashSet<String> entries = new HashSet<>(Collections.singletonList(player.getName()));

            create = new PacketPlayOutScoreboardTeam();
            nameField.set(create, player.getName());
            displayNameField.set(create, player.getName());
            playersField.set(create, entries);
            modeField.set(create, 0);

            update = new PacketPlayOutScoreboardTeam();
            nameField.set(update, player.getName());
            displayNameField.set(update, player.getName());
            playersField.set(update, entries);
            modeField.set(update, 2);
            
            remove = new PacketPlayOutScoreboardTeam();
            nameField.set(remove, player.getName());
            playersField.set(remove, entries);
            modeField.set(remove, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
            prefixField.set(create, prefix);
            suffixField.set(create, suffix);
            prefixField.set(update, prefix);
            suffixField.set(update, suffix);
            
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	        	PlayerUtils.sendPacket(onlinePlayer, create);
	        	PlayerUtils.sendPacket(onlinePlayer, update);
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	public void remove() {
		super.remove();
		
		if (team.hasEntry(player.getName())) {
			team.removeEntry(player.getName());
		}
		
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        	PlayerUtils.sendPacket(onlinePlayer, remove);
        }
	}
}