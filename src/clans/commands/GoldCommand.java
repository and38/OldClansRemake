package clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.player.ClansPlayer;
import clans.player.Players;

public class GoldCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("gold") || cmd.getName().equalsIgnoreCase("g")) {
			if (args.length == 0) {
				ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
				if (cPlayer == null) {
					return true;
				}
				sender.sendMessage(Chat.gold("Your Balance is " + ChatColor.YELLOW + 
						String.valueOf(cPlayer.getGold()) + "g"));
				return true;
			}
			if (args.length == 1) {
				if (!SubCommand.registeredSubCommands("gold").contains(args[0])) {
					OfflinePlayer offPl = Bukkit.getServer().getOfflinePlayer(args[0]);
					if (offPl == null) {
						sender.sendMessage(Chat.message("The player " + args[0] + " does not exist."));
						return true;
					}
					handleGold(sender, offPl);
					return true;
				}
			}
			SubCommand.handleCommands(player, "gold", args);
		}
		return true;
		
	}
	
	private void handleGold(CommandSender sender, OfflinePlayer target) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(target);
		int gold = 0;
		if (cPlayer != null) {
			gold = cPlayer.getGold();
		}
		sender.sendMessage(Chat.gold(target.getName() + "'s Balance is " + ChatColor.YELLOW + 
				String.valueOf(gold) + "g"));
	}

}
