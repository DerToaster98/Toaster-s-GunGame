package de.MrBaumeister98.GunGame.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Grenade;

public final class GrenadeThrowEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Grenade grenade;
	private static Player thrower;
	
	public GrenadeThrowEvent(Player p, Grenade gren) {
		grenade = gren;
		thrower = p;
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

	public static Grenade getGrenade() {
		return grenade;
	}

	public static Player getThrower() {
		return thrower;
	}

}
