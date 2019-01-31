package de.MrBaumeister98.GunGame.API.ArenaEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;

public final class ArenaChangeStateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private static Arena arena;
	private static EGameState oldState;
	private static EGameState newState;
	
	public ArenaChangeStateEvent(Arena rena, EGameState oldStat, EGameState newStat) {
		arena = rena;
		oldState = oldStat;
		newState = newStat;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public EGameState getNewState() {
		return newState;
	}

	public EGameState getOldState() {
		return oldState;
	}

	public Arena getArena() {
		return arena;
	}


}
