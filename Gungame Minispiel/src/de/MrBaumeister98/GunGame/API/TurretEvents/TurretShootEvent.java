package de.MrBaumeister98.GunGame.API.TurretEvents;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;

public final class TurretShootEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Turret turret;
	private static Player shooter;
	private static Projectile projectile;
	
	public TurretShootEvent(Turret turr, Player p, Projectile proj) {
		turret = turr;
		shooter = p;
		projectile = proj;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public Turret getTurret() {
		return turret;
	}
	
	public Player getShooter() {
		return shooter;
	}

	public static Projectile getProjectile() {
		return projectile;
	}

}
