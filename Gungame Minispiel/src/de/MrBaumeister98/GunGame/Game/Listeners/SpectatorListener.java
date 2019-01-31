package de.MrBaumeister98.GunGame.Game.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

@SuppressWarnings("deprecation")
public class SpectatorListener implements Listener {
	
	ArenaManager manager;
	
	public SpectatorListener(GunGamePlugin plugin) {
		this.manager = plugin.arenaManager;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (this.manager.isSpectator(player)) {
			e.setCancelled(true);
		}

	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShot(ProjectileHitEvent event) {
		if(event.getHitEntity() != null) {
			if(event.getHitEntity() instanceof Player) {
				Player p = (Player)event.getHitEntity();
				if(this.manager.isSpectator(p)) {
					p.spigot().setCollidesWithEntities(false);
					p.setCollidable(false);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
			if (this.manager.isSpectator(player)) {
				event.setCancelled(true);
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (this.manager.isSpectator(player)) {
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (this.manager.isSpectator(player)) {
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		Player player = null;
		if (event.getEntity() instanceof Player) {
			player = (Player) event.getEntity();
			if (this.manager.isSpectator(player)) {
				event.setCancelled(true);
			}

		}
	}

}
