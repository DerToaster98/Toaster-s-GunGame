package de.MrBaumeister98.GunGame.API;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Grenade;

public final class GrenadeExplodeEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Grenade grenade;
	private static Location location;
	private static UUID player;
	
	public GrenadeExplodeEvent(Grenade gren, UUID uuid, Location loc) {
		grenade = gren;
		player = uuid;
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

	public static UUID getPlayer() {
		return player;
	}


	public static Location getLocation() {
		return location;
	}


	public static Grenade getGrenade() {
		return grenade;
	}

}
