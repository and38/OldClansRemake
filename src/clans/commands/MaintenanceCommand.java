package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import clans.ClansMain;
import clans.chat.Chat;

public class MaintenanceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!sender.isOp())
			return true;
		if (args.length == 0) {
			ClansMain.getInstance().setMaintenance(!ClansMain.getInstance().isMaintenance());
			sender.sendMessage(Chat.messageFill("Maintenance", "The server's maintenance is now " + 
			(ClansMain.getInstance().isMaintenance() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")));
			ClansMain.getInstance().getConfig().set("maintenance", ClansMain.getInstance().isMaintenance());
		}
		return true;
	}
	
}
