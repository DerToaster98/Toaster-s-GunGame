package de.MrBaumeister98.GunGame.API.ArenaEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;

public final class ArenaJoinEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private static Player player;
	private static Arena arena;
	private static boolean cancelled;
	
	public ArenaJoinEvent(Arena rena, Player p) {
		arena = rena;
		player = p;
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
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
}
