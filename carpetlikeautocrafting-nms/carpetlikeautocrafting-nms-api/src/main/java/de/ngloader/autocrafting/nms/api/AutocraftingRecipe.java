package de.ngloader.autocrafting.nms.api;

import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface AutocraftingRecipe {

	public ItemStack getResultItem();

	public List<ItemStack> getRemainingItems(Inventory inventory);

	public boolean canCraftInDimensions(int rowWidth, int rowHeight);
}
