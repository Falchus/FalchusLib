package com.falchus.lib.minecraft.command.impl;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.falchus.lib.minecraft.command.IBaseCommand;

import lombok.Getter;
import lombok.NonNull;

/**
 * Abstract adapter for Spigot commands.
 */
@Getter
public abstract class SpigotCommandAdapter implements IBaseCommand, CommandExecutor, TabCompleter {

    private final String command;
    private final String[] aliases;
    private final String permission;
    private final String noPermissionMessage;
    private final String usageMessage;
	
    /**
     * Constructs a new SpigotCommandAdapter.
     */
	public SpigotCommandAdapter(@NonNull String command, String permission, String noPermissionMessage, String usageMessage) {
        this.command = command;
        this.aliases = new String[0];
        this.permission = permission;
        this.noPermissionMessage = noPermissionMessage != null ? noPermissionMessage : "§cInsufficient permissions!";
        this.usageMessage = usageMessage != null ? usageMessage : "§cWrong usage.";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!hasPermission(sender)) {
			sendMessage(sender, getNoPermissionMessage());
			return false;
		}
		executeCommand(sender, args);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!hasPermission(sender)) return Collections.emptyList();
		List<String> list = tabComplete(sender, args);
		return list != null ? list : Collections.emptyList();
	}
}