package com.falchus.lib.minecraft.spigot.utils.builder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.falchus.lib.interfaces.consumer.TriConsumer;
import com.falchus.lib.minecraft.spigot.utils.ItemUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.NonNull;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

/**
 * Builder class for creating and customizing {@link ItemStack}s.
 */
public class ItemBuilder {

	private ItemStack item;
	
	/**
	 * Creates an ItemBuilder for the given material.
	 */
	public ItemBuilder(@NonNull Material material) {
		this.item = new ItemStack(material);
	}
	
	/**
	 * Creates an ItemBuilder for the given material and amount.
	 */
	public ItemBuilder(@NonNull Material material, @NonNull Integer amount) {
		this.item = new ItemStack(material, amount);
	}
	
	/**
	 * Creates an ItemBuilder from an existing ItemStack.
	 */
	public ItemBuilder(@NonNull ItemStack item) {
		this.item = item.clone();
	}
	
	/**
	 * Sets the display name.
	 */
	public ItemBuilder setName(@NonNull String name) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(name);
			item.setItemMeta(meta);
		}
		return this;
	}
	
	/**
	 * Sets the lore.
	 */
	public ItemBuilder setLore(@NonNull List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return this;
	}
	
	/**
	 * Adds an unsafe enchantment.
	 */
	public ItemBuilder addEnchantment(@NonNull Enchantment enchantment, @NonNull Integer level) {
		item.addUnsafeEnchantment(enchantment, level);
		return this;
	}
	
	/**
	 * Adds a item flag.
	 */
	public ItemBuilder addItemFlag(@NonNull ItemFlag itemFlag) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.addItemFlags(itemFlag);
			item.setItemMeta(meta);
		}
		return this;
	}
	
	/**
	 * Sets the durability.
	 */
	public ItemBuilder setDurability(@NonNull Short durability) {
		item.setDurability(durability);
		return this;
	}
	
	/**
	 * Sets the skull owner.
	 */
	public ItemBuilder setSkullOwner(@NonNull String owner) {
		if (item.getType() == Material.SKULL_ITEM) {
			setDurability((short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (meta != null) {
				meta.setOwner(owner);
				item.setItemMeta(meta);
			}
		}
		return this;
	}
	
	/**
	 * Sets a custom skull texture using a Base64 texture string.
	 */
	public ItemBuilder setSkullTexture(@NonNull String texture) {
		if (item.getType() == Material.SKULL_ITEM) {
			setDurability((short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (meta != null) {
				try {
					GameProfile profile = new GameProfile(UUID.randomUUID(), null);
					profile.getProperties().put("textures", new Property("textures", texture));
					
					Field profileField = meta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(meta, profile);
					
					item.setItemMeta(meta);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}
	
	/**
	 * Sets a custom UUID (stored in NBT).
	 */
	public ItemBuilder setUuid(@NonNull UUID uuid) {
		net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
		if (nms == null) return this;
		
		NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();
		tag.setString("UUID", uuid.toString());
		nms.setTag(tag);
		item = CraftItemStack.asBukkitCopy(nms);
		
		return this;
	}

	/**
	 * Registers a callback to be executed when a player interacts.
	 */
	public ItemBuilder withInteractListener(@NonNull Consumer<Player> onPlayerInteract) {
	    UUID uuid = ItemUtils.getUUID(item);
	    if (uuid == null) {
	        uuid = UUID.randomUUID();
	        item = ItemUtils.setUUID(item, uuid);
	    }
	    ItemUtils.itemActions.put(uuid, onPlayerInteract);
	    return this;
	}

	/**
	 * Registers a callback to be executed when a player clicks in an inventory.
	 */
	public ItemBuilder withInventoryClickListener(@NonNull TriConsumer<Player, ItemStack, InventoryClickEvent> onInventoryClick) {
	    UUID uuid = ItemUtils.getUUID(item);
	    if (uuid == null) {
	        uuid = UUID.randomUUID();
	        item = ItemUtils.setUUID(item, uuid);
	    }
	    ItemUtils.itemActionsInventory.put(uuid, onInventoryClick);
	    return this;
	}
	
	/**
	 * Builds and returns the final {@link ItemStack}.
	 */
	public ItemStack build() {
		return item;
	}
}