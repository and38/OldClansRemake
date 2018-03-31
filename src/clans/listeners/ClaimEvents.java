package clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Door;
import org.bukkit.material.Openable;

import clans.ClansMain;
import clans.chat.Chat;
import clans.clan.Clan;
import clans.clan.ClansManager;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.player.ClansPlayer;
import clans.player.Players;
import clans.region.Region;
import clans.util.UtilGeneric;

public class ClaimEvents implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent ev) {
		Player p = ev.getPlayer();
		Block b = ev.getBlock();
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(p);
		if (ClansMain.getInstance().getAllClaims()
				.containsKey(b.getLocation().getChunk())) {
			String name = ClansMain.getInstance().getAllClaims()
					.get(b.getLocation().getChunk());
			Clan clan = ClansManager.getInstance().getClan(name);
			if (cPlayer.getClan() != null) {
				if (cPlayer.getClan().getName().equalsIgnoreCase(name)) {
					return;
				}
			}
			p.sendMessage(Chat.message("You cannot break blocks in "
					+ clan.getClanRelationColor(cPlayer.getClan()) + name
					+ "'s" + ChatColor.GRAY + " territory"));
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent ev) {
		Player p = ev.getPlayer();
		Block b = ev.getBlock();
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(p);
		for (BlockFace face : new BlockFace[] { BlockFace.SELF,
				BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST,
				BlockFace.SOUTH }) {
			Block faced = b.getRelative(face);
			if (ClansMain.getInstance().getAllClaims()
					.containsKey(faced.getLocation().getChunk())) {
				String name = ClansMain.getInstance().getAllClaims()
						.get(faced.getLocation().getChunk());
				Clan clan = ClansManager.getInstance().getClan(name);
				if (cPlayer.getClan() != null) {
					if (cPlayer.getClan().getName().equalsIgnoreCase(name)) {
						return;
					}
				}
				p.sendMessage(Chat.message("You cannot place blocks "
						+ (face == BlockFace.SELF ? "in " : "next to ")
						+ clan.getClanRelationColor(cPlayer.getClan()) + name
						+ "'s" + ChatColor.GRAY + " territory"));
				ev.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onToggleOpenable(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!Region.isUseableClaim(e.getClickedBlock(), e.getPlayer()))
				return;
			if (e.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK || 
					e.getClickedBlock().getType() == Material.IRON_TRAPDOOR) {
				Block b = e.getClickedBlock();
				BlockState state = b.getState();
				Openable openable = (Openable) state.getData();
				if (b.getType() == Material.IRON_DOOR_BLOCK) {
					Door door = (Door) state.getData();
					if (door.isTopHalf()) {
						b = b.getRelative(BlockFace.DOWN);
						state = b.getState();
						openable = (Openable) state.getData();
					}
				}
				
				openable.setOpen(!openable.isOpen());
				state.update(true, true);
				b.getWorld().playEffect(b.getLocation(), Effect.DOOR_TOGGLE, 0);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDoorClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			ClansPlayer cp = Players.getInstance().getCPlayerFrom(e.getPlayer());
			if (!Region.isUseableClaim(b, cp) && (b.getState().getData() instanceof Openable)) {
				if (e.getPlayer().getItemInHand() == null)
					return;
				e.setCancelled(true);
				e.getPlayer().sendMessage(Chat.message("You may not use this here."));
			}
		}
	}



	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getFrom().getBlock().getChunk() != e.getTo().getBlock()
				.getChunk()) {
			Player p = e.getPlayer();
			ClansPlayer cp = Players.getInstance().getCPlayerFrom(p);

			String name = ClansMain.getInstance().getAllClaims().get(e.getTo().getBlock().getChunk());
			String from = ClansMain.getInstance().getAllClaims().get(e.getFrom().getBlock().getChunk());

			String oldNameUse = name == null ? ChatColor.GRAY + "Wilderness" : ClansManager.getInstance().getClan(name)
					.getRelationColor(cp.getClan());
			String fromNameUse = from == null ? ChatColor.GRAY + "Wilderness" : ClansManager.getInstance().getClan(from)
					.getRelationColor(cp.getClan());

			ScoreboardChangeEvent scChange;
			if (name == null) {
				scChange = new ScoreboardChangeEvent( ScoreboardPosition.TERRITORY, ChatColor.GRAY
						+ "Wilderness", fromNameUse + (from == null ? "" : from), p);
				Bukkit.getServer().getPluginManager().callEvent(scChange);
			} else {
				scChange = new ScoreboardChangeEvent( ScoreboardPosition.TERRITORY, oldNameUse + name,
						fromNameUse + (from == null ? "" : from), p);
				Bukkit.getServer().getPluginManager().callEvent(scChange);
			}
			if (!oldNameUse.equals(fromNameUse))
				p.sendMessage(Chat.messageFill("Territory", scChange.getNewText()));
		}
	}

}
