package clans.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map.Entry;







import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.UtilGeneric;
import clans.util.UtilNms;

public class ClanClaim extends SubCommand {

	public ClanClaim() {
		super("claim", new String[0], new String[0], ClanRole.ADMIN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length == 1) {
			ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
			if (cPlayer == null) {
				return;
			}
			Clan clan = cPlayer.getClan();
			Chunk chunk = player.getLocation().getChunk();
			if (clan == null) {
				player.sendMessage(Chat.message("You're not in a clan."));
				return;
			}
			if (!checkRole(player)) {
				player.sendMessage(Chat.message("You must be " + getColor() + roleReq.getName() + ChatColor.GRAY + " to run this"
						+ " command."));
				return;
			}

			if (cPlayer.getRole() != ClanRole.LEADER
					&& cPlayer.getRole() != ClanRole.ADMIN) {
				player.sendMessage(Chat
						.message("You must be the Leader or Admin of your clan to claim territory."));
				return;
			}

			if (!ClansManager.getInstance().canClaim(clan)) {
				player.sendMessage(Chat.message("Your clan can not claim any more land!"));
				return;
			}

			// TODO Check map size and cancel!

			for (Entry<Chunk, String> entry : ClansMain.getInstance()
					.getAllClaims().entrySet()) {
				if (entry.getKey() == chunk) {
					player.sendMessage(Chat
							.message("This Territory is owned by "
									+ cPlayer.getClan().getClanRelationColor(ClansManager.getInstance().getClan(entry.getValue()))
									+ entry.getValue() + ChatColor.GRAY + "."));
					return;
				}
			}
			
			for (Entity ent : chunk.getEntities()) {
				if (ent instanceof Player) {
					Player player1 = (Player) ent;
					ClansPlayer cPlayer1 = Players.getInstance().getCPlayerFrom(player1);
					if (cPlayer1 == null) {
						continue;
					}
					
					if (clan.isAllyOf(cPlayer1.getClan()) == false && cPlayer1.getClan() != clan) {
						player.sendMessage(Chat.message("You cannot claim while enemies are nearby."));
						return;
					}
				}
			}
			
			
			//TODO check adjacent
			
			
			ClansManager.getInstance().claim(cPlayer, clan, chunk);
			
			UtilGeneric.updateTerritory(player.getLocation().getChunk());
			String chunkName = "(" + chunk.getX() + ", " + chunk.getZ() + ")";
			player.sendMessage(Chat.message("You claimed Territory " + ChatColor.GREEN + chunkName + ChatColor.GRAY + "."));
			ClansManager.getInstance().messageClan(clan, Chat.message(ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
					" claimed Territory " + ChatColor.GREEN + chunkName + ChatColor.GRAY + "."));
		}
	}

	@Override
	public String getDescription() {
		return "Claim Territory";
	}
	
}
