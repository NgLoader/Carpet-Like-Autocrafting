package de.ngloader.autocrafting;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ngloader.autocrafting.nms.api.AutocraftingRecipe;
import de.ngloader.autocrafting.nms.api.NmsManager;
import de.ngloader.autocrafting.util.MinecraftVersion;

public class NMSInstance {

	private static NmsManager manager;

	static boolean initialize() {
		if (manager != null) {
			return false;
		}

		String nmsVersion = MinecraftVersion.getNmsVersion();
		try {
			System.out.println(String.format("Checking avavible NNS version \"%s\".", nmsVersion));
			Class<?> nmsClass = Class.forName(String.format("de.ngloader.autocrafting.nms.%s.NmsManagerInstance", nmsVersion));
			manager = (NmsManager) nmsClass.getConstructors()[0].newInstance();
			System.out.println(String.format("NMS version \"%s\" was found.", nmsVersion));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(String.format("NMS version \"%s\" is not supported.", nmsVersion));
		return false;
	}

	public static AutocraftingRecipe getRecipe(ItemStack[] content, World world) {
		return manager.getRecipe(content, world);
	}

	public static boolean isAutocraftingDispenser(Inventory inventory) {
		return manager.isAutocraftingDispenser(inventory);
	}

	public static boolean isAutocraftingDispenser(Block block) {
		return manager.isAutocraftingDispenser(block);
	}

	public static BlockFace getBlockFace(Block block) {
		return manager.getBlockFace(block);
	}

	public static Block getBlockFacing(Block block) {
		return manager.getBlockFacing(block);
	}
}