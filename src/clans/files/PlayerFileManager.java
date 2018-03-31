package clans.files;

public class PlayerFileManager extends FileManager {

	private static PlayerFileManager instance = new PlayerFileManager();

	public static PlayerFileManager getInstance() {
		return instance;
	}

	private PlayerFileManager() {
		super("players.yml");
	}

}
