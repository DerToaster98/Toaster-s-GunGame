package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Shop.ShopHelper;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;
import de.tr7zw.itemnbtapi.NBTItem;

public class WeaponManager {
	
	public GunGamePlugin plugin;
	private ShopHelper shHelper;
	public WeaponFileUtil wfu;
	public WeaponListener wl;
	public VisualsHelper visualHelper;
	private WeaponLoader weaponLoader;
	public List<Gun> guns = new ArrayList<Gun>();
	public List<Grenade> grenades = new ArrayList<Grenade>();
	public List<Ammo> ammos = new ArrayList<Ammo>();
	public List<Airstrike> airstrikes = new ArrayList<Airstrike>();
	public List<Landmine> landmines = new ArrayList<Landmine>();
	private HashMap<String, Gun> getGunByName = new HashMap<String, Gun>();
	private HashMap<String, Grenade> getGrenadeByName = new HashMap<String, Grenade>();
	private HashMap<String, Ammo> getAmmoByName = new HashMap<String, Ammo>();
	private HashMap<String, Airstrike> getAirstrikeByName = new HashMap<String, Airstrike>();
	private HashMap<String, Landmine> getLandmineByName = new HashMap<String, Landmine>();
	private HashMap<String, WeaponSoundSet> getSoundSetByName = new HashMap<String, WeaponSoundSet>();
	private HashMap<String, DamageSet> getDamageSetByName = new HashMap<String, DamageSet>();
	
	public List<BukkitRunnable> gunEngineProcesses;
	
	public List<Grenade> standardGrenades;
	public List<Gun> standardGuns;
	
	public WeaponManager(GunGamePlugin main) {
		this.plugin = main;
	}
	public void initialize() {
		Debugger.logInfoWithColoredText(ChatColor.RED + "Initializing Weapon System...");
		
		this.weaponLoader = new WeaponLoader(this);
		this.wfu = new WeaponFileUtil(this);
		this.weaponLoader.load(false);
		this.wl = new WeaponListener(this);
		this.gunEngineProcesses = new ArrayList<BukkitRunnable>();
		this.visualHelper = new VisualsHelper(this);
		
		this.standardGrenades = new ArrayList<Grenade>();
		this.standardGuns = new ArrayList<Gun>();
		
		fillStandardLists();
		
		//this.plugin.gunShop.loadWeapons();
		
	}
	
	public void initializeShop() {
		ShopHelper sh = new ShopHelper(this.plugin, this);
		
		sh.setupFile();
		
		this.shHelper = sh;
	}
	public void reloadWeapons() {
		clearLists();
		this.weaponLoader.load(true);
	}
	
	public void setupItemLores() {
		for(Gun gun : this.guns) {
			gun.createLore();
		}
		for(Ammo ammo : this.ammos) {
			ammo.createLore();
		}
		for(Grenade gren : this.grenades) {
			gren.createLore();
		}
		for(Landmine mine : this.landmines) {
			mine.createLore();
		}
		for(Airstrike strike : this.airstrikes) {
			strike.createLore();
		}
		for(TurretConfig tc : this.plugin.turretManager.turrets) {
			tc.createLore();
		}
		for(TankConfig tac : this.plugin.tankManager.getTankConfigs()) {
			tac.createLore();
		}
	}
	
	private void fillStandardLists() {
		//GRENADES
		for(Grenade gren : this.grenades) {
			if(gren.isStandardWeapon()) {
				this.standardGrenades.add(gren);
			}
		}
		//GUNS
		for(Gun gun : this.guns) {
			if(gun.isStandardWeapon()) {
				this.standardGuns.add(gun);
			}
		}
	}
	public Gun getRandomGun() {
		Gun g = this.standardGuns.get(new Random().nextInt(this.standardGuns.size()));
		return g;
	}
	public Grenade getRandomGrenade() {
		Grenade g = this.standardGrenades.get(new Random().nextInt(this.standardGrenades.size()));
		return g;
	}
	
	
	public Airstrike getAirstrike(String name) {
		Airstrike strike = getAirstrikeByName.get(name);
		
		return strike;
	}
	public Airstrike getAirstrike(NBTItem item) {
		Airstrike strike = null;
		if(ItemUtil.isGGAirstrike(item.getItem())) {
			String name = ItemUtil.getString(item.getItem(), "GGAirstrikeName");
			if(name != null) {
				strike = this.getAirstrike(name);
			}
		}
		return strike;
	}
	public Airstrike getAirstrike(ItemStack item) {
		NBTItem itm = new NBTItem(item);
		return getAirstrike(itm);
	}
	public Landmine getLandmine(String name) {
		Landmine strike = getLandmineByName.get(name);
		
		return strike;
	}
	public Landmine getLandmine(NBTItem item) {
		Landmine strike = null;
		if(ItemUtil.isGGLandmine(item.getItem())) {
			String name = ItemUtil.getString(item.getItem(), "GGLandmineName");
			if(name != null) {
				strike = this.getLandmine(name);
			}
		}
		return strike;
	}
	public Landmine getLandmine(ItemStack item) {
		NBTItem itm = new NBTItem(item);
		return getLandmine(itm);
	}
	public Gun getGun(String name) {
		Gun gun = getGunByName.get(name);
		
		return gun;
	}
	public Gun getGun(NBTItem gunItem) {
		Gun g = null;
		if(ItemUtil.isGGWeapon(gunItem.getItem())) {
			String gName = ItemUtil.getString(gunItem.getItem(), "GGGunName");
			if( gName != null) {
				g = this.getGun(gName);
			}
		}
		return g;
	}
	public Gun getGun(ItemStack gunItem) {
		NBTItem nbti = new NBTItem(gunItem);
		return getGun(nbti);
	}
	public Ammo getAmmo(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		return getAmmo(nbti);
	}
	public Ammo getAmmo(NBTItem item) {
		Ammo a = null;
		if(ItemUtil.isGGAmmo(item.getItem())) {
			String aN = ItemUtil.getString(item.getItem(), "GGAmmoName");
			if(aN != null) {
				a = this.getAmmo(aN);
			}
		}
		return a;
	}
	public Grenade getGrenade(String name) {
		Grenade grenade = getGrenadeByName.get(ChatColor.stripColor(name));
		
		return grenade;
	}
	public Grenade getGrenade(NBTItem gunItem) {
		Grenade g = null;
		if(ItemUtil.isGGWeapon(gunItem.getItem())) {
			String gName = ItemUtil.getString(gunItem.getItem(), "GGGunName");
			if( gName != null) {
				g = this.getGrenade(gName);
			}
		}
		return g;
	}
	public Grenade getGrenade(ItemStack gunItem) {
		NBTItem nbti = new NBTItem(gunItem);
		return getGrenade(nbti);
	}
	public Ammo getAmmo(String name) {
		Ammo ammo = getAmmoByName.get(name);
		
		return ammo;
	}
	public WeaponSoundSet getSoundSet(String name) {
		WeaponSoundSet soundSet = getSoundSetByName.get(name);
		
		return soundSet;
	}
	
	public void registerSoundSet(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		//name = name.replaceAll(".yml", "");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Gun-Sound-Set: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		WeaponSoundSet gun = new WeaponSoundSet(weaponConfig);
		
		this.getSoundSetByName.put(name, gun);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
		Debugger.logInfoWithColoredText(ChatColor.RED +"Added Gun-Sound-Set: " + ChatColor.GREEN + name + ChatColor.RED +"!");
	}
	public void registerGun(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		//name = name.replaceAll(".yml", "");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Gun: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		Gun gun = new Gun(this, name, weaponConfig);
		
		this.getGunByName.put(name, gun);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
		this.guns.add(gun);
		Debugger.logInfoWithColoredText(ChatColor.RED +"Added Gun: " + ChatColor.GREEN + name + ChatColor.RED +"!");
	}
	public void registerGrenade(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		//name = name.replaceAll(".yml", "");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Grenade: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		Grenade grenade = new Grenade(this, name, weaponConfig);
		
		this.getGrenadeByName.put(name, grenade);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
		this.grenades.add(grenade);
		Debugger.logInfoWithColoredText(ChatColor.RED +"Added Grenade: " + ChatColor.GREEN + name + ChatColor.RED +"!");
	}
	public void registerAmmo(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		//name = name.replaceAll(".yml", "");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Ammo: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		Ammo ammo = new Ammo(this, name, weaponConfig);
		
		this.getAmmoByName.put(name, ammo);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
		this.ammos.add(ammo);
		Debugger.logInfoWithColoredText(ChatColor.RED +"Added Ammo: " + ChatColor.GREEN + name + ChatColor.RED +"!");
	}
	public void registerAirstrike(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Registering Airstrike: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		Airstrike strike = new Airstrike(this, name, weaponConfig);
		this.getAirstrikeByName.put(name, strike);
		this.airstrikes.add(strike);
		Debugger.logInfoWithColoredText(ChatColor.RED + "Added Airstrike: " + ChatColor.GREEN + name + ChatColor.RED + "!");
	}
	public void registerLandmine(FileConfiguration weaponConfig) {
		String name = weaponConfig.getString("Name");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Registering Landmine: " + ChatColor.GREEN + name + ChatColor.YELLOW + "...");
		Landmine mine = new Landmine(this, weaponConfig, name);
		this.getLandmineByName.put(name, mine);
		this.landmines.add(mine);
		Debugger.logInfoWithColoredText(ChatColor.RED + "Added Landmine: " + ChatColor.GREEN + name + ChatColor.RED + "!");
	}
	
	public void clearLists() {
		this.getGunByName.clear();
		this.getAmmoByName.clear();
		this.getGrenadeByName.clear();
		this.getSoundSetByName.clear();
		this.getDamageSetByName.clear();
		this.getAirstrikeByName.clear();
		this.getLandmineByName.clear();
		
		this.ammos.clear();
		this.grenades.clear();
		this.guns.clear();
		this.airstrikes.clear();
		this.landmines.clear();
	}
	public void addProcess(BukkitRunnable process) {
		this.gunEngineProcesses.add(process);
	}
	public void removeProcess(BukkitRunnable process) {
		this.gunEngineProcesses.remove(process);
		//process.cancel();
	}
	public ShopHelper getShopHelper() {
		return shHelper;
	}
	public void setShHelper(ShopHelper shHelper) {
		this.shHelper = shHelper;
	}

}
