package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanAlly extends SubCommand {

	public ClanAlly() {
		super("ally", new String[] {"clan"}, new String[0], ClanRole.ADMIN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(Chat.message("Please input a clan to ally."));
			return;
		}
		
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer.getClan() == null) {
			player.sendMessage(Chat.message("You are not in a clan."));
			return;
		}
		if (!checkRole(player)) {
			player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
					+ " command."));
			return;
		}
		
		if (cPlayer.getClan().getAllAllies().size() >= Math.min(8, 3+cPlayer.getClan().getAllPlayers().size())) {
			player.sendMessage(Chat.message("Your clan does not have space for another alliance."));
			return;
		}
		
		Clan toAlly = ClansManager.getInstance().getClan(args[1]);
		
		
		
		if (toAlly == null) {
			player.sendMessage(Chat.message("The clan " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " could not be found."));
			return;
		}
		
		if (toAlly == cPlayer.getClan()) {
			player.sendMessage(Chat.message("You may not request an alliance with yourself!"));
			return;
		}
		
		if (toAlly.getAllAllies().size() >= Math.min(8, 3+toAlly.getAllPlayers().size())) {
			player.sendMessage(Chat.message("The clan " + ChatColor.GOLD + toAlly.getName() + ChatColor.GRAY 
					+ " does not have space for another alliance."));
			return;
		}
		
		
		ClansManager.getInstance().allyClan(cPlayer, cPlayer.getClan(), toAlly);
	}
	
	@Override
	public String getDescription() {
		return "Request Alliance with Clan";
	}
	
}
