package clans.commands;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClanRole;
import clans.clan.ClansManager;
import clans.energy.Energy;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.UtilGeneric;

public class ClanCreate extends SubCommand {

	public ClanCreate() {
		super("create", new String[]{"clan"}, new String[0], ClanRole.NO_CLAN);
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(Chat.message("Please input a clan name."));
			return;
		}
		
		ClansPlayer cplayer = Players.getInstance().getCPlayerFrom(player);
		if (cplayer == null) {
			player.sendMessage(Chat.message("Relog!"));
			return;
		}
		
		if (cplayer.getClan() != null) {
			player.sendMessage(Chat.message("You're already in a clan."));
			return;
		}
		
		String clanName = args[1];
		Clan clan = ClansManager.getInstance().getClan(clanName);
		for (String str : ClansMain.getInstance().getAllClans()) {
			if (str.equalsIgnoreCase(clanName)) {
				player.sendMessage(Chat.message("This clan already exists."));
				return;
			}
		}
		if (!clanName.matches("^[a-zA-Z0-9]*")) {
			player.sendMessage(Chat.message("Your clan name may only contain numbers and letters."));
			return;
		}
		
		if (ClansManager.getInstance().exists(clan)) {
			player.sendMessage(Chat.message("This clan already exists."));
		} else if (clanName.length() > 10 || clanName.length() < 3) {
			player.sendMessage(Chat.message("Clan names above 10 or below 3 characters are not allowed."));
		} else {
			player.sendMessage(Chat.message("Successfully created the clan " + clanName));
			ClansManager.getInstance().createClan(clanName, cplayer);
			Clan clan1 = ClansManager.getInstance().getClan(clanName);
			clan1.setEnergy(Energy.BASE_START_ENERGY);
			UtilGeneric.updateAddScoreboards(player);
			ClansMain.getInstance().resetScoreboard(cplayer);
		}
	}
	
	@Override
	public String getDescription() {
		return "Create a Clan";
	}

}
