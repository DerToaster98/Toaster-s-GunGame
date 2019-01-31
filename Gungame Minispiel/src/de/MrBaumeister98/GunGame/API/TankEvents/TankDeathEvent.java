package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public final class TankDeathEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Tank tank;
	
	public TankDeathEvent(Tank turr) {
		tank = turr;
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

}
