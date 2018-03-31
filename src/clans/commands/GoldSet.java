package clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.ClanRole;
import clans.player.ClansPlayer;
import clans.player.Players;

public class GoldSet extends SubCommand {

	public GoldSet() {
		super("set", new String[]{"amount", "player"}, new String[0], ClanRole.NO_CLAN);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCommand(Player player, String[] args) {
		OfflinePlayer offPl = null;
		boolean other = false;
		if (args.length == 3) {
			offPl = Bukkit.getServer().getOfflinePlayer(args[2]);
			other = true;
			if (offPl == null) {
				player.sendMessage(Chat.message("The player " + args[2] + " does not exist."));
				return;
			}
		}
		ClansPlayer cPlayer = null;
		if (other) {
			cPlayer = Players.getInstance().getCPlayerFrom(offPl);
		} else {
			cPlayer = Players.getInstance().getCPlayerFrom(player);
		}
		
		if (cPlayer == null) {
			player.sendMessage(Chat.message("The specified player has not joined the server!"));
			return;
		}
		int gold;
		try {
			gold = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.sendMessage(Chat.message("Invalid gold amount."));
			return;
		}

		gold = Math.max(0, gold);
		cPlayer.setGold(gold);
		player.sendMessage(Chat.gold(ChatColor.GREEN + cPlayer.getName() + ChatColor.GRAY + " now has " + 
		ChatColor.YELLOW + String.valueOf(cPlayer.getGold()) + ChatColor.GRAY + ""
				+ " gold."));
	}

}