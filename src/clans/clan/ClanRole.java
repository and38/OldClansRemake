package clans.clan;

public enum ClanRole
{
	NO_CLAN("Player"), RECRUIT("Recruit"), MEMBER("Member"), ADMIN("Admin"), LEADER("Leader");
	
	private final String name;

	private ClanRole(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
