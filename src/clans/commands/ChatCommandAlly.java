package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ChatCommandAlly implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom((Player) sender);	
			if (args.length == 0) {
				cPlayer.setAllyChat(!cPlayer.isAllyChat());
				sender.sendMessage(Chat.message("Ally Chat: " + (cPlayer.isAllyChat() ? ChatColor.GREEN + "On" :
					ChatColor.RED + "Off")));
			} else {
				if (cPlayer.getClan() == null) {
					sender.sendMessage(Chat.message("You are not in a clan."));
					return true;
				}
				StringBuilder strBuild = new StringBuilder();
				for (int i = 0; i < args.length; i++) {
					strBuild.append(args[i] + (args.length == 0 ? "" : " "));
				}
				ClansManager.getInstance().messageClan(cPlayer.getClan(), 
						Chat.allyMessage(cPlayer.getPlayer(), strBuild.toString()));
				for (Clan clan : cPlayer.getClan().getOnlineAllies()) {
					ClansManager.getInstance().messageClan(clan, 
							Chat.allyMessage(cPlayer.getPlayer(), strBuild.toString()));
				}
			}
		}
		return true;
	}

}
