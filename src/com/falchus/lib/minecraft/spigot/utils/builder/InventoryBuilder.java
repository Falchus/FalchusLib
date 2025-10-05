package com.falchus.lib.minecraft.spigot.utils.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.falchus.lib.interfaces.consumer.TriConsumer;
import com.falchus.lib.minecraft.spigot.utils.ItemUtils;

import lombok.Getter;
import lombok.NonNull;

/**
 * Builder class for creating and customizing {@link Inventory}s.
 */
@Getter
public class InventoryBuilder {

    private final String title;
    private final int size;
    private final List<ItemUtils.InventoryItem> items = new ArrayList<>();
    private TriConsumer<Player, ItemStack, InventoryClickEvent> globalClickListener;
    
    private ItemStack filler;

    /**
     * Creates a new InventoryBuilder with a title and size.
     */
    public InventoryBuilder(@NonNull String title, @NonNull Integer size) {
        this.title = title;
        this.size = size;
    }

    /**
     * Adds an item to a specific slot without a click listener.
     */
    public InventoryBuilder setItem(int slot, @NonNull ItemStack item) {
        items.add(new ItemUtils.InventoryItem(slot, item, null));
        return this;
    }

    /**
     * Adds an item to a specific slot with a simple player callback.
     */
    public InventoryBuilder setItem(int slot, @NonNull ItemStack item, @NonNull Consumer<Player> onClick) {
        items.add(new ItemUtils.InventoryItem(slot, item, onClick));
        return this;
    }

    /**
     * Adds an item to a specific slot with a full InventoryClickEvent callback.
     */
    public InventoryBuilder setItem(int slot, @NonNull ItemStack item, @NonNull TriConsumer<Player, ItemStack, InventoryClickEvent> onClick) {
        UUID uuid = UUID.randomUUID();
        ItemStack itemWithUUID = ItemUtils.setUUID(item, uuid);
        ItemUtils.itemActionsInventory.put(uuid, onClick);
        items.add(new ItemUtils.InventoryItem(slot, itemWithUUID, null));
        return this;
    }
    
    /**
     * Adds an item without a click listener.
     */
    public InventoryBuilder addItem(@NonNull ItemStack item) {
        items.add(new ItemUtils.InventoryItem(-1, item, null));
        return this;
    }

    /**
     * Adds an item with a simple player callback.
     */
    public InventoryBuilder addItem(@NonNull ItemStack item, @NonNull Consumer<Player> onClick) {
        items.add(new ItemUtils.InventoryItem(-1, item, onClick));
        return this;
    }

    /**
     * Adds an item with a full InventoryClickEvent callback.
     */
    public InventoryBuilder addItem(@NonNull ItemStack item, @NonNull TriConsumer<Player, ItemStack, InventoryClickEvent> onClick) {
        UUID uuid = UUID.randomUUID();
        ItemStack itemWithUUID = ItemUtils.setUUID(item, uuid);
        ItemUtils.itemActionsInventory.put(uuid, onClick);
        items.add(new ItemUtils.InventoryItem(-1, itemWithUUID, null));
        return this;
    }

    /**
     * Sets a global click listener for all items in this inventory.
     */
    public InventoryBuilder withClickListener(@NonNull TriConsumer<Player, ItemStack, InventoryClickEvent> onClick) {
        this.globalClickListener = onClick;
        return this;
    }
    
    /**
     * Defines a filler item that will be placed in all unused slots.
     */
    public InventoryBuilder fill(@NonNull ItemStack filler) {
        this.filler = filler;
        return this;
    }
    
    /**
     * Defines a default filler item that will be placed in all unused slots.
     */
    public InventoryBuilder fill() {
        this.filler = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§r").setDurability((short) 7).build();
        return this;
    }

    /**
     * Builds and returns the final {@link Inventory}.
     */
    public Inventory build() {
    	return buildInventory(items, title, size);
    }
    
    /**
     * Builds multiple pages if items exceed inventory size.
     * The last row is reserved.
     */
    public List<Inventory> buildPages() {
    	List<Inventory> pages = new ArrayList<>();
    	int pageSize = size - 9;
    	
    	int calcPages = (int) Math.ceil(items.size() / (double) pageSize);
    	final int totalPages = calcPages == 0 ? 1 : calcPages;
    	
    	for (int page = 0; page < totalPages; page++) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, items.size());
            List<ItemUtils.InventoryItem> pageItems = items.subList(start, end);
    		
    		Inventory inv = buildInventory(pageItems, title, size);
    		
    		final int currentPage = page;
    		
			inv.setItem(size - 9, new ItemBuilder(Material.SKULL_ITEM).setName("§ePrevious page").setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=").withInventoryClickListener(
				(player, item, event) -> {
					if (currentPage > 0) {
						player.openInventory(pages.get(currentPage - 1));
					}
				})
			.build());
			inv.setItem(size - 1, new ItemBuilder(Material.SKULL_ITEM).setName("§eNext page").setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=").withInventoryClickListener(
				(player, item, event) -> {
	                if (currentPage < totalPages - 1) {
	                    player.openInventory(pages.get(currentPage + 1));
	                }
				})
			.build());
			
    		pages.add(inv);
    	}
    	
    	return pages;
    }
    
    /**
     * Builds and returns the final {@link Inventory}.
     */
    private Inventory buildInventory(List<ItemUtils.InventoryItem> items, String inventoryTitle, int inventorySize) {
    	Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
    	
    	int autoSlot = 0;
    	for (ItemUtils.InventoryItem item : items) {
    		int targetSlot = item.slot == -1 ? autoSlot++ : item.slot;
    		
    	    UUID uuid = ItemUtils.getUUID(item.item);
    	    if (uuid == null) {
    	    	uuid = UUID.randomUUID();
    	    }
    	    ItemStack itemWithUUID = ItemUtils.setUUID(item.item, uuid);
    	    
    	    inventory.setItem(targetSlot, itemWithUUID);

    	    if (item.onInventoryClick != null) {
    	        ItemUtils.itemActionsInventory.put(uuid, (pl, clickedItem, event) -> item.onInventoryClick.accept(pl));
    	    } else if (globalClickListener != null) {
    	        ItemUtils.itemActionsInventory.put(uuid, (pl, clickedItem, event) -> globalClickListener.accept(pl, clickedItem, event));
    	    }
    	}
    	
        if (filler != null) {
            for (int i = 0; i < inventorySize; i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, filler.clone());
                }
            }
        }
        
        if (globalClickListener != null) {
            ItemUtils.inventoryCallbacks.put(inventory, globalClickListener);
        }
        return inventory;
    }
}