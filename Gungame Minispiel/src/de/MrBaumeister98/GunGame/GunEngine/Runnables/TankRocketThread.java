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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;

public class TankRocketThread extends BukkitRunnable {
	
	private ArmorStand hitbox;
	//private Gun gun;
	private Tank tank;
	private UUID shooterID;
	private Location startPoint;
	private Location lastLoc;
	private Vector vector;
	private Boolean running;
	
	private boolean createFire;
	private boolean breakBlocks;
	private boolean noDamage;
	private float explosionDamage;
	private Integer explosionRadius;
	
	public TankRocketThread(Tank tank, Player shooter, Vector direction, Location start) {
		//this.gun = weapon;
		this.shooterID = shooter.getUniqueId();
		this.startPoint = start;
		this.vector = direction;
		this.vector = this.vector.normalize();
		this.vector = this.vector.multiply(40.0D);
		
		this.tank = tank;
		TankConfig tc = tank.getConfig();
		
		this.createFire = tc.isProjectileIncendiary();
		this.breakBlocks = tc.isProjectileExplosionBreakBlocks();
		this.noDamage = tc.isProjectileNoDamage();
		this.explosionDamage = tc.getProjectileExplosionPower();
		this.explosionRadius = tc.getProjectileExplosionRadius();
		
		this.hitbox = (ArmorStand) shooter.getWorld().spawnEntity(this.startPoint, EntityType.ARMOR_STAND);
		this.hitbox.setVisible(false);
		this.hitbox.setSmall(true);
		//this.hitbox.setAI(false);
		this.hitbox.setInvulnerable(true);
		this.hitbox.setCollidable(false);
		this.hitbox.setMarker(true);
			this.hitbox.setHelmet(new ItemStack(Material.FIREWORK_ROCKET));
		//this.hitbox.setGravity(false);
		//SET Head Pose and rotate it to fit!
		
		double pitch = tank.getBarrelArmorStand().getHeadPose().getX();
		double yaw = tank.getBarrelArmorStand().getHeadPose().getY();
		//double yaw = (double)shooter.getLocation().getYaw();
		this.hitbox.getLocation().setPitch(shooter.getLocation().getPitch());
		this.hitbox.getLocation().setYaw(shooter.getEyeLocation().getYaw());
		//pitch = Math.toRadians(pitch);
		//yaw = Math.toRadians(yaw);
		EulerAngle angle = new EulerAngle(pitch, yaw, 0.0);
		this.hitbox.setHeadPose(angle);
		
		this.hitbox.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID));
		this.hitbox.setMetadata("GG_TankRocket", new FixedMetadataValue(GunGamePlugin.instance, true));
		this.hitbox.setMetadata("GG_Tank_ID", new FixedMetadataValue(GunGamePlugin.instance, tank.getTankID().toString()));
		
		this.hitbox.getLocation().setDirection(this.vector);
		this.hitbox.setVelocity(this.vector.normalize());
		this.lastLoc = null;
		this.running = true;
		//Bukkit.broadcastMessage("Launched");
		//Then "launch" it and initialize the checking algorythm
	}
	private Boolean belongsToTank(Entity entity) {
		UUID entID = entity.getUniqueId();
		if(entID.equals(this.tank.getEntities()[0].getUniqueId()) ||
				entID.equals(this.tank.getEntities()[1].getUniqueId()) ||
				entID.equals(this.tank.getEntities()[2].getUniqueId()) ||
				entID.equals(this.tank.getEntities()[3].getUniqueId()) ||
				entID.equals(this.tank.getEntities()[4].getUniqueId()) ||
				entID.equals(this.shooterID)
				) {
			return true;
		}
		if(entity instanceof ArmorStand) {
			if(entity.hasMetadata("GG_TankRocket")) {
				UUID shooterID = UUID.fromString(entity.getMetadata("GG_Owner").get(0).asString());
				if(this.shooterID.equals(shooterID)) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void run() {
		
		
		try {
			List<Entity> entitiesInRange = new ArrayList<Entity>();
			Location radCenter = this.hitbox.getEyeLocation().clone().add(this.vector.clone().normalize().multiply(15.0));
			//for(Entity ent : this.hitbox.getNearbyEntities(15.0, 15.0, 15.0)) {
			for(Entity ent : this.hitbox.getWorld().getNearbyEntities(radCenter, 15.0, 15.0, 15.0)) {
				if(!belongsToTank(ent) && ent instanceof LivingEntity) {
					if(this.tank.getDriverUUID() != null) {
						if(!ent.getUniqueId().equals(this.tank.getDriverUUID())) {
							entitiesInRange.add(ent);
						}
					} else {
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
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		Location nxtLoc = this.hitbox.getEyeLocation().add(this.vector.normalize()).add(0.0,0.9,0.0);
		
		if(this.hitbox != null && !this.hitbox.isDead()) {
			
			this.adjustHeadDirection();
			
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
			if(this.vector.length() <= 0.0 || this.vector == null) {
				this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
				this.hitbox.remove();
				this.running = false;
			}
			if(this.running && !nxtLoc.getWorld().getWorldBorder().isInside(nxtLoc)) {
				//CANCEL
				this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
				this.hitbox.remove();
				this.running = false;
			}
			if(this.running && (this.hitbox.getVelocity().length() == 0.0D || this.hitbox.getVelocity().getY() == 0.0D || (this.lastLoc != null && this.hitbox.getEyeLocation().distance(this.lastLoc) == 0.0D))){
				//EXPLODE
				this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
				this.hitbox.remove();
				
				this.running = false;
			}
			if(this.running && this.hitbox.hasMetadata("GG_Explode")) {
				this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
				this.hitbox.remove();
				
				this.running = false;
			}
			if(this.running && canPass(nxtLoc.getBlock())) {
				Material type = nxtLoc.getBlock().getType();
				if((GunGamePlugin.instance.griefHelper.isGGWorld(nxtLoc.getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.SHOTS_BREAK_GLASS, nxtLoc.getWorld())) &&
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
				this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
				Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
				this.hitbox.remove();
				
				this.running = false;
			}
			if(this.running) {
				for(Entity ent : this.hitbox.getNearbyEntities(0.25, 0.25, 0.25)) {
					if(ent instanceof LivingEntity) {
						if(ent instanceof Player) {
							if(((Player)ent).getUniqueId() != this.shooterID && !GunGamePlugin.instance.arenaManager.isSpectator((Player)ent)) {
								//this.gun.getDamSet().damage((LivingEntity)ent, this.hitbox.getLocation(), this.shooterID);
								//this.tank.getDamageSet().damage((LivingEntity)ent, this.hitbox.getLocation(), this.shooterID);
								((LivingEntity)ent).damage(this.tank.getConfig().getProjectileDamage(), Bukkit.getEntity(this.shooterID));
								this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
								Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
								this.hitbox.remove();
								
								this.running = false;
							} else {
								this.running = true;
								this.hitbox.setVelocity(this.vector);
							}
						} else if(!(ent instanceof ArmorStand && ent.hasMetadata("GG_Rocket"))) {
							//this.tank.getDamageSet().damage((LivingEntity)ent, this.hitbox.getLocation(), this.shooterID);
							((LivingEntity)ent).damage(this.tank.getConfig().getProjectileDamage(), Bukkit.getEntity(this.shooterID));
							this.tank.getSoundset().bullethitSound.play(this.hitbox.getWorld(), this.hitbox.getLocation());
							Util.createExplosion(this.hitbox.getLocation(), this.createFire, this.breakBlocks, this.noDamage, true, this.explosionDamage, this.shooterID, this.explosionRadius, true);
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
				TankRocketThread reference = this;
				Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						reference.run();
					}
				}, 1);
			}
		}
	}
	
	private void adjustHeadDirection() {
		double x = this.vector.getX();
		double z = this.vector.getZ();
		/*if (x == 0.0D && z == 0.0D) {
			double pitch = (vector.getY() > 0.0D ? -90 : 90);
			this.hitbox.setHeadPose(new EulerAngle(Math.toRadians(pitch), this.hitbox.getHeadPose().getY(), this.hitbox.getHeadPose().getZ()));
		} else {*/
			double theta = Math.atan2(-x, z);
			double yaw = Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
			double x2 = NumberConversions.square(x);
			double z2 = NumberConversions.square(z);
			double xz = Math.sqrt(x2 + z2);
			double pitch = Math.toDegrees(Math.atan(-vector.getY() / xz));
			this.hitbox.setHeadPose(new EulerAngle(Math.toRadians(pitch), Math.toRadians(yaw), this.hitbox.getHeadPose().getZ()));
		//}
		
	}
	private Boolean canPass(Block b) {
		Material type = b.getType();
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
					type.equals(Material.WITHER_ROSE) ||
					//m.equals(Material.LEGACY_YELLOW_FLOWER) ||
					type.equals(Material.DANDELION) ||
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
		return false;
	}
	public ArmorStand getEntity() {
		return this.hitbox;
	}

}
