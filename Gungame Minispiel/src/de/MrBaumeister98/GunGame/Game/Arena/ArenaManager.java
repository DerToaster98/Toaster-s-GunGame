package de.MrBaumeister98.GunGame.Game.Arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaJoinEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaLeaveEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ELeaveReason;
import de.MrBaumeister98.GunGame.Achievements.Achievements.GunGameAchievement;
import de.MrBaumeister98.GunGame.Achievements.Achievements.GunGameAchievementUtil;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class ArenaManager {
	
	public GunGamePlugin plugin;
	
	public List<Arena> arenas = new ArrayList<Arena>();
	public List<ArenaWorld> arenaWorlds = new ArrayList<ArenaWorld>();
	//public List<ArenaWorld> usedWorlds = new ArrayList<ArenaWorld>();
	
	public List<World> gunGameWorlds = new ArrayList<World>();
	
	public HashMap<UUID, Arena> players = new HashMap<UUID,Arena>();
	public HashMap<String, Arena> Arenas = new HashMap<String, Arena>();
	public HashMap<String, ArenaWorld> ArenaWorlds = new HashMap<String, ArenaWorld>();
	
	public List<UUID> spectatoriIDs = new ArrayList<UUID>();
	
	private SpectatorHidingThread specHider;
		
	public ArenaManager(GunGamePlugin main) {
		this.plugin = main;
		this.specHider = new SpectatorHidingThread(this);
		this.specHider.start();
	}
	public boolean isSpectator(Player p) {
		if(this.spectatoriIDs.contains(p.getUniqueId())) {
			return true;
		}
		return false;
	}
	public boolean isNameValid(String name) {
		Boolean ret = false;
		for(Arena a : this.arenas) {
			if(a.getName().equalsIgnoreCase(name)) {
				return true;
			} else {
				ret = false;
			}
		} 
		return ret;
	}
	
	public void createArena(String name, int minP, int maxP, int kills) {
		Arena arena = new Arena(this/**, this.arenas.size()**/, name, minP, maxP, kills);
		
		arena.setEnabled(true);
		this.arenas.add(arena);
		this.Arenas.put(name, arena);
		
		//FileManager.getArenaConfig().getStringList("arenas.arenalist").add(name);
		//FileManager.saveArenaConfig();
	}
	
	public void createArenaWorld(String name) {
		ArenaWorld aworld = new ArenaWorld(name, this);
		this.arenaWorlds.add(aworld);
		this.ArenaWorlds.put(name, aworld);
		
		FileManager.getArenaConfig().getStringList("arenaworlds.worldlist").add(name);
		FileManager.saveArenaConfig();
		ArenaFileStuff.addArenaWorldToConfig(name);
	}
	
	public void createArenaWorld(String name, String world) {
		ArenaWorld aworld = new ArenaWorld(name, this);
		aworld.setWorld(world);
		this.arenaWorlds.add(aworld);
		Bukkit.getWorld(world).setAutoSave(false);
		this.ArenaWorlds.put(name, aworld);
		
		FileManager.getArenaConfig().getStringList("arenaworlds.worldlist").add(name);
		FileManager.saveArenaConfig();
		ArenaFileStuff.setArenaWorldToConfig(name, world);
	}

	public void enableArena(Arena a, boolean b) {
		a.setEnabled(b);
	}
	public void enableArena(Arena a) {
		if(a.isEnabled() == true) {
			a.setEnabled(false);
		} else {
			a.setEnabled(true);
		}
	}
	
	public void deleteArena(Arena a) {
		this.arenas.remove(a);
	}
	/*
	public Arena getArena(int arenaID) {
		return this.arenas.get(arenaID);
	}*/
	public Arena getArena(String arenaName) {
		//int temp = arenaNames.get(arenaName);
		return this.Arenas.get(arenaName);
		/**Arena a = null;
		for(Arena a2 : this.arenas) {
			if(arenaName.equals(a2.getName())) {
				a2 = a;
				return a;
			} else {
				return null;
			}
		} return a;**/
	}
	public ArenaWorld getArenaWorld(String arenaWorldName) {
		return this.ArenaWorlds.get(arenaWorldName);
	}
	public Arena getArena(Player p) {
		return getArena(p.getUniqueId());
	}
	public Arena getArena(UUID id) {
		if(this.players.containsKey(id) && this.players.get(id) != null) {
			Arena ret = this.players.get(id);
			
			return ret;
		} else {
			return null;
		}
	}
	
	public int getPlayers(Arena a) {
		int temp = 0;
		
		for (Arena a2 : this.players.values()) {
			if(a2 == a) {
				temp++;
			}			
		}
		
		return temp;
	}
	
	public int getPlayers(String Name) {
		int temp = 0;
		Arena a = this.Arenas.get(Name);
		
		for (Arena a2 : this.players.values()) {
			if(a2 == a) {
				temp++;
			}			
		}
		
		return temp;
	}
	
	public List<Arena> getArenaList() {
		return this.arenas;
	}
	
	public boolean isWorldLoaded(String worldName) {
		for(World w : Bukkit.getServer().getWorlds()) {
			if(w.getName().equals(worldName)) {
				return true;
			}
		} return false;
	}
	
	public void initializeArenas() {
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Arenas...");
		List<String> arenas = FileManager.getArenaConfig().getStringList("arenas.arenalist");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + arenas.size() + ChatColor.YELLOW + " Arena(s), loading...");
		
		try {
			for(String a : arenas ) {
				try {
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Arena: " + ChatColor.GREEN + a);
					boolean enabled = FileManager.getArenaConfig().getBoolean("arenas.arenas." + a + ".enabled");
					Integer minP = FileManager.getArenaConfig().getInt("arenas.arenas." + a + ".minPlayers");
					Integer maxP = FileManager.getArenaConfig().getInt("arenas.arenas." + a + ".maxPlayers");
					Integer killsToWin = FileManager.getArenaConfig().getInt("arenas.arenas." + a + ".killsToWin");
					String Name = a;
					//String aWorld = FileManager.getArenaConfig().getString("arenas.arenas." + a + ".arenaWorld");
					Location lobby = Util.stringToLoc(FileManager.getArenaConfig().getString("arenas.arenas." + Name + ".lobby"));
					//List<String> spawns = FileManager.getArenaConfig().getStringList("arenas.arenas." + Name + ".spawns");
					
					Arena arena = new Arena(this,  Name, minP, maxP, killsToWin);
					arena.setEnabled(enabled);
					
					//initializeRestOfArena(arena/*, spawns*/, lobby, Name, a);
					arena.setLobby(lobby);
					this.arenas.add(arena);
					this.Arenas.put(Name, arena);
					
					List<Location> signs = new ArrayList<Location>();
					for(String signS : FileManager.getArenaConfig().getStringList("arenas.arenas." + Name + ".signs")) {
						Location signL = Util.stringToLoc(signS);
						signs.add(signL);
					}
					arena.setSigns(signs);
					
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Finished loading Arena: " + ChatColor.GREEN + a);
					
				} catch (Exception ex){
					ex.printStackTrace();
					Debugger.logWarning(ex.getMessage());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Debugger.logWarning(ex.getMessage());
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading ArenaWorlds...");
		List<String> arenaworlds = FileManager.getArenaConfig().getStringList("arenaworlds.worldlist");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + arenaworlds.size() + ChatColor.YELLOW + " ArenaWorld(s), loading...");
		
		try {
			for(String a : arenaworlds) {
				try {
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading ArenaWorld: " + ChatColor.GREEN + a);
					String name = a;
					String worldName = FileManager.getArenaConfig().getString("arenaworlds.arenaworlds." + name + ".world");
					ArenaWorld temp = new ArenaWorld(name, this);
					temp.setWorld(worldName);
					if(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName)) == false) {
						temp.loadWorld();
					}
					//temp.loadArenaWorld();
					//if(Bukkit.getWorld(worldName) != null) {
						List<String> spawns = /*Util.stringsToLocs(*/FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + name + ".spawns")/*)*/;
						List<String> builders = FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + name + ".builders");
						List<String> tanks = /*Util.stringsToLocs(*/FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + name + ".tankspawns")/*)*/;
						List<String> turrets = /*Util.stringsToLocs(*/FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + name + ".turretspawns")/*)*/;
						ArenaWorld aworld = new ArenaWorld(name, this, spawns, builders, worldName, tanks, turrets);
						this.arenaWorlds.add(aworld);
						this.ArenaWorlds.put(name, aworld);
						World world = Bukkit.getWorld(worldName);
						if(world != null && Bukkit.getWorlds().contains(world)) {
							world.setAutoSave(false);
							world.setKeepSpawnInMemory(false);
							gunGameWorlds.add(world);
							if(world.isAutoSave() == false) {
								Debugger.logInfoWithColoredText(ChatColor.DARK_RED + "Disabled auto save for arena world: " + ChatColor.GREEN + worldName);
							}
						}
						String resourcepackDL = FileManager.getArenaConfig().getString("arenaworlds.arenaworlds." + name + ".resourcepackLink");
						aworld.setResourcePackLInk(resourcepackDL);
						
						Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Finished loading ArenaWorld: " + ChatColor.GREEN + a);
					/*} else {
						Debugger.broadCastToConsole(ChatColor.RED + "[GunGame] " + ChatColor.YELLOW + "World of ArenaWorld " + ChatColor.GREEN + a + ChatColor.YELLOW + " not found; loading it...");
						ArenaWorld temp2 = new ArenaWorld(name, this);
						temp2.setWorld(worldName);
						temp2.loadArenaWorld();
						
						List<Location> spawns = Util.stringsToLocs(FileManager.getArenaConfig().getStringList("arenaworlds.arenaworlds." + name + ".spawns"));
						ArenaWorld aworld = new ArenaWorld(name, this, spawns, worldName);
						this.arenaWorlds.add(aworld);
						this.ArenaWorlds.put(name, aworld);
						World world = Bukkit.getWorld(worldName);
						world.setAutoSave(false);
						gunGameWorlds.add(world);
						Debugger.broadCastToConsole(ChatColor.RED + "[GunGame] " + ChatColor.DARK_RED + "Disabled auto save for arena world: " + ChatColor.GREEN + worldName);
						Debugger.broadCastToConsole(ChatColor.RED + "[GunGame] " + ChatColor.YELLOW + "Finished loading ArenaWorld: " + ChatColor.GREEN + a);
					}*/
					
				} catch (Exception ex) {
					ex.printStackTrace();
					Debugger.logWarning(ex.getMessage());
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			Debugger.logWarning(ex.getMessage());
		}
	}
	public boolean isIngame(Player p) {
		if(p != null) {
			return isIngame(p.getUniqueId());
		} return false;
	}
	public boolean isIngame(UUID id) {
		if(id != null) {
			if(getArena(id) != null) {
				List<UUID> playersInArena = new ArrayList<UUID>();
				for(Player p : getArena(id).getPlayers()) {
					playersInArena.add(p.getUniqueId());
				}
				if(playersInArena != null && !playersInArena.isEmpty() && playersInArena.contains(id)) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	public List<Player> getPlayerList(Arena a) {
		List<Player> temp = new ArrayList<Player>();
		
		for(UUID uuid : this.players.keySet()) {
			if(this.players.get(uuid) == a) {
				Player p = Bukkit.getPlayer(uuid);
				
				if(p != null) {
					temp.add(p);
				}
			}
		}
		
		return temp;
	}
	
	public void joinGame(Player p, Arena a) {
		Util.saveInventory(p);
		p.setLevel(0);
		p.setExp(0.0F);
		p.setTotalExperience(0);
		
		this.players.put(p.getUniqueId(), a);
		//a.addPlayer(p);
		p.teleport(a.getLobby());
		a.joinLobby(p);
		
		GunGameAchievementUtil ggachUtil = GunGamePlugin.instance.achUtil;
		GunGameAchievement firstAch = ggachUtil.achievements.get(0);
		firstAch.getAdv().grant(p);
	}
	public void ragequit(UUID id) {
		Arena a = getArena(id);
		a.remPlayer(Bukkit.getPlayer(id));
		if(this.spectatoriIDs.contains(id)) {
			this.spectatoriIDs.remove(id);
		}
		this.players.remove(id);
		ArenaLeaveEvent leaveEvent = new ArenaLeaveEvent(a, Bukkit.getPlayer(id), ELeaveReason.QUIT_GAME);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
	}
	@SuppressWarnings("deprecation")
	public void leaveGame(Player p, Arena a) {
		//p.teleport(Util.GlobalLobby);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player: " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has left the Arena: " + ChatColor.RED + a.getName());
		a.remPlayer(p);
		p.teleport(Util.getGlobalLobby());
		try {
			Util.restoreInventory(p);
		} catch (IOException ex) {
			Debugger.logWarning("An error occured while trying to restore the inventory of player " + p.getName() + "! Exception: " + ex);
		}
		this.players.remove(p.getUniqueId());
		/*this.players.remove(p.getUniqueId(), a);
		this.players.put(p.getUniqueId(), null);*/
		p.setInvulnerable(false);
		for(Player pOnline : Bukkit.getOnlinePlayers()) {
			if(!p.getUniqueId().equals(pOnline.getUniqueId())) {
				try {
					if(GunGamePlugin.instance.serverPre113) {
						p.showPlayer(pOnline);
					} else {
						p.showPlayer(GunGamePlugin.instance, pOnline);
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		//p.setResourcePack(Util.defaultPack);
		/*if(a.getPlayers().isEmpty() | a.getMinPlayers() <= a.getPlayers().size()) {
			a.cancelArena();
		}*/
	}
	
	public void unloadArenaWorlds() {
		for(ArenaWorld a : this.arenaWorlds) {
			a.unloadArenaWorld(false);
		}
	}
	
	public EGameState getGameState(Arena a) {
		return a.getGameState();
	}
	
	public void tryJoin(Player p, Arena a) {
		if(isIngame(p) == false) {
			if(a.isEnabled()) {
				if(a.getGameState() == EGameState.LOBBY | a.getGameState() == EGameState.VOTING | a.getGameState() == EGameState.STARTING) {
					if(a.isFull() == false) {
						ArenaJoinEvent joinevent = new ArenaJoinEvent(a, p);
						Bukkit.getServer().getPluginManager().callEvent(joinevent);
						if(!joinevent.isCancelled()) {
							joinGame(p, a);
							Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player: " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has joined the Arena: " + ChatColor.RED +a.getName());
							p.sendMessage(LangUtil.createString("lang.Info.joined",
									a,
									(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
									p,
									null,
									null,
									a.getMinPlayers(),
									a.getMaxPlayers(),
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false));
						}
					} else {
						p.sendMessage(LangUtil.createString("lang.Errors.arenaIsFull",
								a,
								(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
								p,
								null,
								null,
								a.getMinPlayers(),
								a.getMaxPlayers(),
								null,
								null,
								null,
								null,
								null,
								null,
								null,
								false,
								true));
					}
				} else {					
					p.sendMessage(LangUtil.createString("lang.Errors.arenaInProgress",
							a,
							(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
							p,
							null,
							null,
							a.getMinPlayers(),
							a.getMaxPlayers(),
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							false,
							true));
				}
			} else {
				p.sendMessage(LangUtil.createString("lang.Errors.arenaNotEnabled",
						a,
						(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
						p,
						null,
						null,
						a.getMinPlayers(),
						a.getMaxPlayers(),
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						false,
						true));
			}
		}
	}
	public void addSpectator(Player p) {
		p.setGameMode(GameMode.SPECTATOR);
		p.setGlowing(true);
		this.spectatoriIDs.add(p.getUniqueId());
	}
	public void remSpectators(Arena a) {
		for(Player p : a.getPlayers()) {
			List<UUID> specIDs = new ArrayList<UUID>(this.spectatoriIDs);
			if(this.spectatoriIDs.contains(p.getUniqueId())) {
				this.spectatoriIDs.remove(p.getUniqueId());
			}
			for(UUID id : specIDs) {
				Player spec = Bukkit.getPlayer(id);
				Arena specA = this.getArena(spec);
				if(specA.equals(a)) {
					if(!p.canSee(spec)) {
						p.showPlayer(GunGamePlugin.instance, spec);
					}
				}
			}
			specIDs.clear();
		}
	}

}
