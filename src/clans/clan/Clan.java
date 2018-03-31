package clans.clan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.energy.Energy;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.files.ClansFileManager;
import clans.files.PlayerFileManager;
import clans.player.ClansPlayer;
import clans.region.Region;
import clans.util.UtilGeneric;

public class Clan implements ConfigurationSerializable {
	
	public static final int MAX_PLAYERS = 20;
	
	
	private Region territory;
	private List<ClansPlayer> players;
	private List<UUID> allPlayers;
	private List<String> allies;
	private final String founder;
	private Location homeLocation;
	private int energy;
	private String name;
	private int energyCostPerMin;
	
	Clan(String name, ClansPlayer creator) {
		this.territory = new Region(this, new ArrayList<Chunk>());
		this.name = name;
		this.allies = new ArrayList<String>();
		this.players = new ArrayList<ClansPlayer>();
		this.allPlayers = new ArrayList<UUID>();
		this.founder = creator.getName();
		addPlayer(creator, ClanRole.LEADER);
	}
	
	@SuppressWarnings("unchecked")
	private Clan(Map<String, Object> map) {
		this.founder = (String) map.get("founder");
		this.name = (String) map.get("name");
		this.allies = (List<String>) map.get("allies");
		List<ClansPlayer> cplayers = new ArrayList<ClansPlayer>();
		this.players = cplayers;
		List<UUID> tempUUids = new ArrayList<UUID>();
		ClansFileManager.getInstance().getConfig().getStringList(this.name + ".players").forEach((name) -> tempUUids.add(UUID.fromString(name)));
		this.allPlayers = tempUUids;
		if (map.get("homeLocationX") != null) {
			this.homeLocation = new Location(Bukkit.getServer().getWorlds().get(0), (double) map.get("homeLocationX"), 
					(double) map.get("homeLocationY"), (double) map.get("homeLocationZ"));
		}
		this.homeLocation = null;
		this.energy = (int) map.get("energy");
		String chunkMap = (String) map.get("territory");
		this.territory = Region.fromSerliazedString(this, chunkMap);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", this.name);
		map.put("founder", this.founder);
		map.put("allies", this.allies);
		map.put("energy", energy);
		if (homeLocation != null) {
			map.put("homeLocationX", homeLocation.getX());
			map.put("homeLocationY", homeLocation.getY());
			map.put("homeLocationZ", homeLocation.getZ());
		}
		List<String> stringList = new ArrayList<String>();
		allPlayers.forEach((uuid) -> stringList.add(uuid.toString()));
		map.put("players", stringList);
		map.put("territory", territory.toSerializedString());
		
		return map;
	}
	
	public Region getTerritory() {
		return territory;
	}
	
	public void setTerritory(Region territory) {
		this.territory = territory;
		updateEnergyCost();
	}

	public List<OfflinePlayer> getAllPlayers() {
		if (allPlayers != null) {
			List<OfflinePlayer> list = new ArrayList<OfflinePlayer>();
			allPlayers.forEach((ap) -> list.add(Bukkit.getServer().getOfflinePlayer(ap)));
			return list;
		}
		List<OfflinePlayer> tempList = new ArrayList<OfflinePlayer>();
		for (Map<String, Object> linkedMap : ClansFileManager.getInstance().<List<Map<String, Object>>>get(getName() + ".players")) {
			OfflinePlayer offPl = (OfflinePlayer) ConfigurationSerialization.deserializeObject(linkedMap, OfflinePlayer.class);
			tempList.add(offPl);
		}
		return tempList;
	}
	
	public List<ClansPlayer> getOnlinePlayers() {
		return players;
	}
	
	@Deprecated
	public List<ClansPlayer> getPlayers() {
		return players;
	}

	public void addPlayer(ClansPlayer p, ClanRole role) {
		this.players.add(p);
		this.allPlayers.add(p.getUUID());
		p.setClan(this);
		p.setRole(role);
		updateEnergyCost();
	}
	
	public void addPlayer(ClansPlayer p) {
		addPlayer(p, ClanRole.RECRUIT);
	}
	
	public void removePlayer(ClansPlayer p) {
		this.players.remove(p);
		this.allPlayers.remove(p.getUUID());
		p.setClan(null);
		UtilGeneric.updateAddScoreboards(Bukkit.getServer().getPlayer(p.getUUID()));
		updateEnergyCost();
	}
	
	public void removePlayer(OfflinePlayer p) {
		this.allPlayers.remove(p.getUniqueId());
		PlayerFileManager.getInstance().getConfigurationSection(p.getUniqueId().toString()).set("clan", "");
		updateEnergyCost();
	}
	
	public void setPlayers(List<ClansPlayer> players) {
		this.players = players;
		players.forEach((cplayer) -> cplayer.setClan(this));
		updateEnergyCost();
	}

	public int getEnergy() {
		return energy;
	}
	
	public void setEnergy(int energy, boolean update) {
		this.energy = energy;
		if (update) {
			for (ClansPlayer p : players) {
				Player pl = p.getPlayer();
				ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.ENERGY, 
						Energy.computeEnergyString(this), "", pl);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}	
		}
	}

	public void setEnergy(int energy) {
		setEnergy(energy, true);
	}

	public static Clan deserialize(Map<String, Object> map) {	
		return new Clan(map);
	}

	public Location getHomeLocation() {
		return homeLocation;
	}
	
	public void setHomeLocation(Location location) {
		this.homeLocation = location;
	}

	public String getName() {
		return this.name;
	}
	
	public void addAlly(Clan clan) {
		this.allies.add(clan.getName());
	}
	
	public void removeAlly(Clan clan) {
		this.allies.remove(clan.getName());
	}
	
	public void setAllies(Clan... clans) {
		List<String> list = new ArrayList<String>();
		Arrays.asList(clans).forEach((clan) -> list.add(clan.getName()));
		this.allies = list;
	}
	
	public void updateEnergyCost() {
		energyCostPerMin = allPlayers.size() * territory.getChunks().size();
	}
	
	public int getEnergyCostPerMinute() {
		updateEnergyCost();
		return energyCostPerMin;
	}

	public boolean isAllyOf(Clan clan) {
		if (clan == null) {
			return false;
		}
		return allies.contains(clan.getName());
	}
	
	public String getRelationColor(Clan clan) {
		if (clan == null) {
			return Chat.CLAN_COLOR;
		}
		if (this.isAllyOf(clan)) {
			return Chat.PLAYER_COLOR_ALLY;
		} else if (name == clan.getName()) {
			return Chat.PLAYER_COLOR_CLAN;
		} else {
			return Chat.PLAYER_COLOR;
		}
	}
	
	public String getClanRelationColor(Clan clan) {
		if (clan == null) {
			return Chat.CLAN_COLOR;
		}
		if (this.isAllyOf(clan)) {
			return Chat.PLAYER_COLOR_ALLY;
		} else if (name == clan.getName()) {
			return Chat.PLAYER_COLOR_CLAN;
		} else {
			return Chat.CLAN_COLOR;
		}
	}

	public void unclaim(Chunk chunk, boolean silent, Clan unclaimer) {
		if (territory.getChunks().contains(chunk)) {
			territory.getChunks().remove(chunk);
			if (!silent) {
				Bukkit.getServer().broadcastMessage(Chat.messageFill("Territory", "The territory " + ChatColor.YELLOW + "(" + 
			String.valueOf(chunk.getX()) + String.valueOf(chunk.getZ()) + ")" + ChatColor.GRAY + " has been unclaimed" +
						(unclaimer != null ? " by the clan " + unclaimer.getName() + "." : ".")));
			}
		}
	}

	public List<String> getAllAllies() {
		return allies;
	}
	
	public List<Clan> getOnlineAllies() {
		List<Clan> tempList = new ArrayList<Clan>();
		for (Clan clan : ClansManager.getInstance().getClans().values()) {
			if (allies.contains(clan.getName())) {
				tempList.add(clan);
			}
		}
		return tempList;
	}

	public void removeAlly(Clan ally, boolean announce) {
		removeAlly(ally);
		if (announce) {
			ClansManager.getInstance().messageClan(ally, Chat.message("The clan " + ChatColor.YELLOW + name + ChatColor.GRAY
					+ " has revoked their alliance with you."));
			ClansManager.getInstance().messageClan(this, Chat.message("You have revoked your alliance with the clan " +
					ChatColor.YELLOW + ally.getName()));
		}
	}

	public String getFounder() {
		return new String(founder);
	}

	public int getMaximumTerritory() {
		return Math.min(8, allPlayers.size()+2);
	}

	public int getMaximumEnergy() {
		return Energy.BASE_MAX_ENERGY * allPlayers.size();
	}

}
