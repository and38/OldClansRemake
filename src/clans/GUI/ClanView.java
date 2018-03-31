package clans.GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import clans.GUI.InventoryItem.ClickResponse;
import clans.clan.Clan;
import clans.energy.Energy;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.Records;

public class ClanView extends InventoryPage {

	private Clan clan;
	private ClansPlayer opener;

	public ClanView(InventoryPage lastInventory, Clan clan, ClansPlayer opener) {
		super(lastInventory, 9 * 5, clan.getName());
		this.clan = clan;
		this.opener = opener;
	}

	@Override
	public ItemStack[] make(ItemStack[] currentItems) {
		InventoryItem cItem = InventoryItem.makeItem(new ItemStack(Records.RECORD_FAR.getMaterial()), (stack, player, click) -> {
			if (click == ClickType.SHIFT_LEFT)
				return ClickResponse.ACCEPT;
			return ClickResponse.NOTHING;
		});
		addCustomItem(cItem, 20);
		ItemStack icon = new ItemStack(Records.RECORD_STRAD.getMaterial());
		ItemMeta meta = icon.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + getName());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(attribute("Founder", clan.getFounder()));
		lore.add(attribute("Formed", "TODO"));
		lore.add(attribute("Members", clan.getOnlinePlayers().size() + "/" + 
				clan.getAllPlayers().size()));
		lore.add(attribute("Territory", clan.getTerritory().getChunks().size() + 
				"/" + clan.getMaximumTerritory()));
		lore.add("");
		lore.add(attribute("Energy", clan.getEnergy() + "/" + 
				clan.getMaximumEnergy()));
		if (clan.getTerritory().getChunks().size() > 0)
			lore.add(attribute("Energy Depletes", ChatColor.stripColor(Energy.computeEnergyString(clan))));
		lore.add("");
		if (clan.getAllAllies().isEmpty() == false) {
			lore.add(attribute("Allies", ""));
			String allies = " ";
			for (String clan : clan.getAllAllies()) {
				allies = allies + ChatColor.GREEN + clan + ChatColor.WHITE + ", ";
			}
			lore.add(allies);
		}
		meta.setLore(lore);
		icon.setItemMeta(meta);
		currentItems[4] = icon;
		doPlayers(currentItems);
		return currentItems;
	}

	private String attribute(String name, String value) {
		return ChatColor.YELLOW + name + " " + ChatColor.WHITE + value;
	}

	private void doPlayers(ItemStack[] inv) {
		List<OfflinePlayer> players = clan.getAllPlayers();
		List<ClansPlayer> playersOn = clan.getOnlinePlayers();
		List<OfflinePlayer> sortedOff = new ArrayList<OfflinePlayer>();
		List<Player> sortedOn = new ArrayList<Player>();;
		for (OfflinePlayer pl : players) {
			if (Bukkit.getServer().getPlayer(pl.getUniqueId()) != null) {
				continue;
			}
			sortedOff.add(Bukkit.getServer().getOfflinePlayer(pl.getUniqueId()));
		}
		for (ClansPlayer cP : playersOn) {
			sortedOn.add(Bukkit.getServer().getPlayer(cP.getUUID()));
		}
		Collections.sort(sortedOn, (item1, item2) -> {
			ClansPlayer cPlayerr = Players.getInstance().getCPlayerFrom(item1);
			ClansPlayer cPlayerrr = Players.getInstance().getCPlayerFrom(item2);
			
			return cPlayerrr.getRole().ordinal()-cPlayerr.getRole().ordinal();
			
		});
		Collections.sort(sortedOff, (item1, item2) -> {
			ClansPlayer cPlayerr = Players.getInstance().getCPlayerFrom(item1);
			ClansPlayer cPlayerrr = Players.getInstance().getCPlayerFrom(item2);
			
			return cPlayerrr.getRole().ordinal()-cPlayerr.getRole().ordinal();
			
		});
		List<OfflinePlayer> sortedOnf = new ArrayList<OfflinePlayer>();
		List<OfflinePlayer> sortedFinal = new ArrayList<OfflinePlayer>();
		sortedOn.forEach(pl -> sortedOnf.add(Bukkit.getServer().getOfflinePlayer(pl.getUniqueId())));
		sortedFinal.addAll(sortedOnf);
		sortedFinal.addAll(sortedOff);
		for (int slot = 0; slot < sortedFinal.size(); slot++) {
			int added = slot + (3*6);
			OfflinePlayer offPl = sortedFinal.get(slot);
			
			if (offPl.isOnline()) {
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setOwner(offPl.getName());
				meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + offPl.getName());
				List<String> lore = new ArrayList<String>();
				lore.add("");
				ClansPlayer cplaye = Players.getInstance().getCPlayerFrom(offPl);
				lore.add(attribute("Role", cplaye.getRole().getName()));
				if (this.opener.getClan() == clan) {
					Location loc = Bukkit.getServer().getPlayer(cplaye.getUUID()).getLocation();
					lore.add(attribute("Location", "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + 
					loc.getBlockZ() + ")"));
				}
				meta.setLore(lore);
				skull.setItemMeta(meta);
				inv[added] = skull;
			} else {
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal());
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + offPl.getName());
				List<String> lore = new ArrayList<String>();
				lore.add("");
				String cplaye = Players.getInstance().getRoleFromOff(offPl);
				lore.add(attribute("Role", cplaye));
				meta.setLore(lore);
				skull.setItemMeta(meta);
				inv[added] = skull;
			}
		}
	}

}
