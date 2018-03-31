package clans.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.energy.Energy;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanUnclaim extends SubCommand {

	public ClanUnclaim() {
		super("unclaim", new String[0], new String[]{"all"}, ClanRole.ADMIN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer.getClan() == null) {
			player.sendMessage(Chat.message("You are not in a Clan."));
			return;
		}
		
		if (!checkRole(player)) {
			player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
					+ " command."));
			return;
		}
		
		String name = ClansMain.getInstance().getAllClaims().get
				(player.getLocation().getChunk());
		
		Clan clanAtLoc = ClansManager.getInstance().getClan(name);
		
		if (args.length == 1) {
			if (name == null) {
				player.sendMessage(Chat.message("This Territory is not owned by you."));
				return;
			}
			if (clanAtLoc != cPlayer.getClan()) {
				if (clanAtLoc.getTerritory().getChunks().size() > clanAtLoc.getMaximumTerritory()) {
					ClansManager.getInstance().unclaim(cPlayer, player.getLocation().getChunk());
				}
			} else {
				ClansManager.getInstance().unclaim(cPlayer, player.getLocation().getChunk());
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("all") && cPlayer.getRole() == ClanRole.LEADER) {
				if (cPlayer.getClan().getTerritory().getChunks().size() <= 0) {
					player.sendMessage(Chat.message("You do not have any Territory."));
					return;
				}
				cPlayer.getClan().getTerritory().unclaimAll(cPlayer);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Unclaim Territory";
	}
	
}
