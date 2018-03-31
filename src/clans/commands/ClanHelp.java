package clans.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.ClanRole;

public class ClanHelp extends SubCommand {

	public ClanHelp() {
		super("help", new String[0], new String[0], ClanRole.NO_CLAN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		showAllClanSubCommands(player);
	}

	private void showAllClanSubCommands(Player player) {
		List<SubCommand> commands = SubCommand.registeredSubCommands("clan");
		Collections.sort(commands, (o1, o2) -> {
			return o1.getRole().ordinal()-o2.getRole().ordinal();
		});
		player.sendMessage(Chat.message("Commands List: "));
		for (SubCommand subCmd : commands) {
			player.sendMessage(subCmd.getColor() + "/clan" + " " + subCmd.getName() + " " + subCmd.getArgsString() + ChatColor.GRAY + subCmd.getDescription() + " " + subCmd.getColor() + subCmd.getRole().getName());
		}
		player.sendMessage(ChatColor.WHITE + "/clan <clan> " + ChatColor.GRAY + "View Clan Information " + ChatColor.WHITE + "Player" );
	}
	
	@Override
	public String getDescription() {
		return "List Clan commands";
	}
	
}
