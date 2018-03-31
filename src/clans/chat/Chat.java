package clans.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import clans.player.ClansPlayer;
import clans.player.Players;

public class Chat {
	
	public static final String CLAN_COLOR = ChatColor.GOLD.toString();
	public static final String CLAN_COLOR_CLAN = ChatColor.DARK_AQUA.toString();
	public static final String CLAN_COLOR_ALLY = ChatColor.DARK_GREEN.toString();
	public static final String PLAYER_COLOR = ChatColor.YELLOW.toString();
	public static final String PLAYER_COLOR_CLAN = ChatColor.AQUA.toString();
	public static final String PLAYER_COLOR_ALLY = ChatColor.GREEN.toString();
	//                         TYPE_"COLOR"_RELATION
	public static final String HEADER = ChatColor.DARK_RED + "[%s]";
	
	
	public static String message(String message) {
		return messageFill("Clans", message);
	}
	
	public static String gold(String message) {
		return messageFill("Gold", message);
	}

	public static String messageFill(String header, String message) {
		return String.format(HEADER, header) + " " + ChatColor.GRAY + message;
	}
	
	public static String clanMessage(Player player, String message) {
		return CLAN_COLOR + player.getName() + " " + PLAYER_COLOR_CLAN + message;
	}
	
	public static String allyMessage(Player player, String message) {
		ClansPlayer cp = Players.getInstance().getCPlayerFrom(player);
		String name = cp.getClan() == null ? "" : cp.getClan().getName();
		return Chat.CLAN_COLOR_ALLY + name + " " + 
				player.getName() + " " + Chat.PLAYER_COLOR_ALLY + message;
	}
	
	public static String mainChatMessage(Player sender, Player reciever, String message) {
		ClansPlayer cp = Players.getInstance().getCPlayerFrom(sender);
		ClansPlayer rec = Players.getInstance().getCPlayerFrom(reciever);
		
		if (rec.getClan() == null || cp.getClan() == null) {
			return Chat.CLAN_COLOR + cp.getClan() + " " + Chat.PLAYER_COLOR + cp.getName() + " " + message;
		} else if (rec.getClan().isAllyOf(cp.getClan())) {
			return Chat.CLAN_COLOR_ALLY + cp.getClan() + " " + Chat.PLAYER_COLOR_ALLY + cp.getName() + " " + message;
		} else if (rec.getClan() == cp.getClan()) {
			return Chat.CLAN_COLOR_CLAN + cp.getClan() + " " + Chat.PLAYER_COLOR_CLAN + cp.getName() + " " + message;
		} else {
			return Chat.CLAN_COLOR + cp.getClan() + " " + Chat.PLAYER_COLOR + cp.getName() + " " + message;
		}
		
	}
	
}
