package clans.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public abstract class FileManager {
	
	private static List<FileManager> managers = new ArrayList<FileManager>();
	
	private File file;
	private FileConfiguration fileConfig;
	private String path;

	protected FileManager(String path) {  
		this.path = path;
	}
	
	public static void setupManagers(Plugin p) {
		for (FileManager manager : managers) {
			manager.setup(p);
		}
	}
	
	public static List<FileManager> getManagers() {
		return managers;
	}
	
	public static void setManagers(FileManager... manager) {
		for (FileManager m : manager) {
			managers.add(m);
		}
	}

	public void setup(Plugin p) {
		if (!p.getDataFolder().exists()) p.getDataFolder().mkdir();
		
		file = new File(p.getDataFolder(), path);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		}
		
		fileConfig = YamlConfiguration.loadConfiguration(file);
		save();
	}
	
	public ConfigurationSection getConfigurationSection(String path) {
		ConfigurationSection cs = fileConfig.getConfigurationSection(path);
		return cs;
	}
	
	public void removeSection(String path) {
		fileConfig.set(path, null);
		save();
	}
	
	public void createSection(String path, Map<String, Object> keysAndValues) {
		fileConfig.createSection(path, keysAndValues);
		save();
	}
	
	public void createSection(String path) {
		fileConfig.createSection(path);
		save();
	}
	
	public void set(String key, Object value) {
		fileConfig.set(key, value);
		save();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) fileConfig.get(key);
	}
	
	public FileConfiguration getConfig() {
		return fileConfig;
	}
	
	public void save() {
		try {
			fileConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload() {
		YamlConfiguration.loadConfiguration(file);
	}

	public static void saveAll() {
		for (FileManager manager : managers) {
			manager.save();
		}
	}
	
	
}
