package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class TurretCooler extends BukkitRunnable {
	
	private Turret turret;
	private TurretConfig config;
	
	private Boolean running;
	
	private Double cooldown;
	private long coolDelay;
	
	public TurretCooler(Turret turret) {
		this.turret = turret;
		this.config = turret.config;
		this.cooldown = this.config.getCoolDownPerTick();
		this.coolDelay = this.config.getShootDelay().longValue();
	}
	
	@Override
	public void run() {
		if(this.turret.getTemperature() == 0.0) {
			this.running = false;
		}
		if(this.running && this.turret.getTemperature() - this.cooldown >= 0.0) {
			this.turret.setTemperature(this.turret.getTemperature() - this.cooldown);
			if(this.turret.getGunner() != null) {
				this.turret.manager.plugin.weaponManager.visualHelper.sendTurretStatus(this.turret.getGunner(), this.turret);
			}
			TurretCooler tc = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					tc.run();
				}
			}, this.coolDelay);
		} else if(this.running) {
			this.turret.setTemperature(0.0);
		}
	}
	
	public void startCooling() {
		this.running = true;
		run();
	}
	public void stopCooling() {
		this.running = false;
	}

}
