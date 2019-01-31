package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class TankIntegrityChecker {

	private Tank tank;
	
	private boolean running;
	private int taskID;
	
	public TankIntegrityChecker(Tank tank) {
		this.tank = tank;
		this.running = true;
	}
	
	public void start() {
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				execute();
			}
		}, 0, 1);
	}
	
	public void execute() {
		if(this.running && this.tank.isAlive()) {
			this.tank.setBodyEntity((ArmorStand) Bukkit.getEntity(this.tank.getEntityIDs()[0]));
			this.tank.setTurretEntity((ArmorStand) Bukkit.getEntity(this.tank.getEntityIDs()[1]));
			this.tank.setBarrelEntity((ArmorStand) Bukkit.getEntity(this.tank.getEntityIDs()[2]));
			this.tank.setSeatEntity((ArmorStand) Bukkit.getEntity(this.tank.getEntityIDs()[3]));
			
			this.tank.setEntities(new Entity[] {this.tank.getBodyArmorStand(), this.tank.getTurretArmorStand(), this.tank.getBarrelArmorStand(), this.tank.getSeat()});
			if(areTankEntitiesAlive()) {
				Entity[] entities = new Entity[] {
						this.tank.getSeat(),
						//this.tank.getPlaceHolder(),
						this.tank.getBarrelArmorStand(),
						this.tank.getBodyArmorStand(),
						this.tank.getTurretArmorStand()
				};
				
				for(int i = 0; i < entities.length -2; i++) {
					Entity ent = entities[i];
					Entity ent2 = entities[i+1];
					if((distance(ent.getLocation().getX(), ent2.getLocation().getX()) + distance(ent.getLocation().getZ(), ent2.getLocation().getZ())) > 0.25) {
						tryFixEntityPositions();
						tank.takeDamage(10.0);
					}
				}
				
				if(this.tank.getDriverUUID() != null) {
					Entity driver = Bukkit.getEntity(this.tank.getDriverUUID());
					if(!driver.getLocation().getWorld().equals(this.tank.getTankPos().getWorld()) || driver.getLocation().distance(this.tank.getTankPos()) > 5.0) {
						this.tank.disMount((Player)driver);
					}
				}
				
			} else {
				stoprun();
			}
		} else {
			stoprun();
		}
	}
	public void stoprun() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
	private boolean areTankEntitiesAlive() {
		for(Entity entity : this.tank.getEntities()) {
			if(entity == null || entity.isDead()) {
				return false;
			}
		}
		return true;
	}
	private void tryFixEntityPositions() {
		this.tank.getBodyArmorStand().addPassenger(this.tank.getTurretArmorStand());
		this.tank.getTurretArmorStand().addPassenger(this.tank.getBarrelArmorStand());
		this.tank.getBarrelArmorStand().addPassenger(this.tank.getSeat());
		//this.tank.getPlaceHolder().addPassenger(this.tank.getSeat());
	}
	private double distance(double xz1, double xz2) {
		return Math.abs(Math.abs(xz1) - Math.abs(xz2));
	}
}
