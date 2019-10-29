package de.MrBaumeister98.GunGame.Game.Arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.SendableException;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.Util.FileUtil;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.Util.ZipFileUtil;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Mechanics.LootChests;
import de.MrBaumeister98.GunGame.Game.Util.EWorldState;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;
import de.tr7zw.nbtapi.NBTFile;
import net.md_5.bungee.api.ChatColor;

public class GameWorld {
	
	public Arena owner;
	private ArenaManager manager;
	private ArenaWorld parent;
	
	private Boolean explosionsBreakBlocks;
	private Boolean fireSpred;
	private Boolean regenerateHealth;
	private Boolean protectWater;
	private Boolean physics;
	
	private World world;
	private List<Location> spawns;
	private List<Player> players;
	
	private List<Location> turretsToSpawn;
	private List<Location> tanksToSpawn;
	
	private String resourcepack = null;
	
	private EWorldState state;
	
	private List<UUID> tankItems;
	private int tankItemFlameTaskID;
	
	@SuppressWarnings("unused")
	private Boolean readyToPlay;

	
	public GameWorld(Arena owner) {
		this.owner = owner;
		this.setManager(this.owner.manager);
		
		GunGamePlugin.instance.chunkloadlistener.addGameWorld(this);
		
		this.setState(EWorldState.IDLE);
		this.setReadyToPlay(false);
	}
	
	public void initialize(ArenaWorld parent, Boolean explosionBreakBlocks, Boolean spreadFire, Boolean regenerateHealth, Boolean physics, Boolean protectWaterBodies) {
		this.setState(EWorldState.LOADING);
		
		this.explosionsBreakBlocks = explosionBreakBlocks;
		this.fireSpred = spreadFire;
		this.protectWater = protectWaterBodies;
		this.regenerateHealth = regenerateHealth;
		this.physics = physics;
		
		this.parent = parent;
		
		if(this.spawns != null && !this.spawns.isEmpty()) {
			this.spawns.clear();
		}
		this.spawns = new ArrayList<Location>();
		//this.spawns = new ArrayList<Location>(this.parent.getSpawnList());
		for(String sLoc : this.parent.getSpawnList()) {
			Location loc = Util.stringToLoc(sLoc);
			this.spawns.add(loc);
		}
		
		if(this.tanksToSpawn != null && !this.tanksToSpawn.isEmpty()) {
			this.tanksToSpawn.clear();
		}
		this.tanksToSpawn = new ArrayList<Location>();
		//this.tanksToSpawn = new ArrayList<Location>(this.parent.getTankSpawnList());
		for(String sLoc : this.parent.getTankSpawnList()) {
			Location loc = Util.stringToLoc(sLoc);
			this.tanksToSpawn.add(loc);
		}
		
		if(this.turretsToSpawn != null && !this.turretsToSpawn.isEmpty()) {
			this.turretsToSpawn.clear();
		}
		this.turretsToSpawn = new ArrayList<Location>();
		//this.turretsToSpawn = new ArrayList<Location>(this.parent.getTurretSpawnList());
		for(String sLoc : this.parent.getTurretSpawnList()) {
			Location loc = Util.stringToLoc(sLoc);
			this.turretsToSpawn.add(loc);
		}
		
		Bukkit.getScheduler().cancelTask(this.tankItemFlameTaskID);
		if(this.tankItems != null && !this.tankItems.isEmpty()) {
			this.tankItems.clear();
		}
		this.tankItems = new ArrayList<UUID>();
		this.tankItemFlameTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				try {
					if(tankItems != null && !tankItems.isEmpty()) {
						List<Location> locs = new ArrayList<Location>();
						for(UUID id : tankItems) {
							Entity ent = Bukkit.getEntity(id);
							if(ent != null && !ent.isDead()) {
								locs.add(ent.getLocation().getBlock().getLocation());
							}
						}
						if(locs != null && !locs.isEmpty()) {
							for(Location loc : locs) {
								loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.getX() - 0.125, loc.getY() -0.25, loc.getZ() - 0.125, 5, 0.25, 0.5, 0.25,  0.2);
							}
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}, 0, 2);
		
		if(this.players != null && !this.players.isEmpty()) {
			this.players.clear();
		}

		this.players = new ArrayList<Player>();
		this.resourcepack = parent.getResourcePackLInk();
		
		try {
			unzipWorldSave();
		} catch (SendableException e) {
			this.owner.cancelArena();
			e.printStackTrace();
		}
	}
	public void joinGame(Player p) {
		if(this.resourcepack != null) {
			p.setResourcePack(this.resourcepack);
		}
	}
	private void unzipWorldSave() throws SendableException {
		File saveFolder = FileManager.getBackupFolder();
		File worldZipFile = new File(saveFolder, this.parent.getWorld() + ".zip");
		
		File worldContainer = Bukkit.getWorldContainer();
		String worldName = "GunGame-" + this.owner.getName();
		File worldFolder = new File(worldContainer, worldName);
		
		GameWorld temp = this;
		
		Bukkit.getScheduler().runTaskAsynchronously(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				try {
					ZipFileUtil.unzipFileIntoDirectory(worldZipFile, worldFolder);
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Arena: " + temp.owner.getName() + " : " + ChatColor.GREEN + "Successfully unzipped Map files!");
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Arena: " + temp.owner.getName() + " : " + ChatColor.GREEN + "Loading world...");
					File uidFile = new File(worldFolder, "uid.dat");
					//Debugger.logInfo(uidFile.getAbsolutePath());
					try {
						if(uidFile.exists() && uidFile.delete()) {
							Debugger.logInfoWithColoredText(ChatColor.RED + "Successfully deleted the uid.dat file from World " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
						} else {
							Debugger.logInfoWithColoredText(ChatColor.RED + "Unable to delete uid.dat from World " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
						}
						
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					File sessionFile = new File(worldFolder, "session.lock");
					//Debugger.logInfo(uidFile.getAbsolutePath());
					try {
						if(sessionFile.exists() && sessionFile.delete()) {
							Debugger.logInfoWithColoredText(ChatColor.RED + "Successfully deleted the session.lock file from World " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
						} else {
							Debugger.logInfoWithColoredText(ChatColor.RED + "Unable to delete session.lock from World " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
						}
						
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
					File levelFile = new File(worldFolder, "level.dat");
					try {
						NBTFile levelFileNBT = new NBTFile(levelFile);
						levelFileNBT.setString("LevelName", worldName);
						levelFileNBT.save();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
					temp.manager.plugin.achUtil.areAchievementsUpToDate(worldFolder, true);
					
					//World world2 = this.world;
					Bukkit.getServer().getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
						
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							String worldName2 = "GunGame-" + temp.owner.getName();
							//Debugger.logInfo(worldName2);
							World world2 = new WorldCreator(worldName2).type(WorldType.FLAT).createWorld();
							world2.setKeepSpawnInMemory(false);
							world2.setAutoSave(false);
							setWorld(world2);
							
							//if(GunGamePlugin.instance.serverPre113) {
								world2.setGameRuleValue("doMobSpawning", "false");
								world2.setGameRuleValue("doTileDrops", "false");
								world2.setGameRuleValue("announceAdvancements", "false");
								world2.setGameRuleValue("commandBlockOutput", "false");
								world2.setGameRuleValue("doEntityDrops", "false");
								world2.setGameRuleValue("doFireTick", String.valueOf(temp.fireSpred));
								world2.setGameRuleValue("sendCommandFeedback", "false");
								world2.setGameRuleValue("showDeathMessages", "false");
								world2.setGameRuleValue("spawnRadius", "0");
								world2.setGameRuleValue("reducedDebugInfo", "true");
								world2.setGameRuleValue("naturalRegeneration", String.valueOf(temp.regenerateHealth));
								world2.setGameRuleValue("keepInventory", "true");
								world2.setGameRuleValue("mobGriefing", "false");
								world2.setGameRuleValue("spectatorsGenerateChunks", "false");
							/*} else {
								
							}*/
							world2.setMetadata("GG_ExplosionBreakBlocks", new FixedMetadataValue(GunGamePlugin.instance, temp.explosionsBreakBlocks));
							world2.setMetadata("GG_Physics", new FixedMetadataValue(GunGamePlugin.instance, temp.physics));
							world2.setMetadata("GG_ProtectWater", new FixedMetadataValue(GunGamePlugin.instance, temp.protectWater));
							
							for(Location loc : spawns) {
								loc.setWorld(world2);
							}
							for(Location loc : tanksToSpawn) {
								loc.setWorld(world2);
							}
							for(Location loc : turretsToSpawn) {
								loc.setWorld(world2);
							}
							
							state = EWorldState.INGAME;
							setWorld(world2);
							if(!temp.owner.isCancelled()) {
								temp.setReadyToPlay(true);
								temp.owner.preStartArena();
							} else {
								stopGame();
							}
						}
					}, 10L);
				} catch (IOException e) {
					e.printStackTrace();
					try {
						throw new SendableException("Error while unzipping, check console for more info.");
					} catch (SendableException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		

	}
	
	public void stopGame() {
		try {
			if(this.world != null) {
				LootChests.resetChestsinWorld(this.world);
				this.setReadyToPlay(false);
				this.state = EWorldState.UNLOADING;
				if(this.world != null && this.world.getPlayers().size() >= 0) {
					for(Player p : this.world.getPlayers()) {
						try {
							p.teleport(Util.getGlobalLobby());
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}		
				File worldFolder = new File(this.world.getWorldFolder().getAbsolutePath());
				if(Bukkit.unloadWorld(this.world, false)) {
					try {
						worldFolder.delete();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				Bukkit.unloadWorld(this.world, false);
				Bukkit.unloadWorld(this.world.getName(), false);
				worldFolder.delete();
				
				FileUtil.delete(worldFolder);
				for(File f : worldFolder.listFiles()) {
					f.delete();
				}
				
				this.parent = null;
				this.spawns.clear();
				this.players.clear();
				this.world = null;
				
				this.state = EWorldState.IDLE;
			}
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public Location getSpawn(Integer id) {
		if(!this.spawns.isEmpty()) {
			Location loc = this.spawns.get(id);
			return loc;
		} else {
			return null;
		}
	}
	public void spawnTurret(Location loc) {
		this.turretsToSpawn.remove(loc);
		Debugger.logInfoWithColoredText(ChatColor.AQUA + this.world.getName() + ": " + ChatColor.GREEN + "Spawning Turret at: " + ChatColor.LIGHT_PURPLE + Util.locToString(loc) + ChatColor.GREEN + "...");
		TurretConfig tc = GunGamePlugin.instance.turretManager.turrets.get(Util.getRandomNumber(GunGamePlugin.instance.turretManager.turrets.size()));
		new Turret(loc, Util.getRandomNumber(360), tc);
	}
	@SuppressWarnings("deprecation")
	public void spawnTank(Location loc) {
		this.tanksToSpawn.remove(loc);
		Debugger.logInfoWithColoredText(ChatColor.AQUA + this.world.getName() + ": " + ChatColor.GREEN + "Spawning Tank at: " + ChatColor.LIGHT_PURPLE + Util.locToString(loc) + ChatColor.GREEN + "...");
		TankConfig tc = this.manager.plugin.tankManager.getTankConfigs().get(Util.getRandomNumber(this.manager.plugin.tankManager.getTankConfigs().size()));
		//new Tank(loc.add(0, 1, 0), tc, GunGamePlugin.instance.tankManager);
		Item item = loc.getWorld().dropItem(loc, tc.getTankItem());
		item.setVelocity(new Vector(0, 0, 0));
		item.setGravity(true);
		//item.setGlowing(true);
		item.setPersistent(true);
		item.setTicksLived(Integer.MIN_VALUE);
		item.setPickupDelay(1);
		item.setInvulnerable(true);
		item.setMetadata("GG_TankItem", new FixedMetadataValue(GunGamePlugin.instance, true));
		this.tankItems.add(item.getUniqueId());
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getEntity(item.getUniqueId()).setGravity(false);
			}
		}, 10);
	}

	public EWorldState getState() {
		return state;
	}
	public List<Location> getSpawns() {
		return this.spawns;
	}

	public void setState(EWorldState state) {
		this.state = state;
	}

	public ArenaManager getManager() {
		return manager;
	}

	public void setManager(ArenaManager manager) {
		this.manager = manager;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Boolean isReadyToPlay() {
		//return readyToPlay;
		return true;
	}

	public void setReadyToPlay(Boolean readyToPlay) {
		this.readyToPlay = readyToPlay;
	}

	public List<Location> getTurretsToSpawn() {
		return turretsToSpawn;
	}

	public void setTurretsToSpawn(List<Location> turretsToSpawn) {
		this.turretsToSpawn = turretsToSpawn;
	}

	public List<Location> getTanksToSpawn() {
		return tanksToSpawn;
	}

	public void setTanksToSpawn(List<Location> tanksToSpawn) {
		this.tanksToSpawn = tanksToSpawn;
	}

}
