package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.GriefType;

public class RocketThread extends BukkitRunnable {

	private ArmorStand hitbox;
	private Gun gun;
	private UUID shooterID;
	private Location startPoint;
	private Location lastLoc;
	private Vector vector;
	private Boolean running;
	
	public RocketThread(Gun weapon, Player shooter, Vector direction) {
		this.gun = weapon;
		this.shooterID = shooter.getUniqueId();
		this.startPoint = shooter.getLocation().add(0.0, 0.8, 0.0).add(shooter.getLocation().getDirection().normalize());
		this.vector = direction;
		this.vector = this.vector.normalize();
		this.vector = this.vector.multiply(40.0D);
		
		this.hitbox = (ArmorStand) shooter.getWorld().spawnEntity(this.startPoint, EntityType.ARMOR_STAND);
		this.hitbox.setVisible(false);
		this.hitbox.setSmall(true);
		//this.hitbox.setAI(false);
		this.hitbox.setInvulnerable(true);
		this.hitbox.setCollidable(false);
		this.hitbox.setMarker(true);
		this.hitbox.setHelmet(this.gun.getAmmo().getItem());
		//this.hitbox.setGravity(false);
		//SET Head Pose and rotate it to fit!
		
		double pitch = (double)shooter.getEyeLocation().getPitch();
		//double yaw = (double)shooter.getEyeLocation().getYaw();
		//this.hitbox.getLocation().setPitch(shooter.getLocation().getPitch());
		this.hitbox.getLocation().setYaw(shooter.getEyeLocation().getYaw());
		pitch = Math.toRadians(pitch);
		//yaw = Math.toRadians(yaw); // + Math.toRadians(270.0);
		EulerAngle angle = new EulerAngle(pitch, 0.0, 0.0);
		this.hitbox.setHeadPose(angle);
		//this.adjustHeadDirection(this.vector);
		
		this.hitbox.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID));
		this.hitbox.setMetadata("GG_Rocket", new FixedMetadataValue(GunGamePlugin.instance, true));
		this.hitbox.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
		
		this.hitbox.getLocation().setDirection(this.vector);
		this.hitbox.setVelocity(this.vector.normalize());
		this.lastLoc = null;
		this.running = true;
		//Bukkit.broadcastMessage("Launched");
		//Then "launch" it and initialize the checking algorythm
	}
	
	private void adjustHeadDirection(Vector v) {
		double x = v.getX();
		double z = v.getZ();
		/*if (x == 0.0D && z == 0.0D) {
			double pitch = (vector.getY() > 0.0D ? -90 : 90);
			this.hitbox.setHeadPose(new EulerAngle(Math.toRadians(pitch), this.hitbox.getHeadPose().getY(), this.hitbox.getHeadPose().getZ()));
		} else {*/
			double theta = Math.atan2(-x, z);
			Double yaw = Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
			double x2 = NumberConversions.square(x);
			double z2 = NumberConversions.square(z);
			double xz = Math.sqrt(x2 + z2);
			double pitch = Math.toDegrees(Math.atan(-v.getY() / xz));
			this.hitbox.getLocation().setYaw(-yaw.floatValue());
			this.hitbox.setHeadPose(new EulerAngle(Math.toRadians(pitch), this.hitbox.getHeadPose().getY(), this.hitbox.getHeadPose().getZ()));
		//}
		
	}
	
	@Override
	public void run() {
		Boolean adjustHead = false;
		if(this.gun.isSeekingRocket()) {
			try {
				List<Entity> entitiesInRange = new ArrayList<Entity>();
				Location radCenter = this.hitbox.getEyeLocation().clone().add(this.vector.clone().normalize().multiply(4.0));
				//for(Entity ent : this.hitbox.getNearbyEntities(15.0, 15.0, 15.0)) {
				for(Entity ent : this.hitbox.getWorld().getNearbyEntities(radCenter, 3.5, 3.5, 3.5)) {
					if(ent.hasMetadata("GG_Rocket")) {
						UUID entSID = UUID.fromString(ent.getMetadata("GG_Owner").get(0).asString());
						if(!this.shooterID.equals(entSID)) {
							entitiesInRange.add(ent);
						}
					} else if(!ent.getUniqueId().equals(this.hitbox.getUniqueId()) && !ent.getUniqueId().equals(this.shooterID)) {
						if(ent instanceof LivingEntity) {
							entitiesInRange.add(ent);
						}
					}

				}
				ArmorStand hb = this.hitbox;
				Collections.sort(entitiesInRange, new Comparator<Entity>() {

					@Override
					public int compare(Entity o1, Entity o2) {
						Integer distanceE1 = ((Double)hb.getLocation().distance(o1.getLocation())).intValue();
						Integer distanceE2 = ((Double)hb.getLocation().distance(o2.getLocation())).intValue();
						return distanceE1 - distanceE2;
					}
					
				});
				if(!entitiesInRange.isEmpty() && entitiesInRange.size() > 0) {
					Location newTarget = entitiesInRange.get(0).getLocation();
					Location currentLoc = this.hitbox.getLocation();
					Vector vNew = new Vector(newTarget.getX() - currentLoc.getX(), newTarget.getY() - currentLoc.getY(), newTarget.getZ() - currentLoc.getZ());
					vNew = vNew.normalize();
					vNew = vNew.multiply(40.0D);
					this.vector = vNew;
					adjustHead = true;
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		Location nxtLoc = this.hitbox.getEyeLocation().add(this.vector.normalize()).add(0.0,0.9,0.0);
		if(this.hitbox != null && !this.hitbox.isDead()) {
			
			if(adjustHead) {
				this.adjustHeadDirection(this.vector);
			}
			
			if(this.running && nxtLoc.getY() >= 255) {
				//CANCEL
				this.hitbox.remove();

				this.running = false;
			}
			if(this.running && nxtLoc.getY() < 0) {
				//CANCEL
				this.hitbox.remove();

				this.running = false;
			}
			if(this.running && !nxtLoc.getWorld().getWorldBorder().isInside(nxtLoc)) {
				//CANCEL
				this.gun.getSoundSet().explodeSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.gun.getRocketCreateFire(), this.gun.getRocketBreakBlocks(), this.gun.getRocketNoDamage(), true, this.gun.getRocketExplosionDamage(), this.shooterID, this.gun.getRocketExplosionRadius(), true);
				this.hitbox.remove();
				this.running = false;
			}
			if(this.running && (this.hitbox.getVelocity().length() == 0.0D || this.hitbox.getVelocity().getY() == 0.0D || (this.lastLoc != null && this.hitbox.getEyeLocation().distance(this.lastLoc) == 0.0D))){
				//EXPLODE
				this.gun.getSoundSet().explodeSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.gun.getRocketCreateFire(), this.gun.getRocketBreakBlocks(), this.gun.getRocketNoDamage(), true, this.gun.getRocketExplosionDamage(), this.shooterID, this.gun.getRocketExplosionRadius(), true);
				this.hitbox.remove();
				
				this.running = false;
			}
			if(this.running && canPass(nxtLoc.getBlock())) {
				Material type = nxtLoc.getBlock().getType();
				if((GunGamePlugin.instance.griefHelper.isGGWorld(nxtLoc.getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(GriefType.SHOTS_BREAK_GLASS, nxtLoc.getWorld())) &&
						Util.isGlass(type)
						/*(/*type.equals(Material.GLASS) ||
						type.equals(Material.STAINED_GLASS) ||
						type.equals(Material.GLASS_PANE) ||
						type.equals(Material.STAINED_GLASS_PANE) ||
						type.equals(Material.ICE) ||
						type.equals(Material.FROSTED_ICE))*/) {
					nxtLoc.getBlock().breakNaturally();
					nxtLoc.getBlock().getWorld().playSound(nxtLoc, Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
				}
				this.hitbox.setVelocity(this.vector);
			} else if(this.running) {
				//EXPLODE
				this.gun.getSoundSet().explodeSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.gun.getRocketCreateFire(), this.gun.getRocketBreakBlocks(), this.gun.getRocketNoDamage(), true, this.gun.getRocketExplosionDamage(), this.shooterID, this.gun.getRocketExplosionRadius(), true);
				this.hitbox.remove();
				
				this.running = false;
			}
			if(this.running) {
				for(Entity ent : this.hitbox.getNearbyEntities(0.25, 0.25, 0.25)) {
					if(ent instanceof LivingEntity) {
						if(ent instanceof Player) {
							if(((Player)ent).getUniqueId() != this.shooterID && !GunGamePlugin.instance.arenaManager.isSpectator((Player)ent)) {
								this.gun.getDamSet().damage((LivingEntity)ent, this.hitbox.getLocation(), this.shooterID);
								
								this.gun.getSoundSet().explodeSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
								Util.createExplosion(this.hitbox.getLocation(), this.gun.getRocketCreateFire(), this.gun.getRocketBreakBlocks(), this.gun.getRocketNoDamage(), true, this.gun.getRocketExplosionDamage(), this.shooterID, this.gun.getRocketExplosionRadius(), true);
								this.hitbox.remove();
								
								this.running = false;
							} else {
								this.running = true;
								this.hitbox.setVelocity(this.vector);
							}
						} else if(!(ent instanceof ArmorStand && ent.hasMetadata("GG_Rocket"))) {
							this.gun.getDamSet().damage((LivingEntity)ent, this.hitbox.getLocation(), this.shooterID);
							
							this.gun.getSoundSet().explodeSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
							Util.createExplosion(this.hitbox.getLocation(), this.gun.getRocketCreateFire(), this.gun.getRocketBreakBlocks(), this.gun.getRocketNoDamage(), true, this.gun.getRocketExplosionDamage(), this.shooterID, this.gun.getRocketExplosionRadius(), true);
							this.hitbox.remove();
							
							this.running = false;
						}
					} else {
						this.running = true;
						this.hitbox.setVelocity(this.vector);
					}
				}
			}
			if(this.running) {
				this.lastLoc = this.hitbox.getEyeLocation();
				this.hitbox.setVelocity(this.vector);
				this.hitbox.getWorld().spawnParticle(Particle.SMOKE_NORMAL, this.hitbox.getEyeLocation().subtract(this.vector.normalize().multiply(3.0D)), 1, -this.vector.getX(), -this.vector.getY(), -this.vector.getZ(), 0.0125);
				RocketThread reference = this;
				Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						reference.run();
					}
				}, 1);
			}
		}
	}
	
	private Boolean canPass(Block b) {
		Material type = b.getType();
		if(GunGamePlugin.instance.serverPre113) {
			if(type.equals(Material.AIR) ||
					type.equals(Material.STRING) ||
					type.equals(Material.TRIPWIRE) ||
					type.equals(Material.STONE_BUTTON) ||
					type.equals(Material.valueOf("WOOD_BUTTON")) ||
					type.equals(Material.REDSTONE_WIRE) ||
					type.equals(Material.WATER) ||
					type.equals(Material.valueOf("STATIONARY_WATER")) ||
					type.equals(Material.LAVA) ||
					type.equals(Material.valueOf("STATIONARY_LAVA")) ||
					type.equals(Material.GLASS) ||
					type.equals(Material.valueOf("STAINED_GLASS_PANE")) ||
					type.equals(Material.valueOf("THIN_GLASS")) ||
					type.equals(Material.valueOf("STAINED_GLASS")) ||
					type.equals(Material.valueOf("YELLOW_FLOWER")) ||
					type.equals(Material.valueOf("RED_ROSE")) ||
					type.equals(Material.RED_MUSHROOM) ||
					type.equals(Material.BROWN_MUSHROOM) ||
					type.equals(Material.valueOf("LONG_GRASS")) ||
					type.equals(Material.valueOf("DOUBLE_PLANT")) ||
					type.equals(Material.valueOf("CROPS")) ||
					type.equals(Material.VINE) ||
					type.equals(Material.DEAD_BUSH) ||
					type.equals(Material.valueOf("SUGAR_CANE_BLOCK")) ||
					type.equals(Material.valueOf("SAPLING")) ||
					type.equals(Material.TORCH) ||
					type.equals(Material.valueOf("WEB")) ||
					type.equals(Material.valueOf("REDSTONE_TORCH_OFF")) ||
					type.equals(Material.valueOf("REDSTONE_TORCH_ON")) ||
					type.equals(Material.FIRE) ||
					type.equals(Material.ICE) ||
					type.equals(Material.FROSTED_ICE)) {
				return true;
			}
		} else {
			if(type.equals(Material.AIR) ||
					type.equals(Material.STRING) ||
					type.equals(Material.TRIPWIRE) ||
					type.equals(Material.STONE_BUTTON) ||
					type.equals(Material.ACACIA_BUTTON) ||
					type.equals(Material.BIRCH_BUTTON) ||
					type.equals(Material.DARK_OAK_BUTTON) ||
					type.equals(Material.JUNGLE_BUTTON) ||
					type.equals(Material.OAK_BUTTON) ||
					type.equals(Material.SPRUCE_BUTTON) ||
					//type.equals(Material.WOOD_BUTTON) ||
					type.equals(Material.REDSTONE_WIRE) ||
					type.equals(Material.WATER) ||
					//type.equals(Material.STATIONARY_WATER) ||
					type.equals(Material.LAVA) ||
					//type.equals(Material.STATIONARY_LAVA) ||
					Util.isGlass(type) ||
					//type.equals(Material.YELLOW_FLOWER) ||
					//type.equals(Material.RED_ROSE) ||
					type.equals(Material.RED_MUSHROOM) ||
					type.equals(Material.BROWN_MUSHROOM) ||
					/*type.equals(Material.LONG_GRASS) ||
					type.equals(Material.DOUBLE_PLANT) ||*/
					type.equals(Material.SUNFLOWER) ||
					type.equals(Material.LILAC) ||
					type.equals(Material.ROSE_BUSH) ||
					type.equals(Material.PEONY) ||
					type.equals(Material.LARGE_FERN)||
					type.equals(Material.TALL_GRASS) ||
					type.equals(Material.GRASS) ||
					type.equals(Material.ROSE_RED) ||
					//m.equals(Material.LEGACY_YELLOW_FLOWER) ||
					type.equals(Material.DANDELION) ||
					type.equals(Material.DANDELION_YELLOW) ||
					type.equals(Material.POPPY) ||
					type.equals(Material.BLUE_ORCHID) ||
					type.equals(Material.ALLIUM) ||
					type.equals(Material.AZURE_BLUET) ||
					type.equals(Material.ORANGE_TULIP) ||
					type.equals(Material.PINK_TULIP) ||
					type.equals(Material.RED_TULIP) ||
					type.equals(Material.WHITE_TULIP) ||
					type.equals(Material.OXEYE_DAISY) ||
					type.equals(Material.FERN) ||
					type.equals(Material.COCOA) ||
					//type.equals(Material.CROPS) ||
					type.equals(Material.WHEAT) ||
					type.equals(Material.CARROTS) ||
					type.equals(Material.POTATOES) ||
					type.equals(Material.BEETROOTS) ||
					type.equals(Material.SEA_PICKLE) ||
					type.equals(Material.SEAGRASS) ||
					type.equals(Material.VINE) ||
					type.equals(Material.DEAD_BUSH) ||
					type.equals(Material.SUGAR_CANE) ||
					//type.equals(Material.SAPLING) ||
					type.equals(Material.TORCH) ||
					type.equals(Material.COBWEB) ||
					//type.equals(Material.REDSTONE_TORCH_OFF) ||
					type.equals(Material.REDSTONE_TORCH) ||
					type.equals(Material.FIRE)
					) {
				return true;
			}
		}
		return false;
	}
}
