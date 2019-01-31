package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public final class TankShootEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Tank tank;
	private static Player shooter;
	private static Entity projectile;
	
	public TankShootEvent(Tank turr, Player p, Entity proj) {
		tank = turr;
		shooter = p;
		projectile = proj;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public Player getShooter() {
		return shooter;
	}

	public static Entity getProjectile() {
		return projectile;
	}

}
