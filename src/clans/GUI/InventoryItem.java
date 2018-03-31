package clans.GUI;



import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryItem {
	
	private ItemStack itemStack;
	
	private InventoryItem(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	public static enum ClickResponse {
		NOTHING, ACCEPT, DENY, REMOVE;
	}
	
	public abstract ClickResponse click(Player player, ClickType click);
	
	public static InventoryItem makeItem(ItemStack stack, Clicker onClick) {
		return new InventoryItem(stack) {
			@Override
			public ClickResponse click(Player player, ClickType click) {
				return onClick.click(stack, player, click);
			}
		};
	}

	public ItemStack getItemStack() {
		return itemStack.clone();
	}
	
}
