package clans.GUI;

import org.bukkit.inventory.ItemStack;

import clans.clan.Clan;

public class ClanManage extends InventoryPage {

	public ClanManage(InventoryPage lastInventory, Clan clan) {
		super(lastInventory, 6 * 9, "Manage Clan");
	}

	@Override
	protected ItemStack[] make(ItemStack[] currentItems) {
		
		return null;
	}

}
