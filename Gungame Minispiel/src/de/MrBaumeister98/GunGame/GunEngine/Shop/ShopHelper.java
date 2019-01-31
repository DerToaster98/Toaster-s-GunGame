package de.MrBaumeister98.GunGame.GunEngine.Shop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Airstrike;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Landmine;
import de.MrBaumeister98.GunGame.GunEngine.WeaponManager;
import de.MrBaumeister98.GunGame.GunEngine.Enums.EWeaponType;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;
import de.MrBaumeister98.GunGame.Items.TrackPadItem;

public class ShopHelper {
	
	private GunGamePlugin main;
	private WeaponManager weaponmanager;
	
	private File shopFile;
	private FileConfiguration shopConfig;
	
	public ShopHelper(GunGamePlugin plugin, WeaponManager manager) {
		this.main = plugin;
		this.weaponmanager = manager;
	}
	public boolean canPlayerAffordItem(ItemStack item, Integer amount, Player player) {
		if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
			Integer money = player.getLevel();
			Integer price = -1;
			if(ItemUtil.isGGAmmo(item)) {
				Ammo ammo = GunGamePlugin.instance.weaponManager.getAmmo(item);
				price = getPrice(ammo);
			} else if(ItemUtil.isGGAirstrike(item)) {
				 Airstrike strike = GunGamePlugin.instance.weaponManager.getAirstrike(item);
				 price = getPrice(strike);
			} else if(ItemUtil.isGGLandmine(item)) {
				 Landmine mine = GunGamePlugin.instance.weaponManager.getLandmine(item);
				 price = getPrice(mine);
			} else if(ItemUtil.isGGTurret(item)) {
				TurretConfig tc = GunGamePlugin.instance.turretManager.getTurretConfig(item);
				price = getPrice(tc);
			} else if(ItemUtil.isGGTank(item)) {
				TankConfig tac = GunGamePlugin.instance.tankManager.getTankConfig(item);
				price = getPrice(tac);
			} else if(ItemUtil.isGGWeapon(item)) {
				if(GunItemUtil.isGrenade(item)) {
					Grenade gren = GunGamePlugin.instance.weaponManager.getGrenade(item);
					price = getPrice(gren);
				} else {
					Gun g = GunGamePlugin.instance.weaponManager.getGun(item);
					price = getPrice(g);
				}
			} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item_Misc")) {
				price = getPrice(item);
			}
			if(price >= 0 ) {
				price = price * amount;
				if(money >= price) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public EBuySuccess buyItem(ItemStack item, Integer amount, Player player) {
		if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item")) {
			if(canPlayerAffordItem(item, amount, player)) {
				Boolean hasSpace;
				hasSpace = false;
				for(int i = 0; i < 36; i++) {
					if(player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType().equals(Material.AIR)) {
						hasSpace = true;
					}
				}
				if(hasSpace) {
					if(ItemUtil.isGGWeapon(item) && ItemUtil.getWeaponType(item).equals(EWeaponType.GUN)) {
						Gun gun = this.weaponmanager.getGun(item);
						if(gun.hasUsePermission()) {
							if(player.hasPermission(gun.getPermission())) {
								ItemStack bought = item.clone();
								bought = ItemUtil.removeTag(bought, "GG_Shop_Buyable_Item");
								bought.setAmount(amount);
								Integer newLevels = player.getLevel() - getPrice(item);
								player.setLevel(newLevels);
								//player.getInventory().addItem(bought);
								if(ItemUtil.hasTag(bought, "GG_Shop_Buyable_Item_Misc", "TrackPad")) {
									TrackPadItem.giveTrackPad(player, amount);
								} else {
									player.getInventory().addItem(bought);
								}
								return EBuySuccess.SUCCESS;
							} else {
								return EBuySuccess.NO_PERMISSION;
							}
						} else {
							ItemStack bought = item.clone();
							bought = ItemUtil.removeTag(bought, "GG_Shop_Buyable_Item");
							bought.setAmount(amount);
							Integer newLevels = player.getLevel() - getPrice(item);
							player.setLevel(newLevels);
							player.getInventory().addItem(bought);
							return EBuySuccess.SUCCESS;
						}
					} else {
						ItemStack bought = item.clone();
						bought = ItemUtil.removeTag(bought, "GG_Shop_Buyable_Item");
						bought.setAmount(amount);
						Integer newLevels = player.getLevel() - getPrice(item);
						player.setLevel(newLevels);
						player.getInventory().addItem(bought);
						return EBuySuccess.SUCCESS;
					}
				} else {
					return EBuySuccess.INVENTORY_FULL;
				}
			} else {
				return EBuySuccess.NOT_ENOUGH_MONEY;
			}
		}
		return EBuySuccess.NOT_SHOP_ITEM;
	}
	public void setupFile() {
		loadShopFile();
		
		List<String> assaultGuns = getShopFile().getStringList("Shop.Items.Guns.Assault");
		List<String> plasmaGuns = getShopFile().getStringList("Shop.Items.Guns.Plasma");
		List<String> miniguns = getShopFile().getStringList("Shop.Items.Guns.Minigun");
		List<String> standardGuns = getShopFile().getStringList("Shop.Items.Guns.Standard");
		List<String> rocketlaunchers = getShopFile().getStringList("Shop.Items.Guns.Rocketlauncher");
		List<String> grenadelaunchers = getShopFile().getStringList("Shop.Items.Guns.Grenadelauncher");
		List<String> minigunPlasmas = getShopFile().getStringList("Shop.Items.Guns.MinigunPlasma");
		List<String> assaultGunsPlasmas = getShopFile().getStringList("Shop.Items.Guns.AssaultPlasma");
		
		List<String> ammos = getShopFile().getStringList("Shop.Items.Ammo");
		List<String> grenades = getShopFile().getStringList("Shop.Items.Grenades");
		List<String> airstrikes = getShopFile().getStringList("Shop.Items.Airstrikes");
		List<String> landmines = getShopFile().getStringList("Shop.Items.Landmines");
		List<String> turrets = getShopFile().getStringList("Shop.Items.Turrets");
		List<String> tanks = getShopFile().getStringList("Shop.Items.Tanks");
		List<String> misc = getShopFile().getStringList("Shop.Items.Misc");
		
		List<Gun> missingEntry = new ArrayList<Gun>();
		List<Ammo> missingEntryAmmo = new ArrayList<Ammo>();
		List<Grenade> missingEntryGrenade = new ArrayList<Grenade>();
		List<Airstrike> missingEntryAirstrike = new ArrayList<Airstrike>();
		List<Landmine> missingEntryLandmine = new ArrayList<Landmine>();
		List<TurretConfig> missingEntryTurret = new ArrayList<TurretConfig>();
		List<TankConfig> missingEntryTank = new ArrayList<TankConfig>();
 		List<String> missingEntryMisc = new ArrayList<String>();
		
		if(misc.isEmpty() || misc == null || !misc.contains("C4")) {
			missingEntryMisc.add("C4");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("SuicideVest")) {
			missingEntryMisc.add("SuicideVest");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("Crowbar")) {
			missingEntryMisc.add("Crowbar");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("Radar")) {
			missingEntryMisc.add("Radar");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("SuicideVestRemote")) {
			missingEntryMisc.add("SuicideVestRemote");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("C4Remote")) {
			missingEntryMisc.add("C4Remote");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("Medikit")) {
			missingEntryMisc.add("Medikit");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("SuicideVestRemote")) {
			missingEntryMisc.add("SuicideVestRemote");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("C4Remote")) {
			missingEntryMisc.add("C4Remote");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("FlareGun")) {
			missingEntryMisc.add("FlareGun");
		}
		if(misc.isEmpty() || misc == null || !misc.contains("TrackPad")) {
			missingEntryMisc.add("TrackPad");
		}
		
		for(Gun gun : this.weaponmanager.guns) {
			String name = gun.getGunName();
			switch(gun.getType()) {
				default:
					break;
				case ASSAULT:
					if(assaultGuns.isEmpty() || assaultGuns == null || !assaultGuns.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case ASSAULT_PLASMA:
					if(assaultGunsPlasmas.isEmpty() || assaultGunsPlasmas == null || !assaultGunsPlasmas.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case GRENADETHROWER:
					if(grenadelaunchers.isEmpty() || grenadelaunchers == null || !grenadelaunchers.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case MINIGUN:
					if(miniguns.isEmpty() || miniguns == null || !miniguns.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case MINIGUN_PLASMA:
					if(minigunPlasmas.isEmpty() || minigunPlasmas == null || !minigunPlasmas.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case PLASMA:
					if(plasmaGuns.isEmpty() || plasmaGuns == null || !plasmaGuns.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case ROCKETLAUNCHER:
					if(rocketlaunchers.isEmpty() || rocketlaunchers == null || !rocketlaunchers.contains(name)) {
						missingEntry.add(gun);
					}
					break;
				case STANDARD:
					if(standardGuns.isEmpty() || standardGuns == null || !standardGuns.contains(name)) {
						missingEntry.add(gun);
					}
					break;
			}
		}
		for(Ammo a : this.weaponmanager.ammos) {
			if(ammos.isEmpty() || ammos == null || !ammos.contains(a.getAmmoName())) {
				missingEntryAmmo.add(a);
			}
		}
		for(Grenade g : this.weaponmanager.grenades) {
			if(grenades.isEmpty() || grenades == null || !grenades.contains(g.getGrenadeName())) {
				missingEntryGrenade.add(g);
			}
		}
		for(Airstrike strike : this.weaponmanager.airstrikes) {
			if(airstrikes.isEmpty() || airstrikes == null || !airstrikes.contains(strike.name)) {
				missingEntryAirstrike.add(strike);
			}
		}
		for(Landmine mine : this.weaponmanager.landmines) {
			if(landmines.isEmpty() || landmines == null || !landmines.contains(mine.getName())) {
				missingEntryLandmine.add(mine);
			}
		}
		for(TurretConfig tc : this.main.turretManager.turrets) {
			if(turrets.isEmpty() || turrets == null || !turrets.contains(tc.name)) {
				missingEntryTurret.add(tc);
			}
		}
		for(TankConfig tac : this.main.tankManager.getTankConfigs()) {
			if(tanks.isEmpty() || tanks == null || !tanks.contains(tac.name)) {
				missingEntryTank.add(tac);
			}
		}
		for(Gun gun : missingEntry) {
			createEntry(gun);
		}
		for(Ammo a : missingEntryAmmo) {
			createEntry(a);
		}
		for(Grenade g : missingEntryGrenade) {
			createEntry(g);
		}
		for(Airstrike strike : missingEntryAirstrike) {
			createEntry(strike);
		}
		for(String s : missingEntryMisc) {
			createEntry(s);
		}
		for(Landmine mine : missingEntryLandmine) {
			createEntry(mine);
		}
		for(TurretConfig tc : missingEntryTurret) {
			createEntry(tc);
		}
		for(TankConfig tac : missingEntryTank) {
			createEntry(tac);
		}
		saveShopFile();
	}
	private void createEntry(String s) {
		List<String> tmp = getShopFile().getStringList("Shop.Items.Misc");
		tmp.add(s);
		getShopFile().set("Shop.Items.Misc", tmp);
		getShopFile().set("Shop.Prices.Misc." + s, -1);
	}
	private void createEntry(Grenade grenade) {
		String name = grenade.getGrenadeName();
		//getShopFile().set("Shop.Items.Grenades", getShopFile().getStringList("Shop.Items.Grenades").add(name));
		List<String> tmp = getShopFile().getStringList("Shop.Items.Grenades");
		tmp.add(name);
		getShopFile().set("Shop.Items.Grenades", tmp);
		getShopFile().set("Shop.Prices.Grenades." + name, -1);
	}
	private void createEntry(Ammo ammo) {
		String name = ammo.getAmmoName();
		//getShopFile().set("Shop.Items.Ammo", getShopFile().getStringList("Shop.Items.Ammo").add(name));
		List<String> tmp = getShopFile().getStringList("Shop.Items.Ammo");
		tmp.add(name);
		getShopFile().set("Shop.Items.Ammo", tmp);
		getShopFile().set("Shop.Prices.Ammo." + name, -1);
	}
	private void createEntry(Airstrike strike) {
		String name = strike.name;
		
		List<String> tmp = getShopFile().getStringList("Shop.Items.Airstrikes");
		tmp.add(name);
		getShopFile().set("Shop.Items.Airstrikes", tmp);
		getShopFile().set("Shop.Prices.Airstrikes." + name, -1);
	}
	private void createEntry(TurretConfig strike) {
		String name = strike.name;
		
		List<String> tmp = getShopFile().getStringList("Shop.Items.Turrets");
		tmp.add(name);
		getShopFile().set("Shop.Items.Turrets", tmp);
		getShopFile().set("Shop.Prices.Turrets." + name, -1);
	}
	private void createEntry(Landmine mine) {
		String name = mine.getName();
		
		List<String> tmp = getShopFile().getStringList("Shop.Items.Landmines");
		tmp.add(name);
		getShopFile().set("Shop.Items.Landmines", tmp);
		getShopFile().set("Shop.Prices.Landmines." + name, -1);
	}
	private void createEntry(TankConfig tac) {
		String name = tac.name;
		
		List<String> tmp = getShopFile().getStringList("Shop.Items.Tanks");
		tmp.add(name);
		getShopFile().set("Shop.Items.Tanks", tmp);
		getShopFile().set("Shop.Prices.Tanks." + name, -1);
	}
	private void createEntry(Gun gun) {
		String name = gun.getGunName();
		switch(gun.getType()) {
			default:
				
				break;
			case ASSAULT:
				//getShopFile().set("Shop.Items.Guns.Assault", getShopFile().getStringList("Shop.Items.Guns.Assault").add(name));
				List<String> tmp = getShopFile().getStringList("Shop.Items.Guns.Assault");
				tmp.add(name);
				getShopFile().set("Shop.Items.Guns.Assault", tmp);
				getShopFile().set("Shop.Prices.Guns.Assault." + name, -1);
				break;
			case ASSAULT_PLASMA:
				//getShopFile().set("Shop.Items.Guns.AssaultPlasma", getShopFile().getStringList("Shop.Items.Guns.AssaultPlasma").add(name));
				List<String> tmp2 = getShopFile().getStringList("Shop.Items.Guns.AssaultPlasma");
				tmp2.add(name);
				getShopFile().set("Shop.Items.Guns.AssaultPlasma", tmp2);
				getShopFile().set("Shop.Prices.Guns.AssaultPlasma." + name, -1);
				break;
			case GRENADETHROWER:
				//getShopFile().set("Shop.Items.Guns.Grenadelauncher", getShopFile().getStringList("Shop.Items.Guns.Grenadelauncher").add(name));
				List<String> tmp3 = getShopFile().getStringList("Shop.Items.Guns.Grenadelauncher");
				tmp3.add(name);
				getShopFile().set("Shop.Items.Guns.Grenadelauncher", tmp3);
				getShopFile().set("Shop.Prices.Guns.Grenadelauncher." + name, -1);
				break;
			case MINIGUN:
				//getShopFile().set("Shop.Items.Guns.Minigun", getShopFile().getStringList("Shop.Items.Guns.Minigun").add(name));
				List<String> tmp4 = getShopFile().getStringList("Shop.Items.Guns.Minigun");
				tmp4.add(name);
				getShopFile().set("Shop.Items.Guns.Minigun", tmp4);
				getShopFile().set("Shop.Prices.Guns.Minigun." + name, -1);
				break;
			case MINIGUN_PLASMA:
				//getShopFile().set("Shop.Items.Guns.MinigunPlasma", getShopFile().getStringList("Shop.Items.Guns.MinigunPlasma").add(name));
				List<String> tmp5 = getShopFile().getStringList("Shop.Items.Guns.MinigunPlasma");
				tmp5.add(name);
				getShopFile().set("Shop.Items.Guns.MinigunPlasma", tmp5);
				getShopFile().set("Shop.Prices.Guns.MinigunPlasma." + name, -1);
				break;
			case PLASMA:
				//getShopFile().set("Shop.Items.Guns.Plasma", getShopFile().getStringList("Shop.Items.Guns.Plasma").add(name));
				List<String> tmp6 = getShopFile().getStringList("Shop.Items.Guns.Plasma");
				tmp6.add(name);
				getShopFile().set("Shop.Items.Guns.Plasma", tmp6);
				getShopFile().set("Shop.Prices.Guns.Plasma." + name, -1);
				break;
			case ROCKETLAUNCHER:
				//getShopFile().set("Shop.Items.Guns.Rocketlauncher", getShopFile().getStringList("Shop.Items.Guns.Rocketlauncher").add(name));
				List<String> tmp7 = getShopFile().getStringList("Shop.Items.Guns.Rocketlauncher");
				tmp7.add(name);
				getShopFile().set("Shop.Items.Guns.Rocketlauncher", tmp7);
				getShopFile().set("Shop.Prices.Guns.Rocketlauncher." + name, -1);
				break;
			case STANDARD:
				//getShopFile().set("Shop.Items.Guns.Standard", getShopFile().getStringList("Shop.Items.Guns.Standard").add(name));
				List<String> tmp8 = getShopFile().getStringList("Shop.Items.Guns.Standard");
				tmp8.add(name);
				getShopFile().set("Shop.Items.Guns.Standard", tmp8);
				getShopFile().set("Shop.Prices.Guns.Standard." + name, -1);
				break;
		}
	}
	
	public Integer getPrice(Gun gun) {
		String path = "Shop.Prices.Guns.";
		switch(gun.getType()) {
		default:
			break;
		case ASSAULT:
			path = path + "Assault." + gun.getGunName();
			break;
		case ASSAULT_PLASMA:
			path = path + "AssaultPlasma." + gun.getGunName();
			break;
		case GRENADETHROWER:
			path = path + "Grenadelauncher." + gun.getGunName();
			break;
		case MINIGUN:
			path = path + "Minigun." + gun.getGunName();
			break;
		case MINIGUN_PLASMA:
			path = path + "MinigunPlasma." + gun.getGunName();
			break;
		case PLASMA:
			path = path + "Plasma." + gun.getGunName();
			break;
		case ROCKETLAUNCHER:
			path = path + "Rocketlauncher." + gun.getGunName();
			break;
		case STANDARD:
			path = path + "Standard." + gun.getGunName();
			break;
		}
		return getPrice(path);
	}
	public Integer getPrice(Ammo ammo) {
		return getPrice("Shop.Prices.Ammo." + ammo.getAmmoName());
	}
	public Integer getPrice(Grenade grenade) {
		return getPrice("Shop.Prices.Grenades." + grenade.getGrenadeName());
	}
	public Integer getPrice(Airstrike strike) {
		return getPrice("Shop.Prices.Airstrikes." + strike.name);
	}
	public Integer getPrice(Landmine mine) {
		return getPrice("Shop.Prices.Landmines." + mine.getName());
	}
	public Integer getPrice(TurretConfig tc) {
		return getPrice("Shop.Prices.Turrets." + tc.name);
	}
	public Integer getPrice(TankConfig tac) {
		return getPrice("Shop.Prices.Tanks." + tac.name);
	}
 	private Integer getPrice(String path) {
		Integer d = 0;
		try {
			Integer tmp = getShopFile().getInt(path);
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public Integer getPrice(ItemStack item) {
		Integer price = -1;
		if(ItemUtil.isGGAmmo(item)) {
			Ammo ammo = GunGamePlugin.instance.weaponManager.getAmmo(item);
			price = getPrice(ammo);
		} else if(ItemUtil.isGGAirstrike(item)) {
			 Airstrike strike = GunGamePlugin.instance.weaponManager.getAirstrike(item);
			 price = getPrice(strike);
		} else if(ItemUtil.isGGLandmine(item)) {
			 Landmine mine = GunGamePlugin.instance.weaponManager.getLandmine(item);
			 price = getPrice(mine);
		} else if(ItemUtil.isGGTurret(item)) {
			TurretConfig tc = GunGamePlugin.instance.turretManager.getTurretConfig(item);
			price = getPrice(tc);
		} else if(ItemUtil.isGGWeapon(item)) {
			if(GunItemUtil.isGrenade(item)) {
				Grenade gren = GunGamePlugin.instance.weaponManager.getGrenade(item);
				price = getPrice(gren);
			} else {
				Gun g = GunGamePlugin.instance.weaponManager.getGun(item);
				price = getPrice(g);
			}
		} else if(ItemUtil.hasKey(item, "GG_Shop_Buyable_Item_Misc")) {
			String name = ItemUtil.getString(item, "GG_Shop_Buyable_Item_Misc");
			price = getPrice("Shop.Prices.Misc." + name);
		}
		return price;
	}
	
	
	
	
	
	
	
	private void loadShopFile() {
		if(shopFile == null) {
			shopFile = new File(this.main.getDataFolder(), "shop.yml");
			this.main.saveResource("shop.yml", false);
		}
		shopConfig = YamlConfiguration.loadConfiguration(shopFile);
		
		InputStream defStream = this.main.getResource("shop.yml");
		if(defStream != null) {
			YamlConfiguration defShop = YamlConfiguration.loadConfiguration(shopFile);
			shopConfig.setDefaults(defShop);
		}
	}
	private FileConfiguration getShopFile() {
		if(shopConfig == null) {
			loadShopFile();
		}
		return shopConfig;
	}
	private void saveShopFile() {
		if(shopConfig == null || shopFile == null) {
			return;
		}
		try {
			this.main.saveResource("shop.yml", false);
			shopConfig.save(shopFile);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save shop.yml to " + shopFile + "!", ex);
		}
	}

}
