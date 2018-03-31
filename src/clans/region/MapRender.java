package clans.region;

import java.awt.Color;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import clans.ClansMain;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.util.UtilNms;

public class MapRender extends MapRenderer {
	
	boolean firstRender = true;
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		int mapScale = (int) Math.pow((map.getScale().getValue()+1), 2);
		if (Math.abs(player.getLocation().getX()-map.getCenterX()) > (Math.pow(43, mapScale)) ||
				Math.abs(player.getLocation().getZ()-map.getCenterZ()) > (Math.pow(43, mapScale))) {
			
			map.setCenterX(player.getLocation().getBlockX());
			map.setCenterZ(player.getLocation().getBlockZ());
			new BukkitRunnable() {
				public void run() {
					drawClaims(canvas, map, player, mapScale);
				}
			}.runTaskAsynchronously(ClansMain.getInstance());
			
		}
		if (player.getItemInHand().getType() != Material.MAP)
			return;
		if (firstRender == true) {
			drawClaims(canvas, map, player, mapScale);
			player.sendMessage("claim draw");
			firstRender = false;
		}
		
		ClansPlayer cplayer = Players.getInstance().getCPlayerFrom(player);
		MapCursorCollection cursors = canvas.getCursors();
		for (int i = 0; i < canvas.getCursors().size(); i++) {
			if (cursors.getCursor(i).getType() != MapCursor.Type.WHITE_POINTER) {
				cursors.removeCursor(cursors.getCursor(i));
			}
		}
		if (cplayer.getClan() != null) {
			for (ClansPlayer cp : cplayer.getClan().getOnlinePlayers()) {
				if (cp == cplayer)
					continue;
				Player clanmember = Bukkit.getServer().getPlayer(cp.getUUID());
				drawPlayer(canvas, map, clanmember, Type.BLUE_POINTER);
			}
			for (Clan clan : cplayer.getClan().getOnlineAllies()) {
				for (ClansPlayer cp : clan.getOnlinePlayers()) {
					Player clanmember = Bukkit.getServer().getPlayer(cp.getUUID());
					drawPlayer(canvas, map, clanmember, Type.GREEN_POINTER);
				}
			}	
		}
		
		
		
	}
	
	private void drawClaims(MapCanvas canvas, MapView map, Player player, int scale) {
		int topLeftX = (int) (map.getCenterX() - (Math.pow(64, scale)));
		int topLeftZ = (int) (map.getCenterZ() - (Math.pow(64, scale)));
		
		
		
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		for (int i = 0; i < 127; i++) {
			for (int j = 0; j < 127; j++) {
				Block b = player.getWorld().getBlockAt(topLeftX+((int) Math.pow(i, scale)), 
						64, topLeftZ+((int) Math.pow(j, scale)));
				player.sendMessage("Loc: " + b.getX() + ", " + b.getZ());
				Chunk c = b.getChunk();
				Clan clan = ClansManager.getInstance().getClan(ClansMain.getInstance().getAllClaims().get(c));
				if (clan == null)
					continue;
				byte color = 0;
				if (clan.isAllyOf(cPlayer.getClan())) {
					color = MapPalette.LIGHT_GREEN;
				} else if (clan == cPlayer.getClan()) {
					color = MapPalette.BLUE;
				} else {
					color = MapPalette.matchColor(Color.YELLOW);
				}
				if (color == 0)
					return;
				canvas.setPixel(topLeftX+i, topLeftZ+j, color);
				player.sendMessage("Set pixel X: " + topLeftX+i);
				player.sendMessage("Set pixel Z: " + topLeftZ+j);
			}
		}
		
		
		
	}

	private void drawPlayer(MapCanvas canvas, MapView map, Player player, MapCursor.Type type) {
		int topLeftX = map.getCenterX() - 64;
		int topLeftZ = map.getCenterZ() - 64;
		
		int playerX = player.getLocation().getBlockX();
		int playerZ = player.getLocation().getBlockZ();

		int posX = topLeftX - playerX;
		int posY = topLeftZ - playerZ;
		
		if (posX > 127 || posX < -128 || posY > 127 || posY < -128)
			return;
		
		Object craftServer = UtilNms.getCraftBukkitClass("CraftServer").cast(Bukkit.getServer());
		Object nmsServer = UtilNms.getAndInvokeMethod(craftServer.getClass(), "getServer", new Class<?>[0], craftServer);
		Object worldServer = UtilNms.getAndInvokeMethod(UtilNms.getNmsClass("MinecraftServer"), "getWorldServer", new Class<?>[] {int.class}, nmsServer, 0);
		drawCursor(canvas, map, type,
				worldServer, player.getName(), playerX, playerZ, (double) player.getLocation().getYaw());
		
	}
	
	//Get world data daytime
	//THIS IS MOJANG CODE LOOOOL (Most of it)
	private void drawCursor(MapCanvas canvas, MapView map, Type type, Object world, String s, double xPosition,
			double yPosition, double rotation) {
		int scaledBlocks = 1 << map.getScale().getValue();
		float xCenterOffset = (float) (xPosition - map.getCenterX()) / scaledBlocks;
		float yCenterOffset = (float) (yPosition - map.getCenterZ()) / scaledBlocks;
		byte fixedX = (byte) (int) (xCenterOffset * 2.0F + 0.5D);
		byte fixedY = (byte) (int) (yCenterOffset * 2.0F + 0.5D);
		byte offset = 63;
		byte fixedRotation = 0;
		Object wMap = UtilNms.getFieldAndValue(UtilNms.getCraftBukkitClass("map.CraftMapView"), "worldMap", UtilNms.getCraftBukkitClass("map.CraftMapView").cast(map));
		if ((xCenterOffset >= -offset) && (yCenterOffset >= -offset) && (xCenterOffset <= offset) && (yCenterOffset <= offset)) {
			rotation += (rotation < 0.0D ? -8.0D : 8.0D);
			fixedRotation = (byte) (int) (rotation * 16.0D / 360.0D);
			if (((byte) UtilNms.getFieldAndValue(wMap.getClass(), "map", wMap)) < 0) {
				Object worldData = UtilNms.getAndInvokeMethod(UtilNms.getNmsClass("World"), "getWorldData", new Class<?>[0], world);
				long dayTime = UtilNms.getAndInvokeMethod(worldData.getClass(), "getDayTime", new Class<?>[0], worldData);
				int k = (int) (dayTime / 10L);

				fixedRotation = (byte) (k * k * 34187121 + k * 121 >> 15 & 0xF);
			}
		} else {
			if ((Math.abs(xCenterOffset) >= 320.0F) || (Math.abs(yCenterOffset) >= 320.0F)) {
				Map<String, Object> list = UtilNms.getFieldAndValue(wMap.getClass(), "decorations", wMap);
				list.remove(s);
				return;
			}
			if (xCenterOffset <= -offset) {
				fixedX = (byte) (int) (offset * 2 + 2.5D);
			}
			if (yCenterOffset <= -offset) {
				fixedY = (byte) (int) (offset * 2 + 2.5D);
			}
			if (xCenterOffset >= offset) {
				fixedX = (byte) (offset * 2 + 1);
			}
			if (yCenterOffset >= offset) {
				fixedY = (byte) (offset * 2 + 1);
			}
		}
		canvas.getCursors().addCursor(fixedX, fixedY, (byte) Math.min(15, Math.abs(fixedRotation))).setType(type);
	}

}
