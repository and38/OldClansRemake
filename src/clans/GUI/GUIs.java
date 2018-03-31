package clans.GUI;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import clans.GUI.InventoryItem.ClickResponse;
import clans.clan.Clan;
import clans.player.Players;

public class GUIs {

	private static HashMap<UUID, InventoryPage> lastInventory = new HashMap<>();
	private static HashMap<UUID, InventoryPage> currentInventory = new HashMap<>();

	public static void openInventory(Player p, Clan clan, InventoryPage inventory) {
		inventory.open(p);
	}
	
	public static void openSearch(Player p, Clan clan) {
		if (clan == null) {
			return;
		}
		ClanView inventory = new ClanView(null, clan, Players.getInstance().getCPlayerFrom(p));
		inventory.open(p);
	}

	public static void openClanMenu(Player p, Clan clan) {
		if (clan == null) {
			return;
		}
		ClanManage inventory = new ClanManage(null, clan);
		inventory.open(p);
	}

	public static void openLastInventory(Player p, InventoryPage page) {
		page.open(p);
	}

	public static InventoryPage getCurrentInventory(Player p) {
		return currentInventory.get(p.getUniqueId());
	}

	public static void setCurrentInventory(HashMap<UUID, InventoryPage> currentInventoryy) {
		currentInventory = currentInventoryy;
	}

	public static InventoryPage getLastInventory(Player p) {
		return lastInventory.get(p.getUniqueId());
	}

	public static void setLastInventory(HashMap<UUID, InventoryPage> lastInventoryy) {
		lastInventory = lastInventoryy;
	}

	public static void setLastInventory(Player p, InventoryPage inventory) {
		lastInventory.put(p.getUniqueId(), inventory);
	}

	public static void setInventory(Player p, InventoryPage inventory) {
		currentInventory.put(p.getUniqueId(), inventory);
	}
	
	public static void removePlayer(Player player) {
		currentInventory.remove(player);
		lastInventory.remove(player);
	}
	
	public static void closed(Player player) {
		lastInventory.remove(player);
		currentInventory.remove(player);
	}

	//Sounds are the exact ones from Mineplex. They have to be xD
	public static void playClickSound(Player player, ClickResponse click) {
		switch (click) {
		case ACCEPT:
			player.playSound(player.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1.6f);
			return;
		case DENY:
			player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, .6f);
			return;
		case REMOVE:
			player.playSound(player.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 0.6f);
			return;
		default:
			return;
		}
	}
	//Mineplex End


}
