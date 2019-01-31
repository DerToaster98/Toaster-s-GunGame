package de.MrBaumeister98.GunGame.API.ArenaEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;

public final class ArenaLeaveEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private static Player player;
	private static Arena arena;
	private static ELeaveReason reason;
	
	public ArenaLeaveEvent(Arena rena, Player p, ELeaveReason r) {
		arena = rena;
		player = p;
		reason = r;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public Arena getArena() {
		return arena;
	}

	public ELeaveReason getReason() {
		return reason;
	}

}
