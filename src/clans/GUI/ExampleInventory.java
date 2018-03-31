package clans.GUI;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import clans.GUI.InventoryItem.ClickResponse;

public class ExampleInventory extends InventoryPage{

	public ExampleInventory(InventoryPage lastInventory) {
		super(lastInventory, 5 * 9, "Example");
	}

	@Override
	protected ItemStack[] make(ItemStack[] currentItems) {
		addCustomItem(InventoryItem.makeItem(new ItemStack(Material.COAL), (itemStack, player, click) -> {
			player.sendMessage("hello!");
			return ClickResponse.NOTHING;
		}), 5);
		currentItems[12] = new ItemStack(Material.ACACIA_DOOR_ITEM);
		return currentItems;
	}

}
 