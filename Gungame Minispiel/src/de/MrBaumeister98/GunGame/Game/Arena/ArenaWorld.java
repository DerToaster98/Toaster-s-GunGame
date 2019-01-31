package de.MrBaumeister98.GunGame.Game.Arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.SendableException;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations.WorldOperator;
//import de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations.WorldRestorer;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations.WorldSaver;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations.WorldZipper;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.EWorldState;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class ArenaWorld {
	
	private String name;
	
	private String resourcePackLink;
	private String worldName;
	private List<String> spawnlist = new ArrayList<String>();
	private List<Player> players = new ArrayList<Player>();
	private List<String> turretSpawnList = new ArrayList<String>();
	private List<String> tankSpawnList = new ArrayList<String>();
	
	private EWorldState state;
	@SuppressWarnings("unused")
	private ArenaManager manager;
	
	private List<String> builders = new ArrayList<String>();
	
	public ArenaWorld(String Name, ArenaManager manager) {
		this.name = Name;
		this.manager = manager;
		
		this.state = EWorldState.IDLE;
		this.spawnlist = new ArrayList<String>();
		this.players = new ArrayList<Player>();
		this.turretSpawnList = new ArrayList<String>();
		this.tankSpawnList = new ArrayList<String>();
	}
	public ArenaWorld(String Name, ArenaManager manager, List<String> spawns, List<String> builders, String world, List<String> tanks, List<String> turrets) {
		this.state = EWorldState.LOADING;
		this.name = Name;
		this.manager = manager;
		this.spawnlist = spawns;
		this.worldName = world;
		this.state = EWorldState.IDLE;
		this.builders = builders;
		this.tankSpawnList = tanks;
		this.turretSpawnList = turrets;
	}
	
	
	
	
	public void setSpawns(List<String> locs) {
		this.spawnlist = locs;
	}
	public void addSpawn(Location loc) {
		if(this.spawnlist != null) {
			this.spawnlist.add(Util.locToString(loc));
			ArenaFileStuff.addArenaWorldSpawnToConfig(this.name, loc);
		}
	}
	public Location getSpawn(Integer id) {
		if(!this.spawnlist.isEmpty()) {
			Location loc = Util.stringToLoc(this.spawnlist.get(id));
			return loc;
		} else {
			return null;
		}
	}
	public List<String> getSpawnList() {
		return this.spawnlist;
	}
	public void setSpawn(Location loc, Integer id) {
		if(this.spawnlist.get(id) != null) {
			this.spawnlist.set(id, Util.locToString(loc));
			ArenaFileStuff.addArenaWorldSpawnToConfig(this.name, loc, id);
		}
	}
	public void delSpawns() {
		this.spawnlist.clear();
		ArenaFileStuff.resetArenaWorldSpawnsFromConfig(this.name);
	}
	public void delSpawn(Integer id) {
		this.spawnlist.remove(this.spawnlist.get(id));
		ArenaFileStuff.removeArenaWorldSpawnFromConfig(this.name, id);
	}
	
	
	
	public void setTurretSpawns(List<String> locs) {
		this.turretSpawnList = locs;
	}
	public void addTurretSpawn(Location loc) {
		if(this.turretSpawnList != null) {
			this.turretSpawnList.add(Util.locToString(loc));
			ArenaFileStuff.addArenaWorldTurretSpawnToConfig(this.name, loc);
		}
	}
	public Location getTurretSpawn(Integer id) {
		if(!this.turretSpawnList.isEmpty()) {
			Location loc = Util.stringToLoc(this.turretSpawnList.get(id));
			return loc;
		} else {
			return null;
		}
	}
	public void setTurretSpawn(Location loc, Integer id) {
		if(this.turretSpawnList.get(id) != null) {
			this.turretSpawnList.set(id, Util.locToString(loc));
			ArenaFileStuff.addArenaWorldTurretSpawnToConfig(this.name, loc, id);
		}
	}
	public void delTurretSpawns() {
		this.turretSpawnList.clear();
		ArenaFileStuff.resetArenaWorldTurretSpawnsFromConfig(this.name);
	}
	public void delTurretSpawn(Integer id) {
		this.turretSpawnList.remove(this.turretSpawnList.get(id));
		ArenaFileStuff.removeArenaWorldTurretSpawnFromConfig(this.name, id);
	}
	
	
	
	
	
	public void setTankSpawns(List<String> locs) {
		this.tankSpawnList = locs;
	}
	public void addTankSpawn(Location loc) {
		if(this.tankSpawnList != null) {
			this.tankSpawnList.add(Util.locToString(loc));
			ArenaFileStuff.addArenaWorldTankSpawnToConfig(this.name, loc);
		}
	}
	public Location getTankSpawn(Integer id) {
		if(!this.tankSpawnList.isEmpty()) {
			Location loc = Util.stringToLoc(this.tankSpawnList.get(id));
			return loc;
		} else {
			return null;
		}
	}
	public void setTankSpawn(Location loc, Integer id) {
		if(this.tankSpawnList.get(id) != null) {
			this.tankSpawnList.set(id, Util.locToString(loc));
			ArenaFileStuff.addArenaWorldTankSpawnToConfig(this.name, loc, id);
		}
	}
	public void delTankSpawns() {
		this.tankSpawnList.clear();
		ArenaFileStuff.resetArenaWorldTankSpawnsFromConfig(this.name);
	}
	public void delTankSpawn(Integer id) {
		this.tankSpawnList.remove(this.tankSpawnList.get(id));
		ArenaFileStuff.removeArenaWorldTankSpawnFromConfig(this.name, id);
	}

	
	
	
	
	public String getName() {
		return name;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public EWorldState getState() {
		return state;
	}
	public String getWorld() {
		return this.worldName;
	}
	public void setWorld(String s) {
		this.worldName = s;
		//saveArenaWorld();
		//Bukkit.getWorld(s).setAutoSave(false);
		
		ArenaFileStuff.setArenaWorldToConfig(this.name, this.worldName);
	}
	public void loadWorld() {
		String wName = this.worldName;
		Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {

			@Override
			public void run() {
				World w = Bukkit.getServer().createWorld(new WorldCreator(wName));
				
				w.setKeepSpawnInMemory(false);
				w.setAutoSave(false);
			}
			
		});
		
	}
	//Arena WorldStuff (Restoring, Saving, Deleting, Setting world)
	public void saveArenaWorld() {
		String wName = this.worldName;
		Bukkit.getScheduler().runTaskAsynchronously(GunGamePlugin.instance, new Runnable(){

			@Override
			public void run() {
				String worldName = wName;
				
				WorldOperator worldOperator = null;
				
				worldOperator = new WorldSaver(worldName);
				try {
					worldOperator.execute();
					Debugger.logInfoWithColoredText(ChatColor.GREEN + worldOperator.getResultMessage());
				} catch (SendableException e) {
					Debugger.logInfoWithColoredText(ChatColor.RED + e.getMessage());
				}
				
				worldOperator = new WorldZipper(worldName);
				try {
					worldOperator.execute();
					Debugger.logInfoWithColoredText(ChatColor.GREEN + worldOperator.getResultMessage());
				} catch (SendableException e) {
					Debugger.logInfoWithColoredText(ChatColor.RED + e.getMessage());
				}
				
			}
			
		});
		
	}

	public void unloadArenaWorld(boolean b) {
		if(this.worldName != null) {
			World w = Bukkit.getWorld(this.worldName);
			if(w != null) {
				for(Player p : w.getPlayers()) {
					p.teleport(Util.getGlobalLobby());
				}
				//for(Chunk chunk : w.getLoadedChunks()) chunk.unload(false);
				Bukkit.unloadWorld(w, b);
			}
		}		
	}
	public void addBuilder(String builder) {
		this.builders.add(builder);
		ArenaFileStuff.addBuilderToConfig(this, builder);
	}
	public void remBuilder(String builder) {
		this.builders.remove(builder);
		ArenaFileStuff.remBuilderFromConfig(this, builder);
	}
	public void setBuilders(List<String> builders) {
		this.builders = builders;
	}
	public void resetBuilders() {
		this.builders.clear();
		ArenaFileStuff.resetBuildersInConfig(this);
	}
	public List<String> getBuilders() {
		return this.builders;
	}
	public String getResourcePackLInk() {
		return resourcePackLink;
	}
	public void setResourcePackLInk(String resourcePackLInk) {
		this.resourcePackLink = resourcePackLInk;
		ArenaFileStuff.addResourcePackLink(this, this.resourcePackLink);
	}
	public List<String> getTurretSpawnList() {
		return this.turretSpawnList;
	}
	public void setTurretSpawnList(List<String> turretSpawnList) {
		this.turretSpawnList = turretSpawnList;
	}
	public List<String> getTankSpawnList() {
		return this.tankSpawnList;
	}
	public void setTankSpawnList(List<String> tankSpawnList) {
		this.tankSpawnList =tankSpawnList;
	}
	
	

}
