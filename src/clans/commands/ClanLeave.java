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

public class ClanLeave extends SubCommand {

	public ClanLeave() {
		super("leave", new String[0], new String[0], ClanRole.RECRUIT);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer.getClan() == null) {
			player.sendMessage(Chat.message("You are not in a clan."));
			return;
		}
		if (cPlayer.getRole() == ClanRole.LEADER) {
			if (cPlayer.getClan().getAllPlayers().size() <= 1) {
				player.sendMessage(Chat.message("You have left your clan."));
				player.sendMessage(Chat.message("Your clan was disbanded due to lack of players."));
				ClansManager.getInstance().removeClan(cPlayer.getClan());
			} else {
				player.sendMessage(Chat.message("You must give someone else Leader before leaving!"));
			}
			return;
		}
		
		Clan clan = cPlayer.getClan();
		String name = clan.getName();
		cPlayer.getClan().removePlayer(cPlayer);
		player.sendMessage(Chat.message("You left " + ChatColor.YELLOW + "Clan " + name + ChatColor.GRAY + "."));
		ClansManager.getInstance().messageClan(clan, Chat.message(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + 
				" has left your clan."));
		UtilGeneric.updateAddScoreboards(player);
	}

	@Override
	public String getDescription() {
		return "Leave your Clan";
	}
	
}
