package clans.listeners;


import org.bukkit.ChatColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.util.Vector;

import clans.chat.Chat;
import clans.player.ClansPlayer;
import clans.player.Players;

public class PvpListeners implements Listener {

/*	private ClansMain main;

	public PvpListeners(ClansMain main) {
		this.main = main;
	}*/
	
	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player damager = (Player) e.getDamager();
			Player hurt = (Player) e.getEntity();
			
			ClansPlayer cHurt = Players.getInstance().getCPlayerFrom(hurt);
			ClansPlayer cDamager = Players.getInstance().getCPlayerFrom(damager);
			
			if (cHurt == null || cDamager == null || cDamager.getClan() == null || cHurt.getClan() == null) {
				return;
			}
			
			if (cDamager.getClan().isAllyOf(cHurt.getClan()) || cDamager.getClan() == cHurt.getClan()) {
				e.setCancelled(true);
				damager.sendMessage(Chat.message("You cannot harm " + cDamager.getClan().getRelationColor(cHurt.getClan()) + 
						cHurt.getName()) + ChatColor.GRAY + ".");
				
			}
		} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Creeper) {
			Player hurt = (Player) e.getEntity();
			Creeper damager = (Creeper) e.getDamager();
			Vector v = hurt.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
			v.setY(5d);
			v.multiply(6d);
			hurt.setVelocity(v);
		}
	}
	
}

