package de.MrBaumeister98.GunGame.GunEngine.Tanks;


import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.DamageSet;
import net.md_5.bungee.api.ChatColor;

public class TankConfig {
	
	public String name;
	private FileConfiguration file;
	
	private Vector pathToShootPoint;
	private Double tankWidth;
	private Double tankLength;
	private Double tankHeight;
	private Double hitBoxHorizontalRadius;
	private Double maxHealth;
	private Double maxSpeed;
	private Double speedUpPerTick;
	private Float turnAnglePerTick;
	private Double maxSpeedReverse;
	private Double shootingForce;
	private Double maxBarrelAngle;
	private Double minBarrelAngle;
	private Integer magazineSize;
	private Long shootDelay;
	private Long reloadDuration;
	private ETankProjectileType projectileType;
	private TankSoundSet soundSet;
	
	private ItemStack tankItem;
	private ItemStack bodyItem;
	private ItemStack turretItem;
	private ItemStack barrelItem;
	private DamageSet damageSet;
	
	private boolean projectileExplosion;
	private float projectileExplosionPower;
	private Integer projectileExplosionRadius;
	private boolean projectileNoDamage;
	private double projectileDamage;
	private boolean projectileIncendiary;
	private boolean projectileExplosionBreakBlocks;
	
	@SuppressWarnings("deprecation")
	public TankConfig(FileConfiguration config) {
		this.setFile(config);
		
		TankManager manager = GunGamePlugin.instance.tankManager;
		
		String name = config.getString("Name", "tank");
		this.name = name;
		
		String tss = config.getString("SoundSet", "none");
		if(manager.getTankSoundSet(tss) != null) {
			this.setSoundSet(manager.getTankSoundSet(tss));
		} else {
			this.setSoundSet(null);
		}
		
		Double hp = config.getDouble("Health", 250.0);
		this.setMaxHealth(hp);
		
		Material bodyMat = Material.valueOf(config.getString("Items.Body.Item", "STONE"));
		short dmg = (short) config.getInt("Items.Body.Damage", 0); 
		ItemStack bd = new ItemStack(bodyMat, 1, dmg);
		bd.setDurability(dmg);
		ItemMeta bdm = bd.getItemMeta();
		bdm.setUnbreakable(true);
		bd.setItemMeta(bdm);
		this.setBodyItem(bd);
		
		Material turretMat = Material.valueOf(config.getString("Items.Turret.Item", "STONE"));
		short dmg1 = (short) config.getInt("Items.Turret.Damage", 0); 
		ItemStack bt = new ItemStack(turretMat, 1, dmg1);
		bt.setDurability(dmg1);
		ItemMeta btm = bt.getItemMeta();
		btm.setUnbreakable(true);
		bt.setItemMeta(btm);
		this.setTurretItem(bt);
		
		Material barrMat = Material.valueOf(config.getString("Items.Barrel.Item", "STONE"));
		short dmg2 = (short) config.getInt("Items.Barrel.Damage", 0); 
		ItemStack bb = new ItemStack(barrMat, 1, dmg2);
		bb.setDurability(dmg2);
		ItemMeta bbm = bb.getItemMeta();
		bbm.setUnbreakable(true);
		bb.setItemMeta(bbm);
		this.setBarrelItem(bb);
		
		Material iMat = Material.valueOf(config.getString("Items.Icon.Item", "MINECART"));
		short dmg3 = (short) config.getInt("Items.Icon.Damage", 0);
		ItemStack ic = new ItemStack(iMat, 1, dmg3);
		ItemMeta meta = ic.getItemMeta();
		String icdn = ChatColor.translateAlternateColorCodes('&', config.getString("Items.Icon.Name", "tank-spawner"));
		meta.setDisplayName(icdn);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		ic.setItemMeta(meta);
		
		ic = ItemUtil.setGunGameItem(ic);
		ic = ItemUtil.addTags(ic, "GG_Tank", true);
		ic = ItemUtil.addTags(ic, "GG_Tank_Name", this.name);
		
		this.setTankItem(ic);
		
		Double tapt = config.getDouble("Movement.TurnAnglePerTick", 1.0D);
		float ta = tapt.floatValue();
		this.setTurnAnglePerTick(ta);
		
		Double supt = config.getDouble("Movement.SpeedUpPerTick", 0.05D);
		this.setSpeedUpPerTick(supt);
		
		Double msf = config.getDouble("Movement.MaxSpeed", 0.5D);
		if(msf < 0.0D) {
			msf = msf * -1.0;
		}
		this.setMaxSpeed(msf);
		
		Double msb = config.getDouble("Movement.MaxSpeedReverse", -0.4D);
		if(msb > 0.0D) {
			msb = msb * -1.0D;
		}
		this.setMaxSpeedReverse(msb);
		
		Double mba = config.getDouble("Movement.MaxBarrelAngle", -20.0D);
		if(mba > 0.0D) {
			mba = mba * -1.0D;
		}
		this.setMaxBarrelAngle(mba);
		
		Double lba = config.getDouble("Movement.MinBarrelAngle", 5.0D);
		if(lba < 0.0D) {
			lba = lba * -1.0D;
		}
		this.setMinBarrelAngle(lba);
		
		Double ehb = config.getDouble("Hitbox.EntityCollisionBoxRadius", 2.5D);
		this.setHitBoxHorizontalRadius(ehb);
		
		Double tlr = config.getDouble("Hitbox.Radius.Length", 3.0D);
		this.setTankLength(tlr);
		
		Double twr = config.getDouble("Hitbox.Radius.Width", 2.0D);
		this.setTankWidth(twr);
		
		Double thr = config.getDouble("Hitbox.Radius.Height", 2.0D);
		this.setTankHeight(thr);
		
		Vector v1 = Util.stringToVector(config.getString("Gun.PathToShootPoint", "0.0|0.0|0.0"));
		this.setPathToShootPoint(v1);
		
		Double sf = config.getDouble("Gun.ShootingForce", 1.0D);
		this.setShootingForce(sf);
		
		long rd = config.getLong("Gun.ReloadDuration", 40);
		this.setReloadDuration(rd);
		
		long sd = config.getLong("Gun.ShootDelay", 15);
		this.setShootDelay(sd);
		
		Integer mag = config.getInt("Gun.Magazine", 20);
		this.setMagazineSize(mag);
		
		ETankProjectileType tpt = ETankProjectileType.valueOf(config.getString("Gun.Projectile.Type", "TANK_SHELL").toUpperCase());
		this.setProjectileType(tpt);
		
		boolean expen = config.getBoolean("Gun.Projectile.Explosion.Enabled", true);
		this.setProjectileExplosion(expen);
		
		Double expp = config.getDouble("Gun.Projectile.Explosion.Power", 4.0D);
		float exppf = expp.floatValue();
		this.setProjectileExplosionPower(exppf);
		
		Integer expr = config.getInt("Gun.Projectile.Explosion.Radius", 3);
		this.setProjectileExplosionRadius(expr);
		
		boolean expbb = config.getBoolean("Gun.Projectile.Explosion.BreakBlocks", true);
		this.setProjectileExplosionBreakBlocks(expbb);
		
		boolean prince = config.getBoolean("Gun.Projectile.Incendiary", false);
		this.setProjectileIncendiary(prince);
		
		boolean dmge = config.getBoolean("Gun.Projectile.Damage.Enabled", true);
		if(dmge) {
			this.setProjectileNoDamage(false);
		} else {
			this.setProjectileNoDamage(true);
		}
		
		Double dmgg = config.getDouble("Gun.Projectile.Damage.Amount", 8.0D);
		this.setProjectileDamage(dmgg);
	}
	
	public void createLore() {
		ItemStack item = this.tankItem;
		ItemMeta meta = item.getItemMeta();
		List<String> lore = LangUtil.getWeaponItemLore(this);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		this.setTankItem(item);
	}
	
	public Double getWidthRadius() {
		return tankWidth;
	}
	public void setTankWidth(Double tankWidth) {
		this.tankWidth = tankWidth;
	}
	public Double getLengthRadius() {
		return tankLength;
	}
	public void setTankLength(Double tankLength) {
		this.tankLength = tankLength;
	}
	public Double getHeightRadius() {
		return tankHeight;
	}
	public void setTankHeight(Double tankHeight) {
		this.tankHeight = tankHeight;
	}
	public Float getTurnAnglePerTick() {
		return turnAnglePerTick;
	}
	public void setTurnAnglePerTick(float turnAnglePerTick) {
		this.turnAnglePerTick = turnAnglePerTick;
	}
	public Double getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public Double getSpeedUpPerTick() {
		return speedUpPerTick;
	}
	public void setSpeedUpPerTick(Double speedUpPerTick) {
		this.speedUpPerTick = speedUpPerTick;
	}
	public Double getMaxSpeedReverse() {
		return maxSpeedReverse;
	}
	public void setMaxSpeedReverse(Double maxSpeedReverse) {
		this.maxSpeedReverse = maxSpeedReverse;
	}
	public ItemStack getBodyItem() {
		return bodyItem;
	}
	public void setBodyItem(ItemStack bodyItem) {
		this.bodyItem = bodyItem;
	}
	public ItemStack getTurretItem() {
		return turretItem;
	}
	public void setTurretItem(ItemStack turretItem) {
		this.turretItem = turretItem;
	}
	public ItemStack getBarrelItem() {
		return barrelItem;
	}
	public void setBarrelItem(ItemStack barrelItem) {
		this.barrelItem = barrelItem;
	}
	public Integer getMagazineSize() {
		return magazineSize;
	}
	public void setMagazineSize(Integer magazineSize) {
		this.magazineSize = magazineSize;
	}
	public Double getMaxHealth() {
		return maxHealth;
	}
	public void setMaxHealth(Double maxHealth) {
		this.maxHealth = maxHealth;
	}
	public ItemStack getTankItem() {
		return tankItem;
	}
	public void setTankItem(ItemStack tankItem) {
		this.tankItem = tankItem;
	}
	public Vector getPathToShootPoint() {
		return pathToShootPoint;
	}
	public void setPathToShootPoint(Vector pathToShootPoint) {
		this.pathToShootPoint = pathToShootPoint;
	}
	public Double getShootingForce() {
		return shootingForce;
	}
	public void setShootingForce(Double shootingForce) {
		this.shootingForce = shootingForce;
	}
	public Double getMaxBarrelAngle() {
		return maxBarrelAngle;
	}
	public void setMaxBarrelAngle(Double maxBarrelAngle) {
		this.maxBarrelAngle = maxBarrelAngle;
	}
	public Double getMinBarrelAngle() {
		return minBarrelAngle;
	}
	public void setMinBarrelAngle(Double minBarrelAngle) {
		this.minBarrelAngle = minBarrelAngle;
	}
	public long getShootDelay() {
		return shootDelay;
	}
	public void setShootDelay(long shootDelay) {
		this.shootDelay = shootDelay;
	}
	public long getReloadDuration() {
		return reloadDuration;
	}
	public void setReloadDuration(long reloadDuration) {
		this.reloadDuration = reloadDuration;
	}
	public Double getHitBoxHorizontalRadius() {
		return hitBoxHorizontalRadius;
	}
	public void setHitBoxHorizontalRadius(Double hitBoxHorizontalRadius) {
		this.hitBoxHorizontalRadius = hitBoxHorizontalRadius;
	}
	public ETankProjectileType getProjectileType() {
		return projectileType;
	}
	public void setProjectileType(ETankProjectileType projectileType) {
		this.projectileType = projectileType;
	}
	public DamageSet getDamageSet() {
		return this.damageSet;
	}
	public float getProjectileExplosionPower() {
		return projectileExplosionPower;
	}
	public void setProjectileExplosionPower(float projectileExplosionPower) {
		this.projectileExplosionPower = projectileExplosionPower;
	}
	public Integer getProjectileExplosionRadius() {
		return projectileExplosionRadius;
	}
	public void setProjectileExplosionRadius(Integer projectileExplosionRadius) {
		this.projectileExplosionRadius = projectileExplosionRadius;
	}
	public boolean isProjectileNoDamage() {
		return projectileNoDamage;
	}
	public void setProjectileNoDamage(boolean projectileNoDamage) {
		this.projectileNoDamage = projectileNoDamage;
	}
	public double getProjectileDamage() {
		return projectileDamage;
	}
	public void setProjectileDamage(double projectileDamage) {
		this.projectileDamage = projectileDamage;
	}
	public boolean isProjectileIncendiary() {
		return projectileIncendiary;
	}
	public void setProjectileIncendiary(boolean projectileIncendiary) {
		this.projectileIncendiary = projectileIncendiary;
	}
	public boolean isProjectileExplosionBreakBlocks() {
		return projectileExplosionBreakBlocks;
	}
	public void setProjectileExplosionBreakBlocks(boolean projectileExplosionBreakBlocks) {
		this.projectileExplosionBreakBlocks = projectileExplosionBreakBlocks;
	}

	public TankSoundSet getSoundSet() {
		return soundSet;
	}

	public void setSoundSet(TankSoundSet soundSet) {
		this.soundSet = soundSet;
	}

	public boolean isProjectileExplosion() {
		return projectileExplosion;
	}

	public void setProjectileExplosion(boolean projectileExplosion) {
		this.projectileExplosion = projectileExplosion;
	}

	public FileConfiguration getFile() {
		return file;
	}

	public void setFile(FileConfiguration file) {
		this.file = file;
	}

}
