package clans.util;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

public class ScoreboardRunnable extends BukkitRunnable {

	private Objective sideObjective;
	int currentYellowindex = 0;
	int waitCount = 0;
	boolean whiteTrail = false;

	public ScoreboardRunnable(Objective obj) {
		this.sideObjective = obj;
	}

	//No color
	String str = ChatColor.stripColor(ScoreboardType.SIDEBOARD.getObjectiveName());
	String outputDisplay;
	@Override
	public void run() {
		if (currentYellowindex > str.length()) {
			if (waitCount < 6) {
				waitCount++;
				return;
			}
			whiteTrail = !whiteTrail;
			currentYellowindex = 0;
			waitCount = 0;
			return;
		}

		StringBuilder apply = new StringBuilder(colorSwitch(whiteTrail));
		for (int i = 0; i < str.length(); i++) {
			if (i == currentYellowindex) {
				apply.append(ChatColor.YELLOW.toString() + ChatColor.BOLD + 
						str.substring(i, (i == str.length() ? i : i+1)) + colorSwitch(!whiteTrail));
			} else {
				apply.append(str.charAt(i));
			}
			outputDisplay = apply.toString();
		}
		currentYellowindex++;
		sideObjective.setDisplayName(outputDisplay);
	}

	private String colorSwitch(boolean whiteTrail) {
		return (whiteTrail ? ChatColor.WHITE.toString() + ChatColor.BOLD : ChatColor.GOLD.toString() + ChatColor.BOLD);
	}

}
