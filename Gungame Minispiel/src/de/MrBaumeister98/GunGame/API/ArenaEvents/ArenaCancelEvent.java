package de.MrBaumeister98.GunGame.API.ArenaEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;

public final class ArenaCancelEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Arena arena;
	
	public ArenaCancelEvent(Arena rena) {
		arena = rena;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public Arena getArena() {
		return arena;
	}

}
