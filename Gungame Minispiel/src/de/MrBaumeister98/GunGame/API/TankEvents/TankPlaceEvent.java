package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;

public final class TankPlaceEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled;
	private static TankConfig tank;
	private static Player rider;
	private static Location location;
	
	public TankPlaceEvent(TankConfig turr, Player p, Location loc) {
		tank = turr;
		rider = p;
		location = loc;
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
	
	public TankConfig getTank() {
		return tank;
	}
	
	public Player getRider() {
		return rider;
	}

	public static Location getLocation() {
		return location;
	}

}
