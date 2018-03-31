package clans.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import clans.ClansMain;
import clans.events.ScoreboardChangeEvent;
import clans.events.ScoreboardChangeEvent.ScoreboardPosition;
import clans.region.MapRender;
import clans.util.Display;

public class GenericListeners implements Listener {

	private BukkitRunnable checking;
	List<FallingBlock> blockss = new ArrayList<FallingBlock>();
	public static final HashMap<ItemStack, ItemStack> blockChanges = new HashMap<ItemStack, ItemStack>(){{
		put(new ItemStack(Material.SMOOTH_BRICK, 1, (byte) 0), new ItemStack(Material.SMOOTH_BRICK, 1, (byte) 2));
		put(new ItemStack(Material.PRISMARINE, 1, (byte) 1), new ItemStack(Material.PRISMARINE, 1));
		put(new ItemStack(Material.PRISMARINE, 1, (byte) 2), new ItemStack(Material.PRISMARINE, 1));
		put(new ItemStack(Material.IRON_DOOR), new ItemStack(Material.WOODEN_DOOR));
		put(new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.TRAP_DOOR));
		put(new ItemStack(Material.NETHER_BRICK), new ItemStack(Material.NETHERRACK));
	}};
	
	@EventHandler
	public void onScoreboardChange(ScoreboardChangeEvent e) {
		Player player = e.getPlayer();
		if (player.getScoreboard().getTeam(e.getPosition().getTeamName()) == null) return;
		player.getScoreboard().getTeam(e.getPosition().getTeamName()).setPrefix(e.getNewText());
		if (e.getPosition() == ScoreboardPosition.TERRITORY && e.getOldText().equals(e.getNewText()) == false)
			Display.displaySubTitle(player, e.getNewText(), 0, 30, 10);
	}

	@EventHandler
	public void onMap(MapInitializeEvent e) {
		MapView map = e.getMap();
		map.addRenderer(new MapRender());
	}

	@EventHandler
	public void onPrime(ExplosionPrimeEvent e) {
		if (e.getEntityType() != EntityType.PRIMED_TNT) return;
		Block loc = e.getEntity().getLocation().getBlock();
		for (int i = -3; i < 3; i++) {
			for (int i1 = -3; i1 < 3; i1++) {
				for (int i11 = -3; i11 < 3; i11++) {
					
					Block relative = loc.getRelative(i, i1, i11);
					if (relative.isLiquid()) {
						relative.setTypeId(0);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTnt(EntityExplodeEvent e) {
		if (e.getEntityType() != EntityType.PRIMED_TNT) return;
		Iterator<Block> blocks = e.blockList().iterator();
		while (blocks.hasNext()) {
			Block b = blocks.next();
			if (b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER) {
				b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
				b.breakNaturally();
			} else if (blockChanges.containsKey(b.getState().getData().toItemStack(1))) {
				ItemStack blockStack = b.getState().getData().toItemStack(1);
				blocks.remove();
				b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
				b.getLocation().getBlock().setType(blockChanges.get(blockStack).getType());
				b.getLocation().getBlock().setData(blockChanges.get(blockStack).getData().getData());
			} else if (b.getType().toString().endsWith("ORE") || b.getType().toString().endsWith("CHEST") || b.getType() == Material.OBSIDIAN) {
				b.breakNaturally();
			} else {	
				if (Math.random() > 0.70) {
					Vector to = b.getLocation().toVector().subtract(e.getEntity().getLocation().toVector()).normalize();
					Vector finall = to;
					finall.setY(0.7);
					finall.setX(finall.getX() * 0.85);
					finall.setZ(finall.getZ() * 0.85);
					FallingBlock block = (FallingBlock) b.getWorld().spawnFallingBlock(b.getLocation().add(0,0.25,0), 
							b.getType(), b.getData());
					block.setDropItem(false);
					block.setVelocity(finall.toBlockVector());
					block.setHurtEntities(true);
					blockss.add(block);
					if (checking == null) {
						checking = new BukkitRunnable() {
							@Override
							public void run() {
								if (blockss.isEmpty()) {
									checking = null;
									this.cancel();
								}
								Iterator<FallingBlock> iterator = blockss.iterator();
								while (iterator.hasNext()) {
									FallingBlock b = iterator.next();
									if (b == null) {
										iterator.remove();
										continue;
									}
									if (b.isDead() || b.getTicksLived() > 20 * 12 || b.isOnGround() || b.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
										b.remove();
										b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getMaterial());
										b.getLocation().getBlock().setType(Material.AIR);
										b.getWorld().playSound(b.getLocation(), Sound.DIG_STONE, 1, 1.2f);
										iterator.remove();
									}
								}
							}
						};
						checking.runTaskTimer(ClansMain.getInstance(), 0, 1);
					} 
				}
			}
		}
	}
}

