package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.GunType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.ProjectileType;

@SuppressWarnings("deprecation")
public class Gun {
	
	private WeaponManager manager;
	private FileConfiguration weaponFile;
	private ProjectileType bulletType;
	private String GunName;
	private GunType type;
	private ItemStack gunItem;
	private Ammo ammo;
	private Integer maxAmmo;
	private WeaponSoundSet soundSet;
	private Boolean standardWeapon;
	
	private Boolean usePermission;
	private Permission permission;
	
	private long shootingDelay;
	private long laserRayIterationDelay;
	private Double shootingForce;
	private long reloadDuration;
	private Boolean scopeEnabled;
	private Boolean mayHolderMoveWhileAiming;
	private Boolean realisticVisuals;
	private ItemStack realisticVisualsBow;
	private Double shotDamage;
	private Double meleeDamage;
	private Double weight;
	private float accuracy;
	private Boolean akimboAllowed;
	private Double recoilAmount;
	private float recoilAmountVertical;
	private Integer projectileCount;
	private Integer volleyShotAmount;
	
	private Particle shootParticle;
	private Double shootParticleX;
	private Double shootParticleY;
	private Double shootParticleZ;
	private Double shootParticleDX;
	private Double shootParticleDY;
	private Double shootParticleDZ;
	private Double shootParticleSpeed;
	private Integer shootParticleCount;
	private Object shootParticleExtra;
	
	private Particle hitParticle;
	private Double hitParticleX;
	private Double hitParticleY;
	private Double hitParticleZ;
	private Double hitParticleDX;
	private Double hitParticleDY;
	private Double hitParticleDZ;
	private Double hitParticleSpeed;
	private Integer hitParticleCount;
	private Object hitParticleExtra;
	
	private Boolean seekingRocket;
	private Boolean rocketCreateFire;
	private Boolean rocketNoDamage;
	private Boolean rocketBreakBlocks;
	private float rocketExplosionDamage;
	private Integer rocketExplosionRadius;
	
	private WeaponFileUtil wfu;
	private PlasmaParticleUtil laserHelper;
	private DamageSet damSet;
	
	public Gun(WeaponManager manager, String name, FileConfiguration weaponConfig) {
		this.setManager(manager);
		this.setGunName(name);
		this.setWeaponFile(weaponConfig);
		
		this.wfu = this.manager.wfu;		
		setUpAmmo();
		setType();
		setSoundSet();
		
		this.setStandardWeapon(this.wfu.isStandardWeapon(this.weaponFile));
		
		this.setShootingDelay(this.wfu.getShootDelay(this.weaponFile));
		this.setShootingForce(this.wfu.getShootingForce(this.weaponFile));
		this.setReloadDuration(this.wfu.getReloadDuration(this.weaponFile));
		this.setScopeEnabled(this.wfu.getScopeEnabled(this.weaponFile));
		this.setMayHolderMoveWhileAiming(this.wfu.getMayHolderMoveWhileAiming(this.weaponFile));
		this.setShotDamage(this.wfu.getShotDamage(this.weaponFile));
		this.setDamSet(new DamageSet(this.shotDamage));
		this.setMeleeDamage(this.wfu.getMeleeDamage(this.weaponFile));
		this.setWeight(this.wfu.getWeight(this.weaponFile));
		this.setAkimboAllowed(this.wfu.getAkimboAllowed(this.weaponFile));
		this.setRealisticVisuals(this.wfu.getRealisticVisualsEnabled(this.weaponFile));
		this.setShotCount(this.wfu.getProjectileAmount(this.weaponFile));
		if(this.realisticVisuals) {
			this.setRealisticVisualsBow(this.wfu.getRealisticVisualsItem(this.weaponFile));
		}
		this.setRecoilAmount(this.wfu.getRecoil(this.weaponFile));
		this.setRecoilAmountVertical(this.wfu.getPullUpwardAmount(this.weaponFile));
		
		if(!this.isStandardWeapon()) {
			this.setUsePermission(this.wfu.getUsePermissionEnabled(this.weaponFile));
			if(this.hasUsePermission()) {
				this.setPermission(this.wfu.getPermission(this.weaponFile));
			}
		} else {
			this.setUsePermission(false);
		}
		
		if(!this.type.equals(GunType.GRENADETHROWER)) {
			this.setAccuracy(this.wfu.getAccuracy(this.weaponFile));
		}
		if(this.type.equals(GunType.ROCKETLAUNCHER)) {
			this.setRocketBreakBlocks(this.wfu.getRocketBreakBlocks(this.weaponFile));
			this.setRocketCreateFire(this.wfu.getRocketCreateFire(this.weaponFile));
			this.setRocketExplosionDamage(this.wfu.getRocketExplosionDamage(this.weaponFile));
			this.setRocketExplosionRadius(this.wfu.getRocketExplosionRadius(this.weaponFile));
			this.setRocketNoDamage(this.wfu.getRocketNoDamage(this.weaponFile));
			this.setSeekingRocket(this.wfu.getRocketSeekingHeat(this.weaponFile));
		}
		
		if(this.type.equals(GunType.PLASMA)) {
			this.setLaserHelper(new PlasmaParticleUtil(this));
			this.setLaserRayIterationDelay(this.wfu.getLaserRayIterationDelay(this.weaponFile));
		}
		if(this.type.equals(GunType.ASSAULT)) {
			this.setVolleyShotAmount(this.wfu.getVolleyShotAmount(this.weaponFile));
		}
		if(this.type.equals(GunType.ASSAULT_PLASMA)) {
			this.setLaserHelper(new PlasmaParticleUtil(this));
			this.setVolleyShotAmount(this.wfu.getVolleyShotAmount(this.weaponFile));
			this.setLaserRayIterationDelay(this.wfu.getLaserRayIterationDelay(this.weaponFile));
		}
		if(this.type.equals(GunType.MINIGUN_PLASMA)) {
			this.setLaserHelper(new PlasmaParticleUtil(this));
		}
		if(this.type.equals(GunType.ASSAULT) || this.type.equals(GunType.MINIGUN) || this.type.equals(GunType.STANDARD)) {
			this.setBulletType(this.wfu.getProjectileType(this.weaponFile));
		}
		
		this.gunItem = createItem();
		
		//if(!GunGamePlugin.instance.serverPre113) {
			loadHitParticleData();
			loadShootParticleData();
		//}
	}
	
	public ItemStack createItem() {
		ItemStack gun;
		
		gun = this.wfu.getGunItem(this.weaponFile);
		
		if(this.type.equals(GunType.GRENADETHROWER)) {
			gun = ItemUtil.addTags(gun, "GG_GrenadeThrower_LoadedGrenade", "NONE");
			gun = GunItemUtil.updateRemainingShots(gun, 0);
		}
		
		return gun;
	}
	public void setUpAmmo() {
		Ammo temp = this.wfu.getGunAmmo(this.weaponFile);
		setAmmo(temp);
		Integer tempI = this.wfu.getGunCapacity(this.weaponFile);
		setMaxAmmo(tempI);
	}
	public void setType() {
		GunType temp = this.wfu.getGunType(this.weaponFile);
		setType(temp);
	}

	public void setSoundSet() {
		WeaponSoundSet temp = this.wfu.getSoundSet(this.weaponFile);
		setSoundSet(temp);
	}
	public void createLore() {
		ItemStack item = this.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setGunItem(item);
	}
	public void loadShootParticleData() {
		String cfg = this.wfu.getShootingParticle(this.weaponFile);
		String[] vars = cfg.split(",");
		try {
			this.setShootParticle(Particle.valueOf(vars[0].toUpperCase()));
		} catch(Exception ex) {
			this.setShootParticle(Particle.SMOKE_NORMAL);
			ex.printStackTrace();
		}
		String[] v1 = vars[1].split("-");
		this.setShootParticleX(Double.valueOf(v1[0]));
		this.setShootParticleY(Double.valueOf(v1[1]));
		this.setShootParticleZ(Double.valueOf(v1[2]));
		String[] v2 = vars[2].split("-");
		this.setShootParticleDX(Double.valueOf(v2[0]));
		this.setShootParticleDY(Double.valueOf(v2[1]));
		this.setShootParticleDZ(Double.valueOf(v2[2]));
		this.setShootParticleSpeed(Double.valueOf(vars[3]));
		this.setShootParticleCount(Integer.valueOf(vars[4]));
		if(this.getShootParticle().equals(Particle.REDSTONE) || this.getShootParticle().equals(Particle.BLOCK_DUST)) {
			String[] cl = vars[5].split("-");
			org.bukkit.Particle.DustOptions dO = new org.bukkit.Particle.DustOptions(Color.fromRGB(Integer.valueOf(cl[0]), Integer.valueOf(cl[1]), Integer.valueOf(cl[1])), 1.0f);
			this.setShootParticleExtra(dO);
		}
		else if(this.getShootParticle().equals(Particle.BLOCK_CRACK) || this.getShootParticle().equals(Particle.FALLING_DUST)) {
			try {
				/*Material m = Material.valueOf(vars[5]);
				org.bukkit.block.data.BlockData bd = m.createBlockData();
				this.setShootParticleExtra(bd);*/
				Object bd = null;
				if(GunGamePlugin.instance.serverPre113) {
					String[] dta = vars[5].split("-");
					Material m = Material.valueOf(dta[0]);
					byte dmg = 0;
					try {
						byte tmp = Byte.valueOf(dta[1]);
						dmg = tmp;
					} catch(Exception ex) {
						ex.printStackTrace();
						dmg = 0;
					}
					MaterialData md = new MaterialData(m, dmg);
					bd = md;
				} else {
					Material m = Material.valueOf(vars[5]);
					bd = m.createBlockData();
				}
				this.setShootParticleExtra(bd);
			} catch(Exception ex) {
				ex.printStackTrace();
				this.setShootParticleExtra(null);
			}
		}
		else if(this.getShootParticle().equals(Particle.ITEM_CRACK)) {
			ItemStack stack = new ItemStack(Material.valueOf(vars[5]));
			this.setShootParticleExtra(stack);
		}
		else {
			this.setShootParticleExtra(null);
		}
	}
	public void loadHitParticleData() {
		String cfg = this.wfu.getBulletHitParticle(this.weaponFile);
		String[] vars = cfg.split(",");
		try {
			this.setHitParticle(Particle.valueOf(vars[0].toUpperCase()));
		} catch(Exception ex) {
			this.setHitParticle(Particle.SNOWBALL);
			ex.printStackTrace();
		}
		String[] v1 = vars[1].split("-");
		this.setHitParticleX(Double.valueOf(v1[0]));
		this.setHitParticleY(Double.valueOf(v1[1]));
		this.setHitParticleZ(Double.valueOf(v1[2]));
		String[] v2 = vars[2].split("-");
		this.setHitParticleDX(Double.valueOf(v2[0]));
		this.setHitParticleDY(Double.valueOf(v2[1]));
		this.setHitParticleDZ(Double.valueOf(v2[2]));
		this.setHitParticleSpeed(Double.valueOf(vars[3]));
		this.setHitParticleCount(Integer.valueOf(vars[4]));
		if(this.getHitParticle().equals(Particle.REDSTONE) || this.getHitParticle().equals(Particle.BLOCK_DUST)) {
			String[] cl = vars[5].split("-");
			Object dO = null;
			dO = new org.bukkit.Particle.DustOptions(Color.fromRGB(Integer.valueOf(cl[0]), Integer.valueOf(cl[1]), Integer.valueOf(cl[1])), 1.0f);
			this.setHitParticleExtra(dO);
		}
		else if(this.getHitParticle().equals(Particle.BLOCK_CRACK) || this.getHitParticle().equals(Particle.FALLING_DUST)) {
			try {
				
				Object bd = null;
				if(GunGamePlugin.instance.serverPre113) {
					String[] dta = vars[5].split("-");
					Material m = Material.valueOf(dta[0]);
					byte dmg = 0;
					try {
						byte tmp = Byte.valueOf(dta[1]);
						dmg = tmp;
					} catch(Exception ex) {
						ex.printStackTrace();
						dmg = 0;
					}
					MaterialData md = new MaterialData(m, dmg);
					bd = md;
				} else {
					Material m = Material.valueOf(vars[5]);
					bd = m.createBlockData();
				}
				this.setHitParticleExtra(bd);
			} catch(Exception ex) {
				ex.printStackTrace();
				this.setHitParticleExtra(null);
			}
		}
		else if(this.getHitParticle().equals(Particle.ITEM_CRACK)) {
			ItemStack stack = new ItemStack(Material.valueOf(vars[5]));
			this.setHitParticleExtra(stack);
		}
		else {
			this.setHitParticleExtra(null);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public WeaponManager getManager() {
		return this.manager;
	}

	public void setManager(WeaponManager manager) {
		this.manager = manager;
	}

	public FileConfiguration getWeaponFile() {
		return this.weaponFile;
	}

	public void setWeaponFile(FileConfiguration weaponFile) {
		this.weaponFile = weaponFile;
	}

	public String getGunName() {
		return this.GunName;
	}

	public void setGunName(String gunName) {
		this.GunName = gunName;
	}

	public ItemStack getItem() {
		return this.gunItem;
	}

	public void setGunItem(ItemStack gunItem) {
		this.gunItem = gunItem;
	}

	public Ammo getAmmo() {
		return this.ammo;
	}

	public void setAmmo(Ammo ammo) {
		this.ammo = ammo;
	}

	public GunType getType() {
		return this.type;
	}

	public void setType(GunType type) {
		this.type = type;
	}

	public WeaponSoundSet getSoundSet() {
		return soundSet;
	}
	public void setSoundSet(WeaponSoundSet set) {
		this.soundSet = set;
	}

	public Integer getMaxAmmo() {
		return this.maxAmmo;
	}

	public void setMaxAmmo(Integer maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

	public Double getShootingForce() {
		return shootingForce;
	}

	public long getShootingDelay() {
		return shootingDelay;
	}

	public void setShootingDelay(long shootingDelay) {
		this.shootingDelay = shootingDelay;
	}

	public long getReloadDuration() {
		return reloadDuration;
	}

	public void setReloadDuration(long reloadDuration) {
		this.reloadDuration = reloadDuration;
	}

	public void setShootingForce(Double shootingForce) {
		this.shootingForce = shootingForce;
	}

	public Boolean getScopeEnabled() {
		return scopeEnabled;
	}

	public void setScopeEnabled(Boolean scopeEnabled) {
		this.scopeEnabled = scopeEnabled;
	}

	public Boolean getMayHolderMoveWhileAiming() {
		return mayHolderMoveWhileAiming;
	}

	public void setMayHolderMoveWhileAiming(Boolean mayHolderMoveWhileAiming) {
		this.mayHolderMoveWhileAiming = mayHolderMoveWhileAiming;
	}

	public PlasmaParticleUtil getLaserHelper() {
		return laserHelper;
	}

	public void setLaserHelper(PlasmaParticleUtil laserHelper) {
		this.laserHelper = laserHelper;
	}

	public Double getShotDamage() {
		return shotDamage;
	}

	public void setShotDamage(Double shotDamage) {
		this.shotDamage = shotDamage;
	}

	public DamageSet getDamSet() {
		return damSet;
	}

	public void setDamSet(DamageSet damSet) {
		this.damSet = damSet;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public Double getMeleeDamage() {
		return meleeDamage;
	}

	public void setMeleeDamage(Double meleeDamage) {
		this.meleeDamage = meleeDamage;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Boolean getAkimboAllowed() {
		return akimboAllowed;
	}

	public void setAkimboAllowed(Boolean akimboAllowed) {
		this.akimboAllowed = akimboAllowed;
	}

	public ItemStack getRealisticVisualsBow() {
		return realisticVisualsBow;
	}

	public void setRealisticVisualsBow(ItemStack realisticVisualsBow) {
		this.realisticVisualsBow = realisticVisualsBow;
	}

	public Boolean getRealisticVisuals() {
		return realisticVisuals;
	}

	public void setRealisticVisuals(Boolean realisticVisuals) {
		this.realisticVisuals = realisticVisuals;
	}

	public float getRocketExplosionDamage() {
		return rocketExplosionDamage;
	}

	public void setRocketExplosionDamage(float rocketExplosionDamage) {
		this.rocketExplosionDamage = rocketExplosionDamage;
	}

	public Boolean getRocketBreakBlocks() {
		return rocketBreakBlocks;
	}

	public void setRocketBreakBlocks(Boolean rocketBreakBlocks) {
		this.rocketBreakBlocks = rocketBreakBlocks;
	}

	public Boolean getRocketNoDamage() {
		return rocketNoDamage;
	}

	public void setRocketNoDamage(Boolean rocketNoDamage) {
		this.rocketNoDamage = rocketNoDamage;
	}

	public Integer getRocketExplosionRadius() {
		return rocketExplosionRadius;
	}

	public void setRocketExplosionRadius(Integer rocketExplosionRadius) {
		this.rocketExplosionRadius = rocketExplosionRadius;
	}

	public Boolean getRocketCreateFire() {
		return rocketCreateFire;
	}

	public void setRocketCreateFire(Boolean rocketCreateFire) {
		this.rocketCreateFire = rocketCreateFire;
	}

	public float getRecoilAmountVertical() {
		return recoilAmountVertical;
	}

	public void setRecoilAmountVertical(float recoilAmountVertical) {
		this.recoilAmountVertical = recoilAmountVertical;
	}

	public Double getRecoilAmount() {
		return recoilAmount;
	}

	public void setRecoilAmount(Double recoilAmount) {
		this.recoilAmount = recoilAmount;
	}

	public Integer getShotCount() {
		return projectileCount;
	}

	public void setShotCount(Integer shotCount) {
		this.projectileCount = shotCount;
	}

	public Integer getVolleyShotAmount() {
		return volleyShotAmount;
	}

	public void setVolleyShotAmount(Integer volleyShotAmount) {
		this.volleyShotAmount = volleyShotAmount;
	}

	public Boolean isStandardWeapon() {
		return standardWeapon;
	}

	public void setStandardWeapon(Boolean standardWeapon) {
		this.standardWeapon = standardWeapon;
	}

	public Boolean hasUsePermission() {
		return usePermission;
	}

	public void setUsePermission(Boolean usePermission) {
		this.usePermission = usePermission;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		Permission perm = new Permission(permission, PermissionDefault.FALSE);
		perm.addParent("gunengine.admin", false);
		GunGamePlugin.instance.getServer().getPluginManager().addPermission(perm);
		this.permission = perm;
	}

	public ProjectileType getBulletType() {
		return bulletType;
	}

	public void setBulletType(ProjectileType bulletType) {
		this.bulletType = bulletType;
	}

	public long getLaserRayIterationDelay() {
		return laserRayIterationDelay;
	}

	public void setLaserRayIterationDelay(long laserRayIterationDelay) {
		this.laserRayIterationDelay = laserRayIterationDelay;
	}

	public Particle getShootParticle() {
		return shootParticle;
	}

	public void setShootParticle(Particle shootParticle) {
		this.shootParticle = shootParticle;
	}

	public Double getShootParticleDZ() {
		return shootParticleDZ;
	}

	public void setShootParticleDZ(Double shootParticleDZ) {
		this.shootParticleDZ = shootParticleDZ;
	}

	public Double getShootParticleX() {
		return shootParticleX;
	}

	public void setShootParticleX(Double shootParticleX) {
		this.shootParticleX = shootParticleX;
	}

	public Integer getShootParticleCount() {
		return shootParticleCount;
	}

	public void setShootParticleCount(Integer shootParticleCount) {
		this.shootParticleCount = shootParticleCount;
	}

	public Double getShootParticleSpeed() {
		return shootParticleSpeed;
	}

	public void setShootParticleSpeed(Double shootParticleSpeed) {
		this.shootParticleSpeed = shootParticleSpeed;
	}

	public Double getShootParticleY() {
		return shootParticleY;
	}

	public void setShootParticleY(Double shootParticleY) {
		this.shootParticleY = shootParticleY;
	}

	public Double getShootParticleDX() {
		return shootParticleDX;
	}

	public void setShootParticleDX(Double shootParticleDX) {
		this.shootParticleDX = shootParticleDX;
	}

	public Double getShootParticleZ() {
		return shootParticleZ;
	}

	public void setShootParticleZ(Double shootParticleZ) {
		this.shootParticleZ = shootParticleZ;
	}

	public Double getShootParticleDY() {
		return shootParticleDY;
	}

	public void setShootParticleDY(Double shootParticleDY) {
		this.shootParticleDY = shootParticleDY;
	}

	public Object getShootParticleExtra() {
		return shootParticleExtra;
	}

	public void setShootParticleExtra(Object shootParticleExtra) {
		this.shootParticleExtra = shootParticleExtra;
	}

	public Particle getHitParticle() {
		return hitParticle;
	}

	public void setHitParticle(Particle hitParticle) {
		this.hitParticle = hitParticle;
	}

	public Double getHitParticleX() {
		return hitParticleX;
	}

	public void setHitParticleX(Double hitParticleX) {
		this.hitParticleX = hitParticleX;
	}

	public Double getHitParticleY() {
		return hitParticleY;
	}

	public void setHitParticleY(Double hitParticleY) {
		this.hitParticleY = hitParticleY;
	}

	public Double getHitParticleZ() {
		return hitParticleZ;
	}

	public void setHitParticleZ(Double hitParticleZ) {
		this.hitParticleZ = hitParticleZ;
	}

	public Double getHitParticleDX() {
		return hitParticleDX;
	}

	public void setHitParticleDX(Double hitParticleDX) {
		this.hitParticleDX = hitParticleDX;
	}

	public Double getHitParticleDY() {
		return hitParticleDY;
	}

	public void setHitParticleDY(Double hitParticleDY) {
		this.hitParticleDY = hitParticleDY;
	}

	public Double getHitParticleDZ() {
		return hitParticleDZ;
	}

	public void setHitParticleDZ(Double hitParticleDZ) {
		this.hitParticleDZ = hitParticleDZ;
	}

	public Double getHitParticleSpeed() {
		return hitParticleSpeed;
	}

	public void setHitParticleSpeed(Double hitParticleSpeed) {
		this.hitParticleSpeed = hitParticleSpeed;
	}

	public Integer getHitParticleCount() {
		return hitParticleCount;
	}

	public void setHitParticleCount(Integer hitParticleCount) {
		this.hitParticleCount = hitParticleCount;
	}

	public Object getHitParticleExtra() {
		return hitParticleExtra;
	}

	public void setHitParticleExtra(Object hitParticleExtra) {
		this.hitParticleExtra = hitParticleExtra;
	}

	public Boolean isSeekingRocket() {
		return seekingRocket;
	}

	public void setSeekingRocket(Boolean seekingRocket) {
		this.seekingRocket = seekingRocket;
	}
	public Boolean canMeltBlocks() {
		return this.wfu.canMeltBlocks(this.weaponFile);
	}

}
