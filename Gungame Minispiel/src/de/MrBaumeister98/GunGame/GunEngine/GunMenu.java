package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.EWeaponType;

public class GunMenu implements Listener {
	
	private ArrayList<Inventory> grenadeMenu = new ArrayList<Inventory>();
	private ArrayList<Inventory> gunMenu = new ArrayList<Inventory>();
	private ArrayList<Inventory> ammoMenu = new ArrayList<Inventory>();
	private ArrayList<Inventory> airstrikeMenu = new ArrayList<Inventory>();
	private ArrayList<Inventory> landmineMenu = new ArrayList<Inventory>();
	private Inventory selectionMenu;
		
	@SuppressWarnings("unused")
	private GunGamePlugin plugin;
	private WeaponManager manager;
	
	private ItemStack ChangePageArrow;
	
	public GunMenu(GunGamePlugin plugin, WeaponManager manager) {
		this.plugin = plugin;
		this.manager = manager;
		
		this.selectionMenu = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.getGunMenuString("SelectionMenu"));
		
		setupSelectionMenu();
		setupChgnPageItem();
	}
	public enum InvType {
		GUNS,
		GRENADES,
		AIRSTRIKES,
		LANDMINES,
		AMMO;
	}

	private void setupSelectionMenu() {
		/*ItemStack blankItem = new ItemStack(Material.STAINED_GLASS_PANE, 1 , (short) 4);
		ItemMeta meta = blankItem.getItemMeta();
		meta.setDisplayName(" ");
		blankItem.setItemMeta(meta);
		
		this.selectionMenu.setItem(0, blankItem);*/
		//this.selectionMenu.setItem(4, blankItem);
		ItemStack selectLandmines = new ItemStack(LangUtil.getGunMenuItemMaterial("Landmine", GunGamePlugin.instance.serverPre113 ? "IRON_PLATE" : "HEAVY_WEIGHTED_PRESSURE_PLATE"));
		ItemMeta meta1 = selectLandmines.getItemMeta();
		meta1.setDisplayName(LangUtil.getGunMenuString("ItemsMenu.Landmine.Name"));
		selectLandmines.setItemMeta(meta1);
		
		selectLandmines = ItemUtil.setWeaponMenuItem(selectLandmines);
		
		ItemStack selectGrenades = new ItemStack(LangUtil.getGunMenuItemMaterial("Grenade", GunGamePlugin.instance.serverPre113 ? "FIREWORK_CHARGE" : "FIREWORK_STAR"));
		ItemMeta meta2 = selectGrenades.getItemMeta();
		meta2.setDisplayName(LangUtil.getGunMenuString("ItemsMenu.Grenade.Name"));
		selectGrenades.setItemMeta(meta2);
		
		selectGrenades = ItemUtil.setWeaponMenuItem(selectGrenades);
		
		
		ItemStack selectGuns = new ItemStack(LangUtil.getGunMenuItemMaterial("Gun", GunGamePlugin.instance.serverPre113 ? "BOW" : "BOW"));
		ItemMeta meta3 = selectGuns.getItemMeta();
		meta3.setDisplayName(LangUtil.getGunMenuString("ItemsMenu.Gun.Name"));
		selectGuns.setItemMeta(meta3);
		
		selectGuns = ItemUtil.setWeaponMenuItem(selectGuns);
		
		
		ItemStack selectAmmo = new ItemStack(LangUtil.getGunMenuItemMaterial("Ammo", GunGamePlugin.instance.serverPre113 ? "SEEDS" : "WHEAT_SEEDS"));
		ItemMeta meta4 = selectAmmo.getItemMeta();
		meta4.setDisplayName(LangUtil.getGunMenuString("ItemsMenu.Ammo.Name"));
		selectAmmo.setItemMeta(meta4);
		
		selectAmmo = ItemUtil.setWeaponMenuItem(selectAmmo);
		
		ItemStack selectAirstrike = new ItemStack(LangUtil.getGunMenuItemMaterial("Airstrikes", GunGamePlugin.instance.serverPre113 ? "REDSTONE_TORCH_ON" : "REDSTONE_TORCH"));
		ItemMeta meta5 = selectAirstrike.getItemMeta();
		meta5.setDisplayName(LangUtil.getGunMenuString("ItemsMenu.Airstrikes.Name"));
		selectAirstrike.setItemMeta(meta5);
		
		selectAirstrike = ItemUtil.setWeaponMenuItem(selectAirstrike);
		
		
		this.selectionMenu.setItem(0, selectLandmines);
		this.selectionMenu.setItem(1, selectGrenades);
		this.selectionMenu.setItem(2, selectGuns);
		this.selectionMenu.setItem(3, selectAmmo);
		this.selectionMenu.setItem(4, selectAirstrike);
	}
	private void setupChgnPageItem() {
		ItemStack prevPg = new ItemStack(Material.STRUCTURE_VOID);
		ItemMeta meta = prevPg.getItemMeta();
		meta.setDisplayName(LangUtil.getGunMenuString("ChangePage"));
		prevPg.setItemMeta(meta);
		prevPg = ItemUtil.setWeaponMenuItem(prevPg);
		prevPg = ItemUtil.addTags(prevPg, "GGWeaponMenu_PrevPage", true);
		
		this.ChangePageArrow = prevPg;
	}
	public void showSelectionMenu(Player p) {
		if(p != null && p.isOnline()) {
			p.openInventory(this.selectionMenu);
		}
	}
	public void loadPages() {
		if(this.manager.ammos.size() > 16) {
			Integer inventoryCount = getInventoryCount(this.manager.ammos.size(), 16);
			Integer itemPos = 0;
			for(Integer i = 0; i < inventoryCount; i++) {
				Inventory tempInv = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Ammo.Name") + ChatColor.YELLOW + "    <" + (i+1) + "/" + inventoryCount + ">");
				tempInv.setItem(0, this.ChangePageArrow);
				for(Integer i2 = 0; i2 < 17; i2++) {
					//Debugger.logInfo(i2.toString() + " = i2");
					if(((i * 16) + i2) < this.manager.ammos.size() && itemPos < this.manager.ammos.size() ) {

						ItemStack nextItem = this.manager.ammos.get(itemPos).getItem();

						tempInv.addItem(nextItem);
						itemPos = itemPos +1;
						
					} else {
						i2++;
						itemPos = itemPos +1;
					}
				}
				this.ammoMenu.add(tempInv);
			}
		} else /*if(this.manager.ammos.isEmpty() == false)*/ {
			Inventory temp = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Ammo.Name"));
			for(Ammo a : this.manager.ammos) {
				temp.addItem(a.getItem());
			}
			this.ammoMenu.add(temp);
		}
		
		if(this.manager.airstrikes.size() > 16) {
			Integer inventoryCount = getInventoryCount(this.manager.airstrikes.size(), 16);
			Integer itemPos = 0;
			for(Integer i = 0; i < inventoryCount; i++) {
				Inventory tempInv = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Airstrikes.Name") + ChatColor.YELLOW + "    <" + (i+1) + "/" + inventoryCount + ">");
				tempInv.setItem(0, this.ChangePageArrow);
				for(Integer i2 = 0; i2 < 17; i2++) {
					//Debugger.logInfo(i2.toString() + " = i2");
					if(((i * 16) + i2) < this.manager.airstrikes.size() && itemPos < this.manager.airstrikes.size() ) {

						ItemStack nextItem = this.manager.airstrikes.get(itemPos).getItem();

						tempInv.addItem(nextItem);
						itemPos = itemPos +1;
						
					} else {
						i2++;
						itemPos = itemPos +1;
					}
				}
				this.airstrikeMenu.add(tempInv);
			}
		} else /*if(this.manager.ammos.isEmpty() == false)*/ {
			Inventory temp = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Airstrikes.Name"));
			for(Airstrike a : this.manager.airstrikes) {
				temp.addItem(a.getItem());
			}
			this.airstrikeMenu.add(temp);
		}
		
		if(this.manager.landmines.size() > 16) {
			Integer inventoryCount = getInventoryCount(this.manager.landmines.size(), 16);
			Integer itemPos = 0;
			for(Integer i = 0; i < inventoryCount; i++) {
				Inventory tempInv = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Landmine.Name") + ChatColor.YELLOW + "    <" + (i+1) + "/" + inventoryCount + ">");
				tempInv.setItem(0, this.ChangePageArrow);
				for(Integer i2 = 0; i2 < 17; i2++) {
					//Debugger.logInfo(i2.toString() + " = i2");
					if(((i * 16) + i2) < this.manager.landmines.size() && itemPos < this.manager.landmines.size() ) {

						ItemStack nextItem = this.manager.landmines.get(itemPos).getItem();

						tempInv.addItem(nextItem);
						itemPos = itemPos +1;
						
					} else {
						i2++;
						itemPos = itemPos +1;
					}
				}
				this.landmineMenu.add(tempInv);
			}
		} else /*if(this.manager.ammos.isEmpty() == false)*/ {
			Inventory temp = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Landmine.Name"));
			for(Landmine a : this.manager.landmines) {
				temp.addItem(a.getItem());
			}
			this.landmineMenu.add(temp);
		}
		
		
		
		if(this.manager.grenades.size() > 16) {
			Integer inventoryCount = getInventoryCount(this.manager.grenades.size(), 16);
			Integer itemPos = 0;
			for(Integer i = 0; i < inventoryCount; i++) {
				Inventory tempInv = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Grenade.Name") + ChatColor.YELLOW + "    <" + (i+1) + "/" + inventoryCount + ">");
				tempInv.setItem(0, this.ChangePageArrow);
				for(Integer i2 = 0; i2 < 17; i2++) {
					//Debugger.logInfo(i2.toString() + " = i2");
					if(((i * 16) + i2) < this.manager.grenades.size() && itemPos < this.manager.grenades.size() ) {

						ItemStack nextItem = this.manager.grenades.get(itemPos).getGrenadeItem();

						tempInv.addItem(nextItem);
						itemPos = itemPos +1;
						
					} else {
						i2++;
						itemPos = itemPos +1;
					}
				}
				this.grenadeMenu.add(tempInv);
			}
		} else /*if(this.manager.grenades.isEmpty() == false)*/ {
			Inventory temp = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Grenade.Name"));
			for(Grenade g : this.manager.grenades) {
				temp.addItem(g.getGrenadeItem());
			}
			this.grenadeMenu.add(temp);
		}
		
		
		if(this.manager.guns.size() > 16) {
			Integer inventoryCount = getInventoryCount(this.manager.guns.size(), 16);
			Integer itemPos = 0;
			for(Integer i = 0; i < inventoryCount; i++) {
				Inventory tempInv = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Gun.Name") + ChatColor.YELLOW + "    <" + (i+1) + "/" + inventoryCount + ">");
				tempInv.setItem(0, this.ChangePageArrow);
				for(Integer i2 = 0; i2 < 17; i2++) {
					//Debugger.logInfo(i2.toString() + " = i2");
					if(((i * 16) + i2) < this.manager.guns.size() && itemPos < this.manager.guns.size() ) {

						ItemStack nextItem = this.manager.guns.get(itemPos).getItem();

						tempInv.addItem(nextItem);
						itemPos = itemPos +1;
						
					} else {
						i2++;
						itemPos = itemPos +1;
					}
				}
				this.gunMenu.add(tempInv);
			}
		} else /*if(this.manager.guns.isEmpty() == false)*/ {
			Inventory temp = Bukkit.createInventory(null, 18, LangUtil.getGunMenuString("ItemsMenu.Gun.Name"));
				for(Gun g : this.manager.guns) {
					temp.addItem(g.getItem());
				}
				this.gunMenu.add(temp);
		}		
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
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(event.getInventory() != null && (event.getInventory().equals(this.selectionMenu) || isMenuInventory(event.getClickedInventory()))) {
			if(event.isShiftClick()) {
				event.setCancelled(true);
			}
		}
		
		if(event.getClickedInventory() != null && event.getClickedInventory().equals(this.selectionMenu)) {
			//if(event.getSlot() == 0 /*|| event.getSlot() == 4*/) {
				//event.setCancelled(true);
			/*} else*/ if(event.getSlot() <= 4 && event.getSlot() >= 0) {
				event.setCancelled(true);
				switch(event.getSlot()) {
				default:
					event.setCancelled(true);
					break;
				case 0:
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					if(this.landmineMenu != null && this.landmineMenu.size() > 0) {
						Inventory temp = this.landmineMenu.get(0);
						if(temp != null) {
							p.openInventory(temp);
						}
					}
					break;
				case 1:
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					if(this.grenadeMenu != null && this.grenadeMenu.size() > 0) {
						Inventory temp = this.grenadeMenu.get(0);
						if(temp != null) {
							p.openInventory(temp);
						}
					}
					break;
				case 2:
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					if(this.gunMenu != null && this.gunMenu.size() > 0) {
						Inventory temp2 = this.gunMenu.get(0);
						if(temp2 != null) {
							p.openInventory(temp2);
						}
					}
					break;
				case 3:
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					if(this.ammoMenu != null && this.ammoMenu.size() > 0) {
						Inventory temp3 = this.ammoMenu.get(0);
						if(temp3 != null) {
							p.openInventory(temp3);
						}
					}
					break;
				case 4:
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					if(this.airstrikeMenu != null && this.airstrikeMenu.size() > 0) {
						Inventory temp4 = this.airstrikeMenu.get(0);
						if(temp4 != null) {
							p.openInventory(temp4);
						}
					}
					break;
				}
					
			}
		} else if(event.getClickedInventory() != null && isMenuInventory(event.getClickedInventory())) {
			if(getInvType(event.getClickedInventory()) != null) {
				ItemStack clicked = event.getCurrentItem();
				Inventory inv = event.getClickedInventory();
				//p.sendMessage(getInvType(inv).toString());
				switch(getInvType(event.getClickedInventory())) {
				default:
					break;
				case AMMO:
					if(isChangePageItem(clicked)) {
							if(event.isLeftClick()) {
								if(getNumber(inv, InvType.AMMO) <= 0) {
									p.openInventory(this.ammoMenu.get(this.ammoMenu.size() -1));
									event.setCancelled(true);
								} else {
									p.openInventory(this.ammoMenu.get(getNumber(inv, InvType.AMMO) -1));
									event.setCancelled(true);
								}
							} else if(event.isRightClick()) {
								if(getNumber(inv, InvType.AMMO) >= this.ammoMenu.size() -1) {
									p.openInventory(this.ammoMenu.get(0));
									event.setCancelled(true);
								} else {
									p.openInventory(this.ammoMenu.get(getNumber(inv, InvType.AMMO) +1));
									event.setCancelled(true);
								}
							}
						} else if(ItemUtil.isGGAmmo(clicked)) {
							event.setCancelled(true);
							Ammo ammo = this.manager.getAmmo(clicked);
							ItemStack itm = ammo.getItem();
							if(event.isLeftClick()) {
								itm.setAmount(16);
								if(event.isShiftClick()) {
									itm.setAmount(64);
								}
							}
							if(event.isRightClick()) {
								itm.setAmount(1);
								if(event.isShiftClick()) {
									itm.setAmount(32);
								}
							}
							p.getInventory().addItem(itm);
						}
					//}					
					break;
				case GRENADES:
					if(isChangePageItem(clicked)) { 
							if(event.isLeftClick()) {
								if(getNumber(inv, InvType.GRENADES) == 0) {
								p.openInventory(this.grenadeMenu.get(this.grenadeMenu.size() -1));
								event.setCancelled(true);
							} else {
								p.openInventory(this.grenadeMenu.get(getNumber(inv, InvType.GRENADES) -1));
								event.setCancelled(true);
							}
						} else if(event.isRightClick()) {
							if(getNumber(inv, InvType.GRENADES) == (this.grenadeMenu.size() -1)) {
								p.openInventory(this.grenadeMenu.get(0));
								event.setCancelled(true);
							} else {
								p.openInventory(this.grenadeMenu.get(getNumber(inv, InvType.GRENADES) +1));
								event.setCancelled(true);
							}
						}
						} else if(ItemUtil.isGGWeapon(clicked)) {
							if(ItemUtil.getWeaponType(clicked).equals(EWeaponType.GRENADE)) {
								event.setCancelled(true);
								Grenade gren = this.manager.getGrenade(clicked);
								ItemStack itm = gren.getGrenadeItem();
								if(event.isLeftClick()) {
									itm.setAmount(16);
									if(event.isShiftClick()) {
										itm.setAmount(64);
									}
								}
								if(event.isRightClick()) {
									itm.setAmount(1);
									if(event.isShiftClick()) {
										itm.setAmount(32);
									}
								}
								p.getInventory().addItem(itm);
							}
						}
					//}
					break;
				case GUNS:
					//event.setCancelled(true);
					if(isChangePageItem(clicked)) {
							if(getNumber(inv, InvType.GUNS) <= 0) {
								p.openInventory(this.gunMenu.get(this.gunMenu.size() -1));
							} else {
								p.openInventory(this.gunMenu.get(getNumber(inv, InvType.GUNS) -1));
							}
						} else if(ItemUtil.isGGWeapon(clicked)) {
							if(ItemUtil.getWeaponType(clicked).equals(EWeaponType.GUN)) {
								event.setCancelled(true);
								Gun gren = this.manager.getGun(clicked);
								ItemStack itm = gren.getItem();
								/*if(event.isLeftClick()) {
									itm.setAmount(16);
									if(event.isShiftClick()) {
										itm.setAmount(64);
									}
								}*/
								//if(event.isRightClick()) {
									itm.setAmount(1);
									/*if(event.isShiftClick()) {
										itm.setAmount(32);
									}
								}*/
								p.getInventory().addItem(itm);
							}
						}
					break;
				case AIRSTRIKES:
					//event.setCancelled(true);
					if(isChangePageItem(clicked)) {
							if(getNumber(inv, InvType.AIRSTRIKES) <= 0) {
								p.openInventory(this.airstrikeMenu.get(this.airstrikeMenu.size() -1));
							} else {
								p.openInventory(this.airstrikeMenu.get(getNumber(inv, InvType.AIRSTRIKES) -1));
							}
						} else if(ItemUtil.isGGAirstrike(clicked) || this.manager.getAirstrike(clicked) != null) {
							//if(ItemUtil.getWeaponType(clicked).equals(WeaponType.GUN)) {
								event.setCancelled(true);
								Airstrike gren = this.manager.getAirstrike(clicked);
								ItemStack itm = gren.getItem();
								/*if(event.isLeftClick()) {
									itm.setAmount(16);
									if(event.isShiftClick()) {
										itm.setAmount(64);
									}
								}*/
								//if(event.isRightClick()) {
									itm.setAmount(1);
									/*if(event.isShiftClick()) {
										itm.setAmount(32);
									}
								}*/
								p.getInventory().addItem(itm);
							//}
						}
					break;
				case LANDMINES:
					if(isChangePageItem(clicked)) {
						if(getNumber(inv, InvType.LANDMINES) <= 0) {
							p.openInventory(this.landmineMenu.get(this.landmineMenu.size() -1));
						} else {
							p.openInventory(this.landmineMenu.get(getNumber(inv, InvType.LANDMINES) -1));
						}
					} else if(ItemUtil.isGGLandmine(clicked) || this.manager.getLandmine(clicked) != null) {
						//if(ItemUtil.getWeaponType(clicked).equals(WeaponType.GUN)) {
							event.setCancelled(true);
							Landmine gren = this.manager.getLandmine(clicked);
							ItemStack itm = gren.getItem();
							/*if(event.isLeftClick()) {
								itm.setAmount(16);
								if(event.isShiftClick()) {
									itm.setAmount(64);
								}
							}*/
							//if(event.isRightClick()) {
								itm.setAmount(1);
								/*if(event.isShiftClick()) {
									itm.setAmount(32);
								}
							}*/
							p.getInventory().addItem(itm);
						//}
					}
				break;
				}
			}
		}
		
		
	}
	
	private Boolean isMenuInventory(Inventory inv) {
		if(this.grenadeMenu.contains(inv)) {
			return true;
		} else if(this.ammoMenu.contains(inv)) {
			return true;
		} else if(this.gunMenu.contains(inv)) {
			return true;
		} else if(this.airstrikeMenu.contains(inv)) {
			return true;
		} else if(this.landmineMenu.contains(inv)) {
			return true;
		} else {
			return false;
		}
	}
	private InvType getInvType(Inventory inv) {
		if(this.grenadeMenu.contains(inv)) {
			return InvType.GRENADES;
		} else if(this.gunMenu.contains(inv)) {
			return InvType.GUNS;
		} else if(this.ammoMenu.contains(inv)) {
			return InvType.AMMO;
		} else if(this.airstrikeMenu.contains(inv)) {
			return InvType.AIRSTRIKES;
		} else if(this.landmineMenu.contains(inv)) {
			return InvType.LANDMINES;
		}
		return null;
	}
	private Integer getNumber(Inventory inv, InvType type) {
		switch(type) {
		default:
			break;
		case AMMO:
			Integer i = this.ammoMenu.indexOf(inv);
			return i;
			//break;
		case GRENADES:
			Integer i2 = this.grenadeMenu.indexOf(inv);
			return i2;
			//break;
		case GUNS:
			Integer i3 = this.gunMenu.indexOf(inv);
			return i3;
			//break;
		case AIRSTRIKES:
			Integer i4 = this.airstrikeMenu.indexOf(inv);
			return i4;
			//break;
		case LANDMINES:
			Integer i5 = this.landmineMenu.indexOf(inv);
			return i5;
		}
		return 0;
	}
	private Boolean isChangePageItem(ItemStack item) {
		if(ItemUtil.belongsToWeaponMenu(item)) {
			if(ItemUtil.hasKey(item, "GGWeaponMenu_PrevPage"/*, "true"*/)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
