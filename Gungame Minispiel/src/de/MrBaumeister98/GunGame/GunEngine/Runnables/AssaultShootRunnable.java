package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.WeaponListener;
import de.MrBaumeister98.GunGame.GunEngine.WeaponManager;

public class AssaultShootRunnable extends BukkitRunnable {
	
	private Gun gun;
	private long delay;
	private Integer projectileAmount;
	private Boolean running;
	private Integer shotAmount;
	private WeaponManager manager;
	private WeaponListener listener;
	private Vector v;
	private Integer slot;
	private Player shooter;
	private Integer shotsFired;
	private Integer shots;
	private Boolean plasma;
	
	private static int taskID;
	
	public AssaultShootRunnable(Gun weapon, Player shooter, Integer slot, Integer remainingShots, WeaponManager manager, WeaponListener listener, Boolean plasma) {
		shooter.setMetadata("GG_ShootingAssaultGun", new FixedMetadataValue(GunGamePlugin.instance, true));
		this.plasma = plasma;
		this.shooter = shooter;
		this.gun = weapon;
		this.delay = weapon.getShootingDelay();
		this.running = true;
		this.manager = manager;
		this.listener = listener;
		this.slot = slot;
		this.shots = remainingShots;
		this.shotAmount = weapon.getVolleyShotAmount();
		this.projectileAmount = weapon.getShotCount();
		this.shotsFired = 0;
		
		Vector vector = shooter.getEyeLocation().getDirection();//.normalize().multiply(this.gun.getShootingForce());
		float accuracy = this.gun.getAccuracy() * (float)1.5;
		if(accuracy > 0.0D) {
			vector = vector.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
		}
		this.v = vector; 
	}
	@Override
	public void run() {
		if(this.running) {
			if(!GunItemUtil.isOutOfAmmo(this.shooter.getInventory().getItem(this.slot)) && (this.shots > 0)) {
				if(this.shotsFired < this.shotAmount) {
					Double recoil = this.gun.getRecoilAmount();
					Vector move = this.shooter.getLocation().getDirection().normalize().multiply(recoil).multiply(-1.0).setY(0.0);
					if(this.shooter.isSneaking()) {
						this.shooter.setVelocity(this.shooter.getVelocity().add(move.multiply(0.25D)));
					} else {
						this.shooter.setVelocity(this.shooter.getVelocity().add(move));
					}
					Location loc = this.shooter.getEyeLocation().add(this.shooter.getLocation().getDirection().normalize().multiply(1.5D));
					//if(!GunGamePlugin.instance.serverPre113) {
						loc.getWorld().spawnParticle(this.gun.getShootParticle(),
								loc.getX() + this.gun.getShootParticleX(),
								loc.getY() + this.gun.getShootParticleY(),
								loc.getZ() + this.gun.getShootParticleZ(), 
								this.gun.getShootParticleCount(),
								this.gun.getShootParticleDX(),
								this.gun.getShootParticleDY(),
								this.gun.getShootParticleDZ(),
								this.gun.getShootParticleSpeed(),
								this.gun.getShootParticleExtra()
						);
					//}
					this.gun.getSoundSet().shootSound.play(this.shooter.getWorld(), this.shooter.getLocation());
					for(int c = 0; c < this.projectileAmount; c++) {
						recalcVector();
						if(this.plasma) {
							PlasmaBulletThread bully = new PlasmaBulletThread(this.gun, this.shooter, this.v);
							bully.run();
						} else {
							/*BulletThread bully =*/ new BulletThread(this.gun, this.shooter, this.v);
							//bully.run();
						}
					}
					this.shots = this.shots -1;
					this.shotsFired = this.shotsFired +1;
					this.shooter.getInventory().setItem(this.slot, GunItemUtil.updateRemainingShots(this.shooter.getInventory().getItem(this.slot), this.shots));
					this.manager.visualHelper.sendRemainingShots(this.shooter, this.shots, this.gun);
					
					AssaultShootRunnable ref = this;
					Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							ref.run();
						}
					}, this.delay);
				} else {
					cancel();
				}
			} else {
				this.gun.getSoundSet().outOfAmmoSound.play(this.shooter.getWorld(), this.shooter.getLocation());

				cancel();
				this.manager.visualHelper.sendOutOfAmmo(this.shooter);
			}
		}
	}
	
	private void recalcVector() {
		Vector vector = this.shooter.getEyeLocation().getDirection();//.normalize().multiply(this.gun.getShootingForce());;
		float accuracy = this.gun.getAccuracy() * (float)1.5;
		if(accuracy > 0.0D) {
			vector = vector.add(new Vector(Math.random() * accuracy * 2 - accuracy, Math.random() * accuracy * 2 - accuracy,Math.random() * accuracy * 2 - accuracy));
		}
		this.v = vector;
	}
	public void cancel() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(taskID);
		this.shooter.getInventory().setItem(this.slot, GunItemUtil.updateRemainingShots(this.shooter.getInventory().getItem(this.slot), this.shots));
		this.manager.visualHelper.sendRemainingShots(this.shooter, this.shots, this.gun);
		this.shooter.setMetadata("GG_ShootingAssaultGun", new FixedMetadataValue(GunGamePlugin.instance, false));
		this.listener.cancelShooting(this.shooter.getUniqueId());
	}
	public Boolean isRunning() {
		return this.running;
	}

}
