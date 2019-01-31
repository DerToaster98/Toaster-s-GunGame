package de.MrBaumeister98.GunGame.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Grenade;

public final class GrenadeEquipEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Player player;
	private static Grenade grenade;
	
	public GrenadeEquipEvent(Player p, Grenade gren) {
		player = p;
		grenade = gren;
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

	public Player getPlayer() {
		return player;
	}

	public Grenade getGrenade() {
		return grenade;
	}
}
