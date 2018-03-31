package clans.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import clans.GUI.InventoryItem.ClickResponse;

public interface Clicker {
	public ClickResponse click(ItemStack stack, Player player, ClickType click);
}
