package clans.player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;

public class ClansPlayer implements ConfigurationSerializable {

	private int gold;
	private Clan clan;
	private UUID uuid;
	private String name;
	private boolean clanChat;
	private boolean allyChat;
	private ClanRole role;

	protected ClansPlayer(Player player) {
		this.gold = 0;
		this.clan = null;
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.role = ClanRole.NO_CLAN;
	}


	private ClansPlayer(Map<String, Object> map) {
		this.gold = (int) map.get("gold");
		this.uuid = UUID.fromString((String) map.get("uuid"));
		this.name = (String) map.get("name");
		this.role = (ClanRole) ClanRole.valueOf((String) map.get("role"));
		if (map.get("clan").equals("")) {
			this.clan = null;
		} else {
			String clanName = (String) map.get("clan");
			this.clan = ClansManager.getInstance().getClan(clanName);
		}
	}


	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put("gold", this.gold);
		map.put("uuid", this.uuid.toString());
		map.put("name", this.name);
		map.put("role", role.toString());
		if (this.clan == null) {
			map.put("clan", "");
			return map;
		}
		map.put("clan", this.clan.getName());
		return map;
	}

	public static ClansPlayer deserialize(Map<String, Object> map) {
		ClansPlayer cplayer = new ClansPlayer(map);
		Players.getInstance().registerCPlayer(cplayer);
		return cplayer;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	public Clan getClan() {
		return clan;
	}
	
	public String getClanString() {
		if (clan == null) {
			return "";
		}
		return clan.getName() + " ";
	}

	public void setClan(Clan clan) {
		this.clan = clan;
		role = ClanRole.RECRUIT;
	}

	public void changeGold(int gold, boolean silent) {
		int saveGold = this.gold;
		this.gold += gold;
		ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.GOLD, ChatColor.GOLD + String.valueOf(this.gold), 
				ChatColor.GOLD + String.valueOf(saveGold), Bukkit.getServer().getPlayer(uuid));
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!silent) {
			Player pl = Bukkit.getServer().getPlayer(uuid);
			if (pl != null) {
				pl.sendMessage(Chat.message("You have picked up " + ChatColor.YELLOW + String.valueOf(gold) + 
						ChatColor.GRAY + " gold."));
			}
		}
	}
	
	public void changeGold(int gold, boolean silent, boolean death) {
		int saveGold = this.gold;
		
		this.gold += gold;
		ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.GOLD, ChatColor.GOLD + String.valueOf(this.gold), 
				ChatColor.GOLD + String.valueOf(saveGold), Bukkit.getServer().getPlayer(uuid));
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!silent) {
			Player pl = Bukkit.getServer().getPlayer(uuid);
			if (pl != null && death == false) {
				pl.sendMessage(Chat.gold("You have picked up " + ChatColor.YELLOW + String.valueOf(gold) + 
						ChatColor.GRAY + " gold."));
			} else if (pl != null && death == true) {
				pl.sendMessage(Chat.gold("You dropped " + ChatColor.YELLOW + String.valueOf(gold)
						+ ChatColor.GRAY + " on your death!"));
			}
		}
	}

	public int getGold() {
		return gold;
	}


	public void setGold(int gold) {
		int saveGold = this.gold;
		this.gold = gold;
		ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.GOLD, ChatColor.GOLD + 
				String.valueOf(this.gold), 
				ChatColor.GOLD + String.valueOf(saveGold), Bukkit.getServer().getPlayer(uuid));
		Bukkit.getServer().getPluginManager().callEvent(event);
	}


	public boolean isClanChat() {
		return clanChat;
	}


	public void setClanChat(boolean clanChat) {
		this.clanChat = clanChat;
	}


	public boolean isAllyChat() {
		return allyChat;
	}


	public void setAllyChat(boolean allyChat) {
		this.allyChat = allyChat;
	}
	
	@Override
	public String toString() {
		String string = "ClansPlayer[" + name + ":" + uuid.toString() + ":" + String.valueOf(gold) + ":" + 
	(clan == null ? "" : clan.getName()) + ":" + String.valueOf(allyChat) + ":" + String.valueOf(clanChat)  + ":" + 
	role.toString() + "]";
		return string;
	}
	
	public ClanRole getRole() {
		return role;
	}

	public void setRole(ClanRole role) {
		this.role = role;
	}
	
	public void promote(boolean silent) {
		for (ClanRole role : ClanRole.values()) {
			if (this.role.ordinal()+1 == role.ordinal()) {
				this.role = role;
				if (!silent) {
					Player p = Bukkit.getServer().getPlayer(uuid);
					if (p != null) {
						ClansManager.getInstance().messageClan(clan, ChatColor.YELLOW + name + ChatColor.GRAY + " has"
								+ " been promoted to " + ChatColor.YELLOW + role.getName());
					}
				}
				return;
			}
		}
	}
	
	public Player getPlayer() {
		return Bukkit.getServer().getPlayer(uuid);
	}
	
}
