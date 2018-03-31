package clans.listeners;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import clans.GUI.GUIs;
import clans.GUI.InventoryItem;
import clans.GUI.InventoryItem.ClickResponse;
import clans.GUI.InventoryPage;

public class InventoryListeners implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player pl = (Player) e.getWhoClicked();
		if (GUIs.getCurrentInventory(pl) != null && pl.getOpenInventory() != null) {
			if (e.getInventory() != pl.getOpenInventory().getTopInventory()) return;
			e.setCancelled(true);
			InventoryPage page = GUIs.getCurrentInventory(pl);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
				ItemStack stack = e.getCurrentItem();
				if (stack.isSimilar(InventoryPage.BED)) {
					GUIs.getLastInventory(pl).open(pl);
				} else if (!page.getCustomItems().keySet().contains(e.getSlot())) {
					GUIs.playClickSound(pl, ClickResponse.DENY);
				}
			} else {
				GUIs.playClickSound(pl, ClickResponse.DENY);
			}
			for (int slot : page.getCustomItems().keySet()) {
				if (e.getSlot() == slot) {
					InventoryItem item = page.getCustomItems().get(slot);
					ClickResponse success = item.click(pl, e.getClick());
					GUIs.playClickSound(pl, success);
				}
			}
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (GUIs.getCurrentInventory(p) != null) {
			GUIs.getCurrentInventory(p).close(p);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		GUIs.removePlayer(e.getPlayer());
	}
	
}
