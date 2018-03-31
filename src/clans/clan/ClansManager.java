package clans.clan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.chat.Chat;
import clans.energy.Energy;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.files.ClansFileManager;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.region.Region;
import clans.util.UtilGeneric;

public class ClansManager {

	private ClansManager() {}

	private static ClansManager instance = new ClansManager();

	public static ClansManager getInstance() {
		return instance;
	}

	private Map<String, Clan> clans = new HashMap<String, Clan>();
	private List<ClanInviteTask> invites = new ArrayList<ClanInviteTask>();
	private List<ClanAllyTask> allyRequests = new ArrayList<ClanAllyTask>();

	public void serializeAll() {
		for (Clan clan : clans.values()) {
			ClansFileManager.getInstance().createSection(clan.getName().toString(), clan.serialize());
		}
	}

	public void buildClanPlayer(ClansPlayer p) {
		Clan clan = p.getClan();
		if (clan == null) {
			return;
		}
		if (!clans.containsKey(clan.getName())) {
			clans.put(clan.getName(), clan);
			ClansMain.getInstance().getAllClans().add(clan.getName());
		}
		clan.getOnlinePlayers().add(p);
	}

	public void buildClansPlayers() {
		for (Clan clan : clans.values()) {
			for (String playerUUID : ClansFileManager.getInstance().<List<String>>get(clan.getName() + ".players")) {
				OfflinePlayer offPl = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));
				if (offPl.isOnline()) {
					clan.getOnlinePlayers().add(Players.getInstance().getCPlayerFrom(offPl));
				}
			}
		}

	}

	public void cleanClans() {
		Iterator<Clan> it = clans.values().iterator();
		while (it.hasNext()) {
			Clan clan = it.next();
			cleanClan(clan);
		}
	}

	public void buildClans() {
		if (ClansFileManager.getInstance().getConfig() == null) {
			return;
		}
		for (String sectionStr : ClansFileManager.getInstance().getConfig().getKeys(false)) {
			ConfigurationSection section = ClansFileManager.getInstance().getConfigurationSection(sectionStr);
			Clan clan = Clan.deserialize(section.getValues(true));
			clans.put(clan.getName(), clan);
			ClansMain.getInstance().getAllClans().add(clan.getName());
			clan.getTerritory().getChunks().forEach(chunk -> ClansMain.getInstance().getAllClaims().put(chunk, clan.getName()));
		}
	}

	public boolean exists(Clan clan) {
		if (clan == null) {
			return false;
		}
		if (ClansMain.getInstance().getAllClans() == null) {
			return false;
		}
		if (ClansMain.getInstance().getAllClans().isEmpty()) {
			return false;
		}
		return ClansMain.getInstance().getAllClans().contains(clan.getName());
	}

	public boolean isClanOnline(Clan clan) {
		if (clan == null) {
			return false;
		}
		if (clan.getOnlinePlayers() == null || clan.getOnlinePlayers().isEmpty() || clan.getOnlinePlayers().size() <= 0) {
			return false;
		}
		return true;
	}

	public Clan getClan(String name) {
		return clans.get(name);
	}

	public Clan getOfflineClan(String name) {
		ConfigurationSection section = ClansFileManager.getInstance().getConfigurationSection(name);
		if (section == null) {
			return null;
		}

		Clan clan = Clan.deserialize(section.getValues(true));
		return clan;
	}

	
	public Map<String, Clan> getClans() {
		return clans;
	}

	public void createClan(String clanName, ClansPlayer pl) {
		Clan clan = new Clan(clanName, pl);
		clans.put(clanName, clan);
		ClansMain.getInstance().getAllClans().add(clanName);
	}

	@SuppressWarnings("deprecation")
	public void removeClan(Clan clan) {
		clans.remove(clan.getName());
		for (Chunk c : clan.getTerritory().getChunks()) {
			ClansMain.getInstance().getAllClaims().remove(c);
		}
		clan.getTerritory().getChunks().clear();
		ClansMain.getInstance().getAllClans().remove(clan.getName());
		for (String str : clan.getAllAllies()) {
			Clan ally = ClansManager.getInstance().getOfflineClan(str);
			ally.removeAlly(clan, false);
			ClansFileManager.getInstance().createSection(ally.getName().toString(), ally.serialize());
		}
		clan.getAllPlayers().forEach((player) -> {
			if (player.isOnline()) {
				Player pl = Bukkit.getServer().getPlayer(player.getUniqueId());
				ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(pl);
				cPlayer.setClan(null);
				UtilGeneric.updateAddScoreboards(pl);
				ClansMain.getInstance().resetScoreboard(cPlayer);
			} else {
				clan.removePlayer(player);
			}
		});
		ClansFileManager.getInstance().set(clan.getName(), null);
	}

	public void messageClan(Clan clan, String message) {
		for (ClansPlayer cplayer : clan.getOnlinePlayers()) {
			Player player = Bukkit.getServer().getPlayer(cplayer.getUUID());
			if (player != null && player.isOnline()) {
				player.sendMessage(message);
			}
		}
	}


	public boolean isInvited(Clan clan, ClansPlayer player) {
		for (ClanInviteTask task : invites) {
			if (task.getInvited() == player && task.getClan() == clan) {
				return true;
			}
		}
		return false;
	}

	public void inviteEnd(ClanInviteTask clanInviteTask) {
		if (invites.remove(clanInviteTask)) {
			if (Bukkit.getServer().getPlayer(clanInviteTask.getInvited().getUUID()).isOnline() == false) {
				return;
			}
			Bukkit.getServer().getPlayer(clanInviteTask.getInvited().getUUID()).sendMessage(Chat.message("Your invitation to"
					+ " the clan " + ChatColor.YELLOW + clanInviteTask.getClan().getName() + ChatColor.GRAY + " has expired!"));
		}
	}

	public boolean joinClan(Clan clan, ClansPlayer p) {
		if (isInvited(clan, p)) {
			Iterator<ClanInviteTask> it = invites.iterator();
			while (it.hasNext()) {
				ClanInviteTask task = it.next();
				if (task.getClan() == clan && task.getInvited() == p) {
					messageClan(clan, Chat.message(ChatColor.YELLOW + p.getName()
							+ ChatColor.GRAY + " has joined your clan!"));
					clan.addPlayer(p);
					Bukkit.getServer().getPlayer(p.getUUID()).sendMessage(Chat.message("You joined " + ChatColor.YELLOW + 
							"Clan " + clan.getName()));
					UtilGeneric.updateAddScoreboards(Bukkit.getServer().getPlayer(p.getUUID()));
					task.cancel();
					it.remove();
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public void invitePlayer(ClansPlayer inviter, Clan callerClan, ClansPlayer cPlayerFrom) {
		Player pl = Bukkit.getServer().getPlayer(cPlayerFrom.getUUID());
		pl.sendMessage(Chat.message(ChatColor.YELLOW + inviter.getName() + ChatColor.GRAY + " invited you to join " +
		ChatColor.YELLOW + "Clan " + callerClan.getName() + ChatColor.GRAY + "."));
		pl.sendMessage(Chat.message("Type " + ChatColor.YELLOW + "/c join " + callerClan.getName() + ChatColor.GRAY + " to accept!"));
	
		ClanInviteTask task = new ClanInviteTask(callerClan, cPlayerFrom);
		task.runTaskLater(ClansMain.getInstance(), (long) 20 * 60); 
		invites.add(task);
	}

	public List<ClanInviteTask> getInvites() {
		return invites;
	}


	public void cleanClan(Clan clan) {
		if (clan.getOnlinePlayers() == null || clan.getOnlinePlayers().isEmpty() || clan.getOnlinePlayers().size() <= 0) {
			clans.remove(clan);
		}
	}

	public List<String> getAllClans() {
		return ClansMain.getInstance().getAllClans();
	}

	public boolean canClaim(Clan clan) {
		if (clan.getTerritory().getChunks().size() >= (Math.min(8, clan.getAllPlayers().size()+2))) {
			return false;
		}
		return true;
	}

	public void allyEnd(ClanAllyTask clanAllyTask) {
		allyRequests.remove(clanAllyTask);
	}

	
	public void acceptAlly(Clan clan, Clan toAlly) {
		clan.addAlly(toAlly);
		toAlly.addAlly(clan);
		ClansManager.getInstance().messageClan(toAlly, Chat.message("The clan " + clan.getName() + " has accepted your alliance."));
		ClansManager.getInstance().messageClan(clan, Chat.message("You are now allies with the clan " + toAlly.getName()));
		toAlly.getOnlinePlayers().forEach(p -> UtilGeneric.updateAddScoreboards(Bukkit.getServer().getPlayer(p.getUUID())));
		clan.getOnlinePlayers().forEach(p -> UtilGeneric.updateAddScoreboards(Bukkit.getServer().getPlayer(p.getUUID())));
	}

	public void allyClan(ClansPlayer sender, Clan callerClan, Clan toAlly) {
		for (ClanAllyTask task : allyRequests) {
			if (task.getRequestClan() == callerClan && task.getToAlly() == toAlly) {
				Bukkit.getServer().getPlayer(sender.getUUID()).sendMessage(Chat.message("Please do not spam alliance requests."));
				return;
			} else if (task.getRequestClan() == toAlly) {
				acceptAlly(callerClan, toAlly);
				return;
			}
		}
		ClansManager.getInstance().messageClan(toAlly, Chat.message("The clan " + ChatColor.YELLOW + 
				sender.getClan().getName() + ChatColor.GRAY + " has requested alliance with you."));
		ClansManager.getInstance().messageClan(sender.getClan(), Chat.message(sender.getName() 
				+ " has requested alliance with the clan " 
				+ ChatColor.YELLOW + 
				toAlly.getName()));
		ClanAllyTask task = new ClanAllyTask(callerClan, toAlly);
		task.runTaskLaterAsynchronously(ClansMain.getInstance(), (long) 20 * 60); 
		allyRequests.add(task);
	}

	public List<ClanAllyTask> getAllyRequests() {
		return allyRequests;
	}
	
	public void unclaim(ClansPlayer sender, Chunk chunk) {
		Clan clan = sender == null ? null : sender.getClan();
		String name = clan == null ? "" : clan.getName();
		Clan clanAt = ClansManager.getInstance().getClan(ClansMain.getInstance().getAllClaims().get(chunk));
		int size = clanAt.getTerritory().getChunks().size();
		if (!clanAt.getName().equals(name) && clanAt.getTerritory().getChunks().size() > clanAt.getMaximumTerritory()) {
			clanAt.unclaim(chunk, false, clan);
		} else if (clanAt == clan) {
			clanAt.unclaim(chunk, true, clan);
		} else {
			clanAt.unclaim(chunk, true, null);
		}
		if (size == 0) {
			for (ClansPlayer cpp : clanAt.getOnlinePlayers())
				ClansMain.getInstance().resetScoreboard(cpp);
		} else {
			for (ClansPlayer cppp : clanAt.getOnlinePlayers()) {
				Player pl = Bukkit.getServer().getPlayer(cppp.getUUID());
				ScoreboardChangeEvent changed = new ScoreboardChangeEvent(ScoreboardPosition.ENERGY, Energy.computeEnergyString(clanAt), "", pl);
				Bukkit.getServer().getPluginManager().callEvent(changed);
			}
		}
		ClansMain.getInstance().getAllClaims().remove(chunk);
		UtilGeneric.updateTerritory(chunk);
	}
	
	public void claim(ClansPlayer sender, Clan clan, Chunk chunk) {
		Player player = Bukkit.getServer().getPlayer(sender.getUUID());
		List<Block> blocks = new ArrayList<Block>();
		for (int i = 0; i < 15; i++) {
			for (int y = player.getLocation().getBlockX(); y > player.getLocation().getBlockY()-5; y--) {
				int j = 0;
				Block x1 = chunk.getBlock(i, y, j);
				Block z1 = chunk.getBlock(j, y, i);
				j = 15;
				Block x2 = chunk.getBlock(i, y, j);
				Block z2 = chunk.getBlock(j, y, i);
				
				for (Block b : blocks) {
					if (b.getType() == Material.AIR) {
						continue;
					}
					for (Entity e : player.getNearbyEntities(32, 32, 32)) {
						if (e instanceof Player) {
							Player p = (Player) e;
							p.sendBlockChange(b.getLocation(), Material.GLOWSTONE, (byte) 0);
						}
					}
				}
			}
		}
		
		int claims = clan.getTerritory().getChunks().size();
		
		ClansMain.getInstance().getAllClaims().put(chunk, clan.getName());
		clan.getTerritory().addChunk(chunk);
		if (claims == 0) {
			clan.setEnergy(Energy.BASE_START_ENERGY);
			for (ClansPlayer cp : sender.getClan().getOnlinePlayers()) {
				ClansMain.getInstance().resetScoreboard(cp);
			}
		} else {
			for (ClansPlayer cp : sender.getClan().getOnlinePlayers()) {
				Player pl = cp.getPlayer();
				ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.ENERGY, Energy.computeEnergyString(clan), "", pl);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
			
	}


}
