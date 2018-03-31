package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.UtilGeneric;

public class ClanDisband extends SubCommand {

	public ClanDisband() {
		super("disband", new String[0], new String[0], ClanRole.LEADER);
		
	}

	@Override
	public void onCommand(Player player, String[] args) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		Clan clan = cPlayer.getClan();
		if (clan == null) {
			player.sendMessage(Chat.message("You are not in a clan."));
			return;
		}
		
		if (!checkRole(player)) {
			player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
					+ " command."));
			return;
		}
		
		if (cPlayer.getRole() != ClanRole.LEADER) {
			player.sendMessage(Chat.message("You must be Leader to disband your clan."));
			return;
		}
		
		ClansManager.getInstance().messageClan(clan, Chat.message(ChatColor.YELLOW + player.getName() + 
				ChatColor.GRAY + " has disbanded the Clan."));
		ClansManager.getInstance().removeClan(clan);
		player.sendMessage(Chat.message("You disbanded your Clan."));
		UtilGeneric.updateAddScoreboards(player);
	}

	
	@Override
	public String getDescription() {
		return "Disband your clan";
	}
	
}
