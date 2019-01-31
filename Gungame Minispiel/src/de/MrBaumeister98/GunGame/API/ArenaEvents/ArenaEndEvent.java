package de.MrBaumeister98.GunGame.API.ArenaEvents;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;

public final class ArenaEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private static Arena arena;
	private static HashMap<Integer, Player> placemap = new HashMap<Integer, Player>();
	
	public ArenaEndEvent(Arena rena, HashMap<Integer, Player> vList) {
		arena = rena;
		placemap = vList;
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

	public HashMap<Integer, Player> getPlaceMap() {
		return placemap;
	}

}
