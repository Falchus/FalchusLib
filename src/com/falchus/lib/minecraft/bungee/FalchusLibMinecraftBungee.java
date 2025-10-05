package com.falchus.lib.minecraft.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class FalchusLibMinecraftBungee extends Plugin {

	private static FalchusLibMinecraftBungee instance;
	private Contexts contexts;
	
	@Override
	public void onEnable() {
		instance = this;
		contexts = new Contexts();
	}
	
	public static FalchusLibMinecraftBungee getInstance() {
		return instance;
	}
	
	public Contexts getContexts() {
		return contexts;
	}
}
