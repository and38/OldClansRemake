package clans.combat;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.player.ClansPlayer;

public class CombatManager {
	
	private ClansMain main;

	public CombatManager(ClansMain main) {
		this.main = main;
	}
	
	private ConcurrentHashMap<UUID, Long> lastDamaged = new ConcurrentHashMap<UUID, Long>();
	
	public boolean isInCombat(Player player, long currentTime) {
		return ((System.currentTimeMillis()-(lastDamaged.get(player.getUniqueId())))/1000d >= 15d);
	}
	
	public long getLastDamaged(Player p) {
		return lastDamaged.get(p.getUniqueId());
	}
	
	public boolean isInCombat(ClansPlayer player, long currentTime) {
		return ((System.currentTimeMillis()-(lastDamaged.get(player.getUUID())))/1000d >= 15d);
	}
	
	public long getLastDamaged(ClansPlayer p) {
		return lastDamaged.get(p.getUUID());
	}
	
}
