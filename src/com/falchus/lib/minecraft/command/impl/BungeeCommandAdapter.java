package com.falchus.lib.minecraft.command.impl;

import java.util.List;

import com.falchus.lib.minecraft.command.IBaseCommand;

import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Abstract adapter for BungeeCord commands.
 */
public abstract class BungeeCommandAdapter extends Command implements IBaseCommand {
	
    private final String command;
    private final String[] aliases;
    private final String permission;
    private final String noPermissionMessage;
    private final String usageMessage;

    /**
     * Constructs a new BungeeCommandAdapter.
     */
	public BungeeCommandAdapter(@NonNull String command, List<String> aliases, String permission, String noPermissionMessage, String usageMessage) {
		super(command, permission, aliases != null ? aliases.toArray(new String[0]) : new String[0]);
		
        this.command = command;
        this.aliases = aliases != null ? aliases.toArray(new String[0]) : new String[0];
        this.permission = permission;
        this.noPermissionMessage = noPermissionMessage != null ? noPermissionMessage : "§cInsufficient permissions!";
        this.usageMessage = usageMessage != null ? usageMessage : "§cWrong usage.";
	}
	
	@Override
	public void execute(CommandSender sender, @NonNull String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (!IBaseCommand.super.hasPermission(sender)) {
				sendMessage(sender, getNoPermissionMessage());
				return;
			}	
		}
		executeCommand(sender, args);
	}
	
    public String getCommand() {
        return command;
    }

    public String[] getAliases() {
        return aliases;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public String getNoPermissionMessage() {
    	return noPermissionMessage;
    }
    
    public String getUsageMessage() {
    	return usageMessage;
    }
    
    public abstract void executeCommand(Object sender, String[] args);
}