package de.MrBaumeister98.GunGame.API;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Landmine;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.LandmineExplodeRunnable;

public final class LandmineTriggerEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Landmine mine;
	private static Entity trigger;
	private static LandmineExplodeRunnable explosion;
	
	public LandmineTriggerEvent(Landmine lm, Entity trggr, LandmineExplodeRunnable explodeProcess) {
		mine = lm;
		trigger = trggr;
		setExplosion(explodeProcess);
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

	public static Landmine getMine() {
		return mine;
	}

	public static void setMine(Landmine lm) {
		mine = lm;
	}

	public static Entity getTrigger() {
		return trigger;
	}

	public static void setTrigger(Entity trggr) {
		trigger = trggr;
	}

	public static LandmineExplodeRunnable getExplosion() {
		return explosion;
	}

	public static void setExplosion(LandmineExplodeRunnable expl) {
		explosion = expl;
	}

}
