package clans.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ChatListeners implements Listener {
	
	private ClansMain main;

	public ChatListeners(ClansMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + e.getPlayer().getName());
	}
	
	@EventHandler
	public void onJoin(PlayerQuitEvent e) {
		e.setQuitMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + e.getPlayer().getName());
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(p);
		if (cPlayer == null) {
			return;
		}
		e.setCancelled(true);
		if (cPlayer.isClanChat()) {
			for (ClansPlayer pl : cPlayer.getClan().getOnlinePlayers()) {
				Player player = pl.getPlayer();
				player.sendMessage(Chat.clanMessage(p, e.getMessage()));
			}
			return;
		} else if (cPlayer.isAllyChat()) {
			for (Clan clan : cPlayer.getClan().getOnlineAllies()) {
				for (ClansPlayer pl : clan.getOnlinePlayers()) {
					Player player = pl.getPlayer();
					player.sendMessage(Chat.allyMessage(p, e.getMessage()));
				}
			}
			return;
		}
		for (Player rec : e.getRecipients()) {
			Chat.mainChatMessage(p, rec, e.getMessage());
		}
	}
	
}
