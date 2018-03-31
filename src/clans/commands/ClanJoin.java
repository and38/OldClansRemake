package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanJoin extends SubCommand {

	public ClanJoin() {
		super("join", new String[]{"clan"}, new String[0], ClanRole.NO_CLAN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length == 2) {
			String clanToJoin = args[1];
			Clan clan = ClansManager.getInstance().getClan(clanToJoin);
			ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
			if (cPlayer.getClan() != null || cPlayer == null) {
				player.sendMessage(Chat.message("You're already in a Clan"));
				return;
			}
			if (clan != null) {
				if (ClansManager.getInstance().joinClan(clan, cPlayer) == false) {
					player.sendMessage(Chat.message("You're not invited to that Clan"));
				} else {
					ClansMain.getInstance().resetScoreboard(cPlayer);
				}
			} else {
				player.sendMessage(Chat.message("The clan " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + " does not exist."));
			}
		}
	}

	@Override
	public String getDescription() {
		return "Join a Clan";
	}
	
}
