package de.MrBaumeister98.GunGame.Game.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import de.MrBaumeister98.GunGame.Game.Arena.GameWorld;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankData;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankManager;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretData;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretManager;

public class ChunkLoadListener implements Listener {
	
	private List<GameWorld> worlds;
	
	private TurretManager turretManager;
	private TankManager tankManager;
	
	public ChunkLoadListener() {
		this.worlds = new ArrayList<GameWorld>();
		this.turretManager = GunGamePlugin.instance.turretManager;
		this.tankManager = GunGamePlugin.instance.tankManager;
	}
	
	public void addGameWorld(GameWorld gw) {
		if(this.worlds.isEmpty() || !this.worlds.contains(gw)) {
			this.worlds.add(gw);
		}
	}
	public void remGameWorld(GameWorld gw) {
		if(this.worlds.contains(gw)) {
			this.worlds.remove(gw);
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		World w = event.getWorld();
		if(GameListener.processed.contains(w.getName()) || w.getName().contains("GunGame-")) {
			//Bukkit.broadcastMessage("TRIGGER");
			GameWorld gameworld = null;
			for(GameWorld gw : this.worlds) {
				if(event.getChunk() != null && gw != null && gw.getWorld() != null && w != null && gw.getWorld()/*.getUID()*/.equals(w/*.getUID()*/)) {
					gameworld = gw;
				}
			}
			if(gameworld != null) {
				for(Location turrLoc : new ArrayList<Location>(gameworld.getTurretsToSpawn())) {
					if(turrLoc.getChunk().getX() == event.getChunk().getX() && turrLoc.getChunk().getZ() == event.getChunk().getZ()) {
						gameworld.spawnTurret(turrLoc);
					}
				}
				for(Location tankLoc : new ArrayList<Location>(gameworld.getTanksToSpawn())) {
					if(tankLoc.getChunk().getX() == event.getChunk().getX() && tankLoc.getChunk().getZ() == event.getChunk().getZ()) {
						gameworld.spawnTank(tankLoc);
					}
				}
			}
		}	
			List<TurretData> td = null;
			for(TurretData tdata : new ArrayList<TurretData>(this.turretManager.turretsToSpawn)) {
				World tDataWorld = tdata.location.getWorld();
				Chunk tDataChunk = tdata.location.getChunk();
				if(event.getChunk() != null && tdata != null && tDataWorld != null && w != null && tDataWorld.equals(w)) {
					if(td == null || td.isEmpty()) {
						td = new ArrayList<TurretData>();
					}
					if(tDataChunk.getX() == event.getChunk().getX() && tDataChunk.getZ() == event.getChunk().getZ()) {
						td.add(tdata);
					}
				}
			}
			if(td != null && !td.isEmpty()) {
				for(TurretData data : td) {
					this.turretManager.respawnTurret(data);
				}
				td.clear();
			}
					
			List<TankData> tad = null;
			for(TankData tdata : new ArrayList<TankData>(this.tankManager.tanksToSpawn)) {
				World tDataWorld = tdata.location.getWorld();
				Chunk tDataChunk = tdata.location.getChunk();
				if(event.getChunk() != null && tdata != null && tDataWorld != null && w != null && tDataWorld.equals(w)) {
					if(tad == null || tad.isEmpty()) {
						tad = new ArrayList<TankData>();
					}
					if(tDataChunk.getX() == event.getChunk().getX() && tDataChunk.getZ() == event.getChunk().getZ()) {
						tad.add(tdata);
					}
				}
			}
			if(tad != null && !tad.isEmpty()) {
				for(TankData data : tad) {
					this.tankManager.respawnTank(data);
				}
				tad.clear();
			}			
		//}
	}

}
