package de.ngloader.autocrafting.nms.v1_19_R1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.Directional;

import de.ngloader.autocrafting.nms.api.AutocraftingRecipe;
import de.ngloader.autocrafting.nms.api.NmsManager;

public class NmsManagerInstance implements NmsManager {

	public NmsManagerInstance() {
	}

	@Override
	public AutocraftingRecipe getRecipe(ItemStack[] content, World world) {
		Recipe recipe = Bukkit.getCraftingRecipe(content, world);
		if (recipe == null) {
			return null;
		}

		
		// TODO get nms recipe
		return null;
	}

	@Override
	public boolean isAutocraftingDispenser(Inventory inventory) {
		if (inventory.getHolder() instanceof BlockInventoryHolder holder) {
			Block block = holder.getBlock();
			return block != null ? this.isAutocraftingDispenser(block) : false;
		}
		return false;
	}

	@Override
	public boolean isAutocraftingDispenser(Block block) {
		if (block != null
				&& block.getBlockData() instanceof Dispenser dispenser) {
			org.bukkit.block.Dispenser bukkitDispenser = (org.bukkit.block.Dispenser) block.getState();
			if (!bukkitDispenser.isPlaced()) {
				return false;
			}
			
			Block blockFacing = block.getRelative(dispenser.getFacing());
			return blockFacing != null && blockFacing.getType() == Material.CRAFTING_TABLE;
		}
		return false;
	}

	@Override
	public BlockFace getBlockFace(Block block) {
		if (block != null && block.getBlockData() instanceof Directional directional) {
			return directional.getFacing();
		}
		return null;
	}

	@Override
	public Block getBlockFacing(Block block) {
		BlockFace face = this.getBlockFace(block);
		return face != null ? block.getRelative(face) : null;
	}
}