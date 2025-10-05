package com.falchus.lib.minecraft;

import com.falchus.lib.minecraft.enums.Software;

/**
 * Class for detecting the Minecraft server software at runtime.
 */
public class FalchusLibMinecraft {
	
	/**
	 * Detects the server software by checking for known classes.
	 * 
     * @return {@link Software#SPIGOT} if running on Spigot,
     *         {@link Software#BUNGEECORD} if running on BungeeCord,
     *         or {@code null} if unknown.
	 */
	public static Software getSoftware() {
		try {
			Class.forName("org.bukkit.plugin.java.JavaPlugin");
			return Software.SPIGOT;
		} catch (ClassNotFoundException e1) {
			try {
				Class.forName("net.md_5.bungee.api.plugin.Plugin");
				return Software.BUNGEECORD;
			} catch (ClassNotFoundException e2) {
				return null;
			}
		}
	}
}
