package com.falchus.lib.minecraft.command;

import java.util.Collections;
import java.util.List;

import com.falchus.lib.minecraft.FalchusLibMinecraft;
import com.falchus.lib.minecraft.enums.Software;

import lombok.NonNull;

/**
 * Abstraction for commands.
 */
public interface IBaseCommand {
	
	/**
	 * Returns the main command.
	 */
    String getCommand();
    
    /**
     * Returns the aliases.
     */
    String[] getAliases();
    
    /**
     * Returns the permission node required.
     */
    String getPermission();
    
    /**
     * Returns the message to send when a sender does not have permission.
     */
    String getNoPermissionMessage();
    
    /**
     * Returns the usage message for the command.
     */
    String getUsageMessage();
	
    /**
     * Executes the command logic.
     */
    void executeCommand(@NonNull Object sender, @NonNull String[] args);
    
    /**
     * Tab completion.
     */
    default List<String> tabComplete(@NonNull Object sender, @NonNull String[] args) {
    	return Collections.emptyList();
    }

    /**
     * Checks if the sender has the permission required to execute this command.
     */
    default boolean hasPermission(@NonNull Object sender) {
        String permission = getPermission();
        if (permission != null && !permission.isEmpty()) {
            if (FalchusLibMinecraft.getSoftware() == Software.SPIGOT) {
                if (sender instanceof org.bukkit.entity.Player) {
                    return ((org.bukkit.entity.Player) sender).hasPermission(permission);
                }
            } else if (FalchusLibMinecraft.getSoftware() == Software.BUNGEECORD) {
                if (sender instanceof net.md_5.bungee.api.connection.ProxiedPlayer) {
                    return ((net.md_5.bungee.api.connection.ProxiedPlayer) sender).hasPermission(permission);
                }
            }
        }
        return true;
    }

    /**
     * Sends a message to the command sender.
     */
    default void sendMessage(@NonNull Object sender, @NonNull String message) {
        if (FalchusLibMinecraft.getSoftware() == Software.SPIGOT) {
            if (sender instanceof org.bukkit.command.CommandSender) {
                ((org.bukkit.command.CommandSender) sender).sendMessage(message);
            }
        } else if (FalchusLibMinecraft.getSoftware() == Software.BUNGEECORD) {
            if (sender instanceof net.md_5.bungee.api.CommandSender) {
                ((net.md_5.bungee.api.CommandSender) sender).sendMessage(new net.md_5.bungee.api.chat.TextComponent(message));
            }
        }
    }
}
