package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.API.TankEvents.TankMountEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class PlayerInteractAtTankListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(p != null && p.isOnline() && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && p.isSneaking()) {
			List<Tank> tanks = GunGamePlugin.instance.tankManager.getTanksInWorld(p.getWorld());
			if(tanks != null && !tanks.isEmpty()) {
				Vector direction = p.getEyeLocation().getDirection().normalize();
				Location pos0 = p.getEyeLocation();
				Tank tank = null;
				for(int i = 1; i < 26; i++) {
					double multiplier = 0.1 * i;
					direction.multiply(multiplier);
					Location pos = pos0.add(direction);
					
					tank = GunGamePlugin.instance.tankManager.isPositionInATankHitbox(pos);
					if(tank != null) {
						break;
					}
				}
				if(tank != null) {
					if(tank.isAlive() && tank.getDriverUUID() == null) {
						TankMountEvent mountevent = new TankMountEvent(tank, event.getPlayer());
						Bukkit.getServer().getPluginManager().callEvent(mountevent);
						if(!mountevent.isCancelled()) {
							event.setCancelled(true);
							tank.mount((Player)event.getPlayer());
						}
					}
				}
			}
		}
	}

}
