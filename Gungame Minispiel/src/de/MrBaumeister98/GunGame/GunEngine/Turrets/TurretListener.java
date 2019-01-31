package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.API.TurretEvents.TurretPlaceEvent;
import de.MrBaumeister98.GunGame.API.TurretEvents.TurretReloadEvent;
import de.MrBaumeister98.GunGame.API.TurretEvents.TurretTakeDamageEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.GriefType;

public class TurretListener implements Listener {
	
	TurretManager manager = GunGamePlugin.instance.turretManager;
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if(event.getPlayer().hasMetadata("GG_isTurretRider")) {
			String uid = event.getPlayer().getMetadata("GG_TurretID").get(0).asString();
			UUID tID = UUID.fromString(uid);
			
			Turret turret = this.manager.getTurret(tID);
			try {
				turret.disMount(event.getPlayer());
			} catch(Exception ex) {
				ex.printStackTrace();
				turret.forceDismount();
			}
		}
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if(event.getPlayer().hasMetadata("GG_isTurretRider")) {
			String uid = event.getPlayer().getMetadata("GG_TurretID").get(0).asString();
			UUID tID = UUID.fromString(uid);
			
			Turret turret = this.manager.getTurret(tID);
			try {
				turret.disMount(event.getPlayer());
			} catch(Exception ex) {
				ex.printStackTrace();
				turret.forceDismount();
			}
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getEntity().hasMetadata("GG_isTurretRider")) {
			String uid = event.getEntity().getMetadata("GG_TurretID").get(0).asString();
			UUID tID = UUID.fromString(uid);
			
			Turret turret = this.manager.getTurret(tID);
			try {
				turret.disMount(event.getEntity());
			} catch(Exception ex) {
				ex.printStackTrace();
				turret.forceDismount();
			}
		}
	}
	@EventHandler
	public void onAim(PlayerMoveEvent event) {
		if(event.getPlayer().hasMetadata("GG_isTurretRider")) {
			Location toLoc = event.getTo();
			Location fromLoc = event.getFrom();
			
			String uid = event.getPlayer().getMetadata("GG_TurretID").get(0).asString();
			UUID tID = UUID.fromString(uid);
			
			Turret turret = this.manager.getTurret(tID);
			//event.getPlayer().sendMessage("D:= " + fromLoc.distance(toLoc));
			if(fromLoc.distance(toLoc) >= 0.06 && !event.getPlayer().isSneaking()) {
			    //event.getPlayer().sendMessage("TRIGGER");
				
				turret.disMount(event.getPlayer());
			} else {
				Location newTo = new Location(toLoc.getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
				newTo.setPitch(toLoc.getPitch());
				newTo.setYaw(toLoc.getYaw());
				
				event.setTo(newTo);
				turret.adjustHeadPoseToPlayer(event.getPlayer());
			}
			
		}
	}
	@EventHandler
	public void onShoot(PlayerToggleSneakEvent event) {
		if(event.getPlayer().hasMetadata("GG_isTurretRider")) {
			String uid = event.getPlayer().getMetadata("GG_TurretID").get(0).asString();
			UUID tID = UUID.fromString(uid);
			
			Turret turret = this.manager.getTurret(tID);
			//START SHOOTING
			turret.toggleShooting();
		}
	}
	@EventHandler
	public void onStealGunFromTurret(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked().hasMetadata("GG_Turret")) {
			if(!event.getPlayer().hasMetadata("GG_isTurretRider")/* && event.getPlayer().isSneaking()*/) {
				event.setCancelled(true);
				if(this.manager.getTurret(event.getRightClicked().getUniqueId()) != null) {
					this.manager.getTurret(event.getRightClicked().getUniqueId()).mount(event.getPlayer());
				}
			}
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onMount(PlayerInteractAtEntityEvent event) {
		Entity ent = event.getRightClicked();
		if(ent instanceof ArmorStand && ent.hasMetadata("GG_Turret")) {
			if(!event.getPlayer().hasMetadata("GG_isTurretRider")/* && event.getPlayer().isSneaking()*/) {
				this.manager.getTurret(event.getRightClicked().getUniqueId()).mount(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onPlace(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.PHYSICAL)) {
			if(ItemUtil.isGGTurret(item)) {
				
				//Block m = event.getClickedBlock();
				Block b = event.getClickedBlock().getRelative(event.getBlockFace());
				Block d = b.getRelative(BlockFace.DOWN);
				
				if(Util.isFullBlock(d.getLocation())) {
					if(!Util.isFullBlock(b.getLocation())) {
						TurretConfig tc = this.manager.getTurretConfig(item);
						
						TurretPlaceEvent placeevent = new TurretPlaceEvent(tc, event.getPlayer(), d.getLocation());
						Bukkit.getServer().getPluginManager().callEvent(placeevent);
						if(!placeevent.isCancelled()) {
							event.setCancelled(true);
							//event.getPlayer().getInventory().addItem(tc.getGunItem());
							Turret turret = new Turret(b.getLocation(), event.getPlayer().getLocation().getYaw(), tc);
							turret.hitbox.setVisible(false);
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
						}
					}
				}
				
			} 
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityCollide(ProjectileHitEvent event) {
		if(event.getHitEntity() != null && event.getHitEntity() instanceof ArmorStand) {
			Entity proj = event.getEntity();
			if(event.getHitEntity().hasMetadata("GG_Turret")) {
				if(proj.hasMetadata("GG_Turret")) {
					UUID projTurrID = UUID.fromString(proj.getMetadata("GG_Turret").get(0).asString());
					if(event.getHitEntity().getUniqueId().equals(projTurrID)) {
						event.getEntity().remove();
					} else {
			
					}
				}
			} else if(event.getHitEntity() instanceof LivingEntity && proj.hasMetadata("GG_Turret")) {
				UUID projTurrID = UUID.fromString(proj.getMetadata("GG_Turret").get(0).asString());
				if(this.manager.getTurretById.containsKey(projTurrID) && this.manager.getTurret(projTurrID) != null) {
					TurretConfig shooter = this.manager.getTurret(projTurrID).config;
					event.getEntity().remove();
					((LivingEntity)event.getHitEntity()).damage(shooter.getBulletDamage(), event.getEntity());
				}
			}
		}
		if(event.getEntity() != null && event.getEntity().hasMetadata("GG_TurretShell")) {
			UUID turrID = UUID.fromString(event.getEntity().getMetadata("GG_Turret").get(0).asString());
			Turret turret = this.manager.getTurret(turrID);
			if(event.getEntity().hasMetadata("GG_TurretShell_HE")) {
				try {
					/*Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {*/
							Util.createExplosion(event.getEntity().getLocation(), false, false, false, false, turret.config.getBulletDamage().floatValue(), turret.getGunner().getUniqueId(), turret.config.getBulletDamage().intValue() / 3, false);
						/*}
					});*/
				} catch(NullPointerException ex) {
					//No Error
				}
			}
			event.getEntity().remove();
		}
		if(event.getEntity() != null && event.getEntity().hasMetadata("GG_TurretShell") && event.getHitBlock() != null) {
			Material m = event.getHitBlock().getType();
			if((GunGamePlugin.instance.griefHelper.isGGWorld(event.getEntity().getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(GriefType.SHOTS_BREAK_GLASS, event.getEntity().getWorld())) &&
					Util.isGlass(m)
				) {
				event.getHitBlock().getWorld().playSound(event.getHitBlock().getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
				event.getHitBlock().breakNaturally();
				event.getHitBlock().setType(Material.AIR);
				event.getEntity().remove();
			}
		}
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onTurretDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof ArmorStand) {
			if(event.getEntity().hasMetadata("GG_Turret")) {
				event.setCancelled(true);
				Turret turret = this.manager.getTurret(event.getEntity().getUniqueId());
				if(event.getDamager() instanceof Player && event.getDamager().hasMetadata("GG_isTurretRider") && UUID.fromString(event.getDamager().getMetadata("GG_TurretID").get(0).asString()).equals(turret.turretID)) {
					if(turret.getMagazine() <= (turret.config.getMagazineSize() - turret.config.getNeededAmmo().getShotCount())) {
						//BEGIN RELOAD
						TurretReloadEvent reloadevent = new TurretReloadEvent((Player)event.getDamager(), turret, turret.reloader);
						Bukkit.getServer().getPluginManager().callEvent(reloadevent);
						if(!reloadevent.isCancelled()) {
							turret.setReloading(true);
						}
					}
				} else {				
					TurretTakeDamageEvent damageEvent = new TurretTakeDamageEvent(turret, event);
					Bukkit.getServer().getPluginManager().callEvent(damageEvent);
					if(!damageEvent.isCancelled()) {
						if(turret != null) {
							turret.takeDamage(event.getFinalDamage());
							if(event.getDamager() instanceof Projectile) {
								event.getDamager().remove();
							}
						}
					}
				}
				
	
			}
		}
	}
}
