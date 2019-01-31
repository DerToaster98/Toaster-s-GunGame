package de.MrBaumeister98.GunGame.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Gun;

public final class WeaponShootEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Gun weapon;
	private static Player shooter;
	
	public WeaponShootEvent(Player p, Gun gun) {
		weapon = gun;
		shooter = p;
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

	public Gun getWeapon() {
		return weapon;
	}

	public Player getShooter() {
		return shooter;
	}

}
