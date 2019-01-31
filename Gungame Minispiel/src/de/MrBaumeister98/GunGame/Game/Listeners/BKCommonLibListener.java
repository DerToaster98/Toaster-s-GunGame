package de.MrBaumeister98.GunGame.Game.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bergerkiller.bukkit.common.events.EntityMoveEvent;

import de.MrBaumeister98.GunGame.API.LandmineTriggerEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Landmine;
import de.MrBaumeister98.GunGame.GunEngine.Runnables.LandmineExplodeRunnable;

public class BKCommonLibListener implements Listener {
	
	@EventHandler
	public void onEntityStepOnMine(EntityMoveEvent event) {
		if(!(event.getEntity() instanceof Player) && !event.getEntityType().equals(EntityType.PLAYER)) {
			Location toLoc = new Location(event.getWorld(), event.getToX(), event.getToY(), event.getToZ());
			Block block = toLoc.getBlock();
			if(block.hasMetadata("GG_Landmine")) {
				UUID triggerID = event.getEntity().getUniqueId();
				UUID placerID = UUID.fromString(block.getMetadata("GG_Landmine_Placer").get(0).asString());
				if(!triggerID.equals(placerID)) {
					Landmine mine = GunGamePlugin.instance.weaponManager.getLandmine(block.getMetadata("GG_Landmine_Name").get(0).asString());
					LandmineExplodeRunnable ler = new LandmineExplodeRunnable(mine, block, placerID);
					
					LandmineTriggerEvent triggerEvent = new LandmineTriggerEvent(mine, event.getEntity(), ler);
					Bukkit.getServer().getPluginManager().callEvent(triggerEvent);
					
					if(!triggerEvent.isCancelled()) {
						ler.run();
						block.removeMetadata("GG_Landmine", GunGamePlugin.instance);
						block.removeMetadata("GG_Landmine_Name", GunGamePlugin.instance);
						block.removeMetadata("GG_Landmine_Placer", GunGamePlugin.instance);
					}
				}
			}
		}
	}

}
