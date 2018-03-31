package clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import clans.ClansMain;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.player.Players;
import clans.util.UtilGeneric;

public class ConfigListeners implements Listener {

	private ClansMain main;

	public ConfigListeners(ClansMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		if (ClansMain.getInstance().getTasks().get(p.getName()) != null) {
			ClansMain.getInstance().getTasks().get(p.getName()).cancel();
			ClansMain.getInstance().getTasks().remove(p.getName());
		}
		ClansMain.getInstance().removeScoreboard(p);
		if (Players.getInstance().isCPlayer(p)) {
			Clan clan = Players.getInstance().getCPlayerFrom(p).getClan();
			if (clan != null) {
				clan.getOnlinePlayers().remove(Players.getInstance().getCPlayerFrom(p));
			}
			Players.getInstance().serializePlayer(Players.getInstance().getCPlayerFrom(p));
		}
	}

	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (Bukkit.getServer().getWhitelistedPlayers().contains(Bukkit.getServer().getOfflinePlayer(e.getPlayer().getUniqueId())))
			return;
		if (!ClansMain.getInstance().isMaintenance())
			return;
		e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Kicked whilst connecting to " + 
			e.getHostname() + ": " + ChatColor.WHITE + 
				"This server is currently down for maintenance.");
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (Players.getInstance().buildPlayer(p) == false) {
			Players.getInstance().createClansPlayer(p);
		}
		
		ClansManager.getInstance().buildClanPlayer(Players.getInstance().getCPlayerFrom(p));
		UtilGeneric.sendHeaderFooter(p);
		UtilGeneric.updateAddScoreboards(p);
		ClansMain.getInstance().addScoreboardFor(p);
		p.setScoreboard(ClansMain.getInstance().getScoreboardFor(p));
		new BukkitRunnable() {
			public void run() {
				UtilGeneric.updateTerritory(p.getLocation().getChunk());
			}
		}.runTaskLater(ClansMain.getInstance(), 20 * 1);
	}
	
}
