package de.MrBaumeister98.GunGame.GunEngine.Shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.Items.Crowbar_pre_1_13;
import de.MrBaumeister98.GunGame.Items.Crowbar_v1_13_up;
import de.MrBaumeister98.GunGame.Items.FlareGun;
import de.MrBaumeister98.GunGame.Items.Radar;
import de.MrBaumeister98.GunGame.Items.SuicideArmor;
import de.MrBaumeister98.GunGame.Items.TrackPadItem;

public class ShopGUI implements Listener {
	//PATH: getGUI + "GunEngine.Shop.MainMenu...."
	public GunGamePlugin plugin;
	public ShopHelper shophelper;
	private Inventory menu;
	private Inventory gunMenu;
	
	private ItemStack backItem;
	private ItemStack changePageItem;
	
	private List<Inventory> assaultGuns = new ArrayList<Inventory>();
	private List<Inventory> assaultPlasmaGuns = new ArrayList<Inventory>();
	private List<Inventory> miniGuns = new ArrayList<Inventory>();
	private List<Inventory> miniPlasmaGuns = new ArrayList<Inventory>();
	private List<Inventory> standardGuns = new ArrayList<Inventory>();
	private List<Inventory> rocketlaunchers = new ArrayList<Inventory>();
	private List<Inventory> plasmaGuns = new ArrayList<Inventory>();
	private List<Inventory> grenadethrowers = new ArrayList<Inventory>();
	
	private List<Inventory> grenades = new ArrayList<Inventory>();
	
	private List<Inventory> turrets = new ArrayList<Inventory>();
	
	private List<Inventory> ammo = new ArrayList<Inventory>();
	
	private List<Inventory> landmines = new ArrayList<Inventory>();
	
	private List<Inventory> miscItems = new ArrayList<Inventory>();
	
	private List<Inventory> airstrikes = new ArrayList<Inventory>();
	
	private List<Inventory> tanks = new ArrayList<Inventory>();
	
	@SuppressWarnings("deprecation")
	public ShopGUI(GunGamePlugin plugin) {
		this.plugin = plugin;
		
		this.menu = Bukkit.createInventory(null, 9, LangUtil.buildGUIString("GunEngine.Shop.MainMenu.Title"));
		this.gunMenu = Bukkit.createInventory(null, 9, LangUtil.buildGUIString("GunEngine.Shop.GunMenu"));
		
		//Material mat = Material.valueOf(LangUtil.buildGUIString("GunEngine.Shop.NavItems.BackItem.Item"));
		Material mat = LangUtil.getShopGUIItem("NavItems.BackItem.Item", GunGamePlugin.instance.serverPre113 ? "ARROW" : "ARROW");
		short dmgBI = Short.valueOf(LangUtil.buildGUIString("GunEngine.Shop.NavItems.BackItem.Damage"));
		ItemStack bI = new ItemStack(mat, 1, dmgBI);
		ItemMeta meta = bI.getItemMeta();
		meta.setDisplayName(LangUtil.buildGUIString("GunEngine.Shop.NavItems.BackItem.Name"));
		meta.setLore(LangUtil.getStringListByPath("lang.GUI.GunEngine.Shop.NavItems.BackItem.Lore"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		bI.setItemMeta(meta);
		bI = ItemUtil.addTags(bI, "GG_Shop_BackItem", true);
		bI = ItemUtil.addTags(bI, "GG_Shop_Item", true);
		this.backItem = bI;
		
		//Material mat2 = Material.valueOf(LangUtil.buildGUIString("GunEngine.Shop.NavItems.ChangePageItem.Item"));
		Material mat2 = LangUtil.getShopGUIItem("NavItems.ChangePageItem.Item", GunGamePlugin.instance.serverPre113 ? "STRUCTURE_VOID" : "STRUCTURE_VOID");
		short dmgCI = Short.valueOf(LangUtil.buildGUIString("GunEngine.Shop.NavItems.ChangePageItem.Damage"));
		ItemStack cI = new ItemStack(mat2, 1, dmgCI);
		ItemMeta meta2 = cI.getItemMeta();
		meta2.setDisplayName(LangUtil.buildGUIString("GunEngine.Shop.NavItems.ChangePageItem.Name"));
		meta2.setLore(LangUtil.getStringListByPath("lang.GUI.GunEngine.Shop.NavItems.ChangePageItem.Lore"));
		meta2.setUnbreakable(true);
		meta2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta2.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		cI.setItemMeta(meta2);
		cI = ItemUtil.addTags(cI, "GG_Shop_ChangePageItem", true);
		cI = ItemUtil.addTags(cI, "GG_Shop_Item", true);
		this.changePageItem = cI;
		
		String[] keys = {"Grenade", "Gun", "Ammo", "Airstrike", "Landmine", "Turret", "MiscItems", "Tank"};
		for(String k : keys) {
			//Material m = Material.valueOf(LangUtil.buildGUIString("GunEngine.Shop.MainMenu.Icons." + k + ".Item"));
			Material m = LangUtil.getShopGUIItem("MainMenu.Icons." + k + ".Item", GunGamePlugin.instance.serverPre113 ? "COMMAND" : "COMMAND_BLOCK");
			short dmg = Short.valueOf(LangUtil.buildGUIString("GunEngine.Shop.MainMenu.Icons." + k + ".Damage"));
			ItemStack item = new ItemStack(m, 1, dmg);
			ItemMeta met = item.getItemMeta();
			met.setDisplayName(LangUtil.buildGUIString("GunEngine.Shop.MainMenu.Icons." + k + ".Name"));
			met.setLore(LangUtil.getStringListByPath("lang.GUI.GunEngine.Shop.MainMenu.Icons." + k + ".Lore"));
			met.setUnbreakable(true);
			met.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(met);
			item = ItemUtil.addTags(item, "GG_Shop_Icon_" + k, true);
			item = ItemUtil.addTags(item, "GG_Shop_Item", true);
			if(k.equalsIgnoreCase("grenade")) {
				this.menu.setItem(2, item);
			}
			if(k.equalsIgnoreCase("gun")) {
				this.menu.setItem(0, item);
			}
			if(k.equalsIgnoreCase("ammo")) {
				this.menu.setItem(1, item);
			}
			if(k.equalsIgnoreCase("airstrike")) {
				this.menu.setItem(4, item);
			}
			if(k.equalsIgnoreCase("landmine")) {
				this.menu.setItem(5, item);
			}
			if(k.equalsIgnoreCase("turret")) {
				this.menu.setItem(6, item);
			}
			if(k.equalsIgnoreCase("tank")) {
				this.menu.setItem(7, item);
			}
			if(k.equalsIgnoreCase("miscitems")) {
				this.menu.setItem(3, item);
			}
		}
		String[] gunKeys = {"Standard", "Assault", "Minigun", "Rocketlauncher", "Plasma", "AssaultPlasma", "MinigunPlasma", "Grenadethrower"};
		for(String k : gunKeys) {
			//Material m = Material.valueOf(LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons." + k + ".Item"));
			Material m = LangUtil.getShopGUIItem("GunMenuInv.Icons." + k + ".Item", "BOW");
			short dmg = Short.valueOf(LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons." + k + ".Damage"));
			ItemStack item = new ItemStack(m, 1, dmg);
			ItemMeta met = item.getItemMeta();
			met.setDisplayName(LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons." + k + ".Name"));
			met.setLore(LangUtil.getStringListByPath("lang.GUI.GunEngine.Shop.GunMenuInv.Icons." + k + ".Lore"));
			met.setUnbreakable(true);
			met.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(met);
			item = ItemUtil.addTags(item, "GG_Shop_GunIcon_" + k, true);
			item = ItemUtil.addTags(item, "GG_Shop_Item", true);
			this.gunMenu.addItem(item);
		}
		this.gunMenu.addItem(this.backItem);
	}
	public void openShop(Player p) {
		p.openInventory(this.menu);
	}
	public void setupLists() {
		this.shophelper = this.plugin.weaponManager.getShopHelper();
		//AIRSTRIKES
		Integer inventoryCount = 0;
		if(this.plugin.weaponManager.airstrikes.size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.weaponManager.airstrikes.size(), 45);
		}
		String invTitle = LangUtil.buildGUIString("GunEngine.Shop.AirstrikeMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.weaponManager.airstrikes.size()) {
						ItemStack item = this.plugin.weaponManager.airstrikes.get(itemPos).getItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
				this.airstrikes.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.weaponManager.airstrikes.size()) {
					ItemStack item = this.plugin.weaponManager.airstrikes.get(itemPos).getItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.airstrikes.add(temp);
		}
		//MISC ITEMS
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.MiscItemsMenu");
		Inventory tempMisc = Bukkit.createInventory(null, 54, invTitle);
		tempMisc.setItem(49, this.backItem);
		
		String price = LangUtil.buildGUIString("GunEngine.Shop.MiscItemsPrice");
		
		/*ItemStack c4 = C4.c4Throwable(1);
		c4 = ItemUtil.addTags(c4, "GG_Shop_Buyable_Item", true);
		c4 = ItemUtil.addTags(c4, "GG_Shop_Buyable_Item_Misc", "C4");
		ItemMeta c4Meta = c4.getItemMeta();
		List<String> lore = c4Meta.getLore();
		lore.add(price.replaceAll("%worth%", this.shophelper.getPrice(c4).toString()));
		c4Meta.setLore(lore);
		c4.setItemMeta(c4Meta);
		
		ItemStack c4Remote = C4.c4Remote();
		c4Remote = ItemUtil.addTags(c4Remote, "GG_Shop_Buyable_Item", true);
		c4Remote = ItemUtil.addTags(c4Remote, "GG_Shop_Buyable_Item_Misc", "C4Remote");
		ItemMeta c4RemoteMeta = c4Remote.getItemMeta();
		List<String> lore1 = c4RemoteMeta.getLore();
		lore1.add(price.replaceAll("%worth%", this.shophelper.getPrice(c4Remote).toString()));
		c4RemoteMeta.setLore(lore1);
		c4Remote.setItemMeta(c4RemoteMeta);*/
		
		ItemStack crowbar = null;
		if(GunGamePlugin.instance.serverPre113) {
			crowbar = Crowbar_pre_1_13.CrowBar();
		} else {
			crowbar = Crowbar_v1_13_up.CrowBar();
		}
		crowbar = ItemUtil.addTags(crowbar, "GG_Shop_Buyable_Item", true);
		crowbar = ItemUtil.addTags(crowbar, "GG_Shop_Buyable_Item_Misc", "Crowbar");
		ItemMeta crowbarMeta = crowbar.getItemMeta();
		List<String> lore2 = crowbarMeta.getLore();
		lore2.add(price.replaceAll("%worth%", this.shophelper.getPrice(crowbar).toString()));
		crowbarMeta.setLore(lore2);
		crowbar.setItemMeta(crowbarMeta);
		
		ItemStack bombWest = SuicideArmor.suicideWest().clone();
		bombWest = ItemUtil.addTags(bombWest, "GG_Shop_Buyable_Item", true);
		bombWest = ItemUtil.addTags(bombWest, "GG_Shop_Buyable_Item_Misc", "SuicideVest");
		ItemMeta vestMeta = bombWest.getItemMeta();
		List<String> lore3 = vestMeta.getLore();
		lore3.add(price.replaceAll("%worth%", this.shophelper.getPrice(bombWest).toString()));
		vestMeta.setLore(lore3);
		bombWest.setItemMeta(vestMeta);
		
		ItemStack bombWestRemote = SuicideArmor.remote().clone();
		bombWestRemote = ItemUtil.addTags(bombWestRemote, "GG_Shop_Buyable_Item", true);
		bombWestRemote = ItemUtil.addTags(bombWestRemote, "GG_Shop_Buyable_Item_Misc", "SuicideVestRemote");
		ItemMeta vestRemoteMeta = bombWestRemote.getItemMeta();
		List<String> lore4 = vestRemoteMeta.getLore();
		lore4.add(price.replaceAll("%worth%", this.shophelper.getPrice(bombWestRemote).toString()));
		vestRemoteMeta.setLore(lore4);
		bombWestRemote.setItemMeta(vestRemoteMeta);
		
		ItemStack radar = Radar.radar().clone();
		radar = ItemUtil.addTags(radar, "GG_Shop_Buyable_Item", true);
		radar = ItemUtil.addTags(radar, "GG_Shop_Buyable_Item_Misc", "Radar");
		ItemMeta radarMeta = radar.getItemMeta();
		List<String> lore5 = radarMeta.getLore();
		lore5.add(price.replaceAll("%worth%", this.shophelper.getPrice(radar).toString()));
		radarMeta.setLore(lore5);
		radar.setItemMeta(radarMeta);
		
		ItemStack trackpad = TrackPadItem.getTrackPadBaseItem();
		trackpad = ItemUtil.addTags(trackpad, "GG_Shop_Buyable_Item", true);
		trackpad = ItemUtil.addTags(trackpad, "GG_Shop_Buyable_Item_Misc", "TrackPad");
		ItemMeta tpMeta = trackpad.getItemMeta();
		List<String> lore7 = tpMeta.getLore();
		lore7.add(price.replaceAll("%worth%", this.shophelper.getPrice(trackpad).toString()));
		tpMeta.setLore(lore7);
		trackpad.setItemMeta(tpMeta);
		
		ItemStack flaregun = FlareGun.getFlareGun().clone();
		flaregun = ItemUtil.addTags(flaregun, "GG_Shop_Buyable_Item", true);
		flaregun = ItemUtil.addTags(flaregun, "GG_Shop_Buyable_Item_Misc", "FlareGun");
		ItemMeta flareMeta = flaregun.getItemMeta();
		List<String> lore6 = flareMeta.getLore();
		lore6.add(price.replaceAll("%worth%", this.shophelper.getPrice(flaregun).toString()));
		flareMeta.setLore(lore6);
		flaregun.setItemMeta(flareMeta);
		
		/*tempMisc.setItem(0, c4);
		tempMisc.setItem(1, c4Remote);*/
		tempMisc.setItem(9, bombWest);
		tempMisc.setItem(10, bombWestRemote);
		tempMisc.setItem(18, crowbar);
		tempMisc.setItem(19, flaregun);
		tempMisc.setItem(27, radar);
		tempMisc.setItem(28, trackpad);
		
		this.miscItems.add(tempMisc);
		
		//GRENADES
		inventoryCount = 0;
		if(this.plugin.weaponManager.grenades.size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.weaponManager.grenades.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GrenadeMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.weaponManager.grenades.size()) {
						ItemStack item = this.plugin.weaponManager.grenades.get(itemPos).getGrenadeItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
				this.grenades.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.weaponManager.grenades.size()) {
					ItemStack item = this.plugin.weaponManager.grenades.get(itemPos).getGrenadeItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.grenades.add(temp);
		}
		
		//AMMO
		inventoryCount = 0;
		if(this.plugin.weaponManager.ammos.size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.weaponManager.ammos.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.AmmoMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.weaponManager.ammos.size()) {
						ItemStack item = this.plugin.weaponManager.ammos.get(itemPos).getItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.ammo.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.weaponManager.ammos.size()) {
					ItemStack item = this.plugin.weaponManager.ammos.get(itemPos).getItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.ammo.add(temp);
		}
		
		//LANDMINES
		inventoryCount = 0;
		if(this.plugin.weaponManager.landmines.size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.weaponManager.landmines.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.LandmineMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.weaponManager.landmines.size()) {
						ItemStack item = this.plugin.weaponManager.landmines.get(itemPos).getItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
				this.landmines.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.weaponManager.landmines.size()) {
					ItemStack item = this.plugin.weaponManager.landmines.get(itemPos).getItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.landmines.add(temp);
		}
		
		//TURRETS
		inventoryCount = 0;
		if(this.plugin.turretManager.turrets.size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.turretManager.turrets.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.TurretMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.turretManager.turrets.size()) {
						ItemStack item = this.plugin.turretManager.turrets.get(itemPos).getItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.turrets.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.turretManager.turrets.size()) {
					ItemStack item = this.plugin.turretManager.turrets.get(itemPos).getItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.turrets.add(temp);
		}
		//TANKS
		inventoryCount = 0;
		if(this.plugin.tankManager.getTankConfigs().size() > 45) {
			inventoryCount = getInventoryCount(this.plugin.tankManager.getTankConfigs().size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.TankMenu");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < this.plugin.tankManager.getTankConfigs().size()) {
						ItemStack item = this.plugin.tankManager.getTankConfigs().get(itemPos).getTankItem().clone();
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.tanks.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < this.plugin.tankManager.getTankConfigs().size()) {
					ItemStack item = this.plugin.tankManager.getTankConfigs().get(itemPos).getTankItem().clone();
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.tanks.add(temp);
		}
		//GUNS
		List<ItemStack> standardGunss = new ArrayList<ItemStack>();
		List<ItemStack> assaultGunss = new ArrayList<ItemStack>();
		List<ItemStack> assaultPlasmaGunss = new ArrayList<ItemStack>();
		List<ItemStack> miniGunss = new ArrayList<ItemStack>();
		List<ItemStack> miniPlasmaGunss = new ArrayList<ItemStack>();
		List<ItemStack> plasmaGunss = new ArrayList<ItemStack>();
		List<ItemStack> rocketlauncherss = new ArrayList<ItemStack>();
		List<ItemStack> grenadethrowerss = new ArrayList<ItemStack>();
		for(Gun gun : this.plugin.weaponManager.guns) {
			switch(gun.getType()) {
			case ASSAULT:
				assaultGunss.add(gun.getItem().clone());
				break;
			case ASSAULT_PLASMA:
				assaultPlasmaGunss.add(gun.getItem().clone());
				break;
			case GRENADETHROWER:
				grenadethrowerss.add(gun.getItem().clone());
				break;
			case MINIGUN:
				miniGunss.add(gun.getItem().clone());
				break;
			case MINIGUN_PLASMA:
				miniPlasmaGunss.add(gun.getItem().clone());
				break;
			case PLASMA:
				plasmaGunss.add(gun.getItem().clone());
				break;
			case ROCKETLAUNCHER:
				rocketlauncherss.add(gun.getItem().clone());
				break;
			case STANDARD:
				standardGunss.add(gun.getItem().clone());
				break;
			default:
				break;
			
			}
		}
		//STANDARD
		inventoryCount = 0;
		if(standardGunss.size() > 45) {
			inventoryCount = getInventoryCount(standardGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Standard.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < standardGunss.size()) {
						ItemStack item = standardGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.standardGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < standardGunss.size()) {
					ItemStack item = standardGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.standardGuns.add(temp);
		}
		//ASSAULT
		inventoryCount = 0;
		if(standardGunss.size() > 45) {
			inventoryCount = getInventoryCount(assaultGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Assault.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < assaultGunss.size()) {
						ItemStack item = assaultGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.assaultGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < assaultGunss.size()) {
					ItemStack item = assaultGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.assaultGuns.add(temp);
		}
		//ASSAULT PLASMA
		inventoryCount = 0;
		if(assaultPlasmaGunss.size() > 45) {
			inventoryCount = getInventoryCount(assaultPlasmaGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.AssaultPlasma.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < assaultPlasmaGunss.size()) {
						ItemStack item = assaultPlasmaGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.assaultPlasmaGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < assaultPlasmaGunss.size()) {
					ItemStack item = assaultPlasmaGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.assaultPlasmaGuns.add(temp);
		}
		//MINIGUNS
		inventoryCount = 0;
		if(miniGunss.size() > 45) {
			inventoryCount = getInventoryCount(miniGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Minigun.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < miniGunss.size()) {
						ItemStack item = miniGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.miniGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < miniGunss.size()) {
					ItemStack item = miniGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.miniGuns.add(temp);
		}
		//MINIGUNS PLASMA
		inventoryCount = 0;
		if(miniPlasmaGunss.size() > 45) {
			inventoryCount = getInventoryCount(miniPlasmaGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.MinigunPlasma.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < miniPlasmaGunss.size()) {
						ItemStack item = miniPlasmaGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.miniPlasmaGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < miniPlasmaGunss.size()) {
					ItemStack item = miniPlasmaGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.miniPlasmaGuns.add(temp);
		}
		//PLASMA
		inventoryCount = 0;
		if(plasmaGunss.size() > 45) {
			inventoryCount = getInventoryCount(plasmaGunss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Plasma.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < plasmaGunss.size()) {
						ItemStack item = plasmaGunss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.plasmaGuns.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < plasmaGunss.size()) {
					ItemStack item = plasmaGunss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.plasmaGuns.add(temp);
		}
		//ROCKETLAUNCHERS
		inventoryCount = 0;
		if(rocketlauncherss.size() > 45) {
			inventoryCount = getInventoryCount(rocketlauncherss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Rocketlauncher.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < rocketlauncherss.size()) {
						ItemStack item = rocketlauncherss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.rocketlaunchers.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < rocketlauncherss.size()) {
					ItemStack item = rocketlauncherss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.rocketlaunchers.add(temp);
		}
		//GRENADELAUNCHER
		inventoryCount = 0;
		if(grenadethrowerss.size() > 45) {
			inventoryCount = getInventoryCount(grenadethrowerss.size(), 45);
		}
		invTitle = LangUtil.buildGUIString("GunEngine.Shop.GunMenuInv.Icons.Grenadethrower.Name");
		if(inventoryCount > 0) {
			for(Integer invCount = 0; invCount < inventoryCount; invCount++) {
				Inventory temp = Bukkit.createInventory(null, 54, invTitle + ChatColor.YELLOW + "    <" + (invCount+1) + "/" + inventoryCount + ">");
				Integer itemPos = 0;
				for(int tempPos = 0; tempPos < 45; tempPos++) {
					if(itemPos < grenadethrowerss.size()) {
						ItemStack item = grenadethrowerss.get(itemPos);
						item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
						item.setAmount(1);
						temp.addItem(item);
						itemPos = itemPos +1;
					} else {
						break;
					}
				}
				//45 46 47 48 49 50 51 52 53
				temp.setItem(48, this.backItem);
				temp.setItem(50, this.changePageItem);
				
					this.grenadethrowers.add(temp);
			}
		} else {
			Inventory temp = Bukkit.createInventory(null, 54, invTitle);
			Integer itemPos = 0;
			for(int tempPos = 0; tempPos < 45; tempPos++) {
				if(itemPos < grenadethrowerss.size()) {
					ItemStack item = grenadethrowerss.get(itemPos);
					item = ItemUtil.addTags(item, "GG_Shop_Buyable_Item", true);
					item.setAmount(1);
					temp.addItem(item);
					itemPos = itemPos +1;
				} else {
					break;
				}
			}
			//45 46 47 48 49 50 51 52 53
			temp.setItem(49, this.backItem);
			//temp.setItem(50, this.changePageItem);
			
			this.grenadethrowers.add(temp);
		}
		
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() != null && isShopInventory(event.getInventory())) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getInventory() != null && event.getClickedInventory() != null  && event.getCurrentItem() != null && event.isShiftClick() && isShopInventory(event.getInventory())) {
			event.setCancelled(true);
		}
		if(event.getCurrentItem() != null && event.getClickedInventory() != null && event.getInventory() != null && isShopInventory(event.getClickedInventory())) {
			event.setCancelled(true);
			Inventory ci = event.getClickedInventory();
			Boolean isBackItem = isBackItem(event.getCurrentItem());
			Boolean isChangePageItem = isChangePageItem(event.getCurrentItem());
			Player p = (Player)event.getWhoClicked();
			EClickType changeType = null;
			ItemStack item = event.getCurrentItem();
			EBuySuccess result = EBuySuccess.NONE;
			Integer amount = 1;
			if(event.isLeftClick()) {
				changeType = EClickType.LEFT;
				amount = 16;
				if(event.isShiftClick()) {
					amount = 32;
				}
			} else if(event.isRightClick()) {
				changeType = EClickType.RIGHT;
				amount = 1;
			}
			EPageType type = getType(ci);
			switch(type) {
			case TANKS:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case AIRSTRIKES:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case AMMO:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case GRENADES:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case GUNS_ASSAULT:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_ASSAULT_PLASMA:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_GRENADETHROWERS:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_MINIGUNS:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_MINIGUNS_PLASMA:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_PLASMA:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_ROCKETLAUNCHERS:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUNS_STANDARD:
				if(isBackItem) {
					p.openInventory(this.gunMenu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					amount = 1;
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			case GUN_MENU:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else {
					switch(event.getSlot()) {
					default:
						break;
					case 0:
						p.openInventory(this.standardGuns.get(0));
						break;
					case 1:
						p.openInventory(this.assaultGuns.get(0));
						break;
					case 2:
						p.openInventory(this.miniGuns.get(0));
						break;
					case 3:
						p.openInventory(this.rocketlaunchers.get(0));
						break;
					case 4:
						p.openInventory(this.plasmaGuns.get(0));
						break;
					case 5:
						p.openInventory(this.assaultPlasmaGuns.get(0));
						break;
					case 6:
						p.openInventory(this.miniPlasmaGuns.get(0));
						break;
					case 7:
						p.openInventory(this.grenadethrowers.get(0));
						break;
					}
				}
				break;
			case LANDMINES:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case MAIN_MENU:
				switch(event.getSlot()) {
				default:
					break;
				/**
				 * if(k.equalsIgnoreCase("grenade")) {
				this.menu.setItem(3, item);
			}
			if(k.equalsIgnoreCase("gun")) {
				this.menu.setItem(1, item);
			}
			if(k.equalsIgnoreCase("ammo")) {
				this.menu.setItem(2, item);
			}
			if(k.equalsIgnoreCase("airstrike")) {
				this.menu.setItem(5, item);
			}
			if(k.equalsIgnoreCase("landmine")) {
				this.menu.setItem(6, item);
			}
			if(k.equalsIgnoreCase("turret")) {
				this.menu.setItem(7, item);
			}
				 */
				case 0:
					p.openInventory(this.gunMenu);
					break;
				case 1:
					p.openInventory(getInvList(EPageType.AMMO).get(0));
					break;
				case 2:
					p.openInventory(getInvList(EPageType.GRENADES).get(0));
					break;
				case 3:
					p.openInventory(getInvList(EPageType.MISC).get(0));
					break;
				case 4:
					p.openInventory(getInvList(EPageType.AIRSTRIKES).get(0));
					break;
				case 5:
					p.openInventory(getInvList(EPageType.LANDMINES).get(0));
					break;
				case 6:
					p.openInventory(getInvList(EPageType.TURRETS).get(0));
					break;
				case 7:
					p.openInventory(getInvList(EPageType.TANKS).get(0));
					break;
				}
				break;
			case MISC:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, amount, p);
				}
				break;
			case TURRETS:
				if(isBackItem) {
					event.getWhoClicked().openInventory(this.menu);
				} else if(isChangePageItem) {
					changePage(p, ci, type, changeType);
				} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
					result = this.shophelper.buyItem(item, 1, p);
				}
				break;
			default:
				break;
			
			}
			if(!result.equals(EBuySuccess.NONE)) {
				switch(result) {
				case INVENTORY_FULL:
					p.sendMessage(LangUtil.buildGUIString("GunEngine.Shop.Messages.InventoryFull"));
					break;
				case NOT_ENOUGH_MONEY:
					String msg = LangUtil.buildGUIString("GunEngine.Shop.Messages.NotEnoughMoney");
					Integer missing = 0;
					Integer price = this.shophelper.getPrice(item);
					Integer playermoney = p.getLevel();
					missing = Math.abs(price - playermoney);
					msg = msg.replaceAll("%missingmoney%", missing.toString());
					msg = msg.replaceAll("%cost%", price.toString());
					msg = msg.replaceAll("%playermoney%", playermoney.toString());
					p.sendMessage(msg);
					break;
				case NOT_SHOP_ITEM:
					p.sendMessage(LangUtil.buildGUIString("GunEngine.Shop.Messages.NotBuyable"));
					break;
				case SUCCESS:
					String msg1 = LangUtil.buildGUIString("GunEngine.Shop.Messages.ShoppingSuccessful");
					Integer price1 = this.shophelper.getPrice(item);
					Integer playermoney1 = p.getLevel();
					String itemname = item.getItemMeta().getDisplayName();
					msg1 = msg1.replaceAll("%cost%", price1.toString());
					msg1 = msg1.replaceAll("%money%", playermoney1.toString());
					msg1 = msg1.replaceAll("%item%", itemname);
					msg1 = msg1.replaceAll("%count%", "x" + amount);
					p.sendMessage(msg1);
					break;
				case NO_PERMISSION:
					p.sendMessage(LangUtil.buildGUIString("GunEngine.Shop.Messages.NoPermission"));
					break;
				default:
					break;
				
				}
			}
		}
	}
	private Boolean isBackItem(ItemStack item) {
		if(item != null && ItemUtil.hasKey(item, "GG_Shop_Item") && ItemUtil.hasKey(item, "GG_Shop_BackItem")) {
			return true;
		}
		return false;
	}
	private Boolean isChangePageItem(ItemStack item) {
		if(item != null && ItemUtil.hasKey(item, "GG_Shop_Item") && ItemUtil.hasKey(item, "GG_Shop_ChangePageItem")) {
			return true;
		}
		return false;
	}
	private Boolean isShopInventory(Inventory inv) {
		if(inv.equals(this.menu)) {
			return true;
		}
		else if(inv.equals(this.gunMenu)) {
			return true;
		}
		else if(this.airstrikes.contains(inv) ||
				this.ammo.contains(inv) ||
				this.assaultGuns.contains(inv) ||
				this.assaultPlasmaGuns.contains(inv) ||
				this.grenades.contains(inv) ||
				this.grenadethrowers.contains(inv) ||
				this.landmines.contains(inv) ||
				this.miniGuns.contains(inv) ||
				this.miniPlasmaGuns.contains(inv) ||
				this.miscItems.contains(inv) ||
				this.plasmaGuns.contains(inv) ||
				this.rocketlaunchers.contains(inv) ||
				this.standardGuns.contains(inv) ||
				this.tanks.contains(inv) ||
				this.turrets.contains(inv)) {
			return true;
		}
		return false;
	}
	private EPageType getType(Inventory inv) {
		EPageType type = EPageType.MAIN_MENU;
		if(inv.equals(this.gunMenu)) {
			type = EPageType.GUN_MENU;
		}
		else if(this.miscItems.contains(inv)) {
			type = EPageType.MISC;
		}
		else if(this.ammo.contains(inv)) {
			type = EPageType.AMMO;
		}
		else if(this.airstrikes.contains(inv)) {
			type = EPageType.AIRSTRIKES;
		}
		else if(this.turrets.contains(inv)) {
			type = EPageType.TURRETS;
		}
		else if(this.tanks.contains(inv)) {
			type = EPageType.TANKS;
		}
		else if(this.landmines.contains(inv)) {
			type = EPageType.LANDMINES;
		}
		else if(this.grenades.contains(inv)) {
			type = EPageType.GRENADES;
		}
		else if(this.standardGuns.contains(inv)) {
			type = EPageType.GUNS_STANDARD;
		}
		else if(this.assaultGuns.contains(inv)) {
			type = EPageType.GUNS_ASSAULT;
		}
		else if(this.miniGuns.contains(inv)) {
			type = EPageType.GUNS_MINIGUNS;
		}
		else if(this.rocketlaunchers.contains(inv)) {
			type = EPageType.GUNS_ROCKETLAUNCHERS;
		}
		else if(this.grenadethrowers.contains(inv)) {
			type = EPageType.GUNS_GRENADETHROWERS;
		}
		else if(this.plasmaGuns.contains(inv)) {
			type = EPageType.GUNS_PLASMA;
		}
		else if(this.assaultPlasmaGuns.contains(inv)) {
			type = EPageType.GUNS_ASSAULT_PLASMA;
		}
		else if(this.miniPlasmaGuns.contains(inv)) {
			type = EPageType.GUNS_MINIGUNS_PLASMA;
		}
		return type;
	}
	private Integer getPageIndex(Inventory inv) {
		Integer index = 0;
		switch(getType(inv)) {
		case AIRSTRIKES:
			index = this.airstrikes.indexOf(inv);
			break;
		case AMMO:
			index = this.ammo.indexOf(inv);
			break;
		case GRENADES:
			index = this.grenades.indexOf(inv);
			break;
		case GUNS_ASSAULT:
			index = this.assaultGuns.indexOf(inv);
			break;
		case GUNS_ASSAULT_PLASMA:
			index = this.assaultPlasmaGuns.indexOf(inv);
			break;
		case GUNS_GRENADETHROWERS:
			index = this.grenadethrowers.indexOf(inv);
			break;
		case GUNS_MINIGUNS:
			index = this.miniGuns.indexOf(inv);
			break;
		case GUNS_MINIGUNS_PLASMA:
			index = this.miniPlasmaGuns.indexOf(inv);
			break;
		case GUNS_PLASMA:
			index = this.plasmaGuns.indexOf(inv);
			break;
		case GUNS_ROCKETLAUNCHERS:
			index = this.rocketlaunchers.indexOf(inv);
			break;
		case GUNS_STANDARD:
			index = this.standardGuns.indexOf(inv);
			break;
		case LANDMINES:
			index = this.landmines.indexOf(inv);
			break;
		case MISC:
			index = this.miscItems.indexOf(inv);
			break;
		case TURRETS:
			index = this.turrets.indexOf(inv);
			break;
		case TANKS:
			index = this.tanks.indexOf(inv);
			break;
		default:
			break;
		
		}
		return index;
	}
	private void changePage(Player p, Inventory current, EPageType type, EClickType changetype) {
		Integer index = getPageIndex(current);
		Integer next = 0;
		if(changetype.equals(EClickType.RIGHT)) {
			if(index > -1 && index == 0) {
				next = getInvList(type).size() -1;
			} else if(index > -1) {
				next = index -1;
			}
		} else if(changetype.equals(EClickType.LEFT)) {
			if(index > -1 && index == (getInvList(type).size() -1)) {
				next = 0;
			} else if(index > -1) {
				next = index +1;
			}
		}
		p.openInventory(getInvList(type).get(next));
	}
	private List<Inventory> getInvList(EPageType type) {
		switch(type) {
		case AIRSTRIKES:
			return this.airstrikes;
		case AMMO:
			return this.ammo;
		case GRENADES:
			return this.grenades;
		case GUNS_ASSAULT:
			return this.assaultGuns;
		case GUNS_ASSAULT_PLASMA:
			return this.assaultPlasmaGuns;
		case GUNS_GRENADETHROWERS:
			return this.grenadethrowers;
		case GUNS_MINIGUNS:
			return this.miniGuns;
		case GUNS_MINIGUNS_PLASMA:
			return this.miniPlasmaGuns;
		case GUNS_PLASMA:
			return this.plasmaGuns;
		case GUNS_ROCKETLAUNCHERS:
			return this.rocketlaunchers;
		case GUNS_STANDARD:
			return this.standardGuns;
		case LANDMINES:
			return this.landmines;
		case MISC:
			return this.miscItems;
		case TURRETS:
			return this.turrets;
		case TANKS:
			return this.tanks;
		default:
			break;
		}
		return null;
	}
	private Integer getInventoryCount(Integer size, Integer divisor) {
		Integer temp = 0;
		Integer ret = 0;
		while(temp * divisor < size) {
			temp++;
			if((temp + 1) * divisor >= size) {
				ret = temp +1;
				return ret;				
			}
		}
		ret = temp;
		return ret;
	}
}
