package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class Airstrike {
	
	private WeaponManager manager;
	private FileConfiguration weaponFile;
	private WeaponFileUtil wfu;
	private ItemStack item;
	public String name;
	private Boolean breakBlocks;
	private Boolean placeFire;
	private Boolean damage;
	private Boolean physicsEnabled;
	private Integer bombCount;
	private Integer explosionRadius;
	private Integer dropRadius;
	private float power;
	private Integer fuse;
	
	public Airstrike(WeaponManager manager, String name, FileConfiguration config) {
		this.setManager(manager);
		this.name = name;
		this.wfu = manager.wfu;
		this.weaponFile = config;
		
		setup();
		createItem();
	}
	
	private void setup() {
		this.setBreakBlocks(this.wfu.getAirstrikeExplosionBreakBlocks(this.weaponFile));
		this.setPlaceFire(this.wfu.getAirstrikeExplosionPlaceFire(this.weaponFile));
		this.setDamage(this.wfu.getAirstrikeExplosionDamage(this.weaponFile));
		this.setBombCount(this.wfu.getAirStrikeBombCount(this.weaponFile));
		this.setFuse(this.wfu.getAirstrikeFuseTicks(this.weaponFile));
		this.setDropRadius(this.wfu.getAirstrikeDropRadius(this.weaponFile));
		this.setPower(this.wfu.getAirstrikeExplosionPower(this.weaponFile));
		this.setRadius(this.wfu.getAirstrikeExplosionRadius(this.weaponFile));
		this.setPhysicsEnabled(this.wfu.getAirstrikePhysicsEnabled(this.weaponFile));
	}
	private void createItem() {
		ItemStack item = this.wfu.getAirstrikeItem(this.weaponFile);
		this.item = item;
	}
	public void createLore() {
		ItemStack item = this.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setItem(item);
	}
	
	public Boolean canBreakBlocks() {
		return breakBlocks;
	}
	public void setBreakBlocks(Boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}
	public Boolean canPlaceFire() {
		return placeFire;
	}
	public void setPlaceFire(Boolean placeFire) {
		this.placeFire = placeFire;
	}
	public Integer getExplosionRadius() {
		return explosionRadius;
	}
	public void setRadius(Integer radius) {
		this.explosionRadius = radius;
	}
	public float getPower() {
		return power;
	}
	public void setPower(float power) {
		this.power = power;
	}
	public Integer getFuse() {
		return fuse;
	}
	public void setFuse(Integer fuse) {
		this.fuse = fuse;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}

	public Boolean getDamage() {
		return damage;
	}

	public void setDamage(Boolean damage) {
		this.damage = damage;
	}

	public Integer getBombCount() {
		return bombCount;
	}

	public void setBombCount(Integer bombCount) {
		this.bombCount = bombCount;
	}

	public WeaponManager getManager() {
		return manager;
	}

	public void setManager(WeaponManager manager) {
		this.manager = manager;
	}

	public Integer getDropRadius() {
		return dropRadius;
	}

	public void setDropRadius(Integer dropRadius) {
		this.dropRadius = dropRadius;
	}

	public Boolean getPhysicsEnabled() {
		return physicsEnabled;
	}

	public void setPhysicsEnabled(Boolean physicsEnabled) {
		this.physicsEnabled = physicsEnabled;
	}
	public FileConfiguration getConfig() {
		return this.weaponFile;
	}

}
