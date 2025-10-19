package com.falchus.lib.minecraft.command.impl;

import java.util.Collections;
import java.util.List;

import com.falchus.lib.minecraft.command.IBaseCommand;

import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Abstract adapter for BungeeCord commands.
 */
@Getter
public abstract class BungeeCommandAdapter extends Command implements IBaseCommand, TabExecutor {
	
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
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (!IBaseCommand.super.hasPermission(sender)) return Collections.emptyList();
		}
		List<String> list = tabComplete(sender, args);
		return list != null ? list : Collections.emptyList();
	}
}