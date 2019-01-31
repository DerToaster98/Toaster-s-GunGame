package de.MrBaumeister98.GunGame.API.TurretEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;

public final class TurretMountEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled;
	private static Turret turret;
	private static Player rider;
	
	public TurretMountEvent(Turret turr, Player p) {
		turret = turr;
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
	
	public Turret getTurret() {
		return turret;
	}
	
	public Player getRider() {
		return rider;
	}

}
