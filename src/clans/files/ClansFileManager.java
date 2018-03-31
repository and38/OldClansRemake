package clans.files;

public class ClansFileManager extends FileManager {

	private static ClansFileManager instance = new ClansFileManager();
	
	public static ClansFileManager getInstance() {
		return instance;
	}
	
	private ClansFileManager() {
		super("clans.yml");
	}
	
	
	
}
