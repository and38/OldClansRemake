package clans.util;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Display {


	public static enum ChatAction
	{
		CHAT_MESSAGE((byte) 0), SERVER_MESSAGE((byte) 1),  ACTION_BAR((byte) 2);
	  
	  private byte value;
	  
	  private ChatAction(byte valuee) {
		  value = valuee;
	  }
	  
	  public byte getValue() {
		  return value;
	  }
	  
	}
	
	private static final int BARS = 24;
	
	public static void display(String text, Player player) {
		sendJsonMessage(player, text, ChatAction.ACTION_BAR);
	}
	
	
	public static void sendJsonMessage(Player player, String text, ChatAction chatAction) {
		Object chat = buildIChatBase(text);

		Constructor<?> chatConstructor = UtilNms.getConstructor(UtilNms.getNmsClass("PacketPlayOutChat"), 
				UtilNms.getNmsClass("IChatBaseComponent"), byte.class);
		Object packet = UtilNms.callConstructor(chatConstructor, chat, chatAction.getValue());

		UtilNms.sendPacket(player, packet);
	}
	
	public static void setTimes(Player player, int fadeIn, int stay, int fadeOut) {

		Constructor<?> subTitleConstructor = UtilNms.getConstructor(UtilNms.getNmsClass("PacketPlayOutTitle"), int.class, int.class, int.class);

		Object packetSubTitle = UtilNms.callConstructor(subTitleConstructor, fadeIn, stay, fadeOut);
		UtilNms.sendPacket(player, packetSubTitle);
	}
	
	public static void displaySubTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
		setTimes(player, fadeIn, stay, fadeOut);
		displayTitleAndSubtitle(player, "", text, fadeIn, stay, fadeOut);
	}
	
	private static void displayOnlySubTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
		Object subtitle = buildIChatBase(text);

		Constructor<?> subTitleConstructor = UtilNms.getConstructor(UtilNms.getNmsClass("PacketPlayOutTitle"), 
				UtilNms.getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0],
				UtilNms.getNmsClass("IChatBaseComponent"), int.class, int.class, int.class);

		Object packetSubTitle = UtilNms.callConstructor(subTitleConstructor, UtilNms.getEnumConstant(
				UtilNms.getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0], "SUBTITLE"), subtitle, 
				fadeIn, stay, fadeOut);
		UtilNms.sendPacket(player, packetSubTitle);
	}
	
	public static void displayTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
		setTimes(player, fadeIn, stay, fadeOut);
		Object title = buildIChatBase(text);

		Constructor<?> titleConstructor = UtilNms.getConstructor(UtilNms.getNmsClass("PacketPlayOutTitle"), 
				UtilNms.getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0],
				UtilNms.getNmsClass("IChatBaseComponent"), int.class, int.class, int.class);

		Object packetTitle = UtilNms.callConstructor(titleConstructor, UtilNms.getEnumConstant(
				UtilNms.getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0], "TITLE"), title,
				fadeIn, stay, fadeOut);
		UtilNms.sendPacket(player, packetTitle);
	}
	
	public static void displayTitleAndSubtitle(Player player, 
			String titleText, String subTitleText, int fadeIn, int stay, int fadeOut) {
		setTimes(player, fadeIn, stay, fadeOut);
		displayTitle(player, titleText, fadeIn, stay, fadeOut);
		displayOnlySubTitle(player, subTitleText, fadeIn, stay, fadeOut);
	}

	public static void customError(Exception e, boolean printStack) {
		if (printStack) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "------------------------------");
		}
		Bukkit.getServer().getLogger().info(ChatColor.RED + "ClansLegendariesRelease> " + e.getClass().toString() + "\n");
		e.printStackTrace();
		if (printStack) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "------------------------------");
		}
	}

	public static void displayProgress(String prefix, double percent, String suffix, boolean charge, Player... players) {
		if (charge) {
			percent = 1 - percent;
		}

		String progressBar = ChatColor.GREEN + "";
		boolean colorChange = false;
		for (int i=0 ; i<BARS ; i++)
		{
			if (!colorChange && (float)i/(float)BARS >= percent)
			{
				progressBar += ChatColor.RED;
				colorChange = true;
			}

			progressBar += "▌";
		}

		for (Player player : players) {
			display((prefix == null ? "" : prefix + ChatColor.RESET + " ") + progressBar + 
					(suffix == null ? "" : suffix + ChatColor.RESET + " "), player);
		}
	}
	
	
	private static Object buildIChatBase(String text) {
		Class<?>[] arry = UtilNms.getNmsClass("IChatBaseComponent").getDeclaredClasses();
		if (arry.length == 0) {
			 return UtilNms.getAndInvokeMethod(UtilNms.getNmsClass("ChatSerializer"), "a", 
					new Class[]{String.class}, null, 
					"{\"text\":\"" + " " + text + " " + "\"}");
		} else {
			return UtilNms.getAndInvokeMethod(UtilNms.getNmsClass("IChatBaseComponent").getDeclaredClasses()[0], "a", 
					new Class[]{String.class}, null, 
					"{\"text\":\"" + " " + text + " " + "\"}");
		}
	}
	
}
