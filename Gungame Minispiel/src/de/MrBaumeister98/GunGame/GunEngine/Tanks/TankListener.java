package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import de.MrBaumeister98.GunGame.API.TankEvents.TankMountEvent;
import de.MrBaumeister98.GunGame.API.TankEvents.TankPlaceEvent;
import de.MrBaumeister98.GunGame.API.TankEvents.TankTakeDamageEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class TankListener implements Listener {
	
	@EventHandler
	public void onEnterTank(PlayerInteractAtEntityEvent event) {
		if(event.getRightClicked().hasMetadata("GG_Tank") && event.getRightClicked().getMetadata("GG_Tank") != null) {
			UUID tankID = UUID.fromString(event.getRightClicked().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			if(tank != null) {
				
				if(tank.isAlive() && tank.getDriverUUID() == null) {
					//if(event.getEntered() instanceof Player) {
					TankMountEvent mountevent = new TankMountEvent(tank, event.getPlayer());
					Bukkit.getServer().getPluginManager().callEvent(mountevent);
					if(!mountevent.isCancelled()) {
						event.setCancelled(true);
						tank.mount((Player)event.getPlayer());
					}
					//}
				}
			}
		}
	}
	@EventHandler
	public void onStealFromTank(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked().hasMetadata("GG_Tank")) {
			event.setCancelled(true);
			UUID tankID = UUID.fromString(event.getRightClicked().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			if(tank != null) {
				
				if(tank.isAlive() && tank.getDriverUUID() == null) {
					//if(event.getEntered() instanceof Player) {
					TankMountEvent mountevent = new TankMountEvent(tank, event.getPlayer());
					Bukkit.getServer().getPluginManager().callEvent(mountevent);
					if(!mountevent.isCancelled()) {
						event.setCancelled(true);
						tank.mount((Player)event.getPlayer());
					}
					//}
				}
			}
		}
	}
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(event.getEntity().isInsideVehicle() && event.getEntity().getVehicle() != null && event.getEntity().getVehicle().hasMetadata("GG_Tank")) {
				if(event.getCause().equals(DamageCause.SUFFOCATION) ||
						event.getCause().equals(DamageCause.FALL)
				) {
					event.setCancelled(true);
				}
			}
		}
		if(event.getEntity().hasMetadata("GG_Tank") && 
				!event.getCause().equals(DamageCause.SUFFOCATION) && 
				!event.getCause().equals(DamageCause.DROWNING) && 
				!event.getCause().equals(DamageCause.FIRE) && 
				!event.getCause().equals(DamageCause.FIRE_TICK) &&
				!event.getCause().equals(DamageCause.FALL)) {			
			UUID tankID = UUID.fromString(event.getEntity().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			if(tank != null && tank.isAlive()) {
				TankTakeDamageEvent damageevent = new TankTakeDamageEvent(tank, event);
				Bukkit.getServer().getPluginManager().callEvent(damageevent);
				if(!damageevent.isCancelled()) {			
					tank.takeDamage(event.getFinalDamage() * 0.5);
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity().hasMetadata("GG_Tank")) {
			UUID tankID = UUID.fromString(event.getEntity().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			
			Entity damager = event.getDamager();
			if(damager instanceof TNTPrimed) {
				if(damager.hasMetadata("GG_Explosive")) {
					//event.setCancelled(true);;
					Double damage = Double.valueOf(damager.getMetadata("GG_strength").get(0).asString());
					UUID damagerID = null;
					if(damager.hasMetadata("GG_Owner")) {
						 damagerID = UUID.fromString(damager.getMetadata("GG_Owner").get(0).asString());
					}							
					
					Boolean noDamage = damager.getMetadata("GG_NoDamage").get(0).asBoolean();
					
					Player p = null;
					if(damagerID != null) {
						p = Bukkit.getPlayer(damagerID);
					}
					
					if(!p.getUniqueId().equals(tank.getDriverUUID()) || tank.getDriverUUID() == null) {
						if(noDamage) {
							event.setDamage(0.0);
							EntityDamageEvent dEvent = new EntityDamageEvent(tank.getBodyArmorStand(), DamageCause.ENTITY_EXPLOSION, 0.0);
							Bukkit.getServer().getPluginManager().callEvent(dEvent);
						} else {
							event.setDamage(damage);
							EntityDamageEvent dEvent = new EntityDamageEvent(tank.getBodyArmorStand(), DamageCause.ENTITY_EXPLOSION, damage);
							Bukkit.getServer().getPluginManager().callEvent(dEvent);
						}
						event.setCancelled(true);
					}
					
				}
			}
		}
	}
	@EventHandler
	public void onPlaceTank(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = event.getItem();
			if(ItemUtil.isGGTank(item)) {
				
				TankConfig tankC = GunGamePlugin.instance.tankManager.getTankConfig(item);
				Location loc = event.getPlayer().getLocation().add(event.getPlayer().getLocation().getDirection().normalize().multiply(2.5)).add(0.0, 2.0, 0.0);
				
				if(tankC != null) {
					TankPlaceEvent placeevent = new TankPlaceEvent(tankC, event.getPlayer(), loc);
					Bukkit.getServer().getPluginManager().callEvent(placeevent);
					if(!placeevent.isCancelled()) {
						event.setCancelled(true);
						Tank tank = new Tank(loc, tankC, GunGamePlugin.instance.tankManager);
						
						if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
							Player p = event.getPlayer();
							int amount = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount();
							if(amount > 1) {
								p.getInventory().getItem(p.getInventory().getHeldItemSlot()).setAmount(amount -1);
							} 
							if(amount == 1) {
								p.getInventory().remove(p.getInventory().getItem(p.getInventory().getHeldItemSlot()));
							}
						}
						
						if(event.getPlayer().isSneaking()) {
							tank.mount(event.getPlayer());
						}
					}
				}	
			}
		}
	}
	@EventHandler
	public void stopSeatDestroying(VehicleDestroyEvent event) {
		/*if(event.getEntityType().equals(EntityType.MINECART)) {
			if(event.getEntity().hasMetadata("GG_Tank")) {
				event.getEntity().setHealth(20.0);
			}
		}*/
		if(event.getVehicle() != null && event.getVehicle().hasMetadata("GG_Tank")) {
			event.setCancelled(true);
			UUID tankID = UUID.fromString(event.getVehicle().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			tank.takeDamage(10.0);
		}
	}
	@EventHandler
	public void onTankShellImpact(ProjectileHitEvent event) {
		if(event.getEntity().hasMetadata("GG_Tank_Shell")) {
			UUID tankID = UUID.fromString(event.getEntity().getMetadata("GG_Shooter_Tank_ID").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			if(tank.getSoundset() != null) {
				tank.getSoundset().bullethitSound.play(event.getEntity().getWorld(), event.getEntity().getLocation());
			}
			if(event.getEntity().getType().equals(EntityType.WITHER_SKULL)) {
				if(event.getHitEntity() != null) {
					//EXPLODE
					createTankShellExplosion(event.getEntity(), false);
					if(event.getHitEntity() instanceof LivingEntity) {
						((LivingEntity)event.getHitEntity()).damage(tank.getConfig().getProjectileDamage() * 2.0, event.getEntity());
					}
				}
				if(event.getHitBlock() != null) {
					if(Util.isGlass(event.getHitBlock().getType())) {
						//PASS
						event.getHitBlock().getWorld().playSound(event.getHitBlock().getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
						event.getHitBlock().breakNaturally();
						event.getHitBlock().setType(Material.AIR);
						createTankShellExplosion(event.getEntity(), false);
					} else {
						//EXPLODE
						createTankShellExplosion(event.getEntity(), false);
					}
				}
				if(event.getHitEntity() != null && event.getHitEntity() instanceof ArmorStand) {
					if(event.getHitEntity().hasMetadata("GG_Tank")) {
						double damage = (double)(Double.valueOf(tank.getConfig().getProjectileDamage()).floatValue() * tank.getConfig().getProjectileExplosionPower()) / (double)2;
						if(tank.getConfig().isProjectileNoDamage()) {
							((LivingEntity)event.getHitEntity()).damage(0.0);
						} else {
							((LivingEntity)event.getHitEntity()).damage(damage);
						}
					}
				}
			}
		}
		else if(event.getEntity().hasMetadata("GG_Tank_Bullet")) {
			UUID tankID = UUID.fromString(event.getEntity().getMetadata("GG_Shooter_Tank_ID").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			if(tank.getSoundset() != null) {
				tank.getSoundset().bullethitSound.play(event.getEntity().getWorld(), event.getEntity().getLocation());
			}
			if(event.getEntity().hasMetadata("GG_Tank_Bullet_Explosive")) {
				if(event.getHitEntity() != null) {
					//EXPLODE
					createTankShellExplosion(event.getEntity(), false);
				}
				if(event.getHitBlock() != null) {
					if(Util.isGlass(event.getHitBlock().getType())) {
						//PASS
						event.getHitBlock().getWorld().playSound(event.getHitBlock().getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
						event.getHitBlock().breakNaturally();
						event.getHitBlock().setType(Material.AIR);
						createTankShellExplosion(event.getEntity(), false);
					} else {
						//EXPLODE
						createTankShellExplosion(event.getEntity(), false);
					}
				}
			} else {
				if(event.getHitBlock() != null) {
					if(Util.isGlass(event.getHitBlock().getType())) {
						event.getHitBlock().getWorld().playSound(event.getHitBlock().getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
						event.getHitBlock().breakNaturally();
						event.getHitBlock().setType(Material.AIR);
					}
				}
				if(event.getHitEntity() != null) {
					if(event.getHitEntity() instanceof LivingEntity) {
						LivingEntity lEnt = (LivingEntity)event.getHitEntity();
						if(tank.getDriverUUID() != null) {
							lEnt.damage(tank.getConfig().getProjectileDamage(), Bukkit.getEntity(tank.getDriverUUID()));
						} else {
							lEnt.damage(tank.getConfig().getProjectileDamage(), Bukkit.getEntity(tank.getTankID()));
						}
					}
				}				
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTouchWater(EntityDismountEvent event) {
		Entity mount = event.getDismounted();
		Entity rider = event.getEntity();
		if(mount.hasMetadata("GG_Tank") && rider.hasMetadata("GG_Tank")) {
			UUID mountTankID = UUID.fromString(mount.getMetadata("GG_Tank").get(0).asString());
			UUID riderTankID = UUID.fromString(rider.getMetadata("GG_Tank").get(0).asString());
			if(mountTankID != null && riderTankID != null && mountTankID.equals(riderTankID)) {
				try {
					event.setCancelled(true);
				} catch(NoSuchMethodError ex) {
					//On spigot 1.12: error --> setCancelled does not exist
				}
			}
		}
	}
	
	
	public static void createTankShellExplosion(Projectile tankshell, Boolean destroyNoBlocks) {
		UUID tankID = UUID.fromString(tankshell.getMetadata("GG_Shooter_Tank_ID").get(0).asString());
		Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
		UUID shooterID = null;
		if(tank.getDriverUUID() != null) {
			shooterID = tank.getDriverUUID();
		} else {
			shooterID = tank.getTankID();
		}
		
		if(destroyNoBlocks) {
			Util.createExplosion(tankshell.getLocation(),
					false,
					false,
					tank.getConfig().isProjectileNoDamage(),
					false,
					tank.getConfig().getProjectileExplosionPower(),
					shooterID,
					tank.getConfig().getProjectileExplosionRadius(),
					false,
					0);
			tankshell.remove();
		} else {
			Util.createExplosion(tankshell.getLocation(),
					tank.getConfig().isProjectileIncendiary(),
					tank.getConfig().isProjectileExplosionBreakBlocks(),
					tank.getConfig().isProjectileNoDamage(),
					true,
					tank.getConfig().getProjectileExplosionPower(),
					shooterID,
					tank.getConfig().getProjectileExplosionRadius(),
					false,
					0);
			tankshell.remove();
		}
	}
	
}
