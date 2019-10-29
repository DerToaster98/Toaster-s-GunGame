package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class TurretShootController extends BukkitRunnable {

	private Turret turret;
	private TurretConfig config;
	
	private Boolean running;
	//private Boolean coolingDown = false;
	
	private Integer shotDelay;
	private Double criticalHeat;
	
	public TurretShootController(Turret turret) {
		this.turret = turret;
		this.config = turret.config;
		this.criticalHeat = this.config.getCriticalHeat();
		this.shotDelay = this.config.getShootDelay();
	}

	@Override
	public void run() {
		//Part for AI
		/*if(this.turret.getTemperature() >= this.criticalHeat *0.8) {
			this.coolingDown = true;
		}
		if(this.turret.getTemperature() <= this.criticalHeat *0.3 && this.coolingDown) {
			this.coolingDown = false;
		}*/
		
		if(this.running && this.turret.getTemperature() < this.criticalHeat) {
			if(this.turret.getGunner() != null/* && !this.coolingDown*/) {
				this.turret.fireShot();
			}
			TurretShootController ts = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					ts.run();
				}
			}, this.shotDelay.longValue());
		} else if(this.turret.getTemperature() >= this.criticalHeat) {
			this.running = false;
			this.turret.setShooting(this.running);
			this.turret.die();
		}
		this.turret.setShooting(this.running);
		
	}
	
	public void startShooting() {
		this.running = true;
		run();
	}
	public void stopShooting() {
		this.running = false;
	}
}
