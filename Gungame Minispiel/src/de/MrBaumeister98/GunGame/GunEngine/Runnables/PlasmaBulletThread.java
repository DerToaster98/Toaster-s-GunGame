package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.PlasmaParticleUtil;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public class PlasmaBulletThread extends BukkitRunnable {

	private UUID shooterID;
	private Gun gun;
	private PlasmaParticleUtil laserHelper;
	private Location location;
	private Vector vector;
	private Boolean running;
	private Double x;
	private Double y;
	private Double z;
	@SuppressWarnings("unused")
	private static int taskID;
	private Integer iterations;
	
	public PlasmaBulletThread(Gun weapon, Player shooter, Vector direction) {
		this.gun = weapon;
		this.shooterID = shooter.getUniqueId();
		this.laserHelper = this.gun.getLaserHelper();
		this.location = shooter.getEyeLocation().add(shooter.getLocation().getDirection().normalize());
		this.vector = direction;
		this.vector = this.vector.normalize();
		this.vector = this.vector.multiply(20.0);
		//Bukkit.getPlayer(this.shooterID).sendMessage("V: " + this.vector.getX() + " | " + this.vector.getY() + " | " + this.vector.getZ());
		this.running = true;
		this.iterations = 0;
		this.x = this.location.getX();
		this.y = this.location.getY();
		this.z = this.location.getZ();
	}
	
	@Override
	public void run() {
		PlasmaBulletThread reference = this;
		if(reference.running) {
			if(this.gun.getLaserRayIterationDelay() > (long)0) {
				reference.laserHelper.play(reference.location,
						reference.x - reference.vector.normalize().multiply(0.1D).getX(),
						reference.y - reference.vector.normalize().multiply(0.1D).getY(),
						reference.z - reference.vector.normalize().multiply(0.1D).getZ());
				reference.laserHelper.play(reference.location,
						reference.x - reference.vector.normalize().multiply(0.2D).getX(),
						reference.y - reference.vector.normalize().multiply(0.2D).getY(),
						reference.z - reference.vector.normalize().multiply(0.2D).getZ());
			}
				reference.laserHelper.play(reference.location,
						reference.x - reference.vector.normalize().getX(),
						reference.y - reference.vector.normalize().getY(),
						reference.z - reference.vector.normalize().getZ());
				reference.laserHelper.play(reference.location, reference.x, reference.y, reference.z);
				//laserHelper.play(location.add(vector.multiply(-1.0D)));
				//laserHelper.play(location.add(vector.multiply(-2.0D)));
		}
		if(GunGamePlugin.instance.tankManager.isPositionInATankHitbox(this.location) != null && reference.running) {
			Tank tank = GunGamePlugin.instance.tankManager.isPositionInATankHitbox(this.location);
			if(tank.getDriverUUID() == null || !tank.getDriverUUID().equals(this.shooterID)) {
				LivingEntity tankEnt = tank.getBodyArmorStand();
				reference.gun.getDamSet().damage(tankEnt, reference.location, reference.shooterID);
				spawnFireWork(reference.location.subtract(this.vector.normalize()), reference);
				
				reference.running = false;
				killTask();
				try {
					cancel();
				} catch(IllegalStateException ex) {
					//ex.printStackTrace();
					// I DONT CARE ABOUT THIS SHIT!!!!! Is nothing bad, just bukkit!
				}
			}
		}
				Collection<Entity> entities = this.location.getWorld().getNearbyEntities(this.location, 0.2D, 0.2D, 0.2D);
				if(entities != null && entities.size() > 0 && reference.running) {
					for(Entity ent : entities) {
						if(ent instanceof LivingEntity) {
							LivingEntity damaged = (LivingEntity)ent;
							if(damaged instanceof Player) {
								if(!((Player)damaged).getUniqueId().equals(this.shooterID) && !GunGamePlugin.instance.arenaManager.isSpectator((Player)damaged)) {
									//DAMAGE IT!!! USE DAMAGESET FOR HIT DETECTION
									Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
										
										@Override
										public void run() {
											if(reference.gun.canMeltBlocks()) {
												damaged.setFireTicks(10);
											}
											reference.gun.getDamSet().damage(damaged, reference.location, reference.shooterID);
										}
									});
									//killTask();
								}
							} else {
								//DAMAGE IT!!! USE DAMAGESET FOR HIT DTECTION
								if(damaged instanceof ArmorStand) {
									//CHECK IF ITS A ROCKET
									if(damaged.hasMetadata("GG_Rocket")) {
										if(damaged.hasMetadata("GG_OwningWeapon")) {
											Gun g = GunGamePlugin.instance.weaponManager.getGun(damaged.getMetadata("GG_OwningWeapon").get(0).asString());
											this.gun.getSoundSet().explodeSound.play(damaged.getWorld(), damaged.getLocation());
											Util.createExplosion(damaged.getLocation(), g.getRocketCreateFire(), g.getRocketBreakBlocks(), g.getRocketNoDamage(), true, g.getRocketExplosionDamage(), this.shooterID, g.getRocketExplosionRadius(), true);
											
											spawnFireWork(damaged.getEyeLocation(), reference);
											Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
												
												@Override
												public void run() {
													damaged.remove();
												}
											}, 2);
										}
										
										
									} else {
										reference.gun.getDamSet().damage(damaged, reference.location, reference.shooterID);
									}
								} else {
									if(reference.gun.canMeltBlocks()) {
										damaged.setFireTicks(10);
									}
									reference.gun.getDamSet().damage(damaged, reference.location, reference.shooterID);
								}
							}
						}
					}
				}
				Material m = reference.location.getBlock().getType();
				if((!canPass(m) || (this.laserHelper.meltsBlocks && Util.meltMap.containsKey(m))) && reference.running) {
					if(this.laserHelper.meltsBlocks && (GunGamePlugin.instance.griefHelper.isGGWorld(reference.location.getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.BULLETS_IGNITE_TNT, reference.location.getWorld())) && Util.meltMap.containsKey(m)) {
						// DONE: SChmelzliste
						/*
						 * Cobble --> Magma block (1.12: MAGMA, >: MAGMA_BLOCK
						 * Magma block --> lava
						 * Eis --> wasser
						 * packeis --> eis
						 * 
						 * Form: FROM-TO-CHANGEBACK
						 */
						//Bukkit.broadcastMessage("Melting enabled");
						Block block = reference.location.getBlock();
						if(Util.meltMap.containsKey(m) && !block.hasMetadata("GG_Melted")) {
							//Bukkit.broadcastMessage("Block is meltable");
							block.setType(Util.meltMap.get(m).getChangeTo());
							block.setMetadata("GG_Melted", new FixedMetadataValue(GunGamePlugin.instance, true));
							//Bukkit.broadcastMessage("Block melted");
							block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation().add(0.75, 1.125, 0.75), 100, -0.5, -0.75, -0.5, 0.0125);
							block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.75f, 0.75f);
							if(Util.meltMap.get(m).coolsBack()) {
								Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
									
									@Override
									public void run() {
										if(!block.getType().equals(Material.AIR)) {
											block.setType(m);
											block.removeMetadata("GG_Melted", GunGamePlugin.instance);
											block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation().add(0.75, 1.125, 0.75), 100, -0.5, -0.75, -0.5, 0.0125);
											block.getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
											//Bukkit.broadcastMessage("Block cooled back");
										} else {
											block.setType(Util.meltMap.get(m).getChangeTo());
											Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
												
												@Override
												public void run() {
													if(!block.getType().equals(Material.AIR)) {
														block.setType(m);
														block.removeMetadata("GG_Melted", GunGamePlugin.instance);
														block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation().add(0.75, 1.125, 0.75), 100, -0.5, -0.75, -0.5, 0.0125);
														block.getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
														//Bukkit.broadcastMessage("Block cooled back");
													}
												}
											}, 30);
										}
									}
								}, 40 + Util.getRandomNumber(80));
							}
							spawnFireWork(reference.location.subtract(this.vector.normalize()), reference);
														
							reference.running = false;
							killTask();
							try {
								cancel();
							} catch(IllegalStateException ex) {
								//ex.printStackTrace();
								// I DONT CARE ABOUT THIS SHIT!!!!! Is nothing bad, just bukkit!
							}
						}
					}
					else if(m.equals(Material.TNT) && (GunGamePlugin.instance.griefHelper.isGGWorld(reference.location.getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.BULLETS_IGNITE_TNT, reference.location.getWorld()))) {
						reference.location.getBlock().setType(Material.AIR);
						reference.location.getBlock().breakNaturally();
						
						spawnFireWork(reference.location.subtract(this.vector.normalize()), reference);
						
						Util.createExplosion(reference.location.getBlock().getLocation(), false, false, false, true, 4, reference.shooterID, 3, false);
						
						reference.running = false;
						killTask();
						try {
							cancel();
						} catch(IllegalStateException ex) {
							//ex.printStackTrace();
							// I DONT CARE ABOUT THIS SHIT!!!!! Is nothing bad, just bukkit!
						}
					} else {
						//Bukkit.getPlayer(reference.shooterID).sendMessage("COLLISSION!");
						spawnFireWork(reference.location.subtract(this.vector.normalize()), reference);
						reference.running = false;
						killTask();
						try {
							cancel();
						} catch(IllegalStateException ex) {
							//ex.printStackTrace();
							// I DONT CARE ABOUT THIS SHIT!!!!! Is nothing bad, just bukkit!
						}
					}				
				} else
				if(reference.location.getBlockY() >= 255 || reference.location.getBlockY() < 0 || !location.getWorld().getWorldBorder().isInside(location) && reference.running) {
					killTask();
				} else {
					/*Location tmp = reference.location;
					Double tX = reference.x;
					Double tY = reference.y;
					Double tZ = reference.z;*/
					updateLocation();
					/*if(reference.location.getBlock().getType().equals(Material.IRON_BLOCK)) {
						reflectVector();
						reference.location = tmp;
						reference.x = tX;
						reference.y = tY;
						reference.z = tZ;
					}*/
				} 
				
				if(this.running && this.iterations < Util.maxIterationsLaserThread) {
					this.iterations++;
					if(this.gun.getLaserRayIterationDelay() > (long)0) {
						Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
							
							@Override
							public void run() {
								reference.run();
							}
						}, this.gun.getLaserRayIterationDelay());
					} else {
						Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
							
							@Override
							public void run() {
								reference.run();
							}
						}, Long.MIN_VALUE);
					}
				} else if(this.iterations >= Util.maxIterationsLaserThread && this.running) {
					this.running = false;
					spawnFireWork(reference.location.subtract(this.vector.normalize()), reference);
				}
	}
	/*private void reflectVector() {
		Double x = this.vector.getX();
		Double y = this.vector.getY();
		Double z = this.vector.getZ();
		
		x = -(1.0/x);
		y = -(1.0/y);
		z = -(1.0/z);
		
		Vector v = new Vector(x, y, z);
		this.vector = v;
	}*/
	private void killTask() {
		this.running = false;
	}
	private void spawnFireWork(Location loc, PlasmaBulletThread pbt) {
		Firework fw;
		fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		FireworkEffect effect = null;
		if(GunGamePlugin.instance.serverPre113) {
			effect = FireworkEffect.builder().
					trail(false)
					.flicker(true)
					.withColor(Color.fromRGB(Math.round(((Double)(Math.abs(pbt.laserHelper.R) * 255.0)).intValue() ), Math.round(((Double)(Math.abs(pbt.laserHelper.G) * 255.0)).intValue()), Math.round(((Double)(Math.abs(pbt.laserHelper.B) * 255.0)).intValue())))
					.with(FireworkEffect.Type.BURST)
					.build();
		} else {
			effect = FireworkEffect.builder().
					trail(false)
					.flicker(true)
					.withColor(Color.fromRGB(Math.round(((Double)(Math.abs((pbt.laserHelper.R.equals(Double.MIN_VALUE) || pbt.laserHelper.R < 0.0) ? 0 : pbt.laserHelper.R))).intValue()), Math.round(((Double)(Math.abs(pbt.laserHelper.G))).intValue()), Math.round(((Double)(Math.abs(pbt.laserHelper.B))).intValue())))
					.with(FireworkEffect.Type.BURST)
					.build();
		}
		meta.addEffects(new FireworkEffect[] { effect });
		fw.setFireworkMeta(meta);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				fw.detonate();
			}
		}, 1);
	}
	private void updateLocation() {
		this.location = this.location.add(this.vector);
		this.x = this.x + this.vector.getX();
		this.y = this.y + this.vector.getY();
		this.z = this.z + this.vector.getZ();
	}
	private Boolean canPass(Material m) {
		if(GunGamePlugin.instance.serverPre113) {
			if(m.equals(Material.AIR) ||
					m.equals(Material.STRING) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.valueOf("WOOD_BUTTON")) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.LADDER) ||
					m.equals(Material.TORCH) ||
					m.equals(Material.valueOf("REDSTONE_TORCH_OFF")) ||
					m.equals(Material.valueOf("REDSTONE_TORCH_ON")) ||
					m.equals(Material.GLASS) ||
					m.equals(Material.valueOf("THIN_GLASS")) ||
					m.equals(Material.valueOf("STAINED_GLASS")) ||
					m.equals(Material.valueOf("STAINED_GLASS_PANE")) ||
					m.equals(Material.valueOf("IRON_BARDING")) ||
					m.equals(Material.valueOf("IRON_FENCE")) ||
					m.equals(Material.WATER) ||
					m.equals(Material.valueOf("STATIONARY_WATER")) ||
					m.equals(Material.TRIPWIRE_HOOK)||
					m.equals(Material.VINE) ||
					m.equals(Material.FIRE) ||
					m.equals(Material.valueOf("DOUBLE_PLANT")) ||
					m.equals(Material.valueOf("LONG_GRASS")) ||
					m.equals(Material.valueOf("RED_ROSE")) ||
					m.equals(Material.valueOf("YELLOW_FLOWER")) ||
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM) ||
					m.equals(Material.valueOf("SUGAR_CANE_BLOCK")) ||
					m.equals(Material.FIRE) ||
					m.equals(Material.ICE) ||
					m.equals(Material.FROSTED_ICE) ||
					m.equals(Material.WALL_SIGN) ||
					m.equals(Material.valueOf("WALL_BANNER")) ||
					m.equals(Material.valueOf("WEB")) ||
					m.equals(Material.valueOf("CARPET"))) {
				return true;
			}
		} else {
			if(m.equals(Material.AIR) ||
					Util.isGlass(m) ||
					m.equals(Material.WATER) ||
					m.equals(Material.WATER) ||
					m.equals(Material.LILY_PAD) ||
					m.equals(Material.SUGAR_CANE) ||
					m.equals(Material.TALL_SEAGRASS) ||
					m.equals(Material.KELP_PLANT) ||
					m.equals(Material.KELP) ||
					//m.equals(Material.LEGACY_CROPS) ||
					m.equals(Material.WHEAT) ||
					m.equals(Material.CARROTS) ||
					m.equals(Material.POTATOES) ||
					m.equals(Material.BEETROOTS) ||
					m.equals(Material.SEA_PICKLE) ||
					m.equals(Material.SEAGRASS) ||

					m.equals(Material.TORCH) ||
					m.equals(Material.REDSTONE) ||
					m.equals(Material.COMPARATOR) ||
					//m.equals(Material.REDSTONE_COMPARATOR_ON) ||
					m.equals(Material.REDSTONE_WIRE) ||
					m.equals(Material.REDSTONE_TORCH) ||
					//m.equals(Material.REDSTONE_TORCH_OFF) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.ACACIA_BUTTON) ||
					m.equals(Material.BIRCH_BUTTON) ||
					m.equals(Material.DARK_OAK_BUTTON) ||
					m.equals(Material.JUNGLE_BUTTON) ||
					m.equals(Material.OAK_BUTTON) ||
					m.equals(Material.SPRUCE_BUTTON) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.TRIPWIRE_HOOK)||
					m.equals(Material.FLOWER_POT) ||
					m.equals(Material.RAIL) ||
					m.equals(Material.ACTIVATOR_RAIL) ||
					m.equals(Material.DETECTOR_RAIL) ||
					m.equals(Material.POWERED_RAIL) ||
					m.equals(Material.END_ROD) ||
					m.equals(Material.COBWEB) ||

					m.equals(Material.VINE) ||

					m.equals(Material.BLACK_BANNER) ||
					m.equals(Material.BLACK_WALL_BANNER) ||
					m.equals(Material.BLUE_BANNER) ||
					m.equals(Material.BLUE_WALL_BANNER) ||
					m.equals(Material.BROWN_BANNER) ||
					m.equals(Material.BROWN_WALL_BANNER) ||
					m.equals(Material.CYAN_BANNER) ||
					m.equals(Material.CYAN_WALL_BANNER) ||
					m.equals(Material.GRAY_BANNER) ||
					m.equals(Material.GRAY_WALL_BANNER) ||
					m.equals(Material.GREEN_BANNER) ||
					m.equals(Material.GREEN_WALL_BANNER) ||
					m.equals(Material.LIME_BANNER) ||
					m.equals(Material.LIME_WALL_BANNER) ||
					m.equals(Material.LIGHT_BLUE_BANNER) ||
					m.equals(Material.LIGHT_BLUE_WALL_BANNER) ||
					m.equals(Material.LIGHT_GRAY_BANNER) ||
					m.equals(Material.LIGHT_GRAY_WALL_BANNER) ||
					m.equals(Material.MAGENTA_BANNER) ||
					m.equals(Material.MAGENTA_WALL_BANNER) ||
					m.equals(Material.ORANGE_BANNER) ||
					m.equals(Material.ORANGE_WALL_BANNER) ||
					m.equals(Material.PINK_BANNER) ||
					m.equals(Material.PINK_WALL_BANNER) ||
					m.equals(Material.PURPLE_BANNER) ||
					m.equals(Material.PURPLE_WALL_BANNER) ||
					m.equals(Material.RED_BANNER) ||
					m.equals(Material.RED_WALL_BANNER) ||
					m.equals(Material.WHITE_BANNER) ||
					m.equals(Material.WHITE_WALL_BANNER) ||
					m.equals(Material.YELLOW_BANNER) ||
					m.equals(Material.YELLOW_WALL_BANNER) ||
					
					m.equals(Material.SIGN) ||
					m.equals(Material.WALL_SIGN) ||

					m.equals(Material.SUNFLOWER) ||
					m.equals(Material.LILAC) ||
					m.equals(Material.ROSE_BUSH) ||
					m.equals(Material.PEONY) ||
					m.equals(Material.LARGE_FERN)||
					m.equals(Material.TALL_GRASS) ||
					m.equals(Material.GRASS) ||
					m.equals(Material.ROSE_RED) ||
					//m.equals(Material.LEGACY_YELLOW_FLOWER) ||
					m.equals(Material.DANDELION) ||
					m.equals(Material.DANDELION_YELLOW) ||
					m.equals(Material.POPPY) ||
					m.equals(Material.BLUE_ORCHID) ||
					m.equals(Material.ALLIUM) ||
					m.equals(Material.AZURE_BLUET) ||
					m.equals(Material.ORANGE_TULIP) ||
					m.equals(Material.PINK_TULIP) ||
					m.equals(Material.RED_TULIP) ||
					m.equals(Material.WHITE_TULIP) ||
					m.equals(Material.OXEYE_DAISY) ||
					m.equals(Material.FERN) ||
					m.equals(Material.COCOA) ||
					m.equals(Material.LADDER) ||
					m.equals(Material.IRON_BARS) ||
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM)) {
				return true;
			}
		}
		return false;
	}
	
}
