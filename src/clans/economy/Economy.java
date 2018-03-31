package clans.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.player.ClansPlayer;

public class Economy {
	
	public boolean hasEnoughFor(ClansPlayer player, int costForOne) {
		return player.getGold() >= (costForOne*1);
	}
	
	public boolean hasEnoughFor(ClansPlayer player, int amount, int costForOne) {
		return player.getGold() >= (costForOne*amount);
	}
	
	public void buy(ClansPlayer player, int amount, int costForOne) {
		if (hasEnoughFor(player, amount, costForOne)) {
			player.changeGold(costForOne, true);
			Player pl = Bukkit.getServer().getPlayer(player.getUUID());
			pl.sendMessage(Chat.gold("You have purchased " + String.valueOf(amount) + 
					" item(s) for " + String.valueOf(costForOne) + "g"));
		}
	}
	
}
