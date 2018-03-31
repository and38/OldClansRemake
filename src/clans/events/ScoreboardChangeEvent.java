package clans.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScoreboardChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static enum ScoreboardPosition {
		TERRITORY("territoryUpdate", 5, ChatColor.WHITE.toString()),
		GOLD("goldUpdate", 8, ChatColor.GOLD.toString()),
		ENERGY("energyUpdate", 11, ChatColor.GREEN.toString()),
		CLAN("clanUpdate", 14, ChatColor.AQUA.toString()),
		EVENT_NAME("eventNameUpdate", 2),
		EVENT_LOCATION("eventLocationUpdate", 1);

		private int pos;
		private String team;
		private String entry;
		
		private ScoreboardPosition(String team, int pos) {
			this(team, pos, "");
		}
		
		private ScoreboardPosition(String team, int pos, String entry) {
			this.pos = pos;
			this.team = team;
			this.entry = entry;
		}
		
		public int getScore() {
			return this.pos;
		}
		
		public String getTeamName() {
			return this.team;
		}

		public String getEntry() {
			return entry;
		}
		
	}

	private ScoreboardPosition position;
	private String newText;
	private Player player;
	private String oldText;

	public ScoreboardChangeEvent(ScoreboardPosition position, String newText, String oldText, Player player) {
		this.position = position;
		this.newText = newText;
		this.oldText = oldText;
		this.player = player;
	}

	public ScoreboardPosition getPosition() {
		return position;
	}


	public String getNewText() {
		return newText;
	}


	public Player getPlayer() {
		return player;
	}


	public String getOldText() {
		return oldText;
	}


	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
