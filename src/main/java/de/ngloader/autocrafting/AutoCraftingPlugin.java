package de.ngloader.autocrafting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AutoCraftingPlugin extends JavaPlugin implements Listener {

	private static final ItemStack ITEM_AIR = new ItemStack(Material.AIR);

	private final BukkitScheduler scheduler = Bukkit.getScheduler();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onInventoryMove(InventoryMoveItemEvent event) {
		Inventory destination = event.getDestination();
		// Check if the event is called from a block
		if (destination.getHolder() instanceof BlockInventoryHolder holder) {
			Block block = holder.getBlock();
			if (block == null || block.getType() != Material.DISPENSER) {
				return;
			}

			Block facing = this.getBlockFacing(block);
			if (facing == null || facing.getType() != Material.CRAFTING_TABLE) {
				return;
			}

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
		if (this.isAutocraftingDispenser(block)) {
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
		if (this.isAutocraftingDispenser(block)) {
			org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) block.getState();
			if (!dispenser.isPlaced()) {
				return;
			}

			ItemStack craftedItem = this.autoCraftItem(dispenser);
			if (craftedItem != null) {
				Location facingBlock = this.getBlockFacing(block).getLocation().add(0.5, 0.2, 0.5);
				dispenser.getWorld().dropItem(facingBlock, craftedItem);
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
	public ItemStack autoCraftItem(org.bukkit.block.Dispenser dispenser) {
		Inventory inventory = dispenser.getInventory();

		Recipe recipe = Bukkit.getCraftingRecipe(inventory.getContents(), dispenser.getWorld());
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

		return recipe.getResult();
	}

	/**
	 * Return the current face of a block.
	 * Will return null when the block data is not implements {@link Directional}.
	 * 
	 * @param block dispenser
	 * @return block face
	 */
	public BlockFace getBlockFace(Block block) {
		if (block != null && block.getBlockData() instanceof Directional directional) {
			return directional.getFacing();
		}
		return null;
	}

	/**
	 * Return the facing block.
	 * Will return null when the block data is not implements {@link Directional}.
	 * 
	 * @param block
	 * @return faced block
	 */
	public Block getBlockFacing(Block block) {
		BlockFace face = this.getBlockFace(block);
		return face != null ? block.getRelative(face) : null;
	}

	/**
	 * Return true if the current block type an dispenser and is facing towards a crafting table.
	 * 
	 * @param block dispenser
	 * @return block is valid
	 */
	public boolean isAutocraftingDispenser(Block block) {
		if (block != null
				&& block.getBlockData() instanceof Dispenser dispenser) {
			Block blockFacing = block.getRelative(dispenser.getFacing());
			return blockFacing != null && blockFacing.getType() == Material.CRAFTING_TABLE;
		}
		return false;
	}
}