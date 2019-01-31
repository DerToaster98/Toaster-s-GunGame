package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.GrenadeType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.GunType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.LandmineType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.ProjectileType;
import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.NBTList;
import de.tr7zw.itemnbtapi.NBTListCompound;
import de.tr7zw.itemnbtapi.NBTType;

public class WeaponFileUtil {
	
	private WeaponManager manager;
	
	public WeaponFileUtil (WeaponManager manager) {
		this.manager = manager;
	}
	
	public String getWeaponName(FileConfiguration config) {
		String name = null;
		try { 
			name = config.getString("Name");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return name;
	}
	public WeaponSoundSet getSoundSet(FileConfiguration config) {
		WeaponSoundSet ss = null;
		try {
			String temp = config.getString("SoundSet");
			ss = this.manager.getSoundSet(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return ss;
	}
	
	
	
	public Boolean canMeltBlocks(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean b2 = config.getBoolean("Effects.PlasmaTrail.MeltBlocks", false);
			b = b2;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	public Boolean isStandardWeapon(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean b2 = config.getBoolean("StandardWeapon");
			b = b2;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	
	
	
	
	
	
	
	
	
	

	public GrenadeType getGrenadeType(FileConfiguration config) {
		GrenadeType type = GrenadeType.FRAG;
		try {
			String temp = config.getString("Type");
			type = GrenadeType.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return type;
	}

	public ItemStack getGrenadeItem(FileConfiguration config) {
		//ItemStack gun = null;	
		Material mat = null;
		try {
			String temp = config.getString("Item.Material", GunGamePlugin.instance.serverPre113 ? "FIREWORK_CHARGE" : "FIREWORK_STAR");
			mat = Material.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		short damage = 0;
		try {
			String temp = config.getString("Item.Damage");
			damage = Short.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		@SuppressWarnings("deprecation")
		ItemStack gun = new ItemStack(mat, 1, (short)damage);
		
		ItemMeta meta = gun.getItemMeta();
		
		String dn = ChatColor.translateAlternateColorCodes('&', "&e" + getWeaponName(config));
		try {
			String tmp = config.getString("Item.DisplayName");
			dn = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dn));
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		
		gun.setItemMeta(meta);
	
		gun = ItemUtil.addTags(gun, "GGWeapon", true);
		gun = ItemUtil.addTags(gun, "GGWeaponType", "GRENADE");
		gun = ItemUtil.addTags(gun, "GGGunName", getWeaponName(config));
		
		return gun;
	}

	public Integer getGrenadeFuse(FileConfiguration config) {
		Integer timer = 0;
		try {
			Double temp = config.getDouble("Explosion.Fuse");
			Double temp2 = temp * 20D;
			timer = temp2.intValue();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return timer;
	}

	public float getPower(FileConfiguration config) {
		float p = 0F;
		try {
			float temp = Float.valueOf(config.getString("Explosion.Power"));
			p = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return p;
	}

	public Integer getRadius(FileConfiguration config) {
		Integer r = 0;
		try {
			Integer temp = config.getInt("Explosion.Radius");
			r = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}

	public Integer getClusterCount(FileConfiguration config) {
		Integer c = 0;
		try {
			Integer temp = config.getInt("Explosion.Cluster.Count");
			c = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return c;
	}

	public Boolean getFireEnabled(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean temp = config.getBoolean("Explosion.SpreadFire");
			f = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public Boolean getExplosionNoDamage(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean temp = config.getBoolean("Explosion.NoDamage");
			b = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Boolean getExplodeOnImpact(FileConfiguration config) {
		Boolean b = true;
		try {
			Boolean temp = config.getBoolean("Explosion.ExplodeOnImpact");
			b = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public Boolean getBreakBlocks(FileConfiguration config) {
		Boolean b = true;
		try {
			Boolean temp = config.getBoolean("Explosion.BreakBlocks");
			b = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public Boolean getSmokeEnabled(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean temp = config.getBoolean("Explosion.Smoke.Enabled");
			b = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public Integer getSmokeDuration(FileConfiguration config) {
		Integer timer = 0;
		try {
			Double temp = config.getDouble("Explosion.Smoke.Duration");
			Double temp2 = temp * 20D;
			timer = temp2.intValue();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return timer;
	}

	public Integer getSmokeRadius(FileConfiguration config) {
		Integer r = 0;
		try {
			Integer temp = config.getInt("Explosion.Smoke.Radius");
			r = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}

	public Particle getGrenadeSmokeParticle(FileConfiguration config) {
		Particle part = Particle.CLOUD;
		try {
			String s = config.getString("Explosion.Smoke.Particle");
			Particle temp = Particle.valueOf(s);
			part = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return part;
	}

	public Double getSmokeAffectionRange(FileConfiguration config) {
		Double d = 0.25D;
		try {
			Double temp = config.getDouble("Explosion.Smoke.AffectionRange");
			d = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}

	public Integer getSmokeDensity(FileConfiguration config) {
		Integer d = 1;
		try {
			Integer temp = config.getInt("Explosion.Smoke.Density");
			d = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}

	public Boolean getPotionEnabled(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean temp = config.getBoolean("Effect.Enabled");
			b = temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public PotionEffect getGrenadePotionEffect(FileConfiguration config) {
		PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 0, 0);
		try {
			PotionEffectType pet = PotionEffectType.getByName(config.getString("Effect.Potion"));
			Integer dur = config.getInt("Effect.Duration") *20;
			Integer ampli = config.getInt("Effect.Amplifier");
			PotionEffect temp = new PotionEffect(pet, dur, ampli);
			pe = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return pe;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public GunType getGunType(FileConfiguration config) {
		GunType type = GunType.STANDARD;
		try {
			String temp = config.getString("Type");
			type = GunType.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return type;
	}
	public ProjectileType getProjectileType(FileConfiguration config) {
		ProjectileType pt = ProjectileType.ARROW;
		try {
			String tmp = config.getString("ProjectileType");
			pt = ProjectileType.valueOf(tmp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return pt;
	}
	public Double getWeight(FileConfiguration config) {
		Double w = 1.0D;
		try {
			Double tmp = config.getDouble("Weight");
			w = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return w;
	}
	public Double getMeleeDamage(FileConfiguration config) {
		Double d = 1.0D;
		try {
			Double tmp = config.getDouble("MeleeDamage");
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public ItemStack getGunItem(FileConfiguration config) {
		//ItemStack gun = null;	
		boolean invalidMat = false;
		Material mat = null;
		try {
			String temp = config.getString("Item.Material");
			mat = Material.valueOf(temp);
		} catch(Exception ex) {
			invalidMat = true;
			Debugger.logError("Material for gun with configuration: " + config.getName() + " is invalid! Using default material...");
			if(GunGamePlugin.instance.serverPre113) {
				mat = Material.getMaterial("IRON_BARDING");
			} else {
				mat = Material.IRON_HORSE_ARMOR;
			}
			//ex.printStackTrace();
		}
		short damage = 0;
		if(!invalidMat) {
			try {
				String temp = config.getString("Item.Damage");
				damage = Short.valueOf(temp);
			} catch(Exception ex) {
				Debugger.logError("Damage for gun with configuration: " + config.getName() + " is invalid! Using default damage [0]...");
				damage = 0;
				//ex.printStackTrace();
			}
		}
		@SuppressWarnings("deprecation")
		ItemStack gun = new ItemStack(mat, 1, (short)damage);
		
		ItemMeta meta = gun.getItemMeta();
		
		String dn = ChatColor.translateAlternateColorCodes('&', "&e" + getWeaponName(config));
		try {
			String tmp = config.getString("Item.DisplayName", dn);
			dn = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dn));
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		
		gun.setItemMeta(meta);

		gun = ItemUtil.addTags(gun, "GGWeapon", true);
		gun = ItemUtil.addTags(gun, "GGWeaponType", "GUN");
		gun = ItemUtil.addTags(gun, "GGGunName", getWeaponName(config));
		gun = GunItemUtil.setCoolingDown(gun, false);
		gun = GunItemUtil.setReloading(gun, false);
		gun = GunItemUtil.updateRemainingShots(gun, getGunCapacity(config));
		
		NBTItem nbti = new NBTItem(gun);
		NBTList attribute = nbti.getList("AttributeModifiers", NBTType.NBTTagCompound);
		
		if(getWeight(config) > 5.0) {
			Double weight = (getWeight(config) / 25) * -1.0D;
			
			NBTListCompound modifierWeight1 = attribute.addCompound();
			modifierWeight1.setDouble("Amount", weight);
			modifierWeight1.setString("AttributeName", "generic.movementSpeed");
			modifierWeight1.setString("Name", "generic.movementSpeed");
			modifierWeight1.setInteger("Operation", 1);
			modifierWeight1.setString("Slot", "mainhand");
			modifierWeight1.setInteger("UUIDLeast", 690544);
			modifierWeight1.setInteger("UUIDMost", 369471);
			
			NBTListCompound modifierWeight2 = attribute.addCompound();
			modifierWeight2.setDouble("Amount", weight);
			modifierWeight2.setString("AttributeName", "generic.movementSpeed");
			modifierWeight2.setString("Name", "generic.movementSpeed");
			modifierWeight2.setInteger("Operation", 1);
			modifierWeight2.setString("Slot", "offhand");
			modifierWeight2.setInteger("UUIDLeast", 690544);
			modifierWeight2.setInteger("UUIDMost", 369471);
		}
		
		NBTListCompound modifierDamage1 = attribute.addCompound();
		modifierDamage1.setDouble("Amount", getMeleeDamage(config));
		modifierDamage1.setString("AttributeName", "generic.attackDamage");
		modifierDamage1.setString("Name", "generic.attackDamage");
		modifierDamage1.setInteger("Operation", 0);
		modifierDamage1.setString("Slot", "mainhand");
		modifierDamage1.setInteger("UUIDLeast", 720699);
		modifierDamage1.setInteger("UUIDMost", 611507);
		
		NBTListCompound modifierDamage2 = attribute.addCompound();
		modifierDamage2.setDouble("Amount", getMeleeDamage(config));
		modifierDamage2.setString("AttributeName", "generic.attackDamage");
		modifierDamage2.setString("Name", "generic.attackDamage");
		modifierDamage2.setInteger("Operation", 0);
		modifierDamage2.setString("Slot", "offhand");
		modifierDamage2.setInteger("UUIDLeast", 720699);
		modifierDamage2.setInteger("UUIDMost", 611507);
		
		gun = nbti.getItem();		
		
		return gun;
	}
	public Ammo getGunAmmo(FileConfiguration config) {
		Ammo ammo = null;
		try {
			String temp = config.getString("AmmoConfig.Ammo");
			ammo = this.manager.getAmmo(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return ammo;
	}
	public Integer getGunCapacity(FileConfiguration config) {
		Integer cap = 0;
		try {
			Integer temp = config.getInt("AmmoConfig.MaxAmmo");
			cap = temp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return cap;
	}
	public long getReloadDuration(FileConfiguration config) {
		long dur = 0l;
		try {
			long tmp = config.getLong("ReloadTime");
			dur = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dur;
	}
	public Boolean getUsePermissionEnabled(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean b2 = config.getBoolean("Permission.Enabled");
			b = b2;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public String getPermission(FileConfiguration config) {
		String perm = null;
		try {
			String s = config.getString("Permission.Node");
			perm = s;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return perm;
	}
	public long getShootDelay(FileConfiguration config) {
		long del = 0l;
		try {
			long tmp = config.getLong("ShootDelay");
			del = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return del;
	}
	public Double getShootingForce(FileConfiguration config) {
		Double force = 1.0;
		try {
			Double tmp = config.getDouble("ShootingForce");
			force = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return force;
	}
	public long getLaserRayIterationDelay(FileConfiguration config) {
		long d = (long)-1;
		try {
			long tmp = config.getLong("Effects.PlasmaTrail.RayIterationDelay");
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public Boolean getScopeEnabled(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Effects.Scope.Enabled");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Boolean getMayHolderMoveWhileAiming(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Effects.Scope.MoveWhileZooming");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Double getShotDamage(FileConfiguration config) {
		Double dam = 0.0D;
		try {
			Double tmp = config.getDouble("Damage");
			dam = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dam;
	}
	public float getAccuracy(FileConfiguration config) {
		float acc = 1.0f;
		try {
			float tmp = Float.parseFloat(config.getString("ProjectileSpread"));
			acc = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return acc;
	}
	public Boolean getAkimboAllowed(FileConfiguration config) {
		Boolean a = false;
		try {
			Boolean tmp = config.getBoolean("AllowAkimbo");
			a = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return a;
	}
	public Boolean getRealisticVisualsEnabled(FileConfiguration config) {
		Boolean en = false;
		try {
			Boolean tmp = config.getBoolean("BetterVisuals.Enabled");
			en = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return en;
	}
	public ItemStack getRealisticVisualsItem(FileConfiguration config) {
		Integer damage = 0;
		try {
			Integer tmp = config.getInt("BetterVisuals.BowDamage");
			damage = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		@SuppressWarnings("deprecation")
		ItemStack bow = new ItemStack(Material.BOW, 1, damage.shortValue());
		return bow;
	}
	public float getRocketExplosionDamage(FileConfiguration weaponFile) {
		float dam = 0;
		try {
			float tmp = Float.parseFloat(weaponFile.getString("RocketLauncher.Explosion.Damage"));
			dam = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dam;
	}
	public Boolean getRocketBreakBlocks(FileConfiguration config) {
		Boolean bb = false;
		try {
			Boolean tmp = config.getBoolean("RocketLauncher.Explosion.BreakBlocks");
			bb = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return bb;
	}
	public Boolean getRocketCreateFire(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean tmp = config.getBoolean("RocketLauncher.Explosion.CreateFire");
			f = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public Integer getRocketExplosionRadius(FileConfiguration config) {
		Integer r = 0;
		try {
			Integer tmp = config.getInt("RocketLauncher.Explosion.Radius");
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}
	public Boolean getRocketNoDamage(FileConfiguration config) {
		Boolean n = false;
		try {
			Boolean tmp = config.getBoolean("RocketLauncher.NoDamage");
			n = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return n;
	}
    public Boolean getRocketSeekingHeat(FileConfiguration config) {
		Boolean hs = false;
		try {
			Boolean tmp = config.getBoolean("RocketLauncher.HeatSeeking");
			hs = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return hs;
	}
	public Double getRecoil(FileConfiguration weaponFile) {
		Double r = 0.0;
		try {
			Double tmp = weaponFile.getDouble("Recoil.Amount");
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}

	public float getPullUpwardAmount(FileConfiguration weaponFile) {
		float r = 0;
		try {
			float tmp = Float.parseFloat(weaponFile.getString("Recoil.PullUpwardAmount"));
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}
	public Integer getProjectileAmount(FileConfiguration weaponFile) {
		Integer s = 1;
		try {
			Integer tmp = weaponFile.getInt("ShotProjectileAmount");
			s = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}
	public Integer getVolleyShotAmount(FileConfiguration weaponFile) {
		Integer amount = 1;
		try {
			Integer tmp = weaponFile.getInt("FireVolleyAmount");
			amount = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return amount;
	}
	public String getShootingParticle(FileConfiguration config) {
		String prt = null;
		try {
			String tmp = config.getString("Effects.Particles.Shoot");
			prt = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return prt;
	}
	public String getBulletHitParticle(FileConfiguration config) {
		String prt = null;
		try {
			String tmp = config.getString("Effects.Particles.BulletHit");
			prt = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return prt;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public ProjectileType getAmmoType(FileConfiguration config) {
		ProjectileType type = ProjectileType.SNOWBALL;
		try {
			String temp = config.getString("Type");
			type = ProjectileType.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return type;
	}
	public Integer getShotCount(FileConfiguration config) {
		Integer sc = 0;
		try {
			Integer tmp = config.getInt("ShotCount");
			sc = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return sc;
	}
	public ItemStack getAmmoItem(FileConfiguration config) {
		Material mat = null;
		try {
			String temp = config.getString("Item.Material", GunGamePlugin.instance.serverPre113 ? "SEEDS" : "WHEAT_SEEDS");
			mat = Material.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		short damage = 0;
		try {
			String temp = config.getString("Item.Damage");
			damage = Short.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		@SuppressWarnings("deprecation")
		ItemStack ammo = new ItemStack(mat,1,(short)damage);
		
		ItemMeta meta = ammo.getItemMeta();
		
		String dn = ChatColor.translateAlternateColorCodes('&', "&e" + getWeaponName(config));
		try {
			String tmp = config.getString("Item.DisplayName");
			dn = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dn));
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		
		ammo.setItemMeta(meta);
		
		ammo = ItemUtil.addTags(ammo, "GGAmmo", true);
		ammo = ItemUtil.addTags(ammo, "GGAmmoName", getWeaponName(config));
		ammo = ItemUtil.addTags(ammo, "GG_Ammo_ContainingShots", config.getInt("ShotCount"));
		
		return ammo;
	}
	
	
	
	
	
	
	public ItemStack getAirstrikeItem(FileConfiguration config) {
		Material mat = null;
		if(GunGamePlugin.instance.serverPre113) {
			mat = Material.valueOf("REDSTONE_TORCH_ON");
		} else {
			mat = Material.valueOf("REDSTONE_TORCH");
		}
	
		short dmg = 0;
		try {
			String tmp = config.getString("Item.Damage");
			dmg = Short.valueOf(tmp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		@SuppressWarnings("deprecation")
		ItemStack strike = new ItemStack(mat, 1, dmg);
		
		ItemMeta meta = strike.getItemMeta();
		
		String dn = ChatColor.translateAlternateColorCodes('&', "&e" + getWeaponName(config));
		try {
			String tmp = config.getString("Item.DisplayName");
			dn = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dn));
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		
		strike.setItemMeta(meta);
		
		strike = ItemUtil.setGunGameItem(strike);
		
		strike = ItemUtil.addTags(strike, "GGAirStrike", true);
		strike = ItemUtil.addTags(strike, "GGAirstrikeName", getWeaponName(config));
		
		return strike;
	}
	public Integer getAirStrikeBombCount(FileConfiguration config) {
		Integer b = 1;
		try {
			Integer tmp = config.getInt("Explosion.BombCount");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Boolean getAirstrikeExplosionBreakBlocks(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.BreakBlocks");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	} 
	public Boolean getAirstrikePhysicsEnabled(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.Physics");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	} 
	public Boolean getAirstrikeExplosionPlaceFire(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.SpreadFire");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Boolean getAirstrikeExplosionDamage(FileConfiguration config) {
		Boolean b = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.NoDamage");
			b = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public Integer getAirstrikeFuseTicks(FileConfiguration config) {
		Integer t = 40;
		try {
			Integer tmp = config.getInt("Explosion.FuseTicks");
			t = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return t;
	}
	public float getAirstrikeExplosionPower(FileConfiguration config) {
		float d = 1;
		try {
			float tmp = Float.valueOf(config.getString("Explosion.Power"));
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public Integer getAirstrikeExplosionRadius(FileConfiguration config) {
		Integer r = 5;
		try {
			Integer tmp = config.getInt("Explosion.Radius");
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}
	public Integer getAirstrikeDropRadius(FileConfiguration config) {
		Integer r = 20;
		try {
			Integer tmp = config.getInt("Explosion.DropRadius");
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}
	

	

	

	
	
	public Material getLandmineBlockMaterial(FileConfiguration config) {
		Material mat = null;
		if(GunGamePlugin.instance.serverPre113) {
			mat = Material.valueOf("IRON_PLATE");
		} else {
			mat = Material.valueOf("HEAVY_WEIGHTED_PRESSURE_PLATE");
		}
		try {
			String s = config.getString("Block.Material");
			mat = Material.valueOf(s);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return mat;
	}
	public DyeColor getLandmineBlockCarpetColor(FileConfiguration config) {
		DyeColor clr = DyeColor.WHITE;
		try {
			String tmp = config.getString("Block.CarpetColor");
			clr = DyeColor.valueOf(tmp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return clr;
	}
	public float getLandmineExplosionPower(FileConfiguration config) {
		float p = 1;
		try {
			Float tmp = Float.valueOf(config.getString("Explosion.Power"));
			p = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return p;
	}
	public Integer getLandmineExplosionRadius(FileConfiguration config) {
		Integer r = 3;
		try {
			Integer tmp = config.getInt("Explosion.Radius");
			r = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}
	public Boolean getLandmineExplosionIncendiary(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.Incendiary");
			f = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public Boolean getLandmineExplosionBreakBlocks(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.BreakBlocks");
			f = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public Boolean getLandmineExplosionNoDamage(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.NoDamage");
			f = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public Integer getLandmineSmokeDensity(FileConfiguration config) {
		Integer d = 1;
		try {
			Integer tmp = config.getInt("Explosion.Smoke.Density");
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public Integer getLandmineSmokeDuration(FileConfiguration config) {
		Integer d = 40;
		try {
			Integer tmp = config.getInt("Explosion.Smoke.Duration");
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public PotionEffect getLandminePoisonEffect(FileConfiguration config) {
		PotionEffectType type = PotionEffectType.CONFUSION;
		Integer dur = 10;
		Integer ampl = 1;
		try {
			PotionEffectType tmpT = PotionEffectType.getByName(config.getString("Explosion.Potion.Effect"));
			Integer tmpD = config.getInt("Explosion.Potion.Duration");
			Integer tmpA = config.getInt("Explosion.Potion.Amplifier");
			
			type = tmpT;
			dur = tmpD;
			ampl = tmpA;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		PotionEffect pe = new PotionEffect(type, dur, ampl);
		return pe;
	}
	public Integer getLandmineFireDuration(FileConfiguration config) {
		Integer d = 40;
		try {
			Integer tmp = config.getInt("Explosion.Fire.Duration");
			d = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	public LandmineType getLandmineExplosionType(FileConfiguration config) {
		LandmineType type = LandmineType.EXPLOSIVE;
		try {
			String tmp = config.getString("Explosion.Type");
			type = LandmineType.valueOf(tmp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return type;
	}
	public Double getLandmineBearTrapDamage(FileConfiguration config) {
		Double dmg = 1.0D;
		try {
			Double tmp = config.getDouble("Explosion.BearTrap.Damage");
			dmg = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dmg;
	}
	public Integer getLandmineBearTrapEffectDuration(FileConfiguration config) {
		Integer dur = 40;
		try {
			Integer tmp = config.getInt("Explosion.BearTrap.Effect.Duration");
			dur = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dur;
	}
	public Integer getLandmineBearTrapEffectAmplifier(FileConfiguration config) {
		Integer dur = 40;
		try {
			Integer tmp = config.getInt("Explosion.BearTrap.Effect.Amplifier");
			dur = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return dur;
	}

	public Boolean getLandmineExplosionPhysics(FileConfiguration config) {
		Boolean f = false;
		try {
			Boolean tmp = config.getBoolean("Explosion.Physics");
			f = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return f;
	}
	public ItemStack getLandmineItem(FileConfiguration config) {
		Material m = null;
		if(GunGamePlugin.instance.serverPre113) {
			m = Material.valueOf("IRON_PLATE");
		} else {
			m = Material.valueOf("HEAVY_WEIGHTED_PRESSURE_PLATE");
		}
		try {
			String s = config.getString("Item.Material");
			Material tmp = Material.valueOf(s);
			m = tmp;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		short damage = 0;
		try {
			String temp = config.getString("Item.Damage");
			damage = Short.valueOf(temp);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		@SuppressWarnings("deprecation")
		ItemStack mine = new ItemStack(m, 1, damage);
		
		return mine;
	}

	
}
