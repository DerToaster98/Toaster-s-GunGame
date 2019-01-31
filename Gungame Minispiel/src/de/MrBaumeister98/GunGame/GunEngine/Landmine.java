package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.LandmineType;

public class Landmine {
	
	private String name;
	private WeaponManager manager;
	private FileConfiguration config;
	private Material material;
	//private DyeColor woolColor;
	private LandmineType type;
	private Integer smokeDuration;
	private Integer fireDuration;
	private Integer radius;
	private float strength;
	private WeaponFileUtil wfu;
	private Integer smokeDensity;
	private PotionEffect potionEffect;
	private Boolean breakBlocks;
	private Boolean explosionNoDamage;
	private Boolean explosionIncendiary;
	private Boolean usePhysics;
	private Double bearTrapDamage;
	private Integer bearTrapEffectDuration;
	private Integer bearTrapEffectAmplifier;
	private ItemStack item;
	
	public Landmine(WeaponManager manager, FileConfiguration config, String name) {
		this.setManager(manager);
		this.setConfig(config);
		this.setName(name);
		
		this.setWfu(this.manager.wfu);
		this.setType(this.wfu.getLandmineExplosionType(this.config));
		this.setMaterial(this.wfu.getLandmineBlockMaterial(this.config));
		/*if(this.getMaterial().equals(Material.CARPET) || this.getMaterial().equals(Material.WOOL)) {
			this.setWoolColor(this.wfu.getLandmineBlockCarpetColor(this.config));
		}*/
		
		this.setItem(this.wfu.getLandmineItem(this.config));
		
		switch(this.getType()) {
		default:
			
			break;
		case BEARTRAP:
			this.setBearTrapDamage(this.wfu.getLandmineBearTrapDamage(this.config));
			this.setBearTrapEffectDuration(this.wfu.getLandmineBearTrapEffectDuration(this.config));
			this.setBearTrapEffectAmplifier(this.wfu.getLandmineBearTrapEffectAmplifier(this.config));
			break;
		case EXPLOSIVE:
			this.setExplosionNoDamage(this.wfu.getLandmineExplosionNoDamage(this.config));
			this.setStrength(this.wfu.getLandmineExplosionPower(this.config));
			this.setBreakBlocks(this.wfu.getLandmineExplosionBreakBlocks(this.config));
			this.setExplosionIncendiary(this.wfu.getLandmineExplosionIncendiary(this.config));
			this.setRadius(this.wfu.getLandmineExplosionRadius(this.config));
			this.setUsePhysics(this.wfu.getLandmineExplosionPhysics(this.config));
			break;
		case FIRE:
			this.setRadius(this.wfu.getLandmineExplosionRadius(this.config));
			this.setFireDuration(this.wfu.getLandmineFireDuration(this.config));
			break;
		case POISON:
			this.setRadius(this.wfu.getLandmineExplosionRadius(this.config));
			this.setPotionEffect(this.wfu.getLandminePoisonEffect(this.config));
			break;
		case SMOKE:
			this.setRadius(this.wfu.getLandmineExplosionRadius(this.config));
			this.setSmokeDensity(this.wfu.getLandmineSmokeDensity(this.config));
			this.setSmokeDuration(this.wfu.getSmokeDuration(this.config));
			break;
		}
		
		setupItem();
	}
	
	private void setupItem() {
		ItemStack itemL = this.item;
		String name = ChatColor.YELLOW + this.name;
		ItemMeta meta = itemL.getItemMeta();
		
		meta.setDisplayName(name);
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		
		itemL.setItemMeta(meta);
		
		itemL = ItemUtil.setGunGameItem(itemL);
		
		itemL = ItemUtil.addTags(itemL, "GGLandmine", true);
		itemL = ItemUtil.addTags(itemL, "GGLandmineName", this.wfu.getWeaponName(this.config));
		
		this.setItem(itemL);
	}
	
	public void createLore() {
		ItemStack item = this.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setItem(item);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public LandmineType getType() {
		return type;
	}
	public void setType(LandmineType type) {
		this.type = type;
	}
	public Integer getSmokeDuration() {
		return smokeDuration;
	}
	public void setSmokeDuration(Integer smokeDuration) {
		this.smokeDuration = smokeDuration;
	}
	public Integer getRadius() {
		return radius;
	}
	public void setRadius(Integer radius) {
		this.radius = radius;
	}
	public float getStrength() {
		return strength;
	}
	public void setStrength(float strength) {
		this.strength = strength;
	}
	public WeaponFileUtil getWfu() {
		return wfu;
	}
	public void setWfu(WeaponFileUtil wfu) {
		this.wfu = wfu;
	}
	public Integer getSmokeDensity() {
		return smokeDensity;
	}
	public void setSmokeDensity(Integer smokeDensity) {
		this.smokeDensity = smokeDensity;
	}
	public PotionEffect getPotionEffect() {
		return potionEffect;
	}
	public void setPotionEffect(PotionEffect potionEffect) {
		this.potionEffect = potionEffect;
	}
	public Boolean getBreakBlocks() {
		return breakBlocks;
	}
	public void setBreakBlocks(Boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}
	public Boolean getExplosionNoDamage() {
		return explosionNoDamage;
	}
	public void setExplosionNoDamage(Boolean explosionNoDamage) {
		this.explosionNoDamage = explosionNoDamage;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}




	public WeaponManager getManager() {
		return manager;
	}




	public void setManager(WeaponManager manager) {
		this.manager = manager;
	}




	public FileConfiguration getConfig() {
		return config;
	}




	public void setConfig(FileConfiguration config) {
		this.config = config;
	}




	public Boolean getExplosionIncendiary() {
		return explosionIncendiary;
	}




	public void setExplosionIncendiary(Boolean explosionIncendiary) {
		this.explosionIncendiary = explosionIncendiary;
	}




	/*public DyeColor getWoolColor() {
		return woolColor;
	}*/




	/*public void setWoolColor(DyeColor woolColor) {
		this.woolColor = woolColor;
	}*/




	public Integer getFireDuration() {
		return fireDuration;
	}




	public void setFireDuration(Integer fireDuration) {
		this.fireDuration = fireDuration;
	}




	public Double getBearTrapDamage() {
		return bearTrapDamage;
	}




	public void setBearTrapDamage(Double bearTrapDamage) {
		this.bearTrapDamage = bearTrapDamage;
	}




	public Integer getBearTrapEffectDuration() {
		return bearTrapEffectDuration;
	}




	public void setBearTrapEffectDuration(Integer bearTrapEffectDuration) {
		this.bearTrapEffectDuration = bearTrapEffectDuration;
	}




	public Integer getBearTrapEffectAmplifier() {
		return bearTrapEffectAmplifier;
	}




	public void setBearTrapEffectAmplifier(Integer bearTrapEffectAmplifier) {
		this.bearTrapEffectAmplifier = bearTrapEffectAmplifier;
	}




	public Boolean getUsePhysics() {
		return usePhysics;
	}




	public void setUsePhysics(Boolean usePhysics) {
		this.usePhysics = usePhysics;
	}

}
