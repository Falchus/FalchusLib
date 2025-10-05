package com.falchus.lib.minecraft.command.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.falchus.lib.minecraft.command.IBaseCommand;

import lombok.NonNull;

/**
 * Abstract adapter for Spigot commands.
 */
public abstract class SpigotCommandAdapter implements IBaseCommand, CommandExecutor {

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
	
    public String getCommand() {
        return command;
    }

    public String getPermission() {
        return permission;
    }
    
    public String[] getAliases() {
        return aliases;
    }
    
    public String getNoPermissionMessage() {
    	return noPermissionMessage;
    }
    
    public String getUsageMessage() {
    	return usageMessage;
    }
    
    public abstract void executeCommand(Object sender, String[] args);
}