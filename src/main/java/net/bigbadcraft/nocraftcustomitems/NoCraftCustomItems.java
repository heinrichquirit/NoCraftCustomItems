package main.java.net.bigbadcraft.nocraftcustomitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class NoCraftCustomItems extends JavaPlugin implements Listener {

	private ChatColor RED = ChatColor.RED;
	private List<String> whiteList;
	private List<String> translatedList = new ArrayList<String>();

	public void onEnable() {

		saveDefaultConfig();

		this.whiteList = getConfig().getStringList("whitelisted-item-names");
		for (String s : this.whiteList) {
			this.translatedList.add(ChatColor.translateAlternateColorCodes('&', s));
		}

		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerCraft(PrepareItemCraftEvent e) {
		if ((e.getView().getPlayer() instanceof Player)) {
			Player player = (Player) e.getView().getPlayer();
			Inventory inv = e.getInventory();
			if (inv.getType() == InventoryType.CRAFTING) {
				ItemStack[] arrayOfItemStack;
				int j = (arrayOfItemStack = inv.getContents()).length;
				for (int i = 0; i < j; i++) {
					ItemStack stack = arrayOfItemStack[i];
					if ((stack != null) && (stack.hasItemMeta())) {
						ItemMeta itemMeta = stack.getItemMeta();
						String displayName = ChatColor.translateAlternateColorCodes('&', itemMeta.getDisplayName());
						if ((!this.translatedList.contains(displayName)) && (!hasPermission(player, "nocraftcustomitems.craft"))) {
							player.sendMessage(this.RED + "You cannot craft this item.");
							e.getInventory().setResult(new ItemStack(Material.AIR));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getCurrentItem() != null) {
			ItemStack item = e.getCurrentItem();
			InventoryType type = e.getInventory().getType();
			if (item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				String displayName = ChatColor.translateAlternateColorCodes('&', itemMeta.getDisplayName());
				if (!translatedList.contains(displayName)) {
					switch (type) {
					case CRAFTING:
						if (!hasPermission(player, "nocraftcustomitems.workbench")) {
							player.sendMessage(this.RED + "You cannot craft this item.");
							e.setCancelled(true);
						}
						break;
					case CHEST:
						if (!hasPermission(player, "nocraftcustomitems.smelt")) {
							player.sendMessage(this.RED + "You cannot smelt this item.");
							e.setCancelled(true);
						}
						break;
					case DROPPER:
						if (!hasPermission(player, "nocraftcustomitems.brew")) {
							player.sendMessage(this.RED + "You cannot brew this item.");
							e.setCancelled(true);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private boolean hasPermission(Player player, String permission) {
		return player.hasPermission(permission);
	}
}
