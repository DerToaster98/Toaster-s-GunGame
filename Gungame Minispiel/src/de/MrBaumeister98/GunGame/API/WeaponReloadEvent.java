package de.MrBaumeister98.GunGame.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.ReloadRunnable;

public final class WeaponReloadEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private static boolean cancelled = false;
	private static Gun weapon;
	private static Player shooter;
	private static ReloadRunnable reloadRunnable;
	
	public WeaponReloadEvent(Player player, Gun gun, ReloadRunnable reloadProcess) {
		shooter = player;
		weapon = gun;
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
	public void setGun(Gun gun) {
		weapon = gun;
	}
	public Gun getWeapon() {
		return weapon;
	}
	public Player getShooter() {
		return shooter;
	}
	public void setShooter(Player p) {
		shooter = p;
	}

	public static ReloadRunnable getReloadRunnable() {
		return reloadRunnable;
	}

	public static void setReloadRunnable(ReloadRunnable reloadRunnable) {
		WeaponReloadEvent.reloadRunnable = reloadRunnable;
	}

}
