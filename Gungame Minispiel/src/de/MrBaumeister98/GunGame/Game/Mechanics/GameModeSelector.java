package de.MrBaumeister98.GunGame.Game.Mechanics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaGameMode;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class GameModeSelector implements Listener {
	
	private ArenaGameMode gamemode;
	private Arena arena;
	private HashMap<ArenaGameMode, ItemStack> itemMap = new HashMap<ArenaGameMode, ItemStack>();
	private Inventory menu;
	private Boolean timeOut;
	
	
	@SuppressWarnings("deprecation")
	public GameModeSelector(Arena parent) {
		this.gamemode =  ArenaGameMode.ALL_VS_ALL;
		this.arena = parent;
		this.timeOut = false;
		
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.buildGUIString("GameModeSelector.Title"));
		for(ArenaGameMode agm : ArenaGameMode.values()) {
			Material m = Material.STONE_SWORD;
			Material ms = null;
			try {
				String mms = LangUtil.buildGUIString("GameModeSelector.Contents." + agm.toString().toUpperCase() + ".Item");
				ms = Material.valueOf(mms);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			if(ms != null) {
				m = ms;
			}
			ItemStack item = new ItemStack(m, 1);
			item.setDurability(Short.valueOf(LangUtil.buildGUIString("GameModeSelector.Contents." + agm.toString().toUpperCase() + ".Damage")));
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.setDisplayName(LangUtil.buildGUIString("GameModeSelector.Contents." + agm.toString().toUpperCase() + ".Name"));
			item.setItemMeta(meta);
			this.itemMap.put(agm, item);
		}
		this.menu = inv;
		adjustMenu();
	}
	public void setTimedOut() {
		this.timeOut = true;
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onConfirmGameMode(InventoryCloseEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			if(!this.timeOut) {
				Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						arena.confirmGameMode();
					}
				}, 10);
			}
		}
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getInventory() != null && event.getClickedInventory() != null && event.getClickedInventory().equals(this.menu)) {
			event.setCancelled(true);
			if(event.getSlot() > 0 && event.getSlot() < 4) {				
				switch(event.getSlot()) {
				case 1:
					this.gamemode = ArenaGameMode.LAST_MAN_STANDING;
					break;
				case 2:
					this.gamemode = ArenaGameMode.ALL_VS_ALL;
					break;
				case 3:
					this.gamemode = ArenaGameMode.TEAM_DEATHMATCH;
					break;
				default:
					break;
				}
				adjustMenu();
				transmitGameMode();
				this.arena.getScoreboardutil().updateScoreBoard();
			}
		}
	}
	
	public void openMenu(Player p) {
		p.openInventory(this.menu);
	}
	public void reset() {
		this.gamemode = ArenaGameMode.ALL_VS_ALL;
		this.arena.setArenaMode(this.gamemode);
		this.adjustMenu();
	}
	public void transmitGameMode() {
		this.arena.setArenaMode(this.gamemode);
	}
	public void adjustMenu() {
		switch(this.gamemode) {
		case ALL_VS_ALL:
			ItemMeta meta = this.itemMap.get(this.gamemode).getItemMeta();
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			this.itemMap.get(this.gamemode).setItemMeta(meta);
			ItemMeta meta2 = this.itemMap.get(ArenaGameMode.LAST_MAN_STANDING).getItemMeta();
			meta2.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.LAST_MAN_STANDING).setItemMeta(meta2);
			ItemMeta meta3 = this.itemMap.get(ArenaGameMode.TEAM_DEATHMATCH).getItemMeta();
			meta3.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.TEAM_DEATHMATCH).setItemMeta(meta3);
			break;
		case LAST_MAN_STANDING:
			ItemMeta meta1 = this.itemMap.get(this.gamemode).getItemMeta();
			meta1.addEnchant(Enchantment.DURABILITY, 1, true);
			this.itemMap.get(this.gamemode).setItemMeta(meta1);
			ItemMeta meta21 = this.itemMap.get(ArenaGameMode.ALL_VS_ALL).getItemMeta();
			meta21.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.ALL_VS_ALL).setItemMeta(meta21);
			ItemMeta meta31 = this.itemMap.get(ArenaGameMode.TEAM_DEATHMATCH).getItemMeta();
			meta31.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.TEAM_DEATHMATCH).setItemMeta(meta31);
			break;
		case TEAM_DEATHMATCH:
			ItemMeta meta11 = this.itemMap.get(this.gamemode).getItemMeta();
			meta11.addEnchant(Enchantment.DURABILITY, 1, true);
			this.itemMap.get(this.gamemode).setItemMeta(meta11);
			ItemMeta meta211 = this.itemMap.get(ArenaGameMode.ALL_VS_ALL).getItemMeta();
			meta211.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.ALL_VS_ALL).setItemMeta(meta211);
			ItemMeta meta311 = this.itemMap.get(ArenaGameMode.LAST_MAN_STANDING).getItemMeta();
			meta311.removeEnchant(Enchantment.DURABILITY);
			this.itemMap.get(ArenaGameMode.LAST_MAN_STANDING).setItemMeta(meta311);
			break;
		default:
			break;
		}
		this.menu.setItem(1, this.itemMap.get(ArenaGameMode.LAST_MAN_STANDING));
		this.menu.setItem(2, this.itemMap.get(ArenaGameMode.ALL_VS_ALL));
		this.menu.setItem(3, this.itemMap.get(ArenaGameMode.TEAM_DEATHMATCH));
	}

}
