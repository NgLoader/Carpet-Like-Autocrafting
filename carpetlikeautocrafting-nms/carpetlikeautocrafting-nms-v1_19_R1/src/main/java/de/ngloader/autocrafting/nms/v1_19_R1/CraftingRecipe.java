package de.ngloader.autocrafting.nms.v1_19_R1;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ngloader.autocrafting.nms.api.AutocraftingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public class CraftingRecipe implements AutocraftingRecipe {

	private final Recipe<Container> recipe;

	private final ItemStack resultItem;

	public CraftingRecipe(Recipe<Container> recipe) {
		this.recipe = recipe;

		this.resultItem = CraftItemStack.asBukkitCopy(recipe.getResultItem());
	}

	@Override
	public ItemStack getResultItem() {
		return this.resultItem;
	}

	@Override
	public List<ItemStack> getRemainingItems(Inventory inventory) {
		CraftInventory craftInventory = ((CraftInventory) inventory);
		return this.recipe.getRemainingItems(craftInventory.getInventory()).stream()
				.map(item -> CraftItemStack.asBukkitCopy(item))
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public boolean canCraftInDimensions(int rowWidth, int rowHeight) {
		return this.recipe.canCraftInDimensions(rowWidth, rowHeight);
	}
}