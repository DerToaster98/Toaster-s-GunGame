package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public final class TankMountEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled;
	private static Tank tank;
	private static Player rider;
	
	public TankMountEvent(Tank turr, Player p) {
		tank = turr;
		rider = p;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public Player getRider() {
		return rider;
	}

}
