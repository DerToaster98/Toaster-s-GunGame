package de.MrBaumeister98.GunGame.Game.Arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class ArenaFileStuff {

	public static void addArenaToConfig(String Name, Integer minP, Integer maxP, Integer kills, Location loc) {
		//int arenaCount = FileManager.getArenaConfig().getInt("arenas.arenaCount");		
		List<String> temp = FileManager.getArenaConfig().getStringList("arenas.arenalist");	
		List<String> temp2 = new ArrayList<String>();
		temp.add(Name);
		FileManager.getArenaConfig().set("arenas.arenalist", temp);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".signs", temp2);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".enabled", true);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".name", Name);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".minPlayers", minP);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".maxPlayers", maxP);
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".lobby", Util.locToString(loc));
		//FileManager.getArenaConfig().set("arenas.arenas." + Name + ".spawns", new ArrayList<String>());
		FileManager.getArenaConfig().set("arenas.arenas." + Name + ".killsToWin", kills);

		FileManager.saveArenaConfig();
	}
	public static void addJoinSignToConfig(Location sign, String ArenaName) {
		List<String> signs = FileManager.getArenaConfig().getStringList("arenas.arenas." + ArenaName + ".signs");
		signs.add(Util.locToString(sign));
		FileManager.getArenaConfig().set("arenas.arenas." + ArenaName + ".signs", signs);
		FileManager.saveArenaConfig();
	}
	public static void removeJoinSignFromConfig(Location sign, String ArenaName) {
		List<String> signs = FileManager.getArenaConfig().getStringList("arenas.arenas." + ArenaName + ".signs");
		signs.remove(Util.locToString(sign));
		FileManager.getArenaConfig().set("arenas.arenas." + ArenaName + ".signs", signs);
		FileManager.saveArenaConfig();
	}
	public static void resetJoinSignsInConfig(String ArenaName) {
		List<String> signs = FileManager.getArenaConfig().getStringList("arenas.arenas." + ArenaName + ".signs");
		signs.clear();
		FileManager.getArenaConfig().set("arenas.arenas." + ArenaName + ".signs", signs);
		FileManager.saveArenaConfig();
	}
	public static void addArenaWorldToConfig(String Name) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.worldlist");	
		temp.add(Name);
		FileManager.getArenaConfig().set("arenaworlds.worldlist", temp);
		
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + Name + ".spawns", new ArrayList<String>());
		FileManager.saveArenaConfig();
	}
	public static void setArenaWorldToConfig(String ID, String world) {
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".world", world);
		
		FileManager.saveArenaConfig();
	}
	public static void setArenaLobbyToConfig(String ID, Location loc) {
		FileManager.getArenaConfig().set("arenas.arenas." + ID + ".lobby", Util.locToString(loc));
		
		FileManager.saveArenaConfig();
	}
	public static void addArenaWorldSpawnToConfig(String ID, Location loc) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".spawns");
		
		temp.add(Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".spawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void addArenaWorldSpawnToConfig(String ID, Location loc, Integer nmbr) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenas.arenas." + ID + ".spawns");
		
		temp.set(nmbr, Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".spawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void removeArenaWorldSpawnFromConfig(String ID, Integer nmbr) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".spawns").remove(FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".spawns").get(nmbr));
		
		FileManager.saveArenaConfig();
	}
	public static void resetArenaWorldSpawnsFromConfig(String ID) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".spawns").clear();
		
		FileManager.saveArenaConfig();
	}
	
	
	
	
	
	public static void addArenaWorldTurretSpawnToConfig(String ID, Location loc) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".turretspawns");
		
		temp.add(Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".turretspawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void addArenaWorldTurretSpawnToConfig(String ID, Location loc, Integer nmbr) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenas.arenas." + ID + ".turretspawns");
		
		temp.set(nmbr, Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".turretspawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void removeArenaWorldTurretSpawnFromConfig(String ID, Integer nmbr) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".turretspawns").remove(FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".turretspawns").get(nmbr));
		
		FileManager.saveArenaConfig();
	}
	public static void resetArenaWorldTurretSpawnsFromConfig(String ID) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".turretspawns").clear();
		
		FileManager.saveArenaConfig();
	}
	
	
	
	
	
	
	public static void addArenaWorldTankSpawnToConfig(String ID, Location loc) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".tankspawns");
		
		temp.add(Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".tankspawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void addArenaWorldTankSpawnToConfig(String ID, Location loc, Integer nmbr) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenas.arenas." + ID + ".tankspawns");
		
		temp.set(nmbr, Util.locToString(loc));
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + ID + ".tankspawns", temp);
		
		FileManager.saveArenaConfig();
	}
	public static void removeArenaWorldTankSpawnFromConfig(String ID, Integer nmbr) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".tankspawns").remove(FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".tankspawns").get(nmbr));
		
		FileManager.saveArenaConfig();
	}
	public static void resetArenaWorldTankSpawnsFromConfig(String ID) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + ID + ".tankspawns").clear();
		
		FileManager.saveArenaConfig();
	}
	
	
	
	
	
	
	
	public static void addBuilderToConfig(ArenaWorld world, String builder) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + world.getName() + ".builders");
		if(temp != null) {
			temp.add(builder);
		} else {
			temp = new ArrayList<String>();
			temp.add(builder);
		}
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + world.getName() + ".builders", temp);
		FileManager.saveArenaConfig();
	}
	public static void addResourcePackLink(ArenaWorld world, String link) {
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + world.getName() + ".resourcepackLink", link);
		
		FileManager.saveArenaConfig();
	}
	public static void remBuilderFromConfig(ArenaWorld world, String builder) {
		List<String> temp = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + world.getName() + ".builders");
		if(temp != null) {
			temp.remove(builder);
		} else {
			temp = new ArrayList<String>();
			temp.remove(builder);
		}
		FileManager.getArenaConfig().set("arenaworlds.arenaworlds." + world.getName() + ".builders", temp);
		FileManager.saveArenaConfig();
	}
	public static void resetBuildersInConfig(ArenaWorld world) {
		FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + world.getName() + ".builders").clear();
		
		FileManager.saveArenaConfig();
	}
	public static boolean isArenaValid(String ID) {
		String path = "arenas.arenas." + ID;
		//if(FileManager.getArenaConfig().getString(path + ".name") != null) {
			if(FileManager.getArenaConfig().getString(path + ".lobby") != null) {
				if(FileManager.getArenaConfig().getString(path + ".minPlayers") != null
						&& FileManager.getArenaConfig().getInt(path + ".minPlayers") >= 0) {
					if(FileManager.getArenaConfig().getString(path + ".maxPlayers") != null
							&& FileManager.getArenaConfig().getInt(path + ".maxPlayers") >= FileManager.getArenaConfig().getInt(path + ".minPlayers")) {
						if(FileManager.getArenaConfig().getStringList(path + ".spawns") !=null) {
							if(FileManager.getArenaConfig().getStringList(path + ".spawns").get(0) != null) {
								return true;
							}
						}
					}
				}
			}
		//}
		return false;
	}
}
