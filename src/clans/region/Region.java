package clans.region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import clans.ClansMain;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;

public class Region {

	private List<Chunk> chunks;
	private Clan clan;
	private boolean safe = false;

	public List<Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<Chunk> chunks) {
		this.chunks = chunks;
	}

	public Region(List<Chunk> chunks) {
		this.chunks = chunks;
	}

	public Region(Clan clan, List<Chunk> chunks) {
		this.chunks = chunks;
		this.clan = clan;
	}

	
	public String toSerializedString() {
		StringBuilder builder = new StringBuilder();
		chunks.forEach(chunk -> builder.append("(" + chunk.getX() + "," + chunk.getZ() + ")"));
		if (isSafe())
			builder.insert(0, "safe");

		return builder.toString();
	}

	public static Region fromSerliazedString(Clan clan, String str) {
		boolean safe = false;
		if (str.startsWith("safe"))
			safe = true;
		String[] chunks = str.split("\\)");
		List<Chunk> chunkList = new ArrayList<Chunk>();
		for (int i = 0; i < chunks.length; i++) {
			chunks[i] = chunks[i].replaceAll("[^0-9,-]", "");
			String[] chunkXZ = chunks[i].split(",");
			int x = 0;
			int z = 0;
			try {
				x = Integer.parseInt(chunkXZ[0]);
				z = Integer.parseInt(chunkXZ[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			chunkList.add(Bukkit.getServer().getWorld("world").getChunkAt(x, z));
		}
		Region region = new Region(clan, chunkList);
		region.setSafe(safe);
		return region;
	}

	public boolean isSafe() {
		return safe;
	}

	public boolean containsChunk(Chunk c) {
		return chunks.contains(c);
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public void addChunk(Chunk chunk) {
		if (chunks == null) {
			chunks = new ArrayList<Chunk>();
		}
		chunks.add(chunk);
	}

	public static boolean isUseableClaim(Block block, Player player) {
		return isUseableClaim(block, Players.getInstance().getCPlayerFrom(player));
	}

	public static boolean isUseableClaim(Block block, ClansPlayer player) {
		Clan clan = player.getClan();
		if (clan != null) {
			for (Chunk c : clan.getTerritory().getChunks()) {
				if (block.getChunk() == c) {
					return true;
				}
			}
		}
		for (Chunk c : ClansMain.getInstance().getAllClaims().keySet()) {
			if (block.getChunk() == c) {
				return false;
			}
		}
		return true;
	}

	public static boolean isBlockInside(Block block, int x, int y, int z, Region region) {
		for (Chunk chunk : region.getChunks()) {
			if (chunk.getBlock(x, y, z) == block) {
				return true;
			}
		}
		return false;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}

	public Clan getClan() {
		return this.clan;
	}

	public void removeChunk(Chunk chunk) {
		if (chunks == null) {
			chunks = new ArrayList<Chunk>();
		}
		chunks.remove(chunk);
	}

	public void unclaimAll(ClansPlayer unclaimer) {
		Iterator<Chunk> it = chunks.iterator();
		List<Chunk> toRemove = new ArrayList<Chunk>();
		while (it.hasNext()) {
			Chunk chunk = it.next();
			toRemove.add(chunk);
		}
		for (Chunk c : toRemove) {
			ClansManager.getInstance().unclaim(unclaimer, c);
		}
	}

}
