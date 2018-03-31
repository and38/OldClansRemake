package clans.clan;

import org.bukkit.scheduler.BukkitRunnable;

import clans.player.ClansPlayer;

public class ClanInviteTask extends BukkitRunnable {
	
	private ClansPlayer player;
	private Clan clan;

	public ClanInviteTask(Clan clan, ClansPlayer player) {
		this.clan = clan;
		this.player = player;
	}
	
	@Override
	public void run() {
		ClansManager.getInstance().inviteEnd(this);
	}

	public ClansPlayer getInvited() {
		return player;
	}
	
	public Clan getClan() {
		return clan;
	}

}
