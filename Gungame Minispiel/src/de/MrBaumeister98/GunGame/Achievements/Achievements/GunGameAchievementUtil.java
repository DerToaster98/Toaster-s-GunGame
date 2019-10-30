package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.MrBaumeister98.GunGame.Achievements.AdvencementAPI.AdvancementAPI;
import de.MrBaumeister98.GunGame.Achievements.AdvencementAPI.FrameType;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Listeners.GameListener;

public class GunGameAchievementUtil {
	
	public HashMap<String, GunGameAchievement>achievementMap;
	private HashMap<String, AdvancementAPI>advancementMap;
	private HashMap<String, String>parentMap;
	public List<GunGameAchievement> achievements;
	private GunGamePlugin plugin;
	private File achFF;
	private FileConfiguration achFFC;
	private File advFolder;
	public Boolean forceUpdate;
	
	public GunGameAchievementUtil(GunGamePlugin main) {
		
		Debugger.logInfoWithColoredText(ChatColor.RED + "Initializing Achievement System...");
		
		this.loadAchFile();
		
		this.setPlugin(main);
		
		this.achievementMap = new HashMap<String,GunGameAchievement>();
		this.parentMap = new HashMap<String,String>();
		//this.registerdAchievements = new ArrayList<GunGameAchievement>();
		this.advancementMap = new HashMap<String,AdvancementAPI>();
		this.achievements = new ArrayList<GunGameAchievement>();
		
		this.forceUpdate = this.getAchFile().getBoolean("ForceUpdateOnNextStart");
		
		//createGGADV();
		updateAchievementsInMainWorld(Bukkit.getWorlds().get(0), true);
		
		setup();
		
		
		createFolderInAllWords();
		
		if(this.forceUpdate == true) {
			this.getAchFile().set("ForceUpdateOnNextStart", false);
			try {
				this.getAchFile().save(achFF);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bukkit.getServer().reloadData();
		}
	}
	private void updateAchievementsInMainWorld(World world, Boolean unloadWorld) {
		if(!areAchievementsUpToDate(world) || this.forceUpdate == true) {
			
			String wn = world.getName();
			if(unloadWorld) {

				Environment env = world.getEnvironment();
				WorldType wType = world.getWorldType();
				long wSeed = world.getSeed();
				Bukkit.unloadWorld(world, false);
				WorldCreator wc = new WorldCreator(wn);
				wc.seed(wSeed);
				wc.type(wType);
				wc.environment(env);
				World w2 = Bukkit.createWorld(wc);
				w2.setKeepSpawnInMemory(false);
				
				world = w2;
			}
			String folderLoc;
			if(GunGamePlugin.instance.serverPre113) {
				folderLoc = world.getWorldFolder().getAbsolutePath() + "data/advancements/gungame";
			} else {
				folderLoc = world.getWorldFolder().getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame";
			}
			File folder = new File(folderLoc);
			
			if(folder.exists()) {
				try {
					FileUtils.deleteDirectory(folder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				folder.mkdirs();
			}
			
			File infoFileF = new File(world.getWorldFolder().getAbsolutePath() + "/data/gungame", "info.yml");
			FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
			
			if(!infoFileF.exists()) {
				//infoFileF.mkdirs();
				try {
					infoFile.set("GunEngineGrief.Explosions", false);
					infoFile.set("GunEngineGrief.PhysicsEngine", false);
					infoFile.set("GunEngineGrief.BulletsBreakGlass", false);
					infoFile.set("GunEngineGrief.BulletsIgniteTNT", false);
					
					infoFile.save(infoFileF);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			infoFile.set("InstalledAdvancements", true);
			infoFile.set("Version", GunGamePlugin.instance.getDescription().getVersion());
			
			try {
				infoFile.save(infoFileF);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Bukkit.getServer().reloadData();
		}
	}
	
	private void setup() {
		FileConfiguration config = this.getAchFile();
		
		for(String achName : config.getStringList("Names")) {
			String[] temp = achName.split("-");
			
			String key = temp[0];
			String parentName = config.getString("Parents." + key);
			//String name = LangUtil.getStringByPath("lang.Achievements." + key + ".Name");
			String name = /*LangUtil.getAchievementName(key);*/getAchievementName(key);
			//List<String> desc = LangUtil.getStringListByPath("lang.Achievements." + key + ".Desc");
			List<String> desc = /*LangUtil.getAchievementDescription(key);*/getAchievementDescription(key);
			String icon = config.getString("Icons." + key);
			CriteriaE crit = CriteriaE.valueOf(temp[2]);
			Integer toreach = Integer.valueOf(temp[3]);
			FrameType fType = FrameType.valueOf(temp[1]);
			boolean hd = false;
			if(key.length() >= 4) {
				try {
					hd = Boolean.valueOf(temp[4]);
				} catch(ArrayIndexOutOfBoundsException ex) {
					Debugger.logWarning("&eAchievemnt declaration of achievement: " + key + " doesn't seem to be correct! It should have 5 digits separated by '-', this one has " + temp.length + " digits");
					hd = false;
				}
			}
			
			GunGameAchievement ach = new GunGameAchievement(this, key, parentName, name, desc, icon, crit, toreach, fType, hd);
			
			this.achievementMap.put(key, ach);
			this.achievements.add(ach);
			this.parentMap.put(key, parentName);
		}
		
		do {
			Debugger.logInfoWithColoredText(ChatColor.LIGHT_PURPLE + "Installation In Progress...");
		} while (!ParentsLoaded());
	}
	
	private void createFolderInAllWords() {
		World mainWorld = Bukkit.getWorlds().get(0);
		String advFolder;
		if(GunGamePlugin.instance.serverPre113) {
			advFolder = mainWorld.getWorldFolder().getAbsolutePath() + "/data/advancements/gungame";
		} else {
			advFolder = mainWorld.getWorldFolder().getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame";
		}
		File aFolder = new File(advFolder);
		setAdvFolder(aFolder);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Copying Advancement Files to ALL Worlds...");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Main World Folder Advancement Location: " + ChatColor.RED + advFolder);
		List<World> worldList = new ArrayList<World>(Bukkit.getWorlds());
		worldList.remove(Bukkit.getWorlds().get(0));
		for(World w : worldList) {
			if(!areAchievementsUpToDate(w)) {
				copyToWorld(w, aFolder);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.RED + "Copied Advancement Files to all loaded Worlds!");
		
	}
	public Boolean areAchievementsUpToDate(File worldFolder, boolean installIfNotFound) {
		Boolean b;
		File achFolder;
		if(GunGamePlugin.instance.serverPre113) {
			achFolder = new File(worldFolder.getAbsolutePath() + "/data/advancements/gungame");
		} else {
			 achFolder = new File(worldFolder.getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame");
		}
		if(!achFolder.exists()) {
			b = false;
		} else {
			File infoFileF = new File(worldFolder.getAbsolutePath() + "/data/gungame", "info.yml");
			if(!infoFileF.exists()) {
				b = false;
			}
			FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
			
			Boolean installed = infoFile.getBoolean("InstalledAdvancements", false);
			
			if(installed) {
				String installedVersion = infoFile.getString("Version");
				if(GunGamePlugin.instance.getDescription().getVersion().equalsIgnoreCase(installedVersion)) {
					b = true;
				} else {
					b = false;
				}
			} else {
				b = false;
			}
			
		}
		
		if(installIfNotFound == true && b== false) {
			File aFolder = this.getAdvancementFolder();
			String folderLoc;
			if(GunGamePlugin.instance.serverPre113) {
				folderLoc = worldFolder.getAbsolutePath() + "/data/advancements/gungame";
			} else {
				 folderLoc = worldFolder.getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame";
			}
			File folder1 = new File(folderLoc);
			if(!folder1.exists()) {
				folder1.mkdirs();
				try {
					FileUtils.copyDirectory(aFolder, folder1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
			//if(aFolder.exists()) {
				try {
					FileUtils.deleteDirectory(folder1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				folder1.mkdirs();
				try {
					FileUtils.copyDirectory(aFolder, folder1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
			
			File infoFileF = new File(worldFolder.getAbsolutePath() + "/data/gungame", "info.yml");
			FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
			
			if(!infoFileF.exists()) {
				//infoFileF.mkdirs();
				try {
					infoFile.save(infoFileF);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			infoFile.set("InstalledAdvancements", true);
			infoFile.set("Version", GunGamePlugin.instance.getDescription().getVersion());
			
			try {
				infoFile.save(infoFileF);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return b;
	}
	public boolean areAchievementsUpToDate(World world) {
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Checking World: " + ChatColor.AQUA + world.getName() + ChatColor.YELLOW + "...");
		if(this.forceUpdate == true) {
			Debugger.logInfoWithColoredText(ChatColor.RED + "Force Update is enabled, updating Achievement files of world: " + ChatColor.AQUA + world.getName() + ChatColor.YELLOW + "...");
			return false;
		} else {
			try {			
				
				String achFolderLoc;
				if(GunGamePlugin.instance.serverPre113) {
					achFolderLoc = world.getWorldFolder().getAbsolutePath() + "/data/advancements/gungame";
				} else {
					 achFolderLoc = world.getWorldFolder().getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame";
				}
				File achFolder = new File(achFolderLoc);
				if(!achFolder.exists() || achFolder.listFiles().length <= 0) {
					Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are " + ChatColor.RED + "NOT " + ChatColor.YELLOW + "installed!");
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Installing Achievements in World: " + ChatColor.AQUA + world.getName() +  ChatColor.YELLOW + "...");
					return false;
				}
				
				String folderLoc = world.getWorldFolder().getAbsolutePath() + "/data/gungame";
				File folder = new File(folderLoc);
				
				if(!folder.exists()) {
					Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are " + ChatColor.RED + "NOT " + ChatColor.YELLOW + "up to date!");
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Updating Achievements in World: " + ChatColor.AQUA + world.getName() +  ChatColor.YELLOW + "...");
					return false;
				} else {
					File infoFileF = new File(folder, "info.yml");
					if(!infoFileF.exists()) {
						Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are " + ChatColor.RED + "NOT " + ChatColor.YELLOW + "up to date!");
						Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Updating Achievements in World: " + ChatColor.AQUA + world.getName() +  ChatColor.YELLOW + "...");
						return false;
					}
					FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
					
					Boolean installed = infoFile.getBoolean("InstalledAdvancements", false);
					
					if(installed) {
						String installedVersion = infoFile.getString("Version");
						if(GunGamePlugin.instance.getDescription().getVersion().equalsIgnoreCase(installedVersion)) {
							Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are up to date!");
							Debugger.logInfoWithColoredText(ChatColor.GREEN + "Continueing loading...");
							return true;
						} else {
							Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are " + ChatColor.RED + "NOT " + ChatColor.YELLOW + "up to date!");
							Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Updating Achievements in World: " + ChatColor.AQUA + world.getName() +  ChatColor.YELLOW + "...");
							return false;
						}
					} else {
						Debugger.logInfoWithColoredText(ChatColor.AQUA + world.getName() +  ": " + ChatColor.YELLOW + "Achievement files are " + ChatColor.RED + "NOT " + ChatColor.YELLOW + "installed and not up to date and files are missing!");
						Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Updating Achievements in World: " + ChatColor.AQUA + world.getName() +  ChatColor.YELLOW + "...");
						return false;
					}
					
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}	
		return false;
	}
	
	public void copyToWorld(World w, File aFolder) {
		try {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Processing World: " + ChatColor.AQUA + w.getName() + ChatColor.YELLOW + "...");
			String folderLoc;
			if(GunGamePlugin.instance.serverPre113) {
				 folderLoc = w.getWorldFolder().getAbsolutePath() + "/data/advancements/gungame";
			} else {
				 folderLoc = w.getWorldFolder().getAbsolutePath() + "/datapacks/bukkit/data/gungame/advancements/gungame";
			}
			File folder = new File(folderLoc);
			if(!folder.exists()) {
				folder.mkdirs();
				//FileUtils.copyDirectory(aFolder, folder);
			} else {
			//if(aFolder.exists()) {
				FileUtils.deleteDirectory(folder);
				folder.mkdirs();
				//FileUtils.copyDirectory(aFolder, folder);
			}		
			FileUtils.copyDirectory(aFolder, folder);	
			
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Creating Info File...");
			
			File infoFileF = new File(w.getWorldFolder().getAbsolutePath() + "/data/gungame", "info.yml");
			FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
			
			if(infoFileF.exists()) {
				infoFileF.delete();
			}
			
			if(!infoFileF.exists()) {
				//infoFileF.mkdirs();
				try {
					infoFileF.getParentFile().mkdirs();
					infoFileF.createNewFile();
					
					infoFile.set("GunEngineGrief.Explosions", false);
					infoFile.set("GunEngineGrief.PhysicsEngine", false);
					infoFile.set("GunEngineGrief.BulletsBreakGlass", false);
					infoFile.set("GunEngineGrief.BulletsIgniteTNT", false);
					//infoFile.save(infoFileF);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			infoFile.set("InstalledAdvancements", true);
			infoFile.set("Version", GunGamePlugin.instance.getDescription().getVersion());
			
			infoFile.save(infoFileF);
			
			String wn = w.getName();
			/*Environment env = w.getEnvironment();
			WorldType wType = w.getWorldType();
			long wSeed = w.getSeed();*/
			
			/*Bukkit.unloadWorld(w, false);
			WorldCreator wc = new WorldCreator(wn);
			wc.seed(wSeed);
			wc.type(wType);
			wc.environment(env);
			World w2 = Bukkit.createWorld(wc);
			w2.setKeepSpawnInMemory(false);*/
			GameListener.processed.add(wn);
			Bukkit.getServer().reloadData();
			
			Debugger.logInfoWithColoredText(ChatColor.GREEN + "Done!");
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public File getAdvancementFolder() {
		return this.advFolder;
	}
 	
	private Boolean ParentsLoaded() {
		for(/*GunGameAchievement ach : this.achievements*/int i = 0; i < this.achievements.size(); i++) {
			GunGameAchievement ach = this.achievements.get(i);
			if(ach.getParentName() != null && this.achievementMap.get(ach.getParentName()).isLoaded()) {
				ach.initialize();
				Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Successfully loaded Achievement: " + ChatColor.RED + ach.getKey());
				Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Parent: " + ChatColor.AQUA + ach.getParentName());
			} else if(ach.getParentName() == null) {
				ach.initialize();
				Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Successfully loaded Achievement: " + ChatColor.RED + ach.getKey());
				Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Parent: " + ChatColor.AQUA + ach.getParentName());
			} else if(ach.getParentName() != null) {
				return false;
			}
		}
		return true;
	}
	
	public GunGamePlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(GunGamePlugin plugin) {
		this.plugin = plugin;
	}
	
	private String getAchievementName(String achID) {
		String path = "Language." + achID + ".Name";
		String tmp = "Err404, not found";
		try {
			tmp = this.getAchFile().getString(path);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		if(tmp == null || tmp.equals("") || tmp.isEmpty()) {
			tmp = "&4ERROR: &cMissing value for name of achievemt: &e" + achID + "&c pathname: &e" + path;
		}
		tmp = ChatColor.translateAlternateColorCodes('&', tmp);
		return tmp;
	}
	private List<String> getAchievementDescription(String achID) {
		String path = "Language." + achID + ".Desc";
		List<String> temp = this.getAchFile().getStringList(path);
		List<String> ret = new ArrayList<String>();
		for(String s : temp) {
			ret.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		if(ret.isEmpty()) {
			ret.add("&4ERROR: &c List in config is empty");
		}
		return ret;
	}
	
	
	private void loadAchFile() {
		if(this.achFF == null) {
			this.achFF = new File(GunGamePlugin.instance.getDataFolder(), "AchievementDatabase.yml");
			GunGamePlugin.instance.saveResource("AchievementDatabase.yml", false);			
		}
		this.achFFC = new YamlConfiguration();
		
		try {
			achFFC.load(achFF);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	public FileConfiguration getAchFile() {
		if(this.achFFC == null) {
			loadAchFile();
		}
		return this.achFFC;
	}
	public AdvancementAPI getAdvancement(String name) {
		return this.advancementMap.get(name);
	}
	public void addAdvancement(String name, AdvancementAPI adv) {
		this.advancementMap.put(name, adv);
	}

	public void setAdvFolder(File advFolder) {
		this.advFolder = advFolder;
	}

}
