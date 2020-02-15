package de.MrBaumeister98.GunGame.Game.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaWorld;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import net.md_5.bungee.api.ChatColor;

public class VoteUtil {
	
	private static void castVote(Arena arena, String map) {
		if(arena.mapVoteMapping.get(map) != null) {
			Integer votes = arena.mapVoteMapping.get(map);
			votes++;
			arena.mapVoteMapping.put(map, votes);
		} else {
			arena.mapVoteMapping.put(map, 1);
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Arena: " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + ": Voting: Map: " + ChatColor.AQUA + map + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + arena.mapVoteMapping.get(map).toString() + ChatColor.YELLOW + " Votes!");
	}
	
	private static boolean hasEveryoneVoted(Arena arena) {
		Boolean ret = true;
		for(UUID id : arena.canVote.keySet()) {
			if(arena.canVote.get(id).equals(true)) {
				ret = false;
				break;
			}
		}
		return ret;
	}
	
	public static void castVote(Player p, String map) {
		Arena arena = GunGamePlugin.instance.arenaManager.getArena(p);
		arena.voteMap.put(p, map);
		arena.canVote.put(p.getUniqueId(), false);
		p.getInventory().remove(Material.NAME_TAG);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Arena: " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + ": Player " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " voted for the map " + ChatColor.AQUA + map);
		castVote(arena, map);
		
		if(hasEveryoneVoted(arena) && arena.getPlayers().size() >= arena.getMinPlayers()) {
			arena.endVotePhase();
		}
	}
	
	public static ArenaWorld chooseVictorMap(Arena arena) {
		ArenaWorld choosedMap = null;
		Debugger.logInfoWithColoredText( ChatColor.YELLOW + "Arena: " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + ": Computing voted Map...");
		for(String map : arena.mapVoteMapping.keySet()) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    - Map: " + ChatColor.AQUA + map + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + arena.mapVoteMapping.get(map).toString() + ChatColor.YELLOW + " Votes!") ;
		}
		Integer votes = 0;
		List<String> test = new ArrayList<String>();
		for(String gameMap : arena.mapVoteMapping.keySet()) {
			test.add(gameMap);
			int votesOfMap = arena.mapVoteMapping.get(gameMap);
			if(votesOfMap >= votes) {
				votes = votesOfMap;
				if(gameMap != "???") {
					choosedMap = GunGamePlugin.instance.arenaManager.getArenaWorld(gameMap);
				} else {
					choosedMap = getRandomMap(arena);
				}
			}
		}
		
		arena.setArenaWorld(choosedMap);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Arena: " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + ": Voted Map: " + ChatColor.AQUA + choosedMap.getName());
		return choosedMap;
	}
	
	public static void endVotePhase(Arena arena) {
		
		ArenaWorld aWorld = chooseVictorMap(arena);
		arena.setArenaWorld(aWorld);
		
		for(Player p : GunGamePlugin.instance.arenaManager.getPlayerList(arena)) {
			p.getInventory().remove(Material.NAME_TAG);
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Vote Phase of Arena " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + " has ended!");
		if(aWorld == null) {
			aWorld = getRandomMap();
			arena.setArenaWorld(aWorld);
		}
		if(aWorld != null) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Map of Arena " + ChatColor.RED + arena.getName() + ChatColor.YELLOW + " is: " + ChatColor.AQUA + aWorld.getName());
		}
		arena.updateSigns();
		arena.setGameState(EGameState.LOBBY);
	}
	
	public static void fillVoteMap(Arena arena) {
		List<ArenaWorld> maps = new ArrayList<ArenaWorld>(GunGamePlugin.instance.arenaManager.arenaWorlds);
		//maps = Main.plugin.manager.arenaWorlds;
		
		/*for(ArenaWorld used : Main.plugin.arenaManager.usedWorlds) {
			maps.remove(used);
			Debugger.logInfoWithColoredText(used.getName());
		}*/
		
		if(maps.isEmpty() == false) {
			for(int i = 0; i < 4; i++) {
				if(maps.size() >= 1) {
					ArenaWorld map = maps.get(Util.getRandomNumber(maps.size()));		
					arena.mapVoteMapping.put(map.getName(), 0);
					maps.remove(map);
				}				
			}
			arena.mapVoteMapping.put("???", 0);
		}
		
	}
	
	private static ArenaWorld getRandomMap() {
		ArenaWorld randomMap = null;
		List<ArenaWorld> maps = new ArrayList<ArenaWorld>(GunGamePlugin.instance.arenaManager.arenaWorlds);
		//maps = Main.plugin.manager.arenaWorlds;
		
		/*for(ArenaWorld used : Main.plugin.arenaManager.usedWorlds) {
			maps.remove(used);
		}*/
		
		if(maps.isEmpty() == false) {
			randomMap = maps.get(Util.getRandomNumber(maps.size()));
		}
		return randomMap;
	}
	private static ArenaWorld getRandomMap(Arena arena) {
		ArenaWorld choosed = null;
		List<String> mapNames = new ArrayList<String>();
		for(String s : arena.mapVoteMapping.keySet()) {
			mapNames.add(s);
		}
		mapNames.remove("???");
		String map = mapNames.get(Util.getRandomNumber(mapNames.size()));
		choosed = GunGamePlugin.instance.arenaManager.getArenaWorld(map);
		return choosed;		
	}
}
