package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.WeaponListener;
import de.MrBaumeister98.GunGame.GunEngine.WeaponManager;
import de.MrBaumeister98.GunGame.GunEngine.Enums.GunType;

public class ShootRunnable extends BukkitRunnable{
	
	private Player shooter;
	private ItemStack gun;
	private Gun gunObject;
	private Integer gunItemSlot;
	private WeaponManager manager;
	private Boolean offHand;
	private WeaponListener listener;
	private Boolean assaultVolley;
	
	private Vector direction;

	public ShootRunnable(Player shooter, Integer gunItemSlot, ItemStack gun, Boolean offHand, WeaponListener listener, Boolean assaultVolley) {
		this.manager = GunGamePlugin.instance.weaponManager;
		this.shooter = shooter;
		this.gun = gun;
		this.gunItemSlot = gunItemSlot;
		this.gunObject = this.manager.getGun(gun);
		this.offHand = offHand;
		this.listener = listener;
		this.assaultVolley = assaultVolley;
		
		this.direction = this.shooter.getLocation().getDirection().normalize();
		//this.manager.addProcess(this);
	}

	@Override
	public void run() {
		if(GunItemUtil.readyToShoot(this.gun) || this.gunObject.getType().equals(GunType.MINIGUN) || this.gunObject.getType().equals(GunType.MINIGUN_PLASMA)) {
			if(!this.gunObject.hasUsePermission() || (this.gunObject.hasUsePermission() && this.shooter.hasPermission(this.gunObject.getPermission()))) {
				//Vector v = this.shooter.getEyeLocation().getDirection();
				
				GunType type = this.gunObject.getType();
				if(!type.equals(GunType.GRENADETHROWER)) {
					float accuracy = this.gunObject.getAccuracy();
					if(accuracy > (float)0) {
						recalcVector();
						//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
					}
				}
				//Recoil
				
				if(!this.gunObject.getType().equals(GunType.MINIGUN) && !this.gunObject.getType().equals(GunType.MINIGUN_PLASMA) && !this.assaultVolley) {
					Double recoil = this.gunObject.getRecoilAmount();
					Vector move = this.shooter.getLocation().getDirection().normalize().multiply(recoil).multiply(-1.0).setY(0.0);
					if(this.shooter.isSneaking()) {
						this.shooter.setVelocity(this.shooter.getVelocity().add(move.multiply(0.25D)));
					} else {
						this.shooter.setVelocity(this.shooter.getVelocity().add(move));
					}
					Location loc = this.shooter.getEyeLocation().add(this.shooter.getLocation().getDirection().normalize().multiply(1.5D));
					//if(!GunGamePlugin.instance.serverPre113) {
						loc.getWorld().spawnParticle(this.gunObject.getShootParticle(),
								loc.getX() + this.gunObject.getShootParticleX(),
								loc.getY() + this.gunObject.getShootParticleY(),
								loc.getZ() + this.gunObject.getShootParticleZ(), 
								this.gunObject.getShootParticleCount(),
								this.gunObject.getShootParticleDX(),
								this.gunObject.getShootParticleDY(),
								this.gunObject.getShootParticleDZ(),
								this.gunObject.getShootParticleSpeed(),
								this.gunObject.getShootParticleExtra()
						);
					//}
				}
				
				/*float rUpward = this.gunObject.getRecoilAmountVertical();
				rUpward = this.shooter.getLocation().getPitch() - rUpward;
				this.shooter.getLocation().setPitch(rUpward);*/
				//Shoot
				switch(type) {
				default:
							
					break;
				case ASSAULT:
					if(gun != null) {
						if(this.assaultVolley) {
							if(this.listener.isShootingAssaultGun(this.shooter.getUniqueId())) {
								this.listener.cancelShooting(this.shooter.getUniqueId());
							} else {
								AssaultShootRunnable msr = new AssaultShootRunnable(this.gunObject, this.shooter, this.gunItemSlot, GunItemUtil.getRemainingShots(this.gun), this.manager, this.listener, false);
								msr.run();
								this.listener.addAssaultTask(this.shooter.getUniqueId(), msr);
							}
						} else {
							for(int c = 0; c < this.gunObject.getShotCount(); c++) {
								float accuracy = this.gunObject.getAccuracy();
								if(accuracy > (float)0) {
									recalcVector();
									//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
								}
								/*BulletThread bullThread =*/ new BulletThread(this.gunObject, this.shooter, this.direction);
								//bullThread.run();
							}
						}
					}
					break;
				case ASSAULT_PLASMA:
					if(gun != null) {
						if(this.assaultVolley) {
							if(this.listener.isShootingAssaultGun(this.shooter.getUniqueId())) {
								this.listener.cancelShooting(this.shooter.getUniqueId());
							} else {
								AssaultShootRunnable msr = new AssaultShootRunnable(this.gunObject, this.shooter, this.gunItemSlot, GunItemUtil.getRemainingShots(this.gun), this.manager, this.listener, true);
								msr.run();
								this.listener.addAssaultTask(this.shooter.getUniqueId(), msr);
							}
						} else {
							for(int c = 0; c < this.gunObject.getShotCount(); c++) {
								float accuracy = this.gunObject.getAccuracy();
								if(accuracy > (float)0) {
									recalcVector();
									//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
								}
								PlasmaBulletThread plasmaThread = new PlasmaBulletThread(this.gunObject, this.shooter, this.direction);
								plasmaThread.run();
							}
						}
					}
					break;
				case PLASMA:
					if(gun != null) {
						for(int c = 0; c < this.gunObject.getShotCount(); c++) {
							float accuracy = this.gunObject.getAccuracy();
							if(accuracy > (float)0) {
								recalcVector();
								//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
							}
							PlasmaBulletThread laserThread = new PlasmaBulletThread(this.gunObject, this.shooter, this.direction);
							laserThread.run();
						}
					}
					break;
				case GRENADETHROWER:
					if(gun != null && GunItemUtil.getLoadedGrenade(gun) != null) {
						for(int c = 0; c < this.gunObject.getShotCount(); c++) {
							Grenade grenade = GunItemUtil.getLoadedGrenade(gun);
							grenade.throwIt(this.shooter, this.gunObject.getShootingForce());
						}
					}
					break;
				case STANDARD:
					//FOR SHOOTING: SAME AS AUTOMATIC, DIFFERENCE: WHEN MAGAZINE IS EMPTY, AUTOMATIC WILL AUTO RELOAD!		
					if(gun != null) {
						for(int c = 0; c < this.gunObject.getShotCount(); c++) {
							float accuracy = this.gunObject.getAccuracy();
							if(accuracy > (float)0) {
								recalcVector();
								//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
							}
							/*BulletThread bullThread =*/ new BulletThread(this.gunObject, this.shooter, this.direction);
							//bullThread.run();
						}
					}
					break;
				case MINIGUN:
					//First: Warm up
					//Second: If warmed up: Fire
					if(this.listener.isShootingMinigun(this.shooter.getUniqueId())) {
						this.listener.cancelShooting(this.shooter.getUniqueId());
					} else {
						MinigunShootRunnable msr = new MinigunShootRunnable(this.gunObject, this.shooter, this.gunItemSlot, GunItemUtil.getRemainingShots(this.gun), this.manager, this.listener, false);
						msr.run();
						this.listener.addMinigunTask(this.shooter.getUniqueId(), msr);
					}
					break;
				case MINIGUN_PLASMA:
					//First: Warm up
					//Second: If warmed up: Fire
					if(this.listener.isShootingMinigun(this.shooter.getUniqueId())) {
						this.listener.cancelShooting(this.shooter.getUniqueId());
					} else {
						MinigunShootRunnable msr = new MinigunShootRunnable(this.gunObject, this.shooter, this.gunItemSlot, GunItemUtil.getRemainingShots(this.gun), this.manager, this.listener, true);
						msr.run();
						this.listener.addMinigunTask(this.shooter.getUniqueId(), msr);
					}
					break;
				case ROCKETLAUNCHER:
					if(gun != null) {
						for(int c = 0; c < this.gunObject.getShotCount(); c++) {
							float accuracy = this.gunObject.getAccuracy();
							if(accuracy > (float)0) {
								recalcVector();
								//v = v.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
							}
							RocketThread rThread = new RocketThread(this.gunObject, this.shooter, this.direction);
							rThread.run();
						}
					}
					break;
				}
				
				//Play sounds
				if(!this.gunObject.getType().equals(GunType.MINIGUN) && !this.gunObject.getType().equals(GunType.MINIGUN_PLASMA)) {
					this.gunObject.getSoundSet().shootSound.play(shooter.getWorld(), shooter.getLocation());
				}
				//Play particles, if set
						
				//Add delay before next shot
				if(this.offHand) {
					this.shooter.getInventory().setItemInOffHand(GunItemUtil.setCoolingDown(this.shooter.getInventory().getItemInOffHand(), true));
				} else {
					this.shooter.getInventory().setItem(this.gunItemSlot, GunItemUtil.setCoolingDown(this.shooter.getInventory().getItem(this.gunItemSlot), true));
				}
				
				//remove one shot
				if(!this.gunObject.getType().equals(GunType.MINIGUN) && !this.gunObject.getType().equals(GunType.MINIGUN_PLASMA)) {
					this.gun = GunItemUtil.updateRemainingShots(this.gun, GunItemUtil.getRemainingShots(this.gun) -1);
				}
				
				ShootRunnable reference = this;
				//Send remainingShots
				this.manager.visualHelper.sendRemainingShots(this.shooter, GunItemUtil.getRemainingShots(this.gun), this.gunObject);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						if(reference.shooter.isOnline()) {
							if(reference.offHand) {
								reference.shooter.getInventory().setItemInOffHand(reference.gun);
								reference.shooter.getInventory().setItemInOffHand(GunItemUtil.setCoolingDown(reference.shooter.getInventory().getItemInOffHand(), false));
							} else {
								reference.shooter.getInventory().setItem(reference.gunItemSlot, reference.gun);
								reference.shooter.getInventory().setItem(reference.gunItemSlot, GunItemUtil.setCoolingDown(reference.shooter.getInventory().getItem(reference.gunItemSlot), false));
							}
						}
					}
				}, this.gunObject.getShootingDelay());
			
				
				//STAT
				if(GunGamePlugin.instance.arenaManager.isIngame(this.shooter)) {
					GunGamePlugin.instance.arenaManager.getArena(this.shooter).statManager.getStatPlayer.get(this.shooter.getUniqueId()).incrementWeaponShots();
				}
				//STATEND
			} else {
				//NO PERMISSION-->Tell Player
				this.shooter.sendMessage(LangUtil.noPermission);
			}
		} else if(GunItemUtil.isOutOfAmmo(this.gun)) {
			this.manager.visualHelper.sendOutOfAmmo(this.shooter);			
			if(this.gunObject.getType().equals(GunType.ASSAULT) || this.gunObject.getType().equals(GunType.ASSAULT_PLASMA)) {
				this.manager.visualHelper.sendReloadingWeapon(this.shooter);	
				this.gunObject.getSoundSet().reloadSound.play(this.shooter.getWorld(), this.shooter.getLocation());
				ReloadRunnable process = new ReloadRunnable(this.shooter, this.gunItemSlot, this.gun, this.offHand);
				process.run();
			} else if(this.gunObject.getType().equals(GunType.GRENADETHROWER)) {
				if(this.shooter.isOnline()) {
					if(this.offHand) {
						this.shooter.getInventory().setItemInOffHand(GunItemUtil.setLoadedGrenade(this.shooter.getInventory().getItemInOffHand(), "NONE"));
					} else {
						this.shooter.getInventory().setItem(this.gunItemSlot, GunItemUtil.setLoadedGrenade(this.shooter.getInventory().getItem(this.gunItemSlot), "NONE"));
					}
					
				}
			} else {
				this.gunObject.getSoundSet().outOfAmmoSound.play(this.shooter.getWorld(), this.shooter.getLocation());
			}
		}
		
		//this.manager.removeProcess(this);
	}
	
	private void recalcVector() {
		Vector vector = this.shooter.getEyeLocation().getDirection();//.normalize().multiply(this.gun.getShootingForce());;
		float accuracy = this.gunObject.getAccuracy();
		if(accuracy > 0.0D) {
			vector = vector.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
		}
		this.direction = vector;
	}
	
}
