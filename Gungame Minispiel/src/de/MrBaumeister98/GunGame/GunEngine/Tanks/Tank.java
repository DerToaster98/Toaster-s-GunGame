package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.API.TankEvents.TankDeathEvent;
import de.MrBaumeister98.GunGame.API.TankEvents.TankDismountEvent;
import de.MrBaumeister98.GunGame.API.TankEvents.TankShootEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.Game.Util.Math.VectorUtil;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.TankRocketThread;
import net.md_5.bungee.api.ChatColor;

public class Tank {
	//CORE VALUES
	private TankConfig config;
	private TankManager manager;
	private Boolean alive;
	private Boolean driving;
	private Double health;
	
	//ENTITIES OF THE TANK
	private ArmorStand barrelArmorStand;
	private ArmorStand turretArmorStand;
	private ArmorStand bodyArmorStand;	
	//private ArmorStand placeHolder;
	private ArmorStand seat;
	private Entity[] entities;
	private UUID[] entityIDs;
	
	//MISCS
	private int showBarTaskID;	
	private DecimalFormat df = new DecimalFormat("######.#");
	
	//REFERENCE IDs
	private UUID driverUUID;
	private UUID tankID;
		
	//GUN STUFF
	private Boolean reloading;	
	private Boolean mayShoot;
	private Integer magazine;
	private int reloadTaskID;
	private int delayTaskID;
	
	//BEHAVIOR CONTROLERS
	private TankHitboxChecker hitboxChecker;
	private TankMover tankmover;
	private TankIntegrityChecker integrityChecker;
	private TankSoundSet soundset;
	
	public Tank(Location loc, TankConfig config, TankManager manager) {
		this.reloading = false;
		this.mayShoot = true;
		loc.setPitch((float)0.0);
		loc.setYaw((float)0.0);
		this.manager = manager;
		this.config = config;
		this.setMagazine(this.config.getMagazineSize());
		this.health = this.config.getMaxHealth();
		
		if(!loc.getChunk().isLoaded()) {
			loc.getWorld().loadChunk(loc.getChunk());
		}
		
		ArmorStand asbd = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		asbd.setMarker(true);
		asbd.setAI(false);
		//asbd.setGravity(false);
		asbd.setVisible(false);
		asbd.setCollidable(true);
		asbd.setInvulnerable(false);
		asbd.setHelmet(this.config.getBodyItem());
		this.bodyArmorStand = asbd;
		
		ArmorStand asbr = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		asbr.setMarker(false);
		asbr.setAI(false);
		//asbr.setGravity(false);
		asbr.setVisible(false);
		asbr.setCollidable(true);
		asbr.setInvulnerable(false);
		asbr.setHelmet(this.config.getBarrelItem());
		this.barrelArmorStand = asbr;
		
		ArmorStand astr = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		astr.setMarker(true);
		astr.setAI(false);
		astr.setVisible(false);
		//astr.setGravity(false);
		astr.setInvulnerable(false);
		astr.setCollidable(true);
		astr.setHelmet(this.config.getTurretItem());
		this.turretArmorStand = astr;
		
		ArmorStand asph = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		asph.setMarker(false);
		asph.setAI(false);
		asph.setInvulnerable(false);
		//asph.setGravity(false);
		asph.setVisible(false);
		asph.setCollidable(true);
		//this.setPlaceHolder(asph);
		this.setSeat(asph);
		
		/*Minecart ass = (Minecart) loc.getWorld().spawnEntity(loc, EntityType.MINECART);
		ass.setInvulnerable(true);
		ass.setPersistent(true);
		this.setSeat(ass);*/
		
		
		asbd.addPassenger(astr);
		astr.addPassenger(asbr);
		asbr.addPassenger(asph);
		//asph.addPassenger(ass);
		
		
		this.setTankID(asbd.getUniqueId());
		
		this.bodyArmorStand.setMetadata("GG_Tank", new FixedMetadataValue(GunGamePlugin.instance, this.getTankID().toString()));
		this.turretArmorStand.setMetadata("GG_Tank", new FixedMetadataValue(GunGamePlugin.instance, this.getTankID().toString()));
		this.barrelArmorStand.setMetadata("GG_Tank", new FixedMetadataValue(GunGamePlugin.instance, this.getTankID().toString()));
		//this.placeHolder.setMetadata("GG_Tank", new FixedMetadataValue(GunGamePlugin.instance, this.getTankID().toString()));
		this.seat.setMetadata("GG_Tank", new FixedMetadataValue(GunGamePlugin.instance, this.getTankID().toString()));
		
		this.setEntities(new Entity[] {(Entity)asbd, (Entity)astr, (Entity)asbr, (Entity)asph/*, (Entity)ass*/});
		this.setEntityIDs(new UUID[] {asbd.getUniqueId(), astr.getUniqueId(), asbr.getUniqueId(), asph.getUniqueId()});
		
		this.setDriverUUID(null);
		this.setReloading(false);
		
		this.alive = true;
		this.setDriving(false);
		
		
		this.manager.addTankEntity(this);
		
		this.driving = false;
		
		if(this.config.getSoundSet() != null) {
			this.setSoundset(this.config.getSoundSet().clone());
		} else {
			this.setSoundset(null);
		}
		
		TankMover mover = new TankMover(this);
		this.tankmover = mover;
		this.tankmover.start();
		
		TankHitboxChecker hbt = new TankHitboxChecker(this);
		this.setHitboxChecker(hbt);
		this.getHitboxChecker().start();
		
		TankIntegrityChecker tic = new TankIntegrityChecker(this);
		this.setIntegrityChecker(tic);
		this.getIntegrityChecker().start();
	}
	
	public void takeDamage(Double damage) {
		if(this.soundset != null) {
			this.soundset.takeDamageSound.play(this.getWorld(), this.getTankPos());
		}
		if(damage*0.5 >= this.health && this.alive) {
			//DIE
			this.alive = false;
			if(this.seat != null && !this.seat.isDead()) {
				this.seat.setCustomName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "HP: " + ChatColor.RED + df.format(this.health) + ChatColor.GREEN + "/" + this.config.getMaxHealth().toString() + ChatColor.GRAY + "]");
			}
			this.die();
		} else if(this.alive) {
			this.health -= (damage *0.5);
			
			if(this.seat != null && !this.seat.isDead()) {
				this.seat.setCustomName(ChatColor.GRAY + "[" + ChatColor.YELLOW + "HP: " + ChatColor.RED + df.format(this.health) + ChatColor.GREEN + "/" + this.config.getMaxHealth().toString() + ChatColor.GRAY + "]");
				this.seat.setCustomNameVisible(true);
			}
			
			Tank turr = this;
			if(Bukkit.getScheduler().isQueued(this.showBarTaskID)) {
				Bukkit.getScheduler().cancelTask(this.showBarTaskID);
			}
			if(this.driverUUID == null) {
				this.showBarTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						if(turr.seat != null && !turr.seat.isDead()) {
							turr.seat.setCustomNameVisible(false);
						}
					}
				}, 30);
			}
		}
	}
	public void die() {
		
		TankDeathEvent deathevent = new TankDeathEvent(this);
		Bukkit.getServer().getPluginManager().callEvent(deathevent);
		
		this.hitboxChecker.stoprun();
		this.tankmover.stopRun();
		this.integrityChecker.stoprun();
		
		this.seat.remove();
		//this.placeHolder.remove();
		
		this.manager.removeTank(this);
		
		if(this.soundset != null) {
			this.getSoundset().driveSound.stop();
			this.getSoundset().engineIdleSound.stop();
		}
		
		Bukkit.getScheduler().cancelTask(this.delayTaskID);
		Bukkit.getScheduler().cancelTask(this.reloadTaskID);
		
		Tank ref = this;
		try {
			Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					Util.createExplosion(ref.getTankPos(), Util.getRandomBoolean(), Util.getRandomBoolean(), false, false, 5.0F, ref.tankID, 3, false, 0);
				}
			});
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
								
					ref.bodyArmorStand.remove();
					ref.barrelArmorStand.remove();
					ref.turretArmorStand.remove();
					
				}
			}, 5);
		} catch(Exception ex) {
			this.bodyArmorStand.remove();
			this.barrelArmorStand.remove();
			this.turretArmorStand.remove();
		}
	}
	public void remove() {
		this.hitboxChecker.stoprun();
		this.tankmover.stopRun();
		
		//this.placeHolder.remove();
		
		if(this.soundset != null) {
			this.getSoundset().driveSound.stop();
			this.getSoundset().engineIdleSound.stop();
		}
		
		Bukkit.getScheduler().cancelTask(this.delayTaskID);
		Bukkit.getScheduler().cancelTask(this.reloadTaskID);
		
		try {
			this.seat.remove();
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		try {
			this.bodyArmorStand.remove();
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		try {
			this.barrelArmorStand.remove();
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		try {
			this.turretArmorStand.remove();
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	
	public void mount(Player p) {
		if(this.driverUUID == null && this.alive) {
			this.seat.addPassenger(p);
			this.driverUUID = p.getUniqueId();
		}
	}
	public void disMount(Player p) {
		
		TankDismountEvent dismountevent = new TankDismountEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(dismountevent);
		
		this.driverUUID = null;
		try {
			this.seat.removePassenger(p);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void transmitMovementParameters(Boolean w, Boolean s, Boolean a, Boolean d) {
		this.tankmover.setMoveMode(w, s);
		this.tankmover.setTrackStates(a, d);
	}
	public void adjustHeads(Float angle) {
		double angl = Math.toRadians(angle.doubleValue());
		this.bodyArmorStand.setHeadPose(new EulerAngle(this.bodyArmorStand.getHeadPose().getX(), /*this.bodyArmorStand.getHeadPose().getY() + */angl, this.bodyArmorStand.getHeadPose().getZ()));
		this.seat.getLocation().setYaw(angle);
		if(this.driverUUID != null && Bukkit.getEntity(this.driverUUID) != null) {
			Entity driver = Bukkit.getEntity(this.driverUUID);
			driver.getLocation().setYaw(angle);
		}
	}
	public void adjustTurret(Float angleHorizontal, Float angleVertical) {
		double ah = Math.toRadians(angleHorizontal.doubleValue());
		double av = Math.toRadians(angleVertical.doubleValue());
		
		this.turretArmorStand.setHeadPose(new EulerAngle(this.turretArmorStand.getHeadPose().getX(), ah, this.turretArmorStand.getHeadPose().getZ()));
		if(angleVertical <= this.config.getMinBarrelAngle() && angleVertical >= this.config.getMaxBarrelAngle()) {
			this.barrelArmorStand.setHeadPose(new EulerAngle(av, this.turretArmorStand.getHeadPose().getY(), this.barrelArmorStand.getHeadPose().getZ()));
		} else {
			this.barrelArmorStand.setHeadPose(new EulerAngle(this.barrelArmorStand.getHeadPose().getX(), ah, this.barrelArmorStand.getHeadPose().getZ()));
		}
	}
	public void shoot() {
		Tank ref = this;
		Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				if(!ref.reloading && ref.mayShoot) {
					Location shootPoint = ref.getTankPos().clone().add(VectorUtil.rotateVectorAroundY(ref.config.getPathToShootPoint(),  Math.toDegrees(ref.barrelArmorStand.getHeadPose().getY())));
					if(ref.soundset != null) {
						ref.soundset.shootSound.play(ref.getWorld(), shootPoint);
					}
					Entity ent = null;
					Vector velocity = new Vector(0.0,0.0,1.0);
					velocity = VectorUtil.rotateVectorAroundY(velocity, Math.toDegrees(ref.barrelArmorStand.getHeadPose().getY()));
					velocity.setY(ref.barrelArmorStand.getHeadPose().getX() * -1.0);
					switch(ref.config.getProjectileType()) {
					case BULLET:
						Snowball bullet = (Snowball) ref.getWorld().spawnEntity(shootPoint, EntityType.SNOWBALL);
						bullet.setVelocity(velocity.multiply(ref.config.getShootingForce()));
						bullet.setBounce(false);
						bullet.setInvulnerable(false);
						if(ref.driverUUID != null) {
							bullet.setShooter((ProjectileSource)Bukkit.getEntity(ref.driverUUID));
						} else {
							bullet.setShooter((ProjectileSource)Bukkit.getEntity(ref.getTankID()));
						}
						bullet.setMetadata("GG_Tank_Bullet", new FixedMetadataValue(GunGamePlugin.instance, true));
						bullet.setMetadata("GG_Shooter_Tank_ID", new FixedMetadataValue(GunGamePlugin.instance, ref.getTankID().toString()));
						
						ent = (Entity)bullet;
						break;
					case BULLET_EXPLODING:
						Snowball bullet1 = (Snowball) ref.getWorld().spawnEntity(shootPoint, EntityType.SNOWBALL);
						bullet1.setVelocity(velocity.multiply(ref.config.getShootingForce()));
						bullet1.setBounce(false);
						bullet1.setInvulnerable(false);
						if(ref.driverUUID != null) {
							bullet1.setShooter((ProjectileSource)Bukkit.getEntity(ref.driverUUID));
						} else {
							bullet1.setShooter((ProjectileSource)Bukkit.getEntity(ref.getTankID()));
						}
						bullet1.setMetadata("GG_Tank_Bullet", new FixedMetadataValue(GunGamePlugin.instance, true));
						bullet1.setMetadata("GG_Tank_Bullet_Explosive", new FixedMetadataValue(GunGamePlugin.instance, true));
						bullet1.setMetadata("GG_Shooter_Tank_ID", new FixedMetadataValue(GunGamePlugin.instance, ref.getTankID().toString()));
						
						ent = (Entity)bullet1;
						break;
					case ROCKET:
						TankRocketThread rt2 = new TankRocketThread(ref, Bukkit.getPlayer(ref.driverUUID), velocity, shootPoint);
						rt2.run();
						
						ent = (Entity)rt2.getEntity();
						break;
					case TANK_SHELL:
						WitherSkull shell = (WitherSkull) ref.getWorld().spawnEntity(shootPoint, EntityType.WITHER_SKULL);
						shell.setGravity(true);
						shell.setDirection(velocity.multiply(ref.config.getShootingForce()));
						shell.setVelocity(velocity.multiply(ref.config.getShootingForce()));
						if(ref.driverUUID != null) {
							shell.setShooter((ProjectileSource) Bukkit.getEntity(ref.driverUUID));
						} else {
							shell.setShooter((ProjectileSource) Bukkit.getEntity(ref.getTankID()));
						}
						shell.setInvulnerable(true);
						shell.setBounce(false);
						shell.setMetadata("GG_Tank_Shell", new FixedMetadataValue(GunGamePlugin.instance, true));
						shell.setMetadata("GG_Shooter_Tank_ID", new FixedMetadataValue(GunGamePlugin.instance, ref.getTankID().toString()));
						
						ent = (Entity)shell;
						break;
					default:
						break;
					
					}
					
					ref.magazine--;
					ref.mayShoot = false;
					
					TankShootEvent shootevent = new TankShootEvent(ref, Bukkit.getPlayer(ref.getDriverUUID()), ent);
					Bukkit.getServer().getPluginManager().callEvent(shootevent);
					
					if(ref.magazine <= 0) {
						//RELOAD
						if(ref.soundset != null) {
							ref.soundset.reloadSound.play(ref.getWorld(), shootPoint);
						}
						ref.reloading = true;
						Tank rref = ref;
						ref.reloadTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
							
							@Override
							public void run() {
								rref.magazine = rref.config.getMagazineSize();
								rref.mayShoot = true;
								rref.reloading = false;
							}
						}, ref.getConfig().getReloadDuration());
					} else {
						//INIT COOLDOWN
						Tank rref = ref;
						ref.delayTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
							
							@Override
							public void run() {
								rref.mayShoot = true;
							}
						}, ref.getConfig().getShootDelay());
					}
				}
			}
		});
	}
	public String generateDataSaveString() {
		String data = this.config.name;
		data = data + "," + Util.locToString(this.getTankPos().add(0.0, 0.5, 0.0));
		data = data + "," + ((Float)this.tankmover.getTurnAngle()).toString();
		data = data + "," + this.health.toString();
		data = data + "," + this.magazine.toString();
		data = data + "," + ((Double)this.turretArmorStand.getHeadPose().getY()).toString();
		data = data + "," + ((Double)this.barrelArmorStand.getHeadPose().getX()).toString();
		
		return data;
	}

	public TankConfig getConfig() {
		return config;
	}

	public void setConfig(TankConfig config) {
		this.config = config;
	}

	public Boolean isAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	public Location getTankPos() {
		if(this.bodyArmorStand != null) {
			return this.bodyArmorStand.getLocation();
		}
		return null;
	}
	public ArmorStand getBodyArmorStand() {
		return this.bodyArmorStand;
	}

	public UUID getDriverUUID() {
		return driverUUID;
	}

	public void setDriverUUID(UUID driverUUID) {
		this.driverUUID = driverUUID;
	}

	public UUID getTankID() {
		return tankID;
	}

	public void setTankID(UUID tankID) {
		this.tankID = tankID;
	}

	public Boolean isReloading() {
		return reloading;
	}

	public void setReloading(Boolean reloading) {
		this.reloading = reloading;
	}

	public TankHitboxChecker getHitboxChecker() {
		return hitboxChecker;
	}

	public void setHitboxChecker(TankHitboxChecker hitboxChecker) {
		this.hitboxChecker = hitboxChecker;
	}

	public ArmorStand getSeat() {
		return this.seat;
	}

	public void setSeat(ArmorStand seat) {
		this.seat = seat;
	}

	/*public ArmorStand getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(ArmorStand placeHolder) {
		this.placeHolder = placeHolder;
	}*/

	public Boolean isDriving() {
		return driving;
	}

	public void setDriving(Boolean driving) {
		this.driving = driving;
	}

	public Integer getMagazine() {
		return magazine;
	}

	public void setMagazine(Integer magazine) {
		this.magazine = magazine;
	}
	public TankMover getTankMover() {
		return this.tankmover;
	}

	public TankSoundSet getSoundset() {
		return soundset;
	}

	public void setSoundset(TankSoundSet soundset) {
		this.soundset = soundset;
	}
	public World getWorld() {
		return this.getTankPos().getWorld();
	}

	public Entity[] getEntities() {
		return entities;
	}

	public void setEntities(Entity[] entities) {
		this.entities = entities;
	}
	/*public DamageSet getDamageSet() {
		return this.config.getDamageSet();
	}*/
	public Double getHealth() {
		return this.health;
	}
	public void setHealth(Double hp) {
		this.health = hp;
	}
	public ArmorStand getBarrelArmorStand() {
		return this.barrelArmorStand;
	}
	public ArmorStand getTurretArmorStand() {
		return this.turretArmorStand;
	}

	public TankIntegrityChecker getIntegrityChecker() {
		return integrityChecker;
	}

	public void setIntegrityChecker(TankIntegrityChecker integrityChecker) {
		this.integrityChecker = integrityChecker;
	}

	public UUID[] getEntityIDs() {
		return entityIDs;
	}

	public void setEntityIDs(UUID[] entityIDs) {
		this.entityIDs = entityIDs;
	}
	public void setBodyEntity(ArmorStand ent) {
		this.bodyArmorStand = ent;
	}
	public void setTurretEntity(ArmorStand ent) {
		this.turretArmorStand = ent;
	}
	public void setBarrelEntity(ArmorStand ent) {
		this.barrelArmorStand = ent;
	}
	public void setSeatEntity(ArmorStand ent) {
		this.seat = ent;
	}

}
