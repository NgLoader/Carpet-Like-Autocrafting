package de.ngloader.autocrafting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import de.ngloader.autocrafting.nms.api.AutocraftingRecipe;

public class AutoCraftingPlugin extends JavaPlugin implements Listener {

	private static final ItemStack ITEM_AIR = new ItemStack(Material.AIR);

	private final BukkitScheduler scheduler = Bukkit.getScheduler();

	@Override
	public void onEnable() {
		NMSInstance.initialize();

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onInventoryMove(InventoryMoveItemEvent event) {
		Inventory destination = event.getDestination();
		if (NMSInstance.isAutocraftingDispenser(destination)) {
			// Try to find a free inventory slot to place the item in.
			ItemStack item = event.getItem();
			for (int i = 0; i < 9; i++) {
				ItemStack validSlot = destination.getItem(i);
				if (validSlot == null || validSlot.getType() == Material.AIR) {
					destination.setItem(i, item);
					event.setItem(ITEM_AIR);
					return;
				}
			}

			/*
			 * We need to cancel the event because the inventory is full
			 * and we don't like to stack the items because it can place
			 * some items in the wrong order.
			 */
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockDispense(BlockDispenseEvent event) {
		Block block = event.getBlock();
		if (NMSInstance.isAutocraftingDispenser(block)) {
			event.setCancelled(true);

			/*
			 * We need to add a 1 tick delay for the processing because
			 * the event is called in a state where the item was removed
			 * from the source inventory.
			 */
			this.scheduler.runTask(this, () -> this.tryAutoCraft(block));
		}
	}

	/**
	 * When the dispenser is still valid to be used,
	 * it will try to craft the inserted recipe when available.
	 * 
	 * @param target dispenser block
	 */
	public void tryAutoCraft(Block block) {
		if (NMSInstance.isAutocraftingDispenser(block)) {
			org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) block.getState();

			CraftingResult craftedItem = this.autoCraftItem(dispenser);
			if (craftedItem != null) {
				Location facingBlock = NMSInstance.getBlockFacing(block).getLocation().add(0.5, 0.2, 0.5);

				World world = dispenser.getWorld();
				world.dropItem(facingBlock, craftedItem.resultItem);
				craftedItem.remainingItems.forEach(remainingItem -> world.dropItem(facingBlock, remainingItem));
			}
		}
	}

	/**
	 * Will craft the item if the recipe is available.
	 * The recipe item cost will be removed from the dispenser inventory.
	 * 
	 * @param target dispenser
	 * @return crafted item when the recipe was found else null
	 */
	public CraftingResult autoCraftItem(org.bukkit.block.Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();

		AutocraftingRecipe recipe = NMSInstance.getRecipe(inventory.getContents(), dispenser.getWorld());
		if (recipe == null) {
			return null;
		}

		for (int slot = 0; slot < 9; slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}

			int amount = item.getAmount();
			if (amount > 1) {
				item.setAmount(amount - 1);
			} else {
				inventory.setItem(slot, ITEM_AIR);
			}
		}

		CraftingResult result = new CraftingResult();
		result.resultItem = recipe.getResultItem();
		result.remainingItems = recipe.getRemainingItems(inventory);
		return result;
	}
}