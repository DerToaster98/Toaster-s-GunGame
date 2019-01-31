package de.MrBaumeister98.GunGame.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Landmine;

public final class LandminePlaceEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private static Landmine mine;
	private static Location location;
	private static Player placer;
	
	public LandminePlaceEvent(Player plcr, Landmine lm, Location loc) {
		mine = lm;
		placer = plcr;
		location = loc;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public Landmine getMine() {
		return mine;
	}
	public Location getLocation() {
		return location;
	}
	public Player getPlacer() {
		return placer;
	}

}
