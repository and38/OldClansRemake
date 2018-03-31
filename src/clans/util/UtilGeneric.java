package clans.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.player.ClansPlayer;
import clans.player.Players;

public class UtilGeneric {

	public static void sendHeaderFooter(Player player) {
		Class<?> chatSerializer = UtilNms.getNmsClass("IChatBaseComponent")
				.getDeclaredClasses()[0];
		Method outputM = UtilNms.getMethod(chatSerializer, "a", String.class);
		String textTop = ChatColor.BOLD + ClansMain.getInstance().getNetworkName() + " Network          "
				+ ChatColor.RESET + ChatColor.GREEN
				+ player.getWorld().getName();
		Object chatTop = UtilNms.invokeMethod(outputM, null, "{\"text\":\""
				+ textTop + "\"}");
		String textBottom = "Visit " + ChatColor.GREEN
				+ "www.teamxserver.enjin.com" + ChatColor.RESET
				+ " for more information!";
		//THIS ISN'T STOLEN EVEN THO I INCLUDE THEIR STORE LOLOL
		Object chatBottom = UtilNms.invokeMethod(outputM, null, "{\"text\":\""
				+ textBottom + "\"}");
		Constructor<?> construct = UtilNms.getConstructor(UtilNms
				.getNmsClass("PacketPlayOutPlayerListHeaderFooter"));
		Object packet = UtilNms.callConstructor(construct);
		Vector vector = new Vector(4,4,4);
		

		UtilNms.getAndSetField(packet.getClass(), "a", packet, chatTop);
		UtilNms.getAndSetField(packet.getClass(), "b", packet, chatBottom);
		UtilNms.sendPacket(player, packet);
	}

	public static void updateTerritory(Chunk chunk) {
		String name = ClansMain.getInstance().getAllClaims()
				.get(chunk);
		
		for (Entity ent : chunk.getEntities()) {
			if (ent instanceof Player) {
				Player p = (Player) ent;
				ClansPlayer cp = Players.getInstance().getCPlayerFrom(p);
				String newText = (name == null ? ChatColor.GRAY + "Wilderness"
						: ClansManager.getInstance().getClan(name)
						.getRelationColor(cp.getClan()) + name);
				ScoreboardChangeEvent e = new ScoreboardChangeEvent(ScoreboardPosition.TERRITORY, 
						newText, "", p);
				Bukkit.getServer().getPluginManager().callEvent(e);
			}
		}
	}
	
	public static void doScoreboards(ClansPlayer cPlayer) {
		Clan clan = cPlayer.getClan();
		boolean noClan = (clan == null ? true : false);
		Object scoreboardManager = UtilNms.getCraftBukkitClass(
				"scoreboard.CraftScoreboardManager").cast(
						Bukkit.getServer().getScoreboardManager());
		Object scoreboard = UtilNms.getAndInvokeMethod(
				scoreboardManager.getClass(), "getNewScoreboard",
				new Class<?>[0], scoreboardManager);
		
		Object netScore = UtilNms.getAndInvokeMethod(scoreboard.getClass(),
				"getHandle", new Class[0], scoreboard);
		Object team = null;
		try {
			team = UtilNms.getAndInvokeMethod(
					UtilNms.getNmsClass("Scoreboard"), "createTeam",
					new Class<?>[] { String.class }, netScore, (noClan ? "None"
							: clan.getName()));
		} catch (IllegalArgumentException e) {
			team = UtilNms.getAndInvokeMethod(
					UtilNms.getNmsClass("Scoreboard"), "getTeam",
					new Class<?>[] { String.class }, netScore, (noClan ? "None"
							: clan.getName()));
		}

		if (team == null) {
			Bukkit.getServer()
			.getConsoleSender()
			.sendMessage(
					Chat.message(ChatColor.RED
							+ "Scoreboard not working! (Null team)"));
			return;
		}

		Method setPrefix = UtilNms.getMethod(team.getClass(), "setPrefix",
				new Class<?>[] { String.class });

		String prefixClan = "";
		String prefixAlly = "";
		String prefixNormal = "";

		if (cPlayer.getClan() != null) {
			prefixClan = Chat.CLAN_COLOR_CLAN + clan.getName()
					+ Chat.PLAYER_COLOR_CLAN + " ";
			prefixAlly = Chat.CLAN_COLOR_ALLY + clan.getName()
					+ Chat.PLAYER_COLOR_ALLY + " ";
			prefixNormal = Chat.CLAN_COLOR + clan.getName() + Chat.PLAYER_COLOR
					+ " ";
		} else {
			// Inside here is where the no clan packet is sent.
			String prefixNone = ChatColor.YELLOW.toString();
			UtilNms.invokeMethod(setPrefix, team, prefixNone);
			Object teamPacket = UtilNms.callConstructor(UtilNms.getConstructor(
					UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
					UtilNms.getNmsClass("ScoreboardTeam"), int.class), team, 0);
			for (Player pla : Bukkit.getServer().getOnlinePlayers()) {
				UtilNms.sendPacket(pla, teamPacket);
			}

			Object teamJoinPacket = UtilNms.callConstructor(UtilNms
					.getConstructor(
							UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
							UtilNms.getNmsClass("ScoreboardTeam"),
							Collection.class, int.class), team, Arrays
							.asList(cPlayer.getName()), 3);
			joinTeam(teamJoinPacket);
			return;
		}

		// Create team with normal colors
		UtilNms.invokeMethod(setPrefix, team, prefixNormal);
		Object teamPacket = UtilNms.callConstructor(UtilNms.getConstructor(
				UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
				UtilNms.getNmsClass("ScoreboardTeam"), int.class), team, 0);
		for (Player pla : Bukkit.getServer().getOnlinePlayers()) {
			UtilNms.sendPacket(pla, teamPacket);
		}

		// Update team colors for clan
		UtilNms.invokeMethod(setPrefix, team, prefixClan);
		Object teamClanPacket = UtilNms.callConstructor(UtilNms.getConstructor(
				UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
				UtilNms.getNmsClass("ScoreboardTeam"), int.class), team, 2);
		for (ClansPlayer cPl : cPlayer.getClan().getOnlinePlayers()) {
			Player pl = Bukkit.getServer().getPlayer(cPl.getUUID());
			if (pl != null) {
				UtilNms.sendPacket(pl, teamClanPacket);
			}
		}

		// Update team colors for ally
		UtilNms.invokeMethod(setPrefix, team, prefixAlly);
		Object teamAllyPacket = UtilNms.callConstructor(UtilNms.getConstructor(
				UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
				UtilNms.getNmsClass("ScoreboardTeam"), int.class), team, 2);
		for (Clan ally : cPlayer.getClan().getOnlineAllies()) {
			for (ClansPlayer allyP : ally.getOnlinePlayers()) {
				Player pl = Bukkit.getServer().getPlayer(allyP.getUUID());
				if (pl != null) {
					UtilNms.sendPacket(pl, teamAllyPacket);
				}
			}
		}

		Object teamJoinPacket = UtilNms.callConstructor(UtilNms.getConstructor(
				UtilNms.getNmsClass("PacketPlayOutScoreboardTeam"),
				UtilNms.getNmsClass("ScoreboardTeam"), Collection.class,
				int.class), team, Arrays.asList(cPlayer.getName()), 3);
		joinTeam(teamJoinPacket);
	}

	public static void joinTeam(Object packet) {
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			UtilNms.sendPacket(pl, packet);
		}
	}

	public static void updateAddScoreboards(Player player) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (cPlayer == null) {
			return;
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			ClansPlayer cP = Players.getInstance().getCPlayerFrom(p);
			doScoreboards(cP);
		}
	}

	/*public static void doScoreboardsNMS(ClansPlayer cPlayer) {
		Clan clan = cPlayer.getClan();
		boolean noClan = (clan == null ? true : false);
		String clanName = noClan ? "" : clan.getName();

		Scoreboard scoreboard = ((CraftScoreboardManager) Bukkit.getServer().getScoreboardManager()).getNewScoreboard().getHandle();
		ScoreboardTeam team = scoreboard.getTeam(noClan ? "None" : clan.getName());
		if (team == null) {
			team = scoreboard.createTeam(noClan ? "None" : clan.getName());
		}
		String prefixNone = "" + Chat.PLAYER_COLOR.toString();
		String prefixClan = Chat.CLAN_COLOR_CLAN + clanName + Chat.PLAYER_COLOR_CLAN + " ";
		String prefixAlly = Chat.CLAN_COLOR_ALLY + clanName + Chat.PLAYER_COLOR_ALLY + " ";
		String prefixNormal = Chat.CLAN_COLOR + clanName + Chat.PLAYER_COLOR + " ";

		PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam(team, 0);
		PacketPlayOutScoreboardTeam teamColorPacket = new PacketPlayOutScoreboardTeam(team, 2);
		for (Player play : Bukkit.getServer().getOnlinePlayers()) {
			ClansPlayer cPlay = Players.getInstance().getCPlayerFrom(play);
			if (cPlay == null) continue;
			if (cPlayer.getClan() == null) {
				try {
					//I have a util for nms like this if you want it.
					Field f = teamColorPacket.getClass().getDeclaredField("c");
					f.setAccessible(true);
					f.set(teamColorPacket, prefixNone);
					f.setAccessible(false);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				((CraftPlayer) play).getHandle().playerConnection.sendPacket(teamColorPacket);
			} else if (cPlay.getClan() == cPlayer.getClan()) {
				try {
					Field f = teamColorPacket.getClass().getDeclaredField("c");
					f.setAccessible(true);
					f.set(teamColorPacket, prefixClan);
					f.setAccessible(false);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				((CraftPlayer) play).getHandle().playerConnection.sendPacket(teamColorPacket);
			} //Add for loop for enemy settings
			else {
				try {
					Field f = teamColorPacket.getClass().getDeclaredField("c");
					f.setAccessible(true);
					f.set(teamColorPacket, prefixNormal);
					f.setAccessible(false);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				((CraftPlayer) play).getHandle().playerConnection.sendPacket(teamColorPacket);
			}
			for (Clan ally : cPlayer.getClan().getOnlineAllies()) {
				for (ClansPlayer allyP : ally.getOnlinePlayers()) {
					Player pl = Bukkit.getServer().getPlayer(allyP.getUUID());
					if (pl != null) {
						try {
							Field f = teamColorPacket.getClass().getDeclaredField("c");
							f.setAccessible(true);
							f.set(teamColorPacket, prefixAlly);
							f.setAccessible(false);
						} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						((CraftPlayer) play).getHandle().playerConnection.sendPacket(teamColorPacket);
					}
				}
			}
		}
		joinTeam(teamPacket);
	}*/
}
