package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.GUI.GUIs;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
				if (cPlayer == null || cPlayer.getClan() == null) {
					player.sendMessage(Chat.message("You are not in a clan."));
					return true;
				}
				player.sendMessage(Chat.message("You are in the clan " + cPlayer.getClanString()));
				player.sendMessage(Chat.message("Your clan's members are " + cPlayer.getClan().getOnlinePlayers()));
				return true;
			}
			if (!SubCommand.handleCommands(player, "clan", args)) {
				String clanSearch = args[0];
				boolean found = false;
				for (String str : ClansMain.getInstance().getAllClans()) {
					if (str.equalsIgnoreCase(clanSearch)) {
						found = true;
						clanSearch = str;
					}
				}
				if (!found) {
					player.sendMessage(Chat.message("The clan " + ChatColor.YELLOW + clanSearch + ChatColor.GRAY + " could not "
							+ "be found."));
					return true;
				}
				
				//TODO add player searching
				//getOfflineClan returns an IMMUTABLE clan. Never try to edit it.
				Clan onlineClan = ClansManager.getInstance().getClan(clanSearch);
				if (onlineClan != null) {
					GUIs.openSearch(player, onlineClan);
					return true;
				}
			}
		} 
		return true;
	}

}
