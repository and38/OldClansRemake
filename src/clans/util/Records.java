package clans.util;

import org.bukkit.Material;

public enum Records {
	RECORD_13(Material.GOLD_RECORD),
	RECORD_CAT(Material.GREEN_RECORD),
	RECORD_BLOCKS(Material.RECORD_3),
	RECORD_CHIRP(Material.RECORD_4),
	RECORD_FAR(Material.RECORD_5),
	RECORD_MALL(Material.RECORD_6),
	RECORD_MELLOHI(Material.RECORD_7),
	RECORD_STAL(Material.RECORD_8),
	RECORD_STRAD(Material.RECORD_9),
	RECORD_WARD(Material.RECORD_10),
	RECORD_11(Material.RECORD_11),
	RECORD_WAIT(Material.RECORD_12);
	
	private Material material;
	
	private Records(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
}
