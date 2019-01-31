package de.MrBaumeister98.GunGame.Game.Mechanics;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Cake;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;

@SuppressWarnings("deprecation")
public class MediCake_pre_1_13 implements Listener {
	
	private static Boolean isMissingHealth(Player player) {
		if(player.getHealth() < 20.0) {
			return true;
		} else {
			return false;
		}
	}
	
	@EventHandler
	public void onUseCake(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(GunGamePlugin.instance.arenaManager.isIngame(event.getPlayer()) && GunGamePlugin.instance.arenaManager.getArena(event.getPlayer()).getGameState().equals(EGameState.GAME)) {
				if(event.getClickedBlock().getType().toString().equalsIgnoreCase("CAKE_BLOCK")) {
					if(isMissingHealth(event.getPlayer())) {
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
						event.getClickedBlock().getWorld().spawnParticle(Particle.HEART, new Location(event.getClickedBlock().getWorld(),
								event.getClickedBlock().getLocation().getX() -0.25D,
								event.getClickedBlock().getLocation().getY(),
								event.getClickedBlock().getLocation().getZ() -0.25D),
								10,
								0.5D,
								1.0D,
								0.5D,
								0.15);
						BlockState state = event.getClickedBlock().getState();
						MaterialData data = state.getData();
						//Cake cake = (Cake)event.getClickedBlock().getState().getData();
						Cake cake = (Cake) data;
						if(cake.getSlicesRemaining() > 0) {
							cake.setSlicesEaten(cake.getSlicesEaten() +1);
							state.setData(cake);
							state.update();
						} else {
							event.getClickedBlock().breakNaturally();
							/*state.setData(cake);
							state.update();*/
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

}
