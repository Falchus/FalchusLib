package com.falchus.lib.minecraft.bungee;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contexts {
	
    final ProxyServer server = ProxyServer.getInstance();
    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	public Contexts() {
		init();
	}
	
	public void init() {

	}
}
