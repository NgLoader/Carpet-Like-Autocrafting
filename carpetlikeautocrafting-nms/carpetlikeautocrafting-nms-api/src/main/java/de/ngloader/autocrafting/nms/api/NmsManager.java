package de.ngloader.autocrafting.nms.api;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

public interface NmsManager {

	public AutocraftingRecipe getRecipe(ItemStack[] content, World world);

	/**
	 * Return true if the current inventory from an dispenser and is facing towards a crafting table.
	 * 
	 * @param inventory from dispenser
	 * @return block is valid
	 */
	public boolean isAutocraftingDispenser(Inventory inventory);

	/**
	 * Return true if the current block type an dispenser and is facing towards a crafting table.
	 * 
	 * @param block dispenser
	 * @return block is valid
	 */
	public boolean isAutocraftingDispenser(Block block);

	/**
	 * Return the current face of a block.
	 * Will return null when the block data is not implements {@link Directional}.
	 * 
	 * @param block dispenser
	 * @return block face
	 */
	public BlockFace getBlockFace(Block block);

	/**
	 * Return the facing block.
	 * Will return null when the block data is not implements {@link Directional}.
	 * 
	 * @param block
	 * @return faced block
	 */
	public Block getBlockFacing(Block block);
}
