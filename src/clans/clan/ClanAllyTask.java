package clans.clan;

import org.bukkit.scheduler.BukkitRunnable;


public class ClanAllyTask extends BukkitRunnable {
	
	private Clan request;
	private Clan clanToAlly;

	public ClanAllyTask(Clan request, Clan clanToAlly) {
		this.request = request;
		this.clanToAlly = clanToAlly;
	}
	
	@Override
	public void run() {
		ClansManager.getInstance().allyEnd(this);
	}

	public Clan getRequestClan() {
		return request;
	}
	
	public Clan getToAlly() {
		return clanToAlly;
	}

}
