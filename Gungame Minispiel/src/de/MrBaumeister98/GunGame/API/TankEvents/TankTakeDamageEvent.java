package de.MrBaumeister98.GunGame.API.TankEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;

public final class TankTakeDamageEvent extends Event implements Cancellable{

	private static Tank tank;
	private static EntityDamageEvent damageEvent;
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	
	public TankTakeDamageEvent(Tank turr, EntityDamageEvent event) {
		tank = turr;
		damageEvent = event;
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
	public EntityDamageEvent getDamageEvent() {
		return damageEvent;
	}
	public Tank getTank() {
		return tank;
	}
	
}
