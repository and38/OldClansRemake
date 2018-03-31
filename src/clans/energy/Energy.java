package clans.energy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.player.ClansPlayer;


public class Energy extends BukkitRunnable {

	public static final int BASE_MAX_ENERGY = 10080;
	public static final int BASE_START_ENERGY = 4308;
	// 259200 Seconds 3.0 Days = 1 claim and 1 player with the energy values above.


	public Energy(ClansMain main) {
		this.runTaskTimer(main, 0, 20 * 60);
	}

	public static String computeEnergyString(Clan clan) {
		//Compute energy to hours.
		if (clan.getEnergyCostPerMinute() == 0) {
			return "";
		}
		double correctedEnergy = (double)clan.getEnergy()/(double)clan.getEnergyCostPerMinute();
		double seconds = (correctedEnergy*60d);
		double minutes = correctedEnergy;
		double hours = minutes/60d;
		double days = hours/24d;
		String timeName = ChatColor.GREEN.toString();
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		format.setRoundingMode(RoundingMode.HALF_UP);
		format.applyPattern("#0.0");

		if (days >= 1) timeName += (format.format(days)+" Days");
		else if (days < 1 && hours >= 1) timeName += (format.format(hours)+" Hours");
		else if (hours < 1 && minutes >= 1) timeName += (format.format(minutes)+" Minutes");
		else if (hours < 1 && seconds >= 1) timeName += (format.format(seconds)+" Seconds");
		else timeName += "";

		return timeName;
	}

	@Override
	public void run() {
		int toSet = 0;
		for (Clan clan : ClansManager.getInstance().getClans().values()) {
			if (clan.getTerritory().getChunks().size() <= 0)
				continue;
			int energyPerMinute = clan.getEnergyCostPerMinute();
			toSet = Math.max(0, clan.getEnergy()-energyPerMinute);

			for (ClansPlayer p : clan.getOnlinePlayers()) {
				Player pl = Bukkit.getServer().getPlayer(p.getUUID());
				String timeName = computeEnergyString(clan);
				ScoreboardChangeEvent event = new ScoreboardChangeEvent(ScoreboardPosition.ENERGY, timeName, "", pl);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
			
			clan.setEnergy(toSet);
			if (toSet <= 0) {
				clan.getTerritory().unclaimAll(null);
				ClansManager.getInstance().messageClan(clan, Chat.message("Your clan has ran out of energy. Land claims have been removed"));
				for (ClansPlayer p : clan.getOnlinePlayers()) {
					ClansMain.getInstance().resetScoreboard(p);
				}
			}
		}
	}

}
