package com.falchus.lib.minecraft.spigot.player.elements.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

/**
 * Represents a per-player tablist.
 */
public class Tablist extends PlayerElement {
	
	private Tablist(@NonNull Player player) {
		super(player);
	}
	
	/**
	 * Sends a custom header and footer.
	 */
	public void send(@NonNull List<String> header, @NonNull List<String> footer, @NonNull String name) {
		String headerText = String.join("\n", header);
		String footerText = String.join("\n", footer);
		
		IChatBaseComponent headerComponent = new ChatComponentText(headerText);
        IChatBaseComponent footerComponent = new ChatComponentText(footerText);
        
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(headerComponent);

        try {
            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, footerComponent);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        player.setPlayerListName(name);
	}
	
	/**
	 * Updates the tablist periodically with dynamic content.
	 */
	public void sendUpdating(@NonNull Long intervalTicks, @NonNull Supplier<List<String>> headerSupplier, @NonNull Supplier<List<String>> footerSupplier, @NonNull Supplier<String> nameSupplier) {
		super.sendUpdating(intervalTicks, () -> {
			List<String> header = headerSupplier.get();
			List<String> footer = footerSupplier.get();
			String name = nameSupplier.get(); 
			send(header, footer, name);
		});
	}
	
	public void remove() {
		super.remove();
		
		send(null, null, player.getName());
	}
}
