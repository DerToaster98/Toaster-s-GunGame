package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.ProjectileType;

public class Ammo {

	private ProjectileType type;
	private WeaponManager manager;
	private String AmmoName;
	private FileConfiguration weaponFile;
	private WeaponFileUtil wfu;
	private ItemStack ammoItem;
	private Integer shotCount;
	
	public Ammo(WeaponManager manager, String name, FileConfiguration weaponConfig) {
		this.setManager(manager);
		this.setAmmoName(name);
		this.setWeaponFile(weaponConfig);
		
		this.wfu = this.manager.wfu;
		
		this.setShotCount(this.wfu.getShotCount(this.weaponFile));
		
		setAmmoItem(createItem());
	}
	public ItemStack createItem() {
		ItemStack ammo;
		ammo = this.wfu.getAmmoItem(this.weaponFile);
		ammo.setAmount(1);
		
		return ammo;
	}
	public void createLore() {
		ItemStack item = this.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setAmmoItem(item);
	}
	
	
	
	
	
	
	
	
	
	
	public WeaponManager getManager() {
		return this.manager;
	}

	private void setManager(WeaponManager manager) {
		this.manager = manager;
	}

	public String getAmmoName() {
		return this.AmmoName;
	}

	private void setAmmoName(String ammoName) {
		this.AmmoName = ammoName;
	}

	public FileConfiguration getWeaponFile() {
		return this.weaponFile;
	}

	private void setWeaponFile(FileConfiguration weaponFile) {
		this.weaponFile = weaponFile;
	}

	public ProjectileType getType() {
		return this.type;
	}

	public ItemStack getItem() {
		return this.ammoItem;
	}
	private void setAmmoItem(ItemStack ammoItem) {
		this.ammoItem = ammoItem;
	}
	public Integer getShotCount() {
		return shotCount;
	}
	private void setShotCount(Integer shotCount) {
		this.shotCount = shotCount;
	}
}
