package clans.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import clans.clan.ClansManager;
import clans.files.PlayerFileManager;


public class Players {

	private Players() {}

	private static Players instance = new Players();

	public static Players getInstance() {
		return instance;
	}

	public Set<ClansPlayer> players = new HashSet<ClansPlayer>();


	public ClansPlayer getCPlayerFrom(Player player) {
		for (ClansPlayer pl : players) {
			if (pl.getUUID().equals(player.getUniqueId())) {
				return pl;
			}
		}
		return null;
	}

	public ClansPlayer getCPlayerFrom(OfflinePlayer player) {
		for (ClansPlayer pl : players) {
			if (pl.getUUID().equals(player.getUniqueId())) {
				return pl;
			}
		}
		return null;
	}
	
	public String getRoleFromOff(OfflinePlayer player) {
		ConfigurationSection section = PlayerFileManager.getInstance().get(player.getUniqueId().toString());
		if (section != null) {
			String s = section.getString("role").toLowerCase().substring(0, 1).toUpperCase();
			return s + section.getString("role").substring(1).toLowerCase();
		}
		return null;
	}

	public void registerCPlayer(ClansPlayer cplayer) {
		if (players.contains(cplayer)) {
			return;
		}
		players.add(cplayer);
	}

	public boolean isCPlayer(Player player) {
		return getCPlayerFrom(player) == null ? false : true;
	}

	public ClansPlayer createClansPlayer(Player player) {
		if (isCPlayer(player)) {
			return getCPlayerFrom(player);
		}
		ClansPlayer clansPlayer = new ClansPlayer(player);
		registerCPlayer(clansPlayer);
		return clansPlayer;
	}

	public void serializePlayer(ClansPlayer p) {
		if (p.getClan() != null) {
			if (p.getClan().getOnlinePlayers().size() <= 0) {
				ClansManager.getInstance().cleanClan(p.getClan());
			}
		}
		
		PlayerFileManager.getInstance().createSection(p.getUUID().toString(), p.serialize());
		players.remove(p);
	}

	public void serializeAll() {
		for (ClansPlayer player : players) {
			PlayerFileManager.getInstance().createSection(player.getUUID().toString(), player.serialize());
		}
	}

	public boolean buildPlayer(Player p) {
		if (PlayerFileManager.getInstance().getConfig() == null) {
			return false;
		}
		
		boolean found = false;
		for (String sectionStr : PlayerFileManager.getInstance().getConfig().getKeys(false)) {
			ConfigurationSection section = PlayerFileManager.getInstance().getConfigurationSection(sectionStr);
			if (!p.getUniqueId().equals(UUID.fromString(section.getName()))) {
				continue;
			}
			found = true;
			if (Bukkit.getServer().getPlayer(UUID.fromString(section.getName())) != null) {
				ClansPlayer hope = (ClansPlayer) ConfigurationSerialization.deserializeObject(section.getValues(true),
						ClansPlayer.class);
				if (hope == null) {
					hope = ClansPlayer.deserialize(section.getValues(true));
				}
				players.add(hope);
			}
		}
		return found;
	}
	
	public void buildPlayers() {
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			buildPlayer(pl);
		}
	}

	public Set<ClansPlayer> getPlayers() {
		return players;
	}

}
