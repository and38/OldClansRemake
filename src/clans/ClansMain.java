package clans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import clans.clan.ClansManager;
import clans.combat.CombatManager;
import clans.commands.ChatCommandAlly;
import clans.commands.ChatCommandClan;
import clans.commands.ClanCommand;
import clans.commands.GoldCommand;
import clans.commands.MaintenanceCommand;
import clans.commands.SubCommand;
import clans.economy.Economy;
import clans.energy.Energy;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.files.ClansFileManager;
import clans.files.FileManager;
import clans.files.PlayerFileManager;
import clans.listeners.ChatListeners;
import clans.listeners.ClaimEvents;
import clans.listeners.ConfigListeners;
import clans.listeners.GenericListeners;
import clans.listeners.InventoryListeners;
import clans.listeners.PvpListeners;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.ScoreboardRunnable;
import clans.util.UtilGeneric;

public class ClansMain extends JavaPlugin {

	private static ClansMain instance;
	
	private ArrayList<String> allClans = new ArrayList<String>();
	private Map<Chunk, String> allClaims = new HashMap<Chunk, String>();
	private HashMap<String, ScoreboardRunnable> tasks = new HashMap<>();
	private HashMap<UUID, Scoreboard> scoreboards;
	
	private boolean maintenance;
	private int season;
	private String networkName;
	
	private CombatManager combatManager;
	private Economy economy;
	private Energy energy;

	public static ClansMain getInstance() {
		if (instance == null) {
			throw new RuntimeException("Getting instance before plugin is enabled");
		}
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		
		this.economy = new Economy();
		this.combatManager = new CombatManager(this);
		this.energy = new Energy(this);
		
		saveDefaultConfig();
		setMaintenance(getConfig().getBoolean("maintenance", false));
		this.season = getConfig().getInt("season", 1);
		this.networkName = getConfig().getString("network-name", "TeamX");
		scoreboards = new HashMap<UUID, Scoreboard>();
		//TODO add /freeze
		FileManager.setManagers(ClansFileManager.getInstance(), PlayerFileManager.getInstance());
		FileManager.setupManagers(this);
		ClansManager.getInstance().buildClans();
		Players.getInstance().buildPlayers();
		ClansManager.getInstance().buildClansPlayers();
		//ClansManager.getInstance().cleanClans();
		//Dont re-add this
		registerListeners();
		getCommand("cc").setExecutor(new ChatCommandClan());
		getCommand("clan").setExecutor(new ClanCommand());
		getCommand("ac").setExecutor(new ChatCommandAlly());
		getCommand("maintenance").setExecutor(new MaintenanceCommand());
		getCommand("gold").setExecutor(new GoldCommand());
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			if (Players.getInstance().isCPlayer(player) == false) {
				Players.getInstance().createClansPlayer(player);
			}
			addScoreboardFor(player);
			player.setScoreboard(ClansMain.getInstance().getScoreboardFor(player));
			UtilGeneric.updateTerritory(player.getLocation().getChunk());
		}
	}

	public int getSeason() {
		return season;
	}

	public String getNetworkName() {
		return networkName;
	}

	@Override
	public void onDisable() {
		saveConfig();
		ClansManager.getInstance().serializeAll();
		Players.getInstance().serializeAll();
		FileManager.saveAll();
		SubCommand.unload();
		instance = null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("energy") && args.length == 1) {
			int x = 0;
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return true;
			}
			ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
			if (cPlayer.getClan() != null) {
				if (cPlayer.getClan().getTerritory().getChunks().size() > 0) {
					cPlayer.getClan().setEnergy(x);
				}
			}
		}
		return true;
	}

	
	private void registerListeners() {
		Bukkit.getServer().getPluginManager().registerEvents(new ConfigListeners(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ChatListeners(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PvpListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ClaimEvents(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GenericListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
	}

	public List<String> getAllClans() {
		return allClans;
	}

	public Map<Chunk, String> getAllClaims() {
		return allClaims;
	}
	
	public Scoreboard getScoreboardFor(Player p) {
		return scoreboards.get(p.getUniqueId());
	}
	
	private void setUpScoreboard(Player p, Scoreboard sc) {
		sc.registerNewObjective("clanSidebar", "dummy").setDisplaySlot(DisplaySlot.SIDEBAR);
		ClansPlayer cplayer = Players.getInstance().getCPlayerFrom(p);
		
		//Solid scores
		if (cplayer.getClan() != null) {
			if (cplayer.getClan().getTerritory().getChunks().size() > 0) {
				setScore(sc, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Clan Energy", 12);
				setScore(sc, "", 10);
			}
		}
		
		setScore(sc, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Clan", 15);
		setScore(sc, " ", 13);
		setScore(sc, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Gold", 9);
		setScore(sc, "  ", 7);
		setScore(sc, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Territory", 6);
		
		//Updating scores. The entries above are just constants.
		String clanname = ClansMain.getInstance().getAllClaims().get(p.getLocation().getBlock().getChunk());
		ClansPlayer cp = Players.getInstance().getCPlayerFrom(p);
		String value = clanname == null ? ChatColor.GRAY + "Wilderness" : ClansManager.getInstance().getClan(clanname).getRelationColor(cp.getClan());
		
		registerUpdatingScore(sc, ScoreboardPosition.TERRITORY, value);
		registerUpdatingScore(sc, ScoreboardPosition.GOLD, 
				ChatColor.GOLD + String.valueOf(Players.getInstance().getCPlayerFrom(p).getGold()));
		if (cplayer.getClan() != null)
			if (cplayer.getClan().getTerritory().getChunks().size() > 0)
				registerUpdatingScore(sc, ScoreboardPosition.ENERGY, ChatColor.GREEN + Energy.computeEnergyString(Players.getInstance().getCPlayerFrom(p).getClan()));
		
		String name = Players.getInstance().getCPlayerFrom(p).getClan() == null ? ChatColor.WHITE + "No Clan" : 
			Players.getInstance().getCPlayerFrom(p).getClan().getName();
		registerUpdatingScore(sc, ScoreboardPosition.CLAN, ChatColor.AQUA + name);
		
		ScoreboardRunnable runnable = new ScoreboardRunnable(scoreboards.get(p.getUniqueId()).getObjective(DisplaySlot.SIDEBAR));
		runnable.runTaskTimerAsynchronously(this, 0, 7);
		tasks.put(p.getName(), runnable);
		
		p.setScoreboard(sc);
	}

	public void addScoreboardFor(Player p) {
		Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		scoreboards.put(p.getUniqueId(), board);
		setUpScoreboard(p, board);
	}
	
	public void removeScoreboard(Player p) {
		scoreboards.remove(p.getUniqueId());
	}
	
	public void resetScoreboard(ClansPlayer cplayer) {
		Player player = Bukkit.getServer().getPlayer(cplayer.getUUID());
		if (tasks.get(player.getName()) != null)
			tasks.get(player.getName()).cancel();
		tasks.remove(player.getName());
		removeScoreboard(player);
		addScoreboardFor(player);
	}

	public HashMap<String, ScoreboardRunnable> getTasks() {
		return tasks;
	}

	public boolean isMaintenance() {
		return maintenance;
	}

	public void setMaintenance(boolean maintenance) {
		this.maintenance = maintenance;
	}
	
	private void setScore(Scoreboard sc, String str, int scorePos) {
		Score score = sc.getObjective(DisplaySlot.SIDEBAR).getScore(str);
		score.setScore(scorePos);
	}
	
	private void registerUpdatingScore(Scoreboard sc, ScoreboardPosition pos, String value) {
		Team territoryUpdate = sc.registerNewTeam(pos.getTeamName());
		territoryUpdate.addEntry(pos.getEntry());
		sc.getObjective(DisplaySlot.SIDEBAR).getScore(pos.getEntry()).setScore(pos.getScore());
		territoryUpdate.setPrefix(value);
	}
	
	public CombatManager getCombatManager() {
		return combatManager;
	}
	
	public Economy getEconomy() {
		return economy;
	}

	public Energy getEnergy() {
		return energy;
	}


}
