package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Gun;

public class TankHitboxChecker /*extends Thread*/ {
	
	private Tank tank;
	private TankConfig tankconfig;
	
	private int taskID;
	
	private Boolean running;
	
	public TankHitboxChecker(Tank tnk) {
		this.tank = tnk;
		this.tankconfig = this.tank.getConfig();
	}
	
	public void stoprun() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
	public void start() {
		this.running = true;
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				execute();
			}
		}, 0, 1);
	}
	
	//@Override
	public void /*run*/execute() {
		/*while*/if(this.running && this.tank.isAlive() && this.tank.getBodyArmorStand() != null) {
			
			try {
				Collection<Entity> inRangeList = this.tank.getTankPos().getWorld().getNearbyEntities(this.tank.getTankPos().add(new Vector(0.0, this.tankconfig.getHeightRadius(), 0.0)), this.tankconfig.getHitBoxHorizontalRadius(), this.tankconfig.getHeightRadius(), this.tankconfig.getHitBoxHorizontalRadius());
				if(inRangeList != null && !inRangeList.isEmpty()) {
					for(Entity ent : inRangeList) {
						if(ent instanceof WitherSkull) {
							if(ent.hasMetadata("GG_Tank_Shell")) {
								if(ent.hasMetadata("GG_Shooter_Tank_ID")) {
									UUID sTnkID = UUID.fromString(ent.getMetadata("GG_Shooter_Tank_ID").get(0).asString());
									if(!sTnkID.equals(this.tank.getTankID())) {
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												TankListener.createTankShellExplosion((Projectile)ent, true);
											}
										});
									}
								}
							}
						}
						if(ent instanceof Projectile) {
							Projectile proj = (Projectile) ent;
							if(proj.hasMetadata("GG_Projectile")) {
								UUID shooterID = UUID.fromString(proj.getMetadata("GG_Shooter").get(0).asString());
								if(this.tank.getDriverUUID() == null || !shooterID.equals(this.tank.getDriverUUID())) {
									Tank ref = this.tank;
									Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
										
										@Override
										public void run() {
											ProjectileHitEvent hitEvent = new ProjectileHitEvent((Projectile)ent, ref.getBodyArmorStand());
											Bukkit.getPluginManager().callEvent(hitEvent);
										}
									});
								}
							} else if(proj.hasMetadata("GG_Tank_Bullet")) {
								UUID shooterID = UUID.fromString(proj.getMetadata("GG_Shooter_Tank_ID").get(0).asString());
								if(this.tank.getDriverUUID() == null || !shooterID.equals(this.tank.getDriverUUID())) {
									if(proj.hasMetadata("GG_Tank_Bullet_Explosive")) {
										
										UUID tankID = UUID.fromString(proj.getMetadata("GG_Shooter_Tank_ID").get(0).asString());
										Tank tank2 = GunGamePlugin.instance.tankManager.getTankByID(tankID);
										
										TankConfig tc = tank2.getConfig();
										if(tc.isProjectileNoDamage()) {
											this.tank.takeDamage(0.0);
										} else {
											this.tank.takeDamage((double)(Double.valueOf(tc.getProjectileDamage()).floatValue() * tc.getProjectileExplosionPower()) /(double)2);
										}
										
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												TankListener.createTankShellExplosion(proj, true);
											}
										});
									} else {
										Tank ref = this.tank;
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												ProjectileHitEvent hitEvent = new ProjectileHitEvent((Projectile)ent, ref.getBodyArmorStand());
												Bukkit.getPluginManager().callEvent(hitEvent);
											}
										});
									}
								}
							}
							else {
								Tank ref = this.tank;
								Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
									
									@Override
									public void run() {
										ProjectileHitEvent hitEvent = new ProjectileHitEvent((Projectile)ent, ref.getBodyArmorStand());
										Bukkit.getPluginManager().callEvent(hitEvent);
									}
								});
							}
							
						}
						if(ent instanceof LivingEntity && this.tank.isDriving() && tank.getDriverUUID() != null) {
							LivingEntity damaged = (LivingEntity)ent;
							UUID entID = damaged.getUniqueId();
							if(!entID.equals(this.tank.getDriverUUID()) && !ent.hasMetadata("GG_Tank")) {
								try {
										Double force = 0.5D + Math.abs(this.tank.getTankMover().getCurrentSpeed());
										Location entLoc = damaged.getLocation();
										Location tankLoc = this.tank.getTankPos();
										Double vX = entLoc.getX() - tankLoc.getX();
										Double vY = entLoc.getY() - tankLoc.getY() + force + 0.125;
										Double vZ = entLoc.getZ() - tankLoc.getZ();
										Vector v = new Vector(vX, 0.0, vZ);
										v = v.normalize().multiply(force + 2.0);
										v.setY(vY);
										damaged.setVelocity(v);
										Tank tnk = this.tank;
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												damaged.damage(Math.abs(tnk.getTankMover().getCurrentSpeed()), Bukkit.getEntity(tnk.getTankID()));
											}
										});
								} catch(NullPointerException ex) {
									ex.printStackTrace();
									continue;
								}
							}
						}
						if(ent instanceof ArmorStand) {
							if(ent.hasMetadata("GG_Rocket")) {
								//ROCKET
								String gunName = ent.getMetadata("GG_OwningWeapon").get(0).asString();
								Gun weapon = GunGamePlugin.instance.weaponManager.getGun(gunName);
								
								UUID shooterID = UUID.fromString(ent.getMetadata("GG_Owner").get(0).asString());
								
								if(this.tank.getDriverUUID() == null || !shooterID.equals(this.tank.getDriverUUID())) {
									
									if(weapon.getRocketNoDamage()) {
										this.tank.takeDamage(0.0);
									} else {
										this.tank.takeDamage((double)(weapon.getRocketExplosionDamage() * weapon.getShotDamage().floatValue()) / (double)2);
									}
										
									Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
										
										@Override
										public void run() {
											weapon.getSoundSet().explodeSound.play(ent.getWorld(), ent.getLocation());
											Util.createExplosion(ent.getLocation(), weapon.getRocketCreateFire(), weapon.getRocketBreakBlocks(), weapon.getRocketNoDamage(), true, weapon.getRocketExplosionDamage(), shooterID, weapon.getRocketExplosionRadius(), true);
											ent.remove();
										}
									});
								}
							}
							if(ent.hasMetadata("GG_TankRocket")) {
								UUID rID = UUID.fromString(ent.getMetadata("GG_Tank_ID").get(0).asString());
								if(!rID.equals(this.tank.getTankID())) {
									ent.setMetadata("GG_Explode", new FixedMetadataValue(GunGamePlugin.instance, true));
								}
							}
						}
						if(ent instanceof Item) {
							if(ent.hasMetadata("GG_Grenade")) {
								if(ent.getMetadata("GG_Grenade_Thrower").get(0) != null) {
									UUID thrID = UUID.fromString(ent.getMetadata("GG_Grenade_Thrower").get(0).asString());
									if(this.tank.getDriverUUID() == null || !thrID.equals(this.tank.getDriverUUID())) {
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												ent.setMetadata("GG_HitTank", new FixedMetadataValue(GunGamePlugin.instance, true));			
											}
										});
									}
								} else {
									Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
										
										@Override
										public void run() {
											ent.setMetadata("GG_HitTank", new FixedMetadataValue(GunGamePlugin.instance, true));			
										}
									});
								}
							}
						}
					}
				}
			} catch(NoSuchElementException ex) {
				ex.printStackTrace();
				//continue;
				if(this.tank == null || !this.tank.isAlive() || this.tank.getBodyArmorStand() == null || this.tank.getBarrelArmorStand() == null || this.tank.getTurretArmorStand() == null) {
					this.running = false;
					this.tank.remove();
				}
			}
			catch (NullPointerException ex) {
				ex.printStackTrace();
				//continue;
				if(this.tank == null || !this.tank.isAlive() || this.tank.getBodyArmorStand() == null || this.tank.getBarrelArmorStand() == null || this.tank.getTurretArmorStand() == null) {
					this.running = false;
					this.tank.remove();
				}
			}
		} else {
			if(this.tank == null || !this.tank.isAlive() || this.tank.getBodyArmorStand() == null || this.tank.getBarrelArmorStand() == null || this.tank.getTurretArmorStand() == null) {
				this.running = false;
				this.stoprun();
				this.tank.remove();
			}
		}
	}
	
	public boolean isLocationInsideHitbox(Location loc) {
		Double minX, minY, minZ;
		Double maxX, maxY, maxZ;
		
		minX = this.tank.getTankPos().getX() - this.tankconfig.getHitBoxHorizontalRadius();
		maxX = this.tank.getTankPos().getX() + this.tankconfig.getHitBoxHorizontalRadius();
		
		minY = this.tank.getTankPos().getY();
		maxY = this.tank.getTankPos().getY() + (this.tankconfig.getHeightRadius() *2.0D);
		
		minZ = this.tank.getTankPos().getZ() - this.tankconfig.getHitBoxHorizontalRadius();
		maxZ = this.tank.getTankPos().getZ() + this.tankconfig.getHitBoxHorizontalRadius();
		
		if(loc.getX() >= minX && loc.getX() <= maxX
				&& loc.getY() >= minY && loc.getY() <= maxY
				&& loc.getZ() >= minZ && loc.getZ() <= maxZ)
		{
			return true;
		}
		
		return false;
	}

}
