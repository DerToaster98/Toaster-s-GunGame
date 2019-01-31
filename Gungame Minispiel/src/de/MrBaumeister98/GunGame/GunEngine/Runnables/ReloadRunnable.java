package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.WeaponManager;
import de.MrBaumeister98.GunGame.GunEngine.Enums.GunType;
import de.MrBaumeister98.GunGame.GunEngine.Enums.WeaponType;

public class ReloadRunnable extends BukkitRunnable {
	
	private ItemStack gun;
	private static int subTaskID;
	private Gun gunObject;
	private Player shooter;
	private Integer slot;
	private Boolean cancelled;
	private Integer toLoad;
	private Integer ammoToRemove;
	private Boolean offHand;
	
	private WeaponManager manager;
	
	public ReloadRunnable(Player shooter, Integer gunSlot, ItemStack gun, Boolean offHand) {
		this.manager = GunGamePlugin.instance.weaponManager;
		this.shooter = shooter;
		this.slot = shooter.getInventory().getHeldItemSlot();		
		
		this.gunObject = this.manager.getGun(gun);
		this.cancelled = false;
		this.offHand = offHand;
		if(this.offHand) {
			this.gun = gun;
			this.shooter.getInventory().setItemInOffHand(gun);
		} else {
			this.gun = gun;
			this.shooter.getInventory().setItem(this.slot, gun);
		}
		
		this.manager.addProcess(this);
	}

	@Override
	public void run() {
		//this.gun = GunItemUtil.setReloading(this.gun, true);
		if(this.offHand) {
			this.shooter.getInventory().setItemInOffHand(GunItemUtil.setReloading(this.shooter.getInventory().getItemInOffHand(), true));
		} else {
			this.shooter.getInventory().setItem(this.slot, GunItemUtil.setReloading(this.shooter.getInventory().getItem(this.slot), true));
		}
		
		//IF Player is in Creative--> No Ammo needed for reload!
		//Integer magazineCapacity = this.gun.getMaxAmmo();
		
		if(GunItemUtil.getRemainingShots(this.gun) < this.gunObject.getMaxAmmo()) {
			
			if(GunItemUtil.hasPlayerAmmo(this.shooter, this.gunObject.getAmmo())) {
				this.gunObject.getSoundSet().reloadSound.play(shooter.getWorld(), shooter.getLocation());
				
				//gunI = shooter.getInventory().getItem(slot);
				
				if(this.gunObject.getType().equals(GunType.GRENADETHROWER)) {
					if(GunItemUtil.getRemainingShots(this.shooter.getInventory().getItem(this.slot)) > 0) {
						if(getPlayersAmmo(this.shooter, GunItemUtil.getLoadedGrenade(this.shooter.getInventory().getItem(this.slot))) > 0) {
							Integer toLoad2 = this.gunObject.getMaxAmmo() - GunItemUtil.getRemainingShots(this.gun);
							
							this.toLoad = toLoad2;
							this.ammoToRemove = toLoad2;
						} else {
							if(this.offHand) {
								this.manager.visualHelper.sendMissingAmmo(this.shooter, this.gunObject, GunItemUtil.getLoadedGrenade(this.shooter.getInventory().getItemInOffHand()));
							} else {
								this.manager.visualHelper.sendMissingAmmo(this.shooter, this.gunObject, GunItemUtil.getLoadedGrenade(this.shooter.getInventory().getItem(this.slot)));
							}
							
							cancelProcess();
						}
					} else {
						if(hasPlayerGrenades(this.shooter)) {
							Integer toLoad2 = this.gunObject.getMaxAmmo() - GunItemUtil.getRemainingShots(this.gun);
							
							this.toLoad = toLoad2;
							this.ammoToRemove = toLoad2;
						} else {
							this.manager.visualHelper.sendMissingGrenades(this.shooter, this.gunObject);
							cancelProcess();
						}
					}						
				} else {
					//NORMAL GUN-->Check if Player has needed Ammunition, if yes, get Amount and load it into the weapon
					Ammo needed = this.gunObject.getAmmo();
					if(GunItemUtil.hasPlayerAmmo(this.shooter, needed)) {						
						
						Integer shotsPerAmmoItem = needed.getShotCount();
						Integer magazineSpace = this.gunObject.getMaxAmmo() - GunItemUtil.getRemainingShots(this.gun);
						
						Integer ammoItemsNeeded;
						Integer modulo = magazineSpace % shotsPerAmmoItem;
						if(modulo != 0) {
							ammoItemsNeeded = magazineSpace / shotsPerAmmoItem  +1;
						} else {
							ammoItemsNeeded = magazineSpace / shotsPerAmmoItem;
						}
						
						this.ammoToRemove = ammoItemsNeeded;
						
						this.toLoad = this.gunObject.getMaxAmmo() - GunItemUtil.getRemainingShots(this.gun)/* - (this.gunObject.getMaxAmmo() - GunItemUtil.getRemainingShots(this.gun) - shotsOfAmmo)*/;
						//this.shooter.sendMessage("toLoad: " + this.toLoad);
					} else {
						this.manager.visualHelper.sendMissingAmmo(this.shooter, this.gunObject, this.gunObject.getAmmo());
						cancelProcess();
					}
				}
			} else {
				this.gunObject.getSoundSet().outOfAmmoSound.play(shooter.getWorld(), shooter.getLocation());
				this.manager.visualHelper.sendMissingAmmo(this.shooter, this.gunObject, this.gunObject.getAmmo());
				cancelProcess();
			}
					
				}
			if(!this.cancelled) {
			this.manager.visualHelper.sendReloadingWeapon(this.shooter);
			ReloadRunnable reference = this;
			subTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				if(!reference.gunObject.getType().equals(GunType.GRENADETHROWER)) {
					//Normal Gun reload (everything except Grenadelauncher)
					if(getPlayersAmmo(reference.shooter, reference.gunObject.getAmmo()) > 0) {
						
						if(reference.toLoad <= getPlayersAmmo(reference.shooter, reference.gunObject.getAmmo())) {
							if(reference.offHand) {
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), reference.gunObject.getMaxAmmo()));
							} else {
								reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), reference.gunObject.getMaxAmmo()));
							}

							removeAmmoOfPlayer(reference.shooter, reference.gunObject.getAmmo(), reference.ammoToRemove);
						} else {
							Integer shots = 0;
							if(reference.offHand) {
								shots = GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand()) + getPlayersAmmo(reference.shooter, reference.gunObject.getAmmo());
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), shots));
							} else {
								shots = GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)) + getPlayersAmmo(reference.shooter, reference.gunObject.getAmmo());
								reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), shots));
							}
							reference.shooter.getInventory().remove(reference.gunObject.getAmmo().getItem());
							removeAmmoOfPlayer(reference.shooter, reference.gunObject.getAmmo());
						}			
					} else {
						reference.manager.visualHelper.sendMissingAmmo(reference.shooter, reference.gunObject, reference.gunObject.getAmmo());
					}
				} else {
					//GrenadeLauncher reload
					if(hasPlayerGrenades(reference.shooter)) {
						//So, player has Grenades
						//If there are grenades in the weapon, search for these Grenades and how many the player has
						if(GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)) > 0) {
							Grenade inGun = null;
							if(reference.offHand) {
								inGun = GunItemUtil.getLoadedGrenade(reference.shooter.getInventory().getItemInOffHand());
							} else {
								inGun = GunItemUtil.getLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot));
							}
							
							if(reference.offHand) {
								//reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItemInOffHand(), inGun));
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItemInOffHand(), inGun.getGrenadeName()));
							} else {
								//reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot), inGun));
								reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot), inGun.getGrenadeName()));
							}
							
							if(getPlayersAmmo(reference.shooter, inGun) > 0) {
								if(reference.toLoad <= getPlayersAmmo(reference.shooter, inGun)) {
									if(reference.offHand) {
										reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), reference.toLoad + GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand())));
									} else {
										reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), reference.toLoad + GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot))));
									}
									
									removeAmmoOfPlayer(reference.shooter, inGun, reference.toLoad);
								} else {
									Integer shots = GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)) + getPlayersAmmo(reference.shooter, inGun);
									if(reference.offHand) {
										reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), shots));
									} else {
										reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), shots));
									}
									
									removeAmmoOfPlayer(reference.shooter, inGun);
								}
							} else {
								//MISSING GRENADE --> Tell player!
								reference.manager.visualHelper.sendMissingAmmo(reference.shooter, reference.gunObject, inGun);
							}
						} else {
							//GrenadeLauncher is empty --> Pick grenade left of gun, if that's not a grenade, pick right, if also not grenade --> cancel
							Grenade grenLoad = getFirstInInv(reference.shooter);
							//reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot), grenLoad));
							if(reference.offHand) {
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItemInOffHand(), grenLoad.getGrenadeName()));
							} else {
								reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot), grenLoad.getGrenadeName()));
							}
							
							Integer grenadeCount = getPlayersAmmo(reference.shooter, grenLoad);
							if(reference.offHand) {
								if(reference.toLoad <= grenadeCount) {
									reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), reference.toLoad + GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand())));
									
									removeAmmoOfPlayer(reference.shooter, grenLoad, reference.toLoad);
								} else {
									Integer shots = GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand()) + getPlayersAmmo(reference.shooter, grenLoad);
									reference.shooter.getInventory().setItemInOffHand(GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItemInOffHand(), shots));
									
									removeAmmoOfPlayer(reference.shooter, grenLoad);
								}
							} else {
								if(reference.toLoad <= grenadeCount) {
									reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), reference.toLoad + GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot))));
									
									removeAmmoOfPlayer(reference.shooter, grenLoad, reference.toLoad);
								} else {
									Integer shots = GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)) + getPlayersAmmo(reference.shooter, grenLoad);
									reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.updateRemainingShots(reference.shooter.getInventory().getItem(reference.slot), shots));
									
									removeAmmoOfPlayer(reference.shooter, grenLoad);
								}
							}
						}
					} else {
						//MISSING GRENADES AT ALL --> Tell Player
						reference.manager.visualHelper.sendMissingGrenades(reference.shooter, reference.gunObject);
						if(reference.offHand) {
							if(GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand()) == 0) {
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItemInOffHand(), "NONE"));
							}
						} else {
							if(GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)) == 0) {
								reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setLoadedGrenade(reference.shooter.getInventory().getItem(reference.slot), "NONE"));
							}
						}
					}
				}
				
				if(reference.offHand) {
					//Set Weapon to NOT in Reload Mode
					reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setReloading(reference.shooter.getInventory().getItemInOffHand(), false));
					
					
					//VISUALS
					reference.manager.visualHelper.sendRemainingShots(reference.shooter, GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItemInOffHand()), reference.gunObject);
				} else {
					//Set Weapon to NOT in Reload Mode
					reference.shooter.getInventory().setItem(reference.slot, GunItemUtil.setReloading(reference.shooter.getInventory().getItem(reference.slot), false));
					
					
					//VISUALS
					reference.manager.visualHelper.sendRemainingShots(reference.shooter, GunItemUtil.getRemainingShots(reference.shooter.getInventory().getItem(reference.slot)), reference.gunObject);
				}
			}
				
			}, this.gunObject.getReloadDuration());
			/*this.shooter.sendMessage("Your Ammo: " + getPlayersAmmo(this.shooter, this.gunObject.getAmmo()));
			this.shooter.sendMessage("Loaded Gren: " + GunItemUtil.getLoadedGrenade(this.shooter.getInventory().getItem(this.slot)).getGrenadeName());
			this.shooter.sendMessage("Ammo Needed: " + this.toLoad);*/
		}
			 
		//this.manager.removeProcess(this);
		
		//if(!this.cancelled) {
			this.manager.wl.doneReloading(this.shooter.getUniqueId(), this);
		//}
	}
	
	public void cancelProcess() {	
		this.cancelled = true;
		Bukkit.getScheduler().cancelTask(subTaskID);
		if(this.shooter != null && this.shooter.isOnline()) {
			if(this.offHand) {
				this.shooter.getInventory().setItemInOffHand(GunItemUtil.setReloading(this.shooter.getInventory().getItemInOffHand(), false));
			} else {
				if(this.shooter.isOnline()) {				
					this.shooter.getInventory().setItem(this.slot, GunItemUtil.setReloading(this.shooter.getInventory().getItem(this.slot), false));
				}
			}
		}
		this.manager.wl.doneReloading(this.shooter.getUniqueId(), this);
		//this.cancel();
	}
	private Integer getPlayersAmmo(Player p, Ammo ammo) {
		Integer c = 0;
		for(ItemStack s : p.getInventory().getContents()) {
			if(GunItemUtil.isMatchingAmmo(ammo, s)) {
				c = c + (ammo.getShotCount() * s.getAmount());
			}
		}
		return c;
	}
	private Boolean hasPlayerGrenades(Player p) {
		for(ItemStack s : p.getInventory().getContents()) {
			if(ItemUtil.isGGWeapon(s)) {
				if(ItemUtil.getWeaponType(s).equals(WeaponType.GRENADE)) {
					return true;
				}
			}
		}
		return false;
	}
	private Integer getPlayersAmmo(Player p, Grenade ammo) {
		Integer c = 0;
		for(ItemStack s : p.getInventory().getContents()) {
			if(GunItemUtil.isMatchingGrenade(ammo, s)) {
				c = c + s.getAmount();
			}
		}
		return c;
	}
	private Grenade getFirstInInv(Player p) {
		for(ItemStack stack : p.getInventory().getContents()) {
			if(ItemUtil.isGGWeapon(stack)) {
				if(ItemUtil.getWeaponType(stack).equals(WeaponType.GRENADE)) {
					return this.manager.getGrenade(stack);
				}
			}
		}
		return null;
	}
	private void removeAmmoOfPlayer(Player p, Ammo ammo, Integer count) {
		Integer slot = 0;
		while(count >0) {
			ItemStack stack = p.getInventory().getItem(slot);
			if(GunItemUtil.isMatchingAmmo(ammo, stack)) {
				if(stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() -1);
				} else {
					stack.setAmount(1);
					p.getInventory().remove(stack);
					slot = slot +1;
				}
				count = count -1;
			} else {
				slot = slot +1;
			}
		}
	}
	private void removeAmmoOfPlayer(Player p, Grenade grenade, Integer count) {
		Integer slot = 0;
		while(count >0) {
			ItemStack stack = p.getInventory().getItem(slot);
			if(GunItemUtil.isMatchingGrenade(grenade, stack)) {
				if(stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() -1);
				} else {
					stack.setAmount(1);
					p.getInventory().remove(stack);
					slot = slot +1;
				}
				count = count -1;
			} else {
				slot = slot +1;
			}
		}
	}
	private void removeAmmoOfPlayer(Player p, Ammo ammo) {
		for(int i = 0; i < p.getInventory().getContents().length; i++) {
			if(GunItemUtil.isMatchingAmmo(ammo, p.getInventory().getItem(i))) {
				p.getInventory().setItem(i, null);
			}
		}
	}
	private void removeAmmoOfPlayer(Player p, Grenade grenade) {
		for(int i = 0; i < p.getInventory().getContents().length; i++) {
			if(GunItemUtil.isMatchingGrenade(grenade, p.getInventory().getItem(i))) {
				p.getInventory().setItem(i, null);
			}
		}
	}

}
