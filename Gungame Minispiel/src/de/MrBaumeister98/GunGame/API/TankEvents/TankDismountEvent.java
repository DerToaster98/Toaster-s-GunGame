package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public final class TankDismountEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Tank tank;
	private static Player rider;
	
	public TankDismountEvent(Tank turr, Player p) {
		tank = turr;
		rider = p;
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
	
	public Player getRider() {
		return rider;
	}

}
