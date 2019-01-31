package de.MrBaumeister98.GunGame.API.TurretEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;

public final class TurretDeathEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Turret turret;
	
	public TurretDeathEvent(Turret turr) {
		turret = turr;
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

}
