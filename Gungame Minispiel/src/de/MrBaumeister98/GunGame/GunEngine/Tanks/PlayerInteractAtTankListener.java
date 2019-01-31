package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class PlayerInteractAtTankListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(p != null && p.isOnline()) {
			List<Tank> tanks = GunGamePlugin.instance.tankManager.getTanksInWorld(p.getWorld());
			if(tanks != null && !tanks.isEmpty()) {
				
			}
		}
	}

}
