package de.MrBaumeister98.GunGame.API.TurretEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretReloader;

public final class TurretReloadEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Turret turret;
	private static Player shooter;
	private static TurretReloader reloadRunnable;
	
	public TurretReloadEvent(Player player, Turret turr, TurretReloader reloadProcess) {
		shooter = player;
		turret = turr;
		setReloadRunnable(reloadProcess);
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
	public void setTurret(Turret turr) {
		turret = turr;
	}
	public Turret getTurret() {
		return turret;
	}
	public Player getShooter() {
		return shooter;
	}
	public void setShooter(Player p) {
		shooter = p;
	}

	public static TurretReloader getReloadRunnable() {
		return reloadRunnable;
	}

	public static void setReloadRunnable(TurretReloader reload) {
		reloadRunnable = reload;
	}
}
