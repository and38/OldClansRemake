package clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.UtilGeneric;

public class ClanNeutral extends SubCommand {

	public ClanNeutral() {
		super("neutral", new String[] {"clan"}, new String[0], ClanRole.ADMIN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(Chat.message("Please input an allied clan."));
			return;
		}
		
		if (!checkRole(player)) {
			player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
					+ " command."));
			return;
		}
		
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer.getClan() == null) {
			player.sendMessage(Chat.message("You are not in a clan."));
			return;
		}
		
		Clan ally = ClansManager.getInstance().getClan(args[1]);
		if (ally == null) {
			player.sendMessage(Chat.message("The clan " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + " could not be found."));
			return;
		}
		
		if (!cPlayer.getClan().getAllAllies().contains(args[1])) {
			player.sendMessage(Chat.message("You are not allies with the clan " + ChatColor.GOLD + args[1] + ChatColor.GRAY + "."));
			return;
		}
		
		ally.removeAlly(cPlayer.getClan(), true);
		cPlayer.getClan().removeAlly(ally, true);
		for (ClansPlayer p : cPlayer.getClan().getOnlinePlayers()) {
			UtilGeneric.updateAddScoreboards(Bukkit.getServer().getPlayer(p.getUUID()));
		}
	}
	
	@Override
	public String getDescription() {
		return "Request Neutrality with Clan";
	}

}
