package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.API.TurretEvents.TurretDeathEvent;
import de.MrBaumeister98.GunGame.API.TurretEvents.TurretDismountEvent;
import de.MrBaumeister98.GunGame.API.TurretEvents.TurretMountEvent;
import de.MrBaumeister98.GunGame.API.TurretEvents.TurretShootEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import net.md_5.bungee.api.ChatColor;

public class Turret {

	public UUID turretID;
	public ArmorStand hitbox;
	private Double health;
	private Double temperature;
	public Location position;
	private Location spawnLoc;
	
	public TurretManager manager;
	public TurretConfig config;
	public TurretShootController turretShooter;
	public TurretCooler cooler;
	public TurretReloader reloader;
	private Boolean isDead;
	
	private int showBarTaskID;
	private int entityCheckerTaskID;
	
	private DecimalFormat df = new DecimalFormat("######.#");
	
	private Player gunner;
	private Integer magazine;
	private float startYaw;
	
	private Boolean isShooting;
	private Boolean mayShoot;
	private Boolean isReloading;
	
	public Turret(Location position, float rotation, TurretConfig config) {
		this.isDead = false;
		this.spawnLoc = position;
		this.manager = GunGamePlugin.instance.turretManager;
		this.isShooting = false;
		this.mayShoot = true;
		
		this.config = config;
		this.position = position.getBlock().getLocation().add(0.5, 0.0, 0.5);
		
		if(!position.getChunk().isLoaded()) {
			position.getWorld().loadChunk(position.getChunk());
		}
		
		ArmorStand as = (ArmorStand) position.getWorld().spawnEntity(position.getBlock().getLocation().add(0.5, -0.6, 0.5), EntityType.ARMOR_STAND);
		as.getLocation().setYaw(rotation);
		as.setGravity(false);
		as.setCollidable(true);
		as.setBasePlate(false);
		as.setVisible(false);
		//as.setPersistent(true);
		
		as.setMetadata("GG_Turret", new FixedMetadataValue(GunGamePlugin.instance, true));
		as.setHelmet(this.config.getGunItem());
		as.setHeadPose(new EulerAngle(0.0, Math.toRadians(rotation), 0.0));
		this.hitbox = as;
		
		UUID id = as.getUniqueId();
		
		this.manager.getTurretById.put(id, this);
		this.turretID = id;
		
		setHealth(this.config.getMaxHealth().doubleValue());
		setTemperature(0.0);
		setMagazine(this.config.getMagazineSize());
		
		this.reloader = new TurretReloader(this);
		this.turretShooter = new TurretShootController(this);
		this.cooler = new TurretCooler(this);
		
		this.hitbox.setCustomName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "HP: " + ChatColor.RED + df.format(this.health) + ChatColor.GREEN + "/" + this.config.getMaxHealth().toString() + ChatColor.GRAY + "]");
		this.hitbox.setCustomNameVisible(false);
		
		if(GunGamePlugin.instance.serverPre113) {
			position.getBlock().setType(Material.valueOf("IRON_FENCE"));
		} else {
			position.getBlock().setType(Material.valueOf("IRON_BARS"));
		}
		this.entityCheckerTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				//if(hitbox == null) {
					hitbox = (ArmorStand) Bukkit.getEntity(turretID);
				//}
			}
		}, 0, 1);
	}
	
	
	public void mount(Player p) {
		TurretMountEvent mountevent = new TurretMountEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(mountevent);
		if(!mountevent.isCancelled()) {
			Vector pD = p.getLocation().getDirection();
			float pyaw = p.getLocation().getYaw();
			float ppitch = p.getLocation().getPitch();
			
			Vector v = p.getLocation().getDirection().normalize().setY(0).multiply(-1.25D);
			p.teleport(new Location(this.position.getWorld(), this.position.getX() + v.getX(), this.position.getY() + v.getY(), this.position.getZ() + v.getZ()));
			Location tmp = p.getLocation();
			tmp = tmp.setDirection(pD);
			tmp.setYaw(pyaw);
			tmp.setPitch(ppitch);
			p.teleport(tmp);
			p.setMetadata("GG_isTurretRider", new FixedMetadataValue(GunGamePlugin.instance, true));
			p.setMetadata("GG_TurretID", new FixedMetadataValue(GunGamePlugin.instance, this.turretID.toString()));
			this.startYaw = p.getLocation().getYaw();
			setGunner(p);
			adjustHeadPoseToPlayer(p);
			this.config.getSoundSet().equipSound.play(this.position.getWorld(), this.position);
			this.manager.plugin.weaponManager.visualHelper.sendTurretStatus(p, this);
			this.hitbox.setCustomNameVisible(true);
		}
	}
	public void forceDismount() {
		setGunner(null);
		this.hitbox.setCustomNameVisible(false);
	}
	public void disMount(Player p) {
		TurretDismountEvent dismountevent = new TurretDismountEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(dismountevent);
		setGunner(null);
		if(p != null) {
			p.removeMetadata("GG_isTurretRider", GunGamePlugin.instance);
		}
		this.config.getSoundSet().equipSound.play(this.position.getWorld(), this.position);
		this.hitbox.setCustomNameVisible(false);
	}
	public void takeDamage(Double damage) {
		damage = damage * 0.5D;
		if(getHealth() <= damage && !isDead) {
			this.isDead = true;
			this.hitbox.setCustomName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "HP: " + ChatColor.RED + df.format(this.health) + ChatColor.GREEN + "/" + this.config.getMaxHealth().toString() + ChatColor.GRAY + "]");
			die();
		} else if(!isDead) {
			setHealth(getHealth() - damage);
			this.config.getSoundSet().bullethitSound.play(this.position.getWorld(), this.position);
			this.hitbox.setCustomName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "HP: " + ChatColor.RED + df.format(this.health) + ChatColor.GREEN + "/" + this.config.getMaxHealth().toString() + ChatColor.GRAY + "]");
			
			this.hitbox.setCustomNameVisible(true);
			Turret turr = this;
			if(Bukkit.getScheduler().isQueued(this.showBarTaskID)) {
				Bukkit.getScheduler().cancelTask(this.showBarTaskID);
			}
			if(this.gunner == null) {
				this.showBarTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						turr.hitbox.setCustomNameVisible(false);
					}
				}, 30);
			}
		}
	}
	public void die() {
		Bukkit.getScheduler().cancelTask(this.entityCheckerTaskID);
		TurretDeathEvent deathevent = new TurretDeathEvent(this);
		Bukkit.getServer().getPluginManager().callEvent(deathevent);
		//DIE
		disMount(this.gunner);
		setHealth(0.0);
		this.manager.removeTurret(this);
		if(this.isShooting) {
			this.isShooting = false;
			this.turretShooter.stopShooting();
			//STOP SHOOTING
		} else {
			//if(this.config.canOverheat()) {
				this.cooler.stopCooling();
			//}
		}
		this.hitbox.remove();
		try {
			Util.createExplosion(this.position, false, false, true, false, 0.5f, null, 1, false);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		//this.config.getSoundSet().explodeSound.play(this.position.getWorld(), this.position);
	}
	public void remove() {
		if(this.gunner != null && this.gunner.isOnline()) {
			disMount(this.gunner);
		}
		setHealth(0.0);
		if(this.isShooting) {
			this.isShooting = false;
			this.turretShooter.stopShooting();
			//STOP SHOOTING
		} else {
			this.cooler.stopCooling();
		}
		this.hitbox.remove();
	}
	public void adjustHeadPoseToPlayer(Player p) {
		if(isAngleAllowed(p.getLocation().getYaw())) {
			this.hitbox.getLocation().setPitch(p.getLocation().getPitch());
			this.hitbox.getLocation().setYaw(p.getLocation().getYaw());
			
			this.hitbox.getLocation().setDirection(p.getLocation().getDirection());
			
			Location tmp1 = new Location(this.hitbox.getWorld(), this.hitbox.getLocation().getX(), this.hitbox.getLocation().getY(), this.hitbox.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
			this.hitbox.teleport(tmp1);
					
			this.hitbox.getEyeLocation().setPitch(p.getEyeLocation().getPitch());
			this.hitbox.getEyeLocation().setYaw(p.getEyeLocation().getYaw());
			
			this.hitbox.getEyeLocation().setDirection(p.getEyeLocation().getDirection());
			
			this.hitbox.setHeadPose(new EulerAngle(Math.toRadians(p.getLocation().getPitch()), 0.0, 0.0));
		}
		
	}
	public void changeYaw(float yaw) {
		Location tmp1 = new Location(this.hitbox.getWorld(), this.hitbox.getLocation().getX(), this.hitbox.getLocation().getY(), this.hitbox.getLocation().getZ(), this.hitbox.getLocation().getYaw(), yaw);
		this.hitbox.teleport(tmp1);
	}
	public void toggleShooting() {
		if(this.isShooting) {
			this.isShooting = false;
			
			this.turretShooter.stopShooting();
			
			this.cooler.startCooling();
		} else if(!this.isShooting) {
			this.isShooting = true;
			
			this.turretShooter.startShooting();
			
			this.cooler.stopCooling();
		}
	}
	private Boolean isAngleAllowed(float newYaw) {
		if(newYaw >= (this.startYaw -90.0f) && newYaw <= (this.startYaw +90.0f)) {
			return true;
		}
		return false;
	}
	public void fireShot() {
		
		this.setReloading(false);
		
		if(this.mayShoot && this.gunner != null && this.magazine > 0) {
			this.config.getSoundSet().shootSound.play(this.position.getWorld(), this.position);
			this.magazine--;
			heatUp();
			this.manager.plugin.weaponManager.visualHelper.sendTurretStatus(this.gunner, this);
			
			Vector v = null;
			if(isAngleAllowed(this.gunner.getLocation().getYaw())) {
				v = this.gunner.getEyeLocation().getDirection();
			} else {
				v = this.hitbox.getLocation().getDirection();
			}
			if(this.config.getAccuracy() > 0) {
				float accuracy = this.config.getAccuracy();
				v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
			}
			v = v.normalize();
			v = v.multiply(this.config.getShootForce());
			
			Projectile bullet = null;
			
			switch(this.config.getBulletType()) {
			case DEFAULT:
				bullet = this.hitbox.launchProjectile(Arrow.class, v);
				bullet.setInvulnerable(true);
				bullet.setBounce(false);
				bullet.setSilent(true);
				bullet.setShooter(this.gunner);
				bullet.setVelocity(v);
				((Arrow)bullet).setKnockbackStrength(0);
				((Arrow)bullet).setPickupStatus(PickupStatus.DISALLOWED);
				((Arrow)bullet).setVelocity(v);
				break;
			case EXPLOSIVE:
				bullet = this.hitbox.launchProjectile(Arrow.class, v);
				bullet.setInvulnerable(true);
				bullet.setBounce(false);
				bullet.setSilent(true);
				bullet.setShooter(this.gunner);
				bullet.setVelocity(v);
				bullet.setMetadata("GG_TurretShell_HE", new FixedMetadataValue(GunGamePlugin.instance, true));
				((Arrow)bullet).setKnockbackStrength(0);
				((Arrow)bullet).setPickupStatus(PickupStatus.DISALLOWED);
				((Arrow)bullet).setVelocity(v);
				break;
			case FIREBALL:
				bullet = this.hitbox.launchProjectile(SmallFireball.class, v);
				bullet.setInvulnerable(true);
				bullet.setBounce(false);
				bullet.setSilent(true);
				bullet.setShooter(this.gunner);
				bullet.setVelocity(v);
				((SmallFireball)bullet).setIsIncendiary(true);
				((SmallFireball)bullet).setVelocity(v);
				((SmallFireball)bullet).setDirection(v);
				break;
			case FIREBALL_LARGE:
				bullet = this.hitbox.launchProjectile(Fireball.class, v);
				bullet.setInvulnerable(true);
				bullet.setBounce(false);
				bullet.setSilent(true);
				bullet.setShooter(this.gunner);
				bullet.setVelocity(v);
				((Fireball)bullet).setIsIncendiary(true);
				((Fireball)bullet).setVelocity(v);
				((Fireball)bullet).setDirection(v);
				break;
			case WITHERSKULL:
				bullet = this.hitbox.launchProjectile(WitherSkull.class, v);
				bullet.setInvulnerable(true);
				bullet.setBounce(false);
				bullet.setSilent(true);
				bullet.setShooter(this.gunner);
				bullet.setVelocity(v);
				((WitherSkull)bullet).setCharged(Util.getRandomBoolean());
				((WitherSkull)bullet).setIsIncendiary(false);
				((WitherSkull)bullet).setVelocity(v);
				((WitherSkull)bullet).setDirection(v);
				break;
			default:
				break;		
			}
			bullet.teleport(this.hitbox.getEyeLocation().add(v.normalize().multiply(1.0D)));
			bullet.setMetadata("GG_TurretShell", new FixedMetadataValue(GunGamePlugin.instance, true));
			bullet.setMetadata("GG_Turret", new FixedMetadataValue(GunGamePlugin.instance, this.turretID.toString()));
			
			TurretShootEvent shootevent = new TurretShootEvent(this, this.gunner, bullet);
			Bukkit.getServer().getPluginManager().callEvent(shootevent);
			
			this.mayShoot = false;
			Turret ref = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					ref.mayShoot = true;
				}
			}, this.config.getShootDelay().longValue());
		} else if(this.magazine <= 0) {
			this.config.getSoundSet().outOfAmmoSound.play(this.position.getWorld(), this.position);
			GunGamePlugin.instance.weaponManager.visualHelper.sendOutOfAmmo(this.gunner);
		}
		
	}
	public void heatUp() {
		if(this.config.canOverheat()) {
			if(getTemperature() >= this.config.getCriticalHeat()) {
				die();
			} else {
				setTemperature(getTemperature() + this.config.getHeatPerShot());
			}
		}
	}
	public void setShooting(Boolean shooting) {
		this.isShooting = shooting;
	}
	
	public void setReloading(Boolean reloading) {
		this.isReloading = reloading;
		
		if(this.isReloading) {
			//Start Reload Process
			this.reloader.startReload();
		} else {
			//Cancel Reload Process
			this.reloader.cancelReload();
		}
	}
	public String generateDataSaveString() {
		if(this.hitbox != null) {
			this.hitbox.getLocation().getChunk().load();
		}
		String data = this.config.name;
		data = data + "," + Util.locToString(this.spawnLoc);
		data = data + "," + this.hitbox.getLocation().getYaw();
		data = data + "," + this.health.toString();
		data = data + "," + this.magazine.toString();
		data = data + "," + this.temperature.toString();
		data = data + "," + ((Float)this.startYaw).toString();
		
		return data;
	}
	
	
	
	
	public Player getGunner() {
		return gunner;
	}
	public void setGunner(Player gunner) {
		this.gunner = gunner;
	}
	public Integer getMagazine() {
		return magazine;
	}
	public void setMagazine(Integer magazine) {
		this.magazine = magazine;
	}

	public Double getHealth() {
		return this.health;
	}

	public void setHealth(Double health) {
		this.health = health;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Boolean isReloading() {
		return isReloading;
	}


	public World getWorld() {
		return this.position.getWorld();
	}	
}
