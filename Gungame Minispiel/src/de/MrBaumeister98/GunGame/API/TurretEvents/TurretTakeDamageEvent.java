package de.MrBaumeister98.GunGame.API.TurretEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;

public final class TurretTakeDamageEvent extends Event implements Cancellable{

	private static Turret turret;
	private static EntityDamageByEntityEvent damageEvent;
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	
	public TurretTakeDamageEvent(Turret turr, EntityDamageByEntityEvent event) {
		turret = turr;
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
	public EntityDamageByEntityEvent getDamageEvent() {
		return damageEvent;
	}
	public Turret getTurret() {
		return turret;
	}
	
}
