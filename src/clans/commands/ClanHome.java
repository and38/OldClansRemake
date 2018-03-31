package clans.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanHome extends SubCommand {

	public ClanHome() {
		super("home", new String[0], new String[0], ClanRole.RECRUIT);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer != null) {
			if (cPlayer.getClan() != null) {
				
				if (!checkRole(player)) {
					player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
							+ " command."));
					return;
				}
				
				Clan clan = cPlayer.getClan();
				if (clan.getHomeLocation() == null) {
					player.sendMessage(Chat.message("Your clan has no set home."));
					return;
				}
				player.teleport(clan.getHomeLocation().add(0,0.5,0));
			} else {
				player.sendMessage(Chat.message("You are not in a clan."));
			}
		} else {
			player.sendMessage(Chat.message("Relog!"));
		}
	}

	
	@Override
	public String getDescription() {
		return "Go to your Clan's home";
	}
}
