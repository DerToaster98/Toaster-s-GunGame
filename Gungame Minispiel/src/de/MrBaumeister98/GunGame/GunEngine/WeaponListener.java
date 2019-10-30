package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.API.GrenadeEquipEvent;
import de.MrBaumeister98.GunGame.API.GrenadeThrowEvent;
import de.MrBaumeister98.GunGame.API.LandminePlaceEvent;
import de.MrBaumeister98.GunGame.API.LandmineTriggerEvent;
import de.MrBaumeister98.GunGame.API.WeaponEquipEvent;
import de.MrBaumeister98.GunGame.API.WeaponReloadEvent;
import de.MrBaumeister98.GunGame.API.WeaponShootEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Enums.EGunType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.EWeaponType;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.AirstrikeRunnable_v1_13_up;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.AssaultShootRunnable;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.LandmineExplodeRunnable;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.MinigunShootRunnable;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.ReloadRunnable;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.ShootRunnable;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public class WeaponListener implements Listener {
	
	private WeaponManager manager;
	private HashMap<UUID, ReloadRunnable> reloadingMap;
	private HashMap<UUID, MinigunShootRunnable> minigunprocessess;
	private HashMap<UUID, AssaultShootRunnable> assaultprocessess;
	private List<Player> zooming;
	
	public WeaponListener(WeaponManager manager) {
		this.manager = manager;
		this.reloadingMap = new HashMap<UUID, ReloadRunnable>();
		this.minigunprocessess = new HashMap<UUID, MinigunShootRunnable>();
		this.assaultprocessess = new HashMap<UUID, AssaultShootRunnable>();
		this.zooming = new ArrayList<Player>();
	}
	public void doneReloading(UUID player, ReloadRunnable runnable) {
		this.reloadingMap.remove(player, runnable);
	}
	public void addMinigunTask(UUID owner, MinigunShootRunnable msr) {
		this.minigunprocessess.put(owner, msr);
	}
	public void addAssaultTask(UUID owner, AssaultShootRunnable asr) {
		this.assaultprocessess.put(owner, asr);
	}
	public Boolean isShootingMinigun(UUID shooterID) {
		if(this.minigunprocessess.containsKey(shooterID)) {
			if(this.minigunprocessess.get(shooterID).isRunning()) {
				return true;
			}
		}
		return false;
	}
	public Boolean isShootingAssaultGun(UUID shooterID) {
		if(this.assaultprocessess.containsKey(shooterID)) {
			if(this.assaultprocessess.get(shooterID).isRunning()) {
				return true;
			}
		}
		return false;
	}
	public void cancelShooting(UUID shooterID) {
		if(isShootingMinigun(shooterID)) {
			try {
				this.minigunprocessess.get(shooterID).cancel();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			this.minigunprocessess.remove(shooterID);
		}
		if(isShootingAssaultGun(shooterID)) {
			try {
				this.assaultprocessess.get(shooterID).cancel();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			this.assaultprocessess.remove(shooterID);
		}
	}
	private Boolean weaponsInBothHands(Player p) {
		if(ItemUtil.isGGWeapon(p.getInventory().getItemInMainHand())) {
			if(ItemUtil.getWeaponType(p.getInventory().getItemInMainHand()).equals(EWeaponType.GUN)) {
				if(ItemUtil.isGGWeapon(p.getInventory().getItemInOffHand())) {
					if(ItemUtil.getWeaponType(p.getInventory().getItemInOffHand()).equals(EWeaponType.GUN)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(this.reloadingMap.containsKey(event.getPlayer().getUniqueId())) {
			try {
				this.reloadingMap.get(event.getPlayer().getUniqueId()).cancelProcess();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			this.reloadingMap.remove(event.getPlayer().getUniqueId());
		}
		if(isShootingMinigun(event.getPlayer().getUniqueId()) || isShootingAssaultGun(event.getPlayer().getUniqueId())) {
			cancelShooting(event.getPlayer().getUniqueId());
		}
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if(this.reloadingMap.containsKey(event.getPlayer().getUniqueId())) {
			try {
				this.reloadingMap.get(event.getPlayer().getUniqueId()).cancelProcess();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			this.reloadingMap.remove(event.getPlayer().getUniqueId());
		}
		if(isShootingMinigun(event.getPlayer().getUniqueId()) || isShootingAssaultGun(event.getPlayer().getUniqueId())) {
			cancelShooting(event.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onDispenserDispenseGrenade(BlockDispenseEvent event) {
		ItemStack item = event.getItem();
		if(ItemUtil.isGGWeapon(item)) {
			EWeaponType wType = ItemUtil.getWeaponType(item);
			if(wType.equals(EWeaponType.GRENADE)) {
				if(event.getBlock().getType().equals(Material.DISPENSER)) {
					event.setCancelled(true);
					
					Dispenser dispenser = (Dispenser) event.getBlock().getState();
					BlockFace facingFace = ((org.bukkit.material.Dispenser)dispenser.getData()).getFacing();
					Location dispenseAt = event.getBlock().getRelative(facingFace).getLocation();//.getBlock().getRelative(facingFace).getLocation().getBlock().getLocation();
					
					Item itm = dispenseAt.getWorld().dropItem(dispenseAt, item);
					Double rd = 0.0D;
					//Random random = new Random();
					rd = Util.getRandomDouble(); 
					itm.setVelocity(event.getVelocity().multiply((1.0D + rd)));
					//item.setAmount(0);
					
					this.manager.getGrenade(item).throwIt(itm, null, dispenseAt);
				}
			}
		}
	}
	
	@EventHandler
	public void onShoot(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(weaponsInBothHands(p) && !event.getAction().equals(Action.PHYSICAL)) {
			Gun gMain = this.manager.getGun(p.getInventory().getItemInMainHand());
			Gun gSecondary = this.manager.getGun(p.getInventory().getItemInOffHand());
			if(gMain.getAkimboAllowed() && gSecondary.getAkimboAllowed()) {
				if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(!Util.isInteractable(event.getClickedBlock())/*(event.getClickedBlock() instanceof Openable ||
							event.getClickedBlock() instanceof Door ||
							event.getClickedBlock() instanceof TrapDoor ||
							event.getClickedBlock() instanceof Button ||
							event.getClickedBlock() instanceof Lever)*/) {
						event.setCancelled(true);
						//type = WeaponType.GUN;
						if(isShootingAssaultGun(p.getUniqueId())) {
							event.setCancelled(true);
							cancelShooting(p.getUniqueId());
						}
						else if(GunItemUtil.readyToShoot(p.getInventory().getItemInMainHand())) {
							Boolean oh = false;
							WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(event.getItem()));
							Bukkit.getServer().getPluginManager().callEvent(shootevent);
							if(!shootevent.isCancelled()) {
								BukkitRunnable shootProcess = new ShootRunnable(p, p.getInventory().getHeldItemSlot(), event.getItem(), oh, this, false);
								shootProcess.run();
							}
						} else if(GunItemUtil.isOutOfAmmo(p.getInventory().getItemInMainHand())) {
							gMain.getSoundSet().outOfAmmoSound.play(p.getWorld(), p.getLocation());
							
							this.manager.visualHelper.sendOutOfAmmo(p);
						}
						
						//STAT
						if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
							GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponShots();
						}
						//STATEND
					}
				} 
				else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					if(!Util.isInteractable(event.getClickedBlock())/*(event.getClickedBlock() instanceof Openable ||
							event.getClickedBlock() instanceof Door ||
							event.getClickedBlock() instanceof TrapDoor ||
							event.getClickedBlock() instanceof Button ||
							event.getClickedBlock() instanceof Lever)*/) {
						event.setCancelled(true);
						//type = WeaponType.GUN;
						
						if(isShootingAssaultGun(p.getUniqueId())) {
							event.setCancelled(true);
							cancelShooting(p.getUniqueId());
						}
						else if(GunItemUtil.readyToShoot(p.getInventory().getItemInOffHand())) {
							Boolean oh = true;
							WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(p.getInventory().getItemInOffHand()));
							Bukkit.getServer().getPluginManager().callEvent(shootevent);
							if(!shootevent.isCancelled()) {
								BukkitRunnable shootProcess = new ShootRunnable(p, p.getInventory().getHeldItemSlot(), p.getInventory().getItemInOffHand(), oh, this, false);
								shootProcess.run();
							}
						} else if(GunItemUtil.isOutOfAmmo(p.getInventory().getItemInOffHand())) {
							gSecondary.getSoundSet().outOfAmmoSound.play(p.getWorld(), p.getLocation());
							
							this.manager.visualHelper.sendOutOfAmmo(p);
						}
						
						//STAT
						if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
							GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponShots();
						}
						//STATEND
					}
				}
				//NO AKIMBO
			} else if(!event.getAction().equals(Action.PHYSICAL)){
				if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(!Util.isInteractable(event.getClickedBlock())/*(event.getClickedBlock() instanceof Openable ||
							event.getClickedBlock() instanceof Door ||
							event.getClickedBlock() instanceof TrapDoor ||
							event.getClickedBlock() instanceof Button ||
							event.getClickedBlock() instanceof Lever)*/) {
						event.setCancelled(true);
						//type = WeaponType.GUN;
						
						if(GunItemUtil.readyToShoot(p.getInventory().getItemInMainHand()) || gMain.getType().equals(EGunType.MINIGUN) || gMain.getType().equals(EGunType.MINIGUN_PLASMA)) {
							if(gMain.getType().equals(EGunType.MINIGUN) || gMain.getType().equals(EGunType.MINIGUN_PLASMA)) {
								if(isShootingMinigun(p.getUniqueId())) {
									event.setCancelled(true);
									cancelShooting(p.getUniqueId());
								} else {
									Boolean oh = false;
									WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(event.getItem()));
									Bukkit.getServer().getPluginManager().callEvent(shootevent);
									if(!shootevent.isCancelled()) {
										BukkitRunnable shootProcess = new ShootRunnable(p, p.getInventory().getHeldItemSlot(), event.getItem(), oh, this, false);
										shootProcess.run();
									}
									
								}
							} else {
								Boolean ass = false;
								if(gMain.getType().equals(EGunType.ASSAULT) || gMain.getType().equals(EGunType.ASSAULT_PLASMA)) {
									if(isShootingAssaultGun(p.getUniqueId())) {
										event.setCancelled(true);
										cancelShooting(p.getUniqueId());
									} else {
										if(p.isSneaking() && !isShootingAssaultGun(p.getUniqueId())) {
											ass = true;
										}
									}
								}
								Boolean oh = false;
								WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(event.getItem()));
								Bukkit.getServer().getPluginManager().callEvent(shootevent);
								if(!shootevent.isCancelled()) {
									BukkitRunnable shootProcess = new ShootRunnable(p, p.getInventory().getHeldItemSlot(), event.getItem(), oh, this, ass);
									shootProcess.run();
								}
							}
						} else if(GunItemUtil.isOutOfAmmo(p.getInventory().getItemInMainHand())) {
							gMain.getSoundSet().outOfAmmoSound.play(p.getWorld(), p.getLocation());
							
							this.manager.visualHelper.sendOutOfAmmo(p);
						}
						
						//STAT
						if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
							GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponShots();
						}
						//STATEND
					}
				}
				else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					if(p.isSneaking()) {
						event.setCancelled(true);
						if(gMain.getScopeEnabled()) {
							//Zoom
							gMain.getSoundSet().toggleZoom.play(p.getWorld(), p.getLocation());
							if(this.zooming.contains(p)) {
								this.zooming.remove(p);
								p.removePotionEffect(PotionEffectType.SLOW);
								p.removePotionEffect(PotionEffectType.NIGHT_VISION);
							} else {
								this.zooming.add(p); 
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255), true);
								p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1), true);
							}
						}
					}/* else if(gMain.getType().equals(GunType.ASSAULT) || gMain.getType().equals(GunType.ASSAULT_PLASMA)) {
						gMain.getSoundSet().shootSound.play(p.getWorld(), p.getLocation());
						Integer slot = p.getInventory().getHeldItemSlot();
						Boolean oh = false;
						if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
							oh = true;
						}
						if(isShootingAssaultGun(p.getUniqueId())) {
								cancelShooting(p.getUniqueId());
						} else {
							BukkitRunnable shootProcess = new ShootRunnable(p, slot, event.getItem(), oh, this, true);
							shootProcess.run();
						}						
						//STAT
						if(Main.plugin.arenaManager.isIngame(p)) {
							Main.plugin.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponShots();
						}
						//STATEND
					}*/
				}
			}
		} else {
			//Physical Action-->Interact with entity
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				ItemStack item = event.getItem();
				if(ItemUtil.isGGWeapon(item)) {
					EWeaponType WType = ItemUtil.getWeaponType(item);
					if(!Util.isInteractable(event.getClickedBlock()) || 
							(
									Util.isInteractable(event.getClickedBlock()) && 
									p.isSneaking() && 
									WType.equals(EWeaponType.GRENADE
								)
						)/*(event.getClickedBlock() instanceof Openable ||
							event.getClickedBlock() instanceof Door ||
							event.getClickedBlock() instanceof TrapDoor ||
							event.getClickedBlock() instanceof Button ||
							event.getClickedBlock() instanceof Lever)*/) {
						
						String itemName = ItemUtil.getString(item, "GGGunName");
						Integer slot = p.getInventory().getHeldItemSlot();
						switch(WType) {
							default:
								event.setCancelled(false);
							case GUN:
								event.setCancelled(true);
								//type = WeaponType.GUN;
								Gun gun = this.manager.getGun(itemName);
								
								if(GunItemUtil.readyToShoot(item) || gun.getType().equals(EGunType.MINIGUN) || gun.getType().equals(EGunType.MINIGUN_PLASMA)) {
									if(gun.getType().equals(EGunType.MINIGUN) || gun.getType().equals(EGunType.MINIGUN_PLASMA)) {
										if(isShootingMinigun(p.getUniqueId())) {
											event.setCancelled(true);
											cancelShooting(p.getUniqueId());
										} else {
											Boolean oh = false;
											if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
												oh = true;
											}
											Boolean ass = false;
											if(gun.getType().equals(EGunType.ASSAULT) || gun.getType().equals(EGunType.ASSAULT_PLASMA)) {
												if(isShootingAssaultGun(p.getUniqueId())) {
													cancelShooting(p.getUniqueId());
												} else {
													if(p.isSneaking() && !isShootingAssaultGun(p.getUniqueId())) {
														ass = true;
													}
												}
											}
											WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(event.getItem()));
											Bukkit.getServer().getPluginManager().callEvent(shootevent);
											if(!shootevent.isCancelled()) {
												BukkitRunnable shootProcess = new ShootRunnable(p, slot, event.getItem(), oh, this, ass);
												shootProcess.run();
											}
											
										}
									} else {
										Boolean oh = false;
										if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
											oh = true;
										}
										Boolean ass = false;
										if(gun.getType().equals(EGunType.ASSAULT) || gun.getType().equals(EGunType.ASSAULT_PLASMA)) {
											if(isShootingAssaultGun(p.getUniqueId())) {
												event.setCancelled(true);
												cancelShooting(p.getUniqueId());
											} else {
												if(p.isSneaking()) {
													ass = true;
												}
											}
										}
										WeaponShootEvent shootevent = new WeaponShootEvent(p, this.manager.getGun(event.getItem()));
										Bukkit.getServer().getPluginManager().callEvent(shootevent);
										if(!shootevent.isCancelled()) {
											BukkitRunnable shootProcess = new ShootRunnable(p, slot, event.getItem(), oh, this, ass);
											shootProcess.run();
										}
										
									}
								} else if(GunItemUtil.isOutOfAmmo(item)) {
									gun.getSoundSet().outOfAmmoSound.play(p.getWorld(), p.getLocation());
									
									this.manager.visualHelper.sendOutOfAmmo(p);
								}
								
								
								break;
								
							case GRENADE:
								event.setCancelled(true);
								//type = WeaponType.GRENADE;
								Grenade grenade = this.manager.getGrenade(itemName);
								
								GrenadeThrowEvent throwevent = new GrenadeThrowEvent(p, grenade);
								Bukkit.getServer().getPluginManager().callEvent(throwevent);
								if(!throwevent.isCancelled()) {
									grenade.throwIt(p, 1.5D);
									
									//STAT
									if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
										GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementGrenadesThrown();
									}
									//STATEND
									
									if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
										int amount = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount();
										if(amount > 1) {
											p.getInventory().getItem(p.getInventory().getHeldItemSlot()).setAmount(amount -1);
										} 
										if(amount == 1) {
											p.getInventory().remove(p.getInventory().getItem(p.getInventory().getHeldItemSlot()));
										}
									}
								}
								
								break;
						}
					}
				}
			}
			else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if(p.isSneaking()) {
					ItemStack item = event.getItem();
					if(ItemUtil.isGGWeapon(item)) {
						/*if(!(event.getClickedBlock() instanceof Openable ||
								event.getClickedBlock() instanceof Door ||
								event.getClickedBlock() instanceof TrapDoor ||
								event.getClickedBlock() instanceof Button ||
								event.getClickedBlock() instanceof Lever)) {*/
							EWeaponType WType = ItemUtil.getWeaponType(item);
							if(WType.equals(EWeaponType.GUN)) {
								event.setCancelled(true);
								Gun gun = this.manager.getGun(item);
								if(gun.getScopeEnabled()) {
									//Zoom
									gun.getSoundSet().toggleZoom.play(p.getWorld(), p.getLocation());
									if(this.zooming.contains(p)) {
										this.zooming.remove(p);
										p.removePotionEffect(PotionEffectType.SLOW);
										p.removePotionEffect(PotionEffectType.NIGHT_VISION);
									} else {
										this.zooming.add(p); 
										p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255), true);
										p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1), true);
									}
								}
							}
						//}
					}
				}
			}
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(this.zooming.contains(event.getEntity())) {
			event.getEntity().removePotionEffect(PotionEffectType.SLOW);
			event.getEntity().removePotionEffect(PotionEffectType.NIGHT_VISION);
			this.zooming.remove(event.getEntity());
		}
		if(isShootingMinigun(event.getEntity().getUniqueId()) || isShootingAssaultGun(event.getEntity().getUniqueId())) {
			cancelShooting(event.getEntity().getUniqueId());
		}
		if(this.reloadingMap.containsKey(event.getEntity().getUniqueId())) {
			try {
				this.reloadingMap.get(event.getEntity().getUniqueId()).cancelProcess();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			this.reloadingMap.remove(event.getEntity().getUniqueId());
		}
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		ItemStack item = event.getCursor();
		if(ItemUtil.isGGWeapon(item)) {
			if(ItemUtil.getWeaponType(item).equals(EWeaponType.GUN)) {
				if(GunItemUtil.isReloading(item)) {
					event.setCancelled(true);
					try {
						this.reloadingMap.get(((Player)event.getWhoClicked()).getUniqueId()).cancelProcess();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					this.reloadingMap.remove(event.getWhoClicked().getUniqueId());
				} 
				if(isShootingMinigun(event.getWhoClicked().getUniqueId()) || isShootingAssaultGun(event.getWhoClicked().getUniqueId())) {
					event.setCancelled(true);
					cancelShooting(event.getWhoClicked().getUniqueId());
				}
				if(this.reloadingMap.containsKey(event.getWhoClicked().getUniqueId())) {
					event.setCancelled(true);
					try {
						this.reloadingMap.get(((Player)event.getWhoClicked()).getUniqueId()).cancelProcess();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					this.reloadingMap.remove(event.getWhoClicked().getUniqueId());
				}
			}
		}
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if(ItemUtil.isGGWeapon(item)) {
			if(ItemUtil.getWeaponType(item).equals(EWeaponType.GUN)) {
				if(GunItemUtil.isReloading(item)) {
					event.setCancelled(true);
					try {
						this.reloadingMap.get(((Player)event.getWhoClicked()).getUniqueId()).cancelProcess();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					this.reloadingMap.remove(event.getWhoClicked().getUniqueId());
				}
				if(isShootingMinigun(event.getWhoClicked().getUniqueId()) || isShootingAssaultGun(event.getWhoClicked().getUniqueId())) {
					event.setCancelled(true);
					cancelShooting(event.getWhoClicked().getUniqueId());
				}
				if(this.reloadingMap.containsKey(event.getWhoClicked().getUniqueId())) {
					event.setCancelled(true);
					try {
						this.reloadingMap.get(event.getWhoClicked().getUniqueId()).cancelProcess();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					this.reloadingMap.remove(event.getWhoClicked().getUniqueId());
				}
			}
		}
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();
		if(ItemUtil.isGGWeapon(item) && !p.hasMetadata("GG_isTurretRider")) {
			if(ItemUtil.getWeaponType(item).equals(EWeaponType.GUN)) {
				/*if(reloadingMap.containsKey(p.getUniqueId()) && reloadingMap.get(p.getUniqueId()) != null) {
					event.setCancelled(true);
				} else {*/
					if(p.isSneaking()) {
						event.setCancelled(false);
					} else {
						if(isShootingMinigun(p.getUniqueId())) {
							//event.getItemDrop().getItemStack().setType(Material.AIR);
							//event.getItemDrop().remove();
							event.setCancelled(true);
							//cancelShooting(p.getUniqueId());
							
							//event.setCancelled(true);
						}
						else if(isShootingAssaultGun(p.getUniqueId())) {
							event.setCancelled(true);
							//cancelShooting(p.getUniqueId());							
						} else {
						//event.setCancelled(true);
						event.getItemDrop().getItemStack().setType(Material.AIR);
						event.getItemDrop().remove();
						///////////////////////////STAT///////////////////////////////
						if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
							GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponsReloaded();
						}
						///////////////////////////STAT-END///////////////////////////
						Integer slot = new Integer(event.getPlayer().getInventory().getHeldItemSlot());
						ReloadRunnable process = new ReloadRunnable(p, slot, item, false);
						WeaponReloadEvent reloadevent = new WeaponReloadEvent(p, this.manager.getGun(item), process);
						Bukkit.getServer().getPluginManager().callEvent(reloadevent);
						if(!reloadevent.isCancelled()) {
							this.reloadingMap.put(p.getUniqueId(), process);
							process.run();
						}
						}
				}
			}
			if(ItemUtil.getWeaponType(item).equals(EWeaponType.GRENADE)) {
				event.setCancelled(false);
			}
		} else {
			if(!p.hasMetadata("GG_isTurretRider") && ItemUtil.isGGWeapon(p.getInventory().getItemInOffHand()) && (ItemUtil.getWeaponType(p.getInventory().getItemInOffHand()).equals(EWeaponType.GUN) == true)) {
					if(p.isSneaking()) {
						event.setCancelled(false);
					} 
					else {
					event.setCancelled(true);
					event.getItemDrop().getItemStack().setType(Material.AIR);
					event.getItemDrop().remove();
					///////////////////////////STAT///////////////////////////////
					if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
						GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementWeaponsReloaded();
					}
					///////////////////////////STAT-END///////////////////////////
					//Integer slot = new Integer(event.getPlayer().getInventory().getHeldItemSlot());
					ReloadRunnable process = new ReloadRunnable(p, null, p.getInventory().getItemInOffHand(), true);
					WeaponReloadEvent reloadevent = new WeaponReloadEvent(p, this.manager.getGun(item), process);
					Bukkit.getServer().getPluginManager().callEvent(reloadevent);
					if(!reloadevent.isCancelled()) {
						this.reloadingMap.put(p.getUniqueId(), process);
						process.run();
					}
				}
			}
		}
	}
	@EventHandler
	public void cancelEggHatch(PlayerEggThrowEvent event) {
		if(event.getEgg().hasMetadata("GG_Projectile")) {
			event.setHatching(false);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		if(event.getEntity().hasMetadata("GG_Projectile")) {
			Boolean b = true;
			Boolean r = true;
			
			Gun gun = this.manager.getGun(event.getEntity().getMetadata("GG_OwningWeapon").get(0).asString());
			
			if(event.getHitBlock() != null && event.getHitEntity() == null) {
				Location loc = event.getEntity().getLocation();
				//if(!GunGamePlugin.instance.serverPre113) {
					loc.getWorld().spawnParticle(gun.getHitParticle(),
							loc.getX() + gun.getHitParticleX(),
							loc.getY() + gun.getHitParticleY(),
							loc.getZ() + gun.getHitParticleZ(), 
							gun.getHitParticleCount(),
							gun.getHitParticleDX(),
							gun.getHitParticleDY(),
							gun.getHitParticleDZ(),
							gun.getHitParticleSpeed(),
							gun.getHitParticleExtra()
					);
				//}
			}
			UUID shooterID = UUID.fromString(event.getEntity().getMetadata("GG_Shooter").get(0).asString());
			if(event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {			
				if(!event.getHitEntity().getUniqueId().equals(shooterID)) {
					if(event.getHitEntity().getType().equals(EntityType.MINECART) && event.getHitEntity().hasMetadata("GG_Tank")) {
						UUID tankID = UUID.fromString(event.getHitEntity().getMetadata("GG_Tank").get(0).asString());
						Tank tnk = GunGamePlugin.instance.tankManager.getTankByID(tankID);
						if(tnk.getDriverUUID() == null || !shooterID.equals(tnk.getDriverUUID())) {
							r = false;
							b = false;
							ProjectileHitEvent hitEvent = new ProjectileHitEvent(event.getEntity(), tnk.getBodyArmorStand());
							Bukkit.getPluginManager().callEvent(hitEvent);
						}
					} else {
						//HEADSHOT CALCULATION
		
						gun.getDamSet().damage((LivingEntity) event.getHitEntity(), event.getEntity().getLocation(), shooterID);
					}
				} else {
					b = false;
				}
			}
			if(event.getHitEntity() != null && (event.getHitEntity().hasMetadata("GG_Projectile") || event.getHitEntity() instanceof TNTPrimed)) {
				b = false;
			}
			if(event.getHitBlock() != null && 
					((GunGamePlugin.instance.griefHelper.isGGWorld(event.getHitBlock().getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.SHOTS_BREAK_GLASS, event.getHitBlock().getWorld())) &&
							Util.isGlass(event.getHitBlock().getType()))) {
				event.getHitBlock().getWorld().playSound(event.getHitBlock().getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
				event.getHitBlock().breakNaturally();
				event.getHitBlock().setType(Material.AIR);
			}
			if(event.getHitBlock() != null && event.getHitBlock().getType().equals(Material.TNT) && ((GunGamePlugin.instance.griefHelper.isGGWorld(event.getEntity().getWorld()) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.BULLETS_IGNITE_TNT, event.getEntity().getWorld())))) {
				UUID cause = ((Player)((Projectile)event.getEntity()).getShooter()).getUniqueId();
				event.getHitBlock().setType(Material.AIR);
				Util.createExplosion(event.getHitBlock().getLocation(), false, false, false, false, 3.0f, cause, 2, false, 40 + Util.getRandomNumber(80));
				b = false;
			}
			if(event.getHitBlock() != null && event.getHitBlock().hasMetadata("GG_Landmine")) {
				Block block = event.getHitBlock();
				UUID triggerID = shooterID;
				UUID placerID = UUID.fromString(block.getMetadata("GG_Landmine_Placer").get(0).asString());
				if(!triggerID.equals(placerID)) {
					Landmine mine = this.manager.getLandmine(block.getMetadata("GG_Landmine_Name").get(0).asString());
					LandmineExplodeRunnable ler = new LandmineExplodeRunnable(mine, block, placerID);
					LandmineTriggerEvent triggerEvent = new LandmineTriggerEvent(mine, (Entity)Bukkit.getPlayer(triggerID), ler);
					Bukkit.getServer().getPluginManager().callEvent(triggerEvent);
					if(!triggerEvent.isCancelled()) {
						ler.run();
						block.removeMetadata("GG_Landmine", GunGamePlugin.instance);
						block.removeMetadata("GG_Landmine_Name", GunGamePlugin.instance);
						block.removeMetadata("GG_Landmine_Placer", GunGamePlugin.instance);
					}
				}
			}
			if(b && event.getEntity().hasMetadata("GG_Projectile_Explode_On_Contact")) {
				Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						Util.createExplosion(event.getEntity().getLocation(), false, false, false, false, gun.getShotDamage().floatValue(), UUID.fromString(event.getEntity().getMetadata("GG_Shooter").get(0).asString()), gun.getShotDamage().intValue() / 3, false);
					}
				});
			}
			if(r) {
				event.getEntity().remove();
			}
		}
	}

	@EventHandler
	public void onEquip(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		//�berpr�fe, ob die vorherige Waffe nachl�dt, falls ja, !sofort! den Nachladeprozess canceln
		if(GunItemUtil.isReloading(p.getInventory().getItem(event.getPreviousSlot())) && this.reloadingMap.containsKey(p.getUniqueId())) {
			this.reloadingMap.get(p.getUniqueId()).cancelProcess();
		} //else {
		if(this.zooming.contains(p)) {
			this.zooming.remove(p);
			p.removePotionEffect(PotionEffectType.SLOW);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
		if(isShootingMinigun(p.getUniqueId()) || isShootingAssaultGun(p.getUniqueId())) {
			cancelShooting(p.getUniqueId());
		}
			ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
			//if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.isGGWeapon(item)) {
				EWeaponType WType = ItemUtil.getWeaponType(item);
				//String weaponName = ItemUtil.getString(item, "GGGunName");
				switch(WType) {
					default: 
						event.setCancelled(false);
					case GUN:
						Gun gun = this.manager.getGun(item);
						if(gun != null) {
							WeaponEquipEvent equipevent = new WeaponEquipEvent(p, gun);
							Bukkit.getServer().getPluginManager().callEvent(equipevent);
							if(!equipevent.isCancelled()) {
								gun.getSoundSet().equipSound.play(p.getWorld(), p.getLocation());
								if(GunItemUtil.isCoolingDown(item)) {
									if(GunItemUtil.getRemainingShots(item) > 1 ) {
										this.manager.visualHelper.sendRemainingShots(p, GunItemUtil.getRemainingShots(item) -1, gun);
									} else {
										this.manager.visualHelper.sendRemainingShots(p, 0, gun);
									}
								} else {
									this.manager.visualHelper.sendRemainingShots(p, GunItemUtil.getRemainingShots(item), gun);
								}
								if(gun.getRealisticVisuals()) {
									//this.manager.visualHelper.sendRealisticHolding(gun, p);
								}
							} else {
								event.setCancelled(true);
							}
						}
						
						break;
					case GRENADE:
						Grenade grenade = this.manager.getGrenade(item);
						
						GrenadeEquipEvent equipevent = new GrenadeEquipEvent(p, grenade);
						Bukkit.getServer().getPluginManager().callEvent(equipevent);
						if(!equipevent.isCancelled()) {
							if(grenade != null) {
								WeaponSoundSet s = grenade.getSoundSet();
								s.equipSound.play(p.getWorld(), p.getLocation());
							}
						} else {
							event.setCancelled(true);
						}
						
						break;
				}
			}
		//}
	}
	@EventHandler
	public void onAirstrikePlace(PlayerInteractEvent event) {
		if(ItemUtil.isGGAirstrike(event.getItem())) {
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.PHYSICAL)) {
				if(event.getBlockFace().equals(BlockFace.UP)) {
					event.setCancelled(false);
					Airstrike strike = this.manager.getAirstrike(event.getItem());
					
					Block block = event.getClickedBlock().getRelative(BlockFace.UP);
					
						AirstrikeRunnable_v1_13_up process = new AirstrikeRunnable_v1_13_up(event.getPlayer().getUniqueId(), block, strike);
						process.run();
					
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
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
 	}
	@EventHandler
	public void onLandminePlace(PlayerInteractEvent event) {
		if(ItemUtil.isGGLandmine(event.getItem())) {
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.PHYSICAL)) {
				if(!event.getBlockFace().equals(BlockFace.DOWN)) {
					event.setCancelled(true);
					Landmine mine = this.manager.getLandmine(event.getItem());
					
					Block block = event.getClickedBlock().getRelative(event.getBlockFace());
					
					if(Util.isFullBlock(block.getRelative(BlockFace.DOWN).getLocation())) {
						LandminePlaceEvent placeevent = new LandminePlaceEvent(event.getPlayer(), mine, block.getLocation());
						Bukkit.getServer().getPluginManager().callEvent(placeevent);
						if(!placeevent.isCancelled()) {
							block.setType(mine.getMaterial());
							
							block.setMetadata("GG_Landmine", new FixedMetadataValue(GunGamePlugin.instance, true));
							block.setMetadata("GG_Landmine_Name", new FixedMetadataValue(GunGamePlugin.instance, mine.getName()));
							block.setMetadata("GG_Landmine_Placer", new FixedMetadataValue(GunGamePlugin.instance, event.getPlayer().getUniqueId().toString()));
							
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
					} else {
						event.setCancelled(true);
					}
					
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onStepOnMine(PlayerMoveEvent event) {
		Block block = event.getTo().getBlock();
		if(block.hasMetadata("GG_Landmine")) {
			UUID triggerID = event.getPlayer().getUniqueId();
			UUID placerID = UUID.fromString(block.getMetadata("GG_Landmine_Placer").get(0).asString());
			if(!triggerID.equals(placerID)) {
				Landmine mine = this.manager.getLandmine(block.getMetadata("GG_Landmine_Name").get(0).asString());
				LandmineExplodeRunnable ler = new LandmineExplodeRunnable(mine, block, placerID);
				LandmineTriggerEvent triggerEvent = new LandmineTriggerEvent(mine, (Entity)event.getPlayer(), ler);
				Bukkit.getServer().getPluginManager().callEvent(triggerEvent);
				if(!triggerEvent.isCancelled()) {
					ler.run();
					block.removeMetadata("GG_Landmine", GunGamePlugin.instance);
					block.removeMetadata("GG_Landmine_Name", GunGamePlugin.instance);
					block.removeMetadata("GG_Landmine_Placer", GunGamePlugin.instance);
				}
			}
		}
	}
	
	@EventHandler
	public void onAirstrikeBombHitGround(EntityChangeBlockEvent event) {
		if(event.getEntity().getType().equals(EntityType.FALLING_BLOCK)) {
			FallingBlock bomb = (FallingBlock) event.getEntity();
			if(bomb.hasMetadata("GG_Airstrike_Bomb")) {
				String strike = ((MetadataValue)bomb.getMetadata("GG_Airstrike_Name").get(0)).asString();
				Airstrike as = this.manager.getAirstrike(strike);
				
				UUID uid = UUID.fromString(((MetadataValue)bomb.getMetadata("GG_Airstrike_Shooter").get(0)).asString());
				
				Util.createExplosion(event.getEntity().getLocation(), as.canPlaceFire(), as.canBreakBlocks(), as.getDamage(), as.getPhysicsEnabled(), as.getPower(), uid, as.getExplosionRadius(), false);
				
				bomb.remove();
				event.setCancelled(true);
			}
		}
	}
}
