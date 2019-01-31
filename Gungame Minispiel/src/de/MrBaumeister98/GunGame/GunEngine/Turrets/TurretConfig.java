package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.WeaponSoundSet;

public class TurretConfig {

	private ItemStack gunItem;
	private ItemStack item;
	private Integer magazineSize;
	private Ammo neededAmmo;
	private Integer maxHealth;
	public String name;
	private FileConfiguration config;
	private Integer shootDelay;
	private Double shootForce;
	private EBulletType bulletType;
	private Double bulletDamage;
	private WeaponSoundSet soundSet;
	private float accuracy;
	
	private Boolean overheat;
	private Double heatPerShot;
	private Double criticalHeat;
	private Double coolDownPerTick;
	private long reloadDuration;
	
	public TurretConfig(FileConfiguration file) {
		this.config = file;
	}
	public void createLore() {
		ItemStack item = this.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setItem(item);
	}
	public void load() {
		this.name = this.config.getString("Name");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Turret: " + ChatColor.GREEN + this.name + ChatColor.YELLOW + "...");
		String ssName = this.config.getString("SoundSet");
		if(GunGamePlugin.instance.weaponManager.getSoundSet(ssName) != null) {
			setSoundSet(GunGamePlugin.instance.weaponManager.getSoundSet(ssName));
		} else {
			setSoundSet(null);
		}
		String amName = this.config.getString("Ammo");
		if(GunGamePlugin.instance.weaponManager.getAmmo(amName) != null) {
			setNeededAmmo(GunGamePlugin.instance.weaponManager.getAmmo(amName));
		} else {
			setNeededAmmo(null);
		}
		setMaxHealth(this.config.getInt("MaxHealth"));
		setMagazineSize(this.config.getInt("MagazineSize"));
		setShootDelay(this.config.getInt("Ballistics.ShootDelay"));
		setShootForce(this.config.getDouble("Ballistics.ShootingForce"));
		setBulletDamage(this.config.getDouble("Ballistics.Damage"));
		setBulletType(EBulletType.valueOf(this.config.getString("Ballistics.BulletType")));
		setReloadDuration(this.config.getLong("Ballistics.ReloadDuration"));
		setOverheat(this.config.getBoolean("Overheat.Enabled"));
		setAccuracy(Float.parseFloat(this.config.getString("Ballistics.Accuracy")));
		if(canOverheat()) {
			setHeatPerShot(this.config.getDouble("Overheat.HeatPerShot"));
			setCriticalHeat(this.config.getDouble("Overheat.CriticalHeat"));
			setCoolDownPerTick(this.config.getDouble("Overheat.CooldownPerTick"));
		}
		
		String gMatS = this.config.getString("Item.Gun");
		Material m = Material.valueOf(gMatS);
		short dmg = (short)this.config.getInt("Item.Damage");
		
		@SuppressWarnings("deprecation")
		ItemStack gItem = new ItemStack(m, 1, dmg);
		
		ItemMeta metaG = gItem.getItemMeta();
		
		metaG.setUnbreakable(true);
		metaG.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		gItem.setItemMeta(metaG);
		setGunItem(gItem);
		
		
		ItemStack item = null;
		if(GunGamePlugin.instance.serverPre113) {
			item = new ItemStack(Material.valueOf("BREWING_STAND_ITEM"), 1);
		} else {
			item = new ItemStack(Material.BREWING_STAND, 1);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + this.name);
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		
		item = ItemUtil.setGunGameItem(item);
		item = ItemUtil.addTags(item, "GG_Turret", true);
		item = ItemUtil.addTags(item, "GG_Turret_Name", this.name);
		
		setItem(item);
	}
	public ItemStack getGunItem() {
		return this.gunItem;
	}
	public void setGunItem(ItemStack item) {
		this.gunItem = item;
	}
	public Integer getMagazineSize() {
		return magazineSize;
	}
	public void setMagazineSize(Integer magazineSize) {
		this.magazineSize = magazineSize;
	}
	public Ammo getNeededAmmo() {
		return neededAmmo;
	}
	public void setNeededAmmo(Ammo neededAmmo) {
		this.neededAmmo = neededAmmo;
	}
	public Integer getMaxHealth() {
		return maxHealth;
	}
	public void setMaxHealth(Integer maxHealth) {
		this.maxHealth = maxHealth;
	}
	public Integer getShootDelay() {
		return shootDelay;
	}
	public void setShootDelay(Integer shootDelay) {
		this.shootDelay = shootDelay;
	}
	public Double getShootForce() {
		return shootForce;
	}
	public void setShootForce(Double shootForce) {
		this.shootForce = shootForce;
	}
	public EBulletType getBulletType() {
		return bulletType;
	}
	public void setBulletType(EBulletType bulletType) {
		this.bulletType = bulletType;
	}
	public Double getBulletDamage() {
		return bulletDamage;
	}
	public void setBulletDamage(Double bulletDamage) {
		this.bulletDamage = bulletDamage;
	}
	public WeaponSoundSet getSoundSet() {
		return soundSet;
	}
	public void setSoundSet(WeaponSoundSet soundSet) {
		this.soundSet = soundSet;
	}
	public Boolean canOverheat() {
		return overheat;
	}
	public void setOverheat(Boolean overheat) {
		this.overheat = overheat;
	}
	public Double getHeatPerShot() {
		return heatPerShot;
	}
	public void setHeatPerShot(Double heatPerShot) {
		this.heatPerShot = heatPerShot;
	}
	public Double getCriticalHeat() {
		return criticalHeat;
	}
	public void setCriticalHeat(Double criticalHeat) {
		this.criticalHeat = criticalHeat;
	}
	public Double getCoolDownPerTick() {
		return coolDownPerTick;
	}
	public void setCoolDownPerTick(Double coolDownPerTick) {
		this.coolDownPerTick = coolDownPerTick;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public ItemStack getItem() {
		return this.item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
	public long getReloadDuration() {
		return reloadDuration;
	}
	public void setReloadDuration(long reloadDuration) {
		this.reloadDuration = reloadDuration;
	}
	public FileConfiguration getConfig() {
		return this.config;
	}
	
}
