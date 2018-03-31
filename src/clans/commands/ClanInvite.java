package clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanInviteTask;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class ClanInvite extends SubCommand {

	public ClanInvite() {
		super("invite", new String[]{"player"}, new String[0], ClanRole.ADMIN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length == 2) {
			Clan callerClan = Players.getInstance().getCPlayerFrom(player).getClan();
			if (callerClan == null) {
				player.sendMessage(Chat.message("You are not in a clan."));
				return;
			}
			
			if (!checkRole(player)) {
				player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
						+ " command."));
				return;
			}
			
			if (callerClan.getAllPlayers().size() >= Clan.MAX_PLAYERS) {
				player.sendMessage(Chat.message("Your clan is full!"));
				return;
			}
			String playerToInvite = args[1];
			Player target = Bukkit.getServer().getPlayer(playerToInvite);
			ClansPlayer targetCP = Players.getInstance().getCPlayerFrom(target);
			for (ClanInviteTask task : ClansManager.getInstance().getInvites()) {
				if (task.getClan() == callerClan && task.getInvited() == targetCP){
					player.sendMessage(Chat.message("The player " + ChatColor.YELLOW + playerToInvite + ChatColor.GRAY + " "
							+ "has already been invited."));
					return;
				}
			}
			if (target == null) {
				player.sendMessage(Chat.message("The player " + ChatColor.YELLOW + playerToInvite + ChatColor.GRAY + " "
						+ "could not be found."));
				return;
			}
			if (callerClan.getOnlinePlayers().contains(targetCP)) {
				player.sendMessage(Chat.message("The player " + ChatColor.YELLOW + playerToInvite + ChatColor.GRAY + ""
						+ " is already in your Clan."));
				return; 
			}
			ClansManager.getInstance().messageClan(callerClan, Chat.message(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + 
					"" + " has invited " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " to join your clan."));
			ClansManager.getInstance().invitePlayer(Players.getInstance().getCPlayerFrom(player), 
					callerClan, Players.getInstance().getCPlayerFrom(target));
		}
	}
	
	@Override
	public String getDescription() {
		return "Invite player to your Clan";
	}

}
