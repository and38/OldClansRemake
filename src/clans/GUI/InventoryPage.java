package clans.GUI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InventoryPage {

	public static final ItemStack BED;
	private Inventory inventory;
	private HashMap<Integer, InventoryItem> customItems = new HashMap<>();
	private InventoryPage lastInventory;
	private int size;
	private String name;

	static {
		ItemStack bed = new ItemStack(Material.BED);
		ItemMeta meta = bed.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "<- Go Back");
		bed.setItemMeta(meta);
		BED = bed.clone();
	}
	
	public InventoryPage(InventoryPage lastInventory, int size,
			String name) {
		this.inventory = Bukkit.getServer().createInventory(null, size, name);
		this.lastInventory = lastInventory;
		this.name = name;
		this.size = size;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public void setLastInventory(InventoryPage lastInventory) {
		this.lastInventory = lastInventory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public List<ItemStack> getContents() {
		return Arrays.asList(inventory.getContents());
	}
	
	public InventoryPage getLastInventory() {
		return lastInventory;
	}

	public String getName() {
		return name;
	}

	public void close(Player p) {
		if (GUIs.getCurrentInventory(p) != null) {
			if (p.getOpenInventory().getTopInventory() != null) {
				GUIs.setLastInventory(p, GUIs.getCurrentInventory(p));
			} else {
				GUIs.closed(p);
			}
		}
	}
	
	public void addCustomItem(InventoryItem item, int slot) {
		customItems.put(slot, item);
	}
	
	public void addCustomItems(HashMap<InventoryItem, Integer> itemsAndSlots) {
		for (InventoryItem item : itemsAndSlots.keySet()) {
			addCustomItem(item, itemsAndSlots.get(item));
		}
	}
	
	public void open(Player p) {
		inventory.setContents(make(inventory.getContents()));
		placeCustomItems();
		doBack();
		if (p.getOpenInventory().getTopInventory() != null && GUIs.getCurrentInventory(p) != null) {
			GUIs.setLastInventory(p, GUIs.getCurrentInventory(p));
		}
		p.openInventory(inventory);
		p.updateInventory();
		GUIs.setInventory(p, this);
	}

	public HashMap<Integer, InventoryItem> getCustomItems() {
		return customItems;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected abstract ItemStack[] make(ItemStack[] currentItems);

	private void placeCustomItems() {
		for (int x : customItems.keySet()) {
			inventory.setItem(x, customItems.get(x).getItemStack().clone());
		}
	}
	
	private void doBack() {
		if (lastInventory != null && inventory.getContents()[0] == null) {
			inventory.setItem(0, BED.clone());
		}
	}

}
