package de.MrBaumeister98.GunGame.Game.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.EArenaGameMode;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import de.MrBaumeister98.GunGame.Items.C4;
import de.MrBaumeister98.GunGame.Items.Crowbar_pre_1_13;
import de.MrBaumeister98.GunGame.Items.Crowbar_v1_13_up;
import de.MrBaumeister98.GunGame.Items.InfoItem;
import de.MrBaumeister98.GunGame.Items.LeaveLobbyItem;
import de.MrBaumeister98.GunGame.Items.Radar;
import de.MrBaumeister98.GunGame.Items.SuicideArmor;
import de.MrBaumeister98.GunGame.Items.Voter;

public abstract class Util {
	
	public static Location GlobalLobby = null;
	public static String defaultPack = GunGamePlugin.instance.getConfig().getString("Config.DefaultResourcepack");
	//public static List<TNTPrimed> GunGameExplosives = new ArrayList<TNTPrimed>();
		
	private static Random randomGenerator = new Random();
	
	public static List<Material> shopBlocks = new ArrayList<Material>();
	public static List<Material> breakableBlocks = new ArrayList<Material>();

	//public static final boolean isCrowBarUnbreakable = GunGamePlugin.instance.getConfig().getBoolean("Config.Items.Crowbar.Unbreakable", true);
	//public static final short CrowBarDur = Short.valueOf(GunGamePlugin.instance.getConfig().getString("Config.Items.Crowbar.Durability"));

	public static final Integer votingPhaseDuration = GunGamePlugin.instance.getConfig().getInt("Config.VotePhaseDuration", 30);

	public static final Integer lobbyCountDown = GunGamePlugin.instance.getConfig().getInt("Config.LobbyCountDown", 10);
	public static final Integer protectionPhaseDuration = GunGamePlugin.instance.getConfig().getInt("Config.PreparingPhaseDuration", 10);
	
	public static final Integer maxIterationsLaserThread = GunGamePlugin.instance.getConfig().getInt("Config.GunEngine.LaserHelperIterations", 200);
	public static final Integer coinsPerKill = GunGamePlugin.instance.getConfig().getInt("Config.CoinsPerKill", 10);
	
	public static final boolean grantAchievementsAfterUnlocking = GunGamePlugin.instance.getConfig().getBoolean("Config.GrantAchievementsAfterMeetingCondition", true);
	
	public static List<String> allowedCmds = new ArrayList<String>();
	
	public static HashMap<Material, MeltToConfig> meltMap = new HashMap<Material, MeltToConfig>();

	public static void fillMeltList() {
		List<String> inFile = GunGamePlugin.instance.getConfig().getStringList("Config.GunEngine.LaserMeltConfig");
		if(inFile != null && !inFile.isEmpty()) {
			for(String cfg : inFile) {
				String[] vars = cfg.split("\\-");
				if(vars.length == 3) {
					try {
						Material from = Material.valueOf(vars[0]);
						Material to = Material.valueOf(vars[1]);
						Boolean changeBack = Boolean.valueOf(vars[2]);
						
						MeltToConfig mtcfg = new MeltToConfig(to, changeBack);
						
						Util.meltMap.put(from, mtcfg);
					} catch(IllegalArgumentException ex) {
						Debugger.logWarning("Warning: Unknown materials in meltlist ('"+ cfg + "')!");
					}
				}
			}
		}
	}


	//Helps to convert Locations to Strings and back
	//Needed for saving and loading the arenas of the arenas.yml file
	public static void getAllowedCommands() {
		List<String> inFile = GunGamePlugin.instance.getConfig().getStringList("Config.AllowedCommands");
		for(String s : inFile) {
			String add = "/" + s;
			allowedCmds.add(add);
		}
	}
	public static String locToString(Location l) {
		String ret;
		ret = l.getWorld().getName() + "|" + l.getBlockX() + "|" + l.getBlockY() + "|" + l.getBlockZ();
		
		return ret;
	}
	public static Location stringToLoc(String s) {
		String s2 = new String(s);
		
		String[] a = s2.split("\\|");
		
		World w = Bukkit.getServer().getWorld(a[0]);
		
		float x = Float.parseFloat(a[1]);
		float y = Float.parseFloat(a[2]);
		float z = Float.parseFloat(a[3]);
		
		return new Location(w, x, y, z);
	}
	public static Location getGlobalLobby() {
		String loc = GunGamePlugin.instance.getConfig().getString("Config.GlobalLobby");
		if(loc == null || loc == "") {
			
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Global Lobby not found. Using default Spawn Location instead! ");
			
			World DefaultWorld = Bukkit.getServer().getWorlds().get(0);
			GlobalLobby = DefaultWorld.getSpawnLocation();
			
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Global Lobby was set to: " + ChatColor.LIGHT_PURPLE + locToString(GlobalLobby));
			
			GunGamePlugin.instance.getConfig().set("Config.GlobalLobby", locToString(GlobalLobby));
			GunGamePlugin.instance.saveConfig();
			
			GunGamePlugin.instance.saveConfig();
			
			return GlobalLobby;			
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Global Lobby was found!");
			GlobalLobby = stringToLoc(loc);
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Global Lobby was set to: " + ChatColor.LIGHT_PURPLE + locToString(GlobalLobby));
			GunGamePlugin.instance.getConfig().set("Config.GlobalLobby", loc);
			GunGamePlugin.instance.saveConfig();
			
			return GlobalLobby;
		}
	}
	
	public static boolean isNameValid(String newArenaName) {
		if(FileManager.getArenaConfig().getStringList("arenas.arenalist").contains(ChatColor.stripColor(newArenaName)) == true) {
			return false;
		} else {
			return true;
		}
	}
	public static boolean isWorldNameValid(String newName) {
		if(FileManager.getArenaConfig().getStringList("arenaworlds.worldlist").contains(ChatColor.stripColor(newName)) == true) {
			return false;
		} else {
			return true;
		}
	}
	//My own Code
	public static List<Location> stringsToLocs(List<String> strings) {
		List<Location> locs = new ArrayList<Location>();
		
		for(String s : strings) {
			Location input = stringToLoc(s);
			locs.add(input);
		}
		return locs;
	}
	public static int getRandomNumber(int limit) {
		return randomGenerator.nextInt(limit);
	}
	public static boolean getRandomBoolean() {
		return randomGenerator.nextBoolean();
	}
	public static Double getRandomDouble() {
		return randomGenerator.nextDouble();
	}
	//Füllt die shopblock liste
	public static void loadShopBlocks() {
		List<String> shopBlockStrings = GunGamePlugin.instance.getConfig().getStringList("Config.ShopBlocks");
		for(String s : shopBlockStrings) {
			Material temp = Material.getMaterial(s);
			shopBlocks.add(temp);
		}
	}
	//Zum prüfen für onInteract methoden
	public static boolean isShopBlock(Material m) {
		int temp = 0;
		for(Material mat : shopBlocks) {
			if(m != null && m == mat) {
				temp++;
			}		
		} if(temp > 0) {
			return true;
		} else {
			return false;
		}
	}
	public static void giveLobbyItems(Player p, Boolean canVote) {
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().setItem(8, LeaveLobbyItem.leaveItem());
		p.getInventory().setItem(0, InfoItem.infoItemStack());
		if(canVote == true) {
			p.getInventory().setItem(1, Voter.votePaper());
		}		
		//p.getInventory().setItem(4, StatItem.statViewer(p));
	}
	public static File getPlayerGunGameFile(Player p) {
		String path = FileManager.getPlayerDataFolder().getAbsolutePath();
		String name = p.getUniqueId().toString() + ".yml";
		
		File file = new File(path, name);
		if(!file.exists()) {
			file.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	public static FileConfiguration getPlayerGunGameFileConfiguration(Player p) {
		String path = FileManager.getPlayerDataFolder().getAbsolutePath();
		String name = p.getUniqueId().toString() + ".yml";
		
		File file = new File(path, name);
		if(!file.exists()) {
			file.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		try {
			config.save(file);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return config;
	}
	public static void saveInventory(Player p) {
		File f = new File(FileManager.getPlayerDataFolder().getAbsolutePath(), p.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Saving inventory of Player " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + "...");

		ItemStack[] armor = p.getInventory().getArmorContents();
		ItemStack[] inv = p.getInventory().getContents();
		ItemStack[] extra = p.getInventory().getExtraContents();
		
		c.set("inventory.armor", armor);
		c.set("inventory.content", inv);
		c.set("inventory.extra", extra);
		
		c.set("experience.level", p.getLevel());
		c.set("experience.xp", p.getExp());
		c.set("experience.totalXP", p.getTotalExperience());
		
		c.set("gamemode", p.getGameMode().toString());
		
		c.set("location.join", locToString(p.getLocation()));
		c.set("location.compassloc", locToString(p.getCompassTarget()));
		
		c.set("inGame", "true");
		
		
		try {
			c.save(f);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save " + f.getName() + " to " + f + " !", ex);
		}
		p.setLevel(0);
		p.setExp(0.0F);
		p.setTotalExperience(0);
		
		p.getInventory().clear();
	}
	
	public static void restoreInventory(Player p) throws IOException {
		p.getInventory().clear();
		File f = new File(FileManager.getPlayerDataFolder().getAbsolutePath(), p.getUniqueId().toString() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
	
		String joinLocS = c.getString("location.join");
		Location joinLoc = stringToLoc(joinLocS);
		
		try {
			String[] temp = joinLocS.split("\\|");
			if(!GunGamePlugin.instance.arenaManager.isWorldLoaded(temp[0])) {
				WorldCreator worldCreator = new WorldCreator(temp[0]);
				worldCreator.createWorld();
				
				return;
			}
			p.teleport(joinLoc);
		} catch (Exception ex) {
			Debugger.logWarning("An error occured while restoring the inventory of player " + p.getName() +  "! Exception: " + ex);
			p.teleport(Util.getGlobalLobby());
		}
		
		String compassLocS = c.getString("location.compassloc");
		Location compassLoc = stringToLoc(compassLocS);
		
		p.setCompassTarget(compassLoc);
		
		List<?> armorL = c.getList("inventory.armor");
		List<?> contentL = c.getList("inventory.content");
		List<?> extraL = c.getList("inventory.extra");
		
		try {
			ItemStack[] armor = armorL.toArray(new ItemStack[armorL.size()]); 
			ItemStack[] content = contentL.toArray(new ItemStack[contentL.size()]);
			ItemStack[] extra = extraL.toArray(new ItemStack[extraL.size()]); 
			
			p.getInventory().setArmorContents(armor);
			p.getInventory().setContents(content);
			p.getInventory().setExtraContents(extra);
			
			int level = c.getInt("experience.level");
			float xp = Float.valueOf(c.getString("experience.xp"));
			int totalXP = c.getInt("experience.totalXP");
			
			p.setLevel(level);
			p.setExp(xp);
			p.setTotalExperience(totalXP);
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		
		//f.delete();
		c.set("inventory.armor", null);
		c.set("inventory.content", null);
		c.set("inventory.extra", null);
		
		String gm = c.getString("gamemode");
		GameMode m = GameMode.valueOf(gm);

		c.set("inGame", false);
		p.setResourcePack(defaultPack);
		p.setGlowing(false);
		p.setGameMode(m);
		
		c.set("gamemode", "ADVENTURE");
		
		try {
			c.save(f);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save " + f.getName() + " to " + f + " !", ex);
		}
	}
	public static void setGlobalLobby(Location loc) {
		String locS = locToString(loc);
		
		GunGamePlugin.instance.getConfig().set("Config.GlobalLobby", locS);
		GunGamePlugin.instance.saveConfig();
		GlobalLobby = loc;
		
	}
	private static List<Location> getFireLocs(int radius, Location loc) {
		List<Location> temp = new ArrayList<Location>();
		for(int i = 0; i <= radius; i++) {
			for(int x = 0; x <= radius; x++) {
				Location temp1 = new Location(loc.getWorld(), loc.getX() + i, loc.getY(), loc.getZ() + x);
				if(temp.isEmpty() || !temp.contains(temp1)) {
					temp.add(temp1);
				}
				Location temp2 = new Location(loc.getWorld(), loc.getX() - i, loc.getY(), loc.getZ() - x);
				if(temp.isEmpty() || !temp.contains(temp2)) {
					temp.add(temp2);
				}
				Location temp5 = new Location(loc.getWorld(), loc.getX() + i, loc.getY(), loc.getZ() - x);
				if(temp.isEmpty() || !temp.contains(temp5)) {
					temp.add(temp5);
				}
				Location temp6 = new Location(loc.getWorld(), loc.getX() - i, loc.getY(), loc.getZ() + x);
				if(temp.isEmpty() || !temp.contains(temp6)) {
					temp.add(temp6);
				}
			}
		}
		List<Location> locs = new ArrayList<Location>(temp);
		for(Location loc2 : temp) {
			for(int i = 1; i<= radius; i++) {
				Location t1 = new Location(loc2.getWorld(), loc2.getX(), loc2.getY() + i, loc2.getZ());
				locs.add(t1);
				Location t2 = new Location(loc2.getWorld(), loc2.getX(), loc2.getY() - i, loc2.getZ());
				locs.add(t2);
			}
		}
		List<Location> fireLocs = new ArrayList<Location>();
		for(Location l : locs) {
			Material m = l.getBlock().getType();
			if(m.equals(Material.AIR) |
					m.equals(Material.TRIPWIRE) |
					m.equals(Material.REDSTONE_WIRE)) {
				if(isFullBlock(l.getBlock().getRelative(BlockFace.DOWN)) |
						isFullBlock(l.getBlock().getRelative(BlockFace.UP)) |
						isFullBlock(l.getBlock().getRelative(BlockFace.NORTH)) |
						isFullBlock(l.getBlock().getRelative(BlockFace.EAST)) |
						isFullBlock(l.getBlock().getRelative(BlockFace.WEST)) |
						isFullBlock(l.getBlock().getRelative(BlockFace.SOUTH))) {
					fireLocs.add(l);
				}
			}
		}
		return fireLocs;
	}
	public static Boolean isFullBlock(Location blockLoc) {
		Block b = blockLoc.getBlock();
		return isFullBlock(b);
	}
	private static Boolean isFullBlock(Block block) {
		return isFullBlock(block.getType());
	}
	public static Boolean isFullBlock(Material m) {
		//Material m = block.getType();
		if(GunGamePlugin.instance.serverPre113) {
			if(m.equals(Material.AIR) ||
					m.equals(Material.valueOf("STATIONARY_WATER")) ||
					m.equals(Material.WATER) ||
					m.equals(Material.valueOf("WATER_LILY")) ||
					m.equals(Material.valueOf("SUGAR_CANE_BLOCK")) ||
					m.equals(Material.valueOf("CROPS")) ||
					m.equals(Material.IRON_TRAPDOOR) ||
					m.equals(Material.TORCH) ||
					m.equals(Material.REDSTONE) ||
					m.equals(Material.valueOf("REDSTONE_COMPARATOR_OFF")) ||
					m.equals(Material.valueOf("REDSTONE_COMPARATOR_ON")) ||
					m.equals(Material.REDSTONE_WIRE) ||
					m.equals(Material.valueOf("REDSTONE_TORCH_ON")) ||
					m.equals(Material.valueOf("REDSTONE_TORCH_OFF")) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.valueOf("WOOD_BUTTON")) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.TRIPWIRE_HOOK)||
					m.equals(Material.FLOWER_POT) ||
					m.equals(Material.valueOf("RAILS")) ||
					m.equals(Material.ACTIVATOR_RAIL) ||
					m.equals(Material.DETECTOR_RAIL) ||
					m.equals(Material.POWERED_RAIL) ||
					m.equals(Material.END_ROD) ||
					m.equals(Material.valueOf("BED_BLOCK")) ||
					m.equals(Material.valueOf("STAINED_GLASS_PANE")) ||
					m.equals(Material.valueOf("THIN_GLASS")) ||
					m.equals(Material.valueOf("FENCE")) ||
					m.equals(Material.valueOf("FENCE_GATE")) ||
					m.equals(Material.VINE) ||
					m.equals(Material.valueOf("BANNER")) ||
					m.equals(Material.valueOf("WALL_BANNER")) ||
					m.equals(Material.valueOf("SIGN_POST")) ||
					m.equals(Material.WALL_SIGN) ||
					m.equals(Material.valueOf("COBBLE_WALL")) ||
					m.equals(Material.valueOf("TRAP_DOOR")) ||
					m.equals(Material.ACACIA_DOOR) ||
					m.equals(Material.BIRCH_DOOR) ||
					m.equals(Material.DARK_OAK_DOOR) ||
					m.equals(Material.IRON_DOOR) ||
					m.equals(Material.JUNGLE_DOOR) ||
					m.equals(Material.SPRUCE_DOOR) ||
					m.equals(Material.valueOf("WOOD_DOOR")) ||
					m.equals(Material.valueOf("DOUBLE_PLANT")) ||
					m.equals(Material.valueOf("LONG_GRASS")) ||
					m.equals(Material.valueOf("RED_ROSE")) ||
					m.equals(Material.valueOf("YELLOW_FLOWER")) ||
					m.equals(Material.valueOf("LADDER")) ||
					m.equals(Material.valueOf("IRON_FENCE")) ||
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM)
					|| m.toString().equals("STONE_PLATE")
					|| m.toString().equals("WOOD_PLATE")
					|| m.toString().equals("LIGHT_WEIGHTED_PRESSURE_PLATE")
					|| m.toString().equals("HEAVY_WEIGHTED_PRESSURE_PLATE")
					|| m.toString().equals("IRON_PLATE")
					|| m.toString().equals("GOLD_PLATE")) {
				return false;
			}
		} else {
			if(m.equals(Material.AIR) ||
					m.equals(Material.WATER) ||
					m.equals(Material.WATER) ||
					m.equals(Material.LILY_PAD) ||
					m.equals(Material.SUGAR_CANE) ||
					//m.equals(Material.LEGACY_CROPS) ||
					m.equals(Material.WHEAT) ||
					m.equals(Material.CARROTS) ||
					m.equals(Material.POTATOES) ||
					m.equals(Material.BEETROOTS) ||
					m.equals(Material.SEA_PICKLE) ||
					m.equals(Material.SEAGRASS) ||
					m.equals(Material.BRAIN_CORAL_FAN) ||
					m.equals(Material.BRAIN_CORAL_WALL_FAN) ||
					m.equals(Material.BUBBLE_CORAL_FAN) ||
					m.equals(Material.BUBBLE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_BRAIN_CORAL_FAN) ||
					m.equals(Material.DEAD_BRAIN_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_BUBBLE_CORAL_FAN) ||
					m.equals(Material.DEAD_BUBBLE_CORAL_WALL_FAN) ||
					m.equals(Material.FIRE_CORAL_FAN) ||
					m.equals(Material.FIRE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_FIRE_CORAL_FAN) ||
					m.equals(Material.DEAD_FIRE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_HORN_CORAL_FAN) ||
					m.equals(Material.DEAD_HORN_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_TUBE_CORAL_FAN) ||
					m.equals(Material.DEAD_TUBE_CORAL_WALL_FAN) ||
					m.equals(Material.HORN_CORAL_FAN) ||
					m.equals(Material.HORN_CORAL_WALL_FAN) ||
					m.equals(Material.TUBE_CORAL_FAN) ||
					m.equals(Material.TUBE_CORAL_WALL_FAN) || 
					m.equals(Material.IRON_TRAPDOOR) ||
					m.equals(Material.TORCH) ||
					m.equals(Material.REDSTONE) ||
					m.equals(Material.COMPARATOR) ||
					//m.equals(Material.REDSTONE_COMPARATOR_ON) ||
					m.equals(Material.REDSTONE_WIRE) ||
					m.equals(Material.REDSTONE_TORCH) ||
					//m.equals(Material.REDSTONE_TORCH_OFF) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.ACACIA_BUTTON) ||
					m.equals(Material.BIRCH_BUTTON) ||
					m.equals(Material.DARK_OAK_BUTTON) ||
					m.equals(Material.JUNGLE_BUTTON) ||
					m.equals(Material.OAK_BUTTON) ||
					m.equals(Material.SPRUCE_BUTTON) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.TRIPWIRE_HOOK)||
					m.equals(Material.FLOWER_POT) ||
					m.equals(Material.RAIL) ||
					m.equals(Material.ACTIVATOR_RAIL) ||
					m.equals(Material.DETECTOR_RAIL) ||
					m.equals(Material.POWERED_RAIL) ||
					m.equals(Material.END_ROD) ||
					//m.equals(Material.LEGACY_BED_BLOCK) ||) 
					m.equals(Material.BLACK_BED) ||
					m.equals(Material.BLUE_BED) ||
					m.equals(Material.BROWN_BED) ||
					m.equals(Material.CYAN_BED) ||
					m.equals(Material.GRAY_BED) ||
					m.equals(Material.GREEN_BED) ||
					m.equals(Material.LIME_BED) ||
					m.equals(Material.LIGHT_BLUE_BED) ||
					m.equals(Material.LIGHT_GRAY_BED) ||
					m.equals(Material.MAGENTA_BED) ||
					m.equals(Material.ORANGE_BED) ||
					m.equals(Material.PINK_BED) ||
					m.equals(Material.PURPLE_BED) ||
					m.equals(Material.RED_BED) ||
					m.equals(Material.WHITE_BED) ||
					m.equals(Material.YELLOW_BED) ||
					/*m.equals(Material.LEGACY_STAINED_GLASS_PANE) ||
					m.equals(Material.LEGACY_THIN_GLASS) ||*/
					isGlassPane(m) ||
					//m.equals(Material.LEGACY_FENCE) ) ||
					m.equals(Material.OAK_FENCE) ||
					m.equals(Material.OAK_FENCE_GATE) ||
					m.equals(Material.ACACIA_FENCE) ||
					m.equals(Material.ACACIA_FENCE_GATE) ||
					m.equals(Material.BIRCH_FENCE) ||
					m.equals(Material.BIRCH_FENCE_GATE) ||
					m.equals(Material.DARK_OAK_FENCE) ||
					m.equals(Material.DARK_OAK_FENCE_GATE) ||
					m.equals(Material.JUNGLE_FENCE) ||
					m.equals(Material.JUNGLE_FENCE_GATE) ||
					m.equals(Material.NETHER_BRICK_FENCE) ||
					m.equals(Material.SPRUCE_FENCE) ||
					m.equals(Material.SPRUCE_FENCE_GATE) ||
					//m.equals(Material.LEGACY_FENCE_GATE) ||
					m.equals(Material.VINE) ||
					/*m.equals(Material.LEGACY_BANNER) ||
					m.equals(Material.LEGACY_WALL_BANNER) ||*/
					m.equals(Material.BLACK_BANNER) ||
					m.equals(Material.BLACK_WALL_BANNER) ||
					m.equals(Material.BLUE_BANNER) ||
					m.equals(Material.BLUE_WALL_BANNER) ||
					m.equals(Material.BROWN_BANNER) ||
					m.equals(Material.BROWN_WALL_BANNER) ||
					m.equals(Material.CYAN_BANNER) ||
					m.equals(Material.CYAN_WALL_BANNER) ||
					m.equals(Material.GRAY_BANNER) ||
					m.equals(Material.GRAY_WALL_BANNER) ||
					m.equals(Material.GREEN_BANNER) ||
					m.equals(Material.GREEN_WALL_BANNER) ||
					m.equals(Material.LIME_BANNER) ||
					m.equals(Material.LIME_WALL_BANNER) ||
					m.equals(Material.LIGHT_BLUE_BANNER) ||
					m.equals(Material.LIGHT_BLUE_WALL_BANNER) ||
					m.equals(Material.LIGHT_GRAY_BANNER) ||
					m.equals(Material.LIGHT_GRAY_WALL_BANNER) ||
					m.equals(Material.MAGENTA_BANNER) ||
					m.equals(Material.MAGENTA_WALL_BANNER) ||
					m.equals(Material.ORANGE_BANNER) ||
					m.equals(Material.ORANGE_WALL_BANNER) ||
					m.equals(Material.PINK_BANNER) ||
					m.equals(Material.PINK_WALL_BANNER) ||
					m.equals(Material.PURPLE_BANNER) ||
					m.equals(Material.PURPLE_WALL_BANNER) ||
					m.equals(Material.RED_BANNER) ||
					m.equals(Material.RED_WALL_BANNER) ||
					m.equals(Material.WHITE_BANNER) ||
					m.equals(Material.WHITE_WALL_BANNER) ||
					m.equals(Material.YELLOW_BANNER) ||
					m.equals(Material.YELLOW_WALL_BANNER) ||
					
					m.equals(Material.SIGN) ||
					m.equals(Material.WALL_SIGN) ||
					m.equals(Material.COBBLESTONE_WALL) ||
					m.equals(Material.BIRCH_TRAPDOOR) ||
					m.equals(Material.DARK_OAK_TRAPDOOR) ||
					m.equals(Material.ACACIA_TRAPDOOR) ||
					m.equals(Material.JUNGLE_TRAPDOOR) ||
					m.equals(Material.SPRUCE_TRAPDOOR) ||
					m.equals(Material.OAK_TRAPDOOR) ||
					m.equals(Material.ACACIA_DOOR) ||
					m.equals(Material.BIRCH_DOOR) ||
					m.equals(Material.DARK_OAK_DOOR) ||
					m.equals(Material.IRON_DOOR) ||
					m.equals(Material.JUNGLE_DOOR) ||
					m.equals(Material.SPRUCE_DOOR) ||
					m.equals(Material.OAK_DOOR) ||
					//m.equals(Material.LEGACY_DOUBLE_PLANT) ||
					m.equals(Material.SUNFLOWER) ||
					m.equals(Material.LILAC) ||
					m.equals(Material.ROSE_BUSH) ||
					m.equals(Material.PEONY) ||
					m.equals(Material.LARGE_FERN)||
					m.equals(Material.TALL_GRASS) ||
					m.equals(Material.GRASS) ||
					m.equals(Material.ROSE_RED) ||
					//m.equals(Material.LEGACY_YELLOW_FLOWER) ||
					m.equals(Material.DANDELION) ||
					m.equals(Material.DANDELION_YELLOW) ||
					m.equals(Material.POPPY) ||
					m.equals(Material.BLUE_ORCHID) ||
					m.equals(Material.ALLIUM) ||
					m.equals(Material.AZURE_BLUET) ||
					m.equals(Material.ORANGE_TULIP) ||
					m.equals(Material.PINK_TULIP) ||
					m.equals(Material.RED_TULIP) ||
					m.equals(Material.WHITE_TULIP) ||
					m.equals(Material.OXEYE_DAISY) ||
					m.equals(Material.FERN) ||
					m.equals(Material.COCOA) ||
					m.equals(Material.LADDER) ||
					m.equals(Material.IRON_BARS) ||
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM)
					|| m.equals(Material.ACACIA_PRESSURE_PLATE)
					|| m.equals(Material.BIRCH_PRESSURE_PLATE)
					|| m.equals(Material.DARK_OAK_PRESSURE_PLATE)
					|| m.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
					|| m.equals(Material.JUNGLE_PRESSURE_PLATE)
					|| m.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
					|| m.equals(Material.OAK_PRESSURE_PLATE)
					|| m.equals(Material.SPRUCE_PRESSURE_PLATE)
					|| m.equals(Material.STONE_PRESSURE_PLATE)) {
				return false;
			}
		}
		return true;
		
	}
	public static void createExplosion(Location loc, boolean fire, boolean breakBlocks, boolean noDamage, boolean physics, float strength, UUID cause, int radius, Boolean silent) {
		createExplosion(loc, fire, breakBlocks, noDamage, physics, strength, cause, radius, silent, 0);
	}
	public static void createExplosion(Location loc, boolean fire, boolean breakBlocks, boolean noDamage, boolean physics, float strength, UUID cause, int radius, Boolean silent, Integer fuse) {
		if (radius > 20) {
			radius = 20;
		}
		if((breakBlocks && 
				(
						loc.getWorld().hasMetadata("GG_ExplosionBreakBlocks") && 
						loc.getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()
						)) ||
				(breakBlocks &&
				(
						GunGamePlugin.instance.griefHelper.isGGWorld(loc.getWorld()) &&
						GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.EXPLOSIONS, loc.getWorld())
						))
				) {
			loc.getBlock().breakNaturally();
			loc.getBlock().setType(Material.AIR);
		}
		//loc.getBlock().setType(Material.AIR);
		TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawn(loc, TNTPrimed.class);
		tnt.setIsIncendiary(fire);
		tnt.setYield(radius);		

		if(breakBlocks == false || 
				(
						loc.getWorld().hasMetadata("GG_ExplosionBreakBlocks") && 
						!loc.getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()
						) ||
				(
						!GunGamePlugin.instance.griefHelper.isGGWorld(loc.getWorld()) &&
						!GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.EXPLOSIONS, loc.getWorld())
						)
				) {
			tnt.setMetadata("GG_breakNoBlocks", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(breakBlocks)));
		}
		
		if(silent == true) {
			tnt.setSilent(true);
		}
		
		if(cause != null) {
			tnt.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));
		}
		if(!physics || (
				!GunGamePlugin.instance.griefHelper.isGGWorld(loc.getWorld()) &&
				!GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.PHYSIC_ENGINE, loc.getWorld())
				)) {
			tnt.setMetadata("GG_Physics", new FixedMetadataValue(GunGamePlugin.instance, true));
		}
		tnt.setMetadata("GG_strength", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(strength)));
		tnt.setMetadata("GG_Explosive", new FixedMetadataValue(GunGamePlugin.instance, "true"));
		tnt.setMetadata("GG_NoDamage", new FixedMetadataValue(GunGamePlugin.instance, noDamage));
		
		//GunGameExplosives.add(tnt);
		
		tnt.setFuseTicks(fuse);
		if(fire) {
			placeFire(radius, loc, breakBlocks, 0);
		}
	}
	public static void placeFire(Integer radius, Location center, boolean breakBlocks, Integer burnDuration) {
		List<Location> locs = new ArrayList<Location>();
		List<Location> fires = new ArrayList<Location>();
		try {
			Integer burnDuration2 = 0;
			if(burnDuration <= 0) {
				burnDuration2 = 120;
			} else {
				burnDuration2 = burnDuration;
			}
			locs = getFireLocs(radius, center);
			
			for(Location loc : locs) {
				//Random rdm = new Random();
				Boolean b = getRandomBoolean();
				if(b == true) {
					fires.add(loc);
					
					loc.getBlock().breakNaturally();
					loc.getBlock().setType(Material.FIRE);
					/*Random rdm2 = new Random();
					Boolean b2 = rdm2.nextBoolean();
					if(breakBlocks == true && b2 == true) {
						loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.COAL_BLOCK);
					}*/					
				}
			}
			for(Location loc : fires) {
				if(loc.getBlock().getType().equals(Material.FIRE)) {
					loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() -0.25D, loc.getY(), loc.getZ() -0.25D, 40, 0.5D, 1.5D, 0.5D, 0.1D);
				}
			}
			center.getWorld().playSound(center, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.5f, 1.5f);
			//if(!Boolean.valueOf(center.getWorld().getGameRuleValue("doFireTick"))) {
				Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						for(Location f : fires) {
							Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
								
								@Override
								public void run() {
									if(f.getBlock().getType().equals(Material.FIRE)) {
										f.getBlock().setType(Material.AIR);
										f.getWorld().playSound(f, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
										f.getWorld().spawnParticle(Particle.SMOKE_LARGE, f, 50, 0.0, 0.25, 0.0, 0.005);
									}
								}
							}, burnDuration + getRandomNumber(120));
						}
						
					}
				}, burnDuration2);
			//}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	@SuppressWarnings("deprecation")
	public static void computeBlockDamage_pre1_13(List<Block> BlockList, UUID cause) {
		
		if(BlockList != null && !BlockList.isEmpty()) {
			if((BlockList.get(0).getLocation().getWorld().hasMetadata("GG_Physics") && 
					BlockList.get(0).getLocation().getWorld().getMetadata("GG_Physics").get(0).asBoolean()) || (
							!GunGamePlugin.instance.griefHelper.isGGWorld(BlockList.get(0).getLocation().getWorld()) &&
							GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.PHYSIC_ENGINE, BlockList.get(0).getLocation().getWorld())
							)) {
				
				List<String> changeList = GunGamePlugin.instance.getConfig().getStringList("Config.BlockChangeList");
				List<Material> blockFrom = new ArrayList<Material>();
				List<String> blockTo = new ArrayList<String>();
				List<Material> blockToMat = new ArrayList<Material>();
				HashMap<Material, Integer> computedList = new HashMap<Material, Integer>();
				
				for(String s : changeList) {
					String[] temp = s.split(";");
					Material mat = Material.valueOf(temp[0]);
					blockFrom.add(mat);
					blockTo.add(temp[1]);
					computedList.put(mat, blockTo.indexOf(temp[1]));
				}
				if(BlockList != null && BlockList.size() > 0) {
					for(Block b : BlockList) {
						List<Block> fallingBlocks = new ArrayList<Block>(BlockList);
						Material type = b.getType();
						//short data = block.getData();
						//Filter if block is a falling one
						if(blockFrom.contains(type)) {
							String toBlock = blockTo.get(computedList.get(type));
							String[] temp = toBlock.split(":");
							Material mat = Material.valueOf(temp[0]);
							blockToMat.add(mat);
							//String dat = temp[1];
							
							if(WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), mat)) {
								b.setType(mat);
							}
							//b.setData(Byte.valueOf(dat));
							
							fallingBlocks.remove(b);
						} 
						if(type != Material.TNT & type != Material.FIRE &  type != Material.DISPENSER && fallingBlocks.contains(b) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							float x = -1.0F + (float)(Math.random() * 1.5D);
					        float y = -2.0F + (float)(Math.random() * 2.5D);
					        float z = -1.5F + (float)(Math.random() * 2.0D);
					          
					        FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
					        fallingBlock.setVelocity(new org.bukkit.util.Vector(x, y, z));
					        fallingBlock.setDropItem(false);
					        fallingBlock.setMetadata("GG_GravBlock", new FixedMetadataValue(GunGamePlugin.instance, "true"));
					        if(cause != null) {
					        	fallingBlock.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));

					        }
					        b.setType(Material.AIR);
					        b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
					        //b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
						}
						if(type.equals(Material.TNT) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							TNTPrimed tnt = (TNTPrimed)b.getLocation().getWorld().spawn(b.getLocation(), TNTPrimed.class);
							b.setType(Material.AIR);
							tnt.setIsIncendiary(false);
							//Random random = new Random();
							tnt.setFuseTicks(10 + getRandomNumber(20));
							tnt.setYield(3.0f);
							tnt.setMetadata("GG_breakNoBlocks", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(getRandomBoolean())));
							
							if(cause != null) {
								tnt.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));
							}
							
							tnt.setMetadata("GG_strength", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(8)));
							tnt.setMetadata("GG_Explosive", new FixedMetadataValue(GunGamePlugin.instance, "true"));
							tnt.setMetadata("GG_NoDamage", new FixedMetadataValue(GunGamePlugin.instance, "false"));
							
							if(getRandomBoolean()) {
								placeFire(1 + getRandomNumber(3), b.getLocation(), getRandomBoolean(), 0);
							}
						}
					}
				}	
			} else {
				if((BlockList.get(0).getLocation().getWorld().hasMetadata("GG_ExplosionBreakBlocks") && 
						BlockList.get(0).getLocation().getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()) || (
								!GunGamePlugin.instance.griefHelper.isGGWorld(BlockList.get(0).getLocation().getWorld()) &&
								GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.EXPLOSIONS, BlockList.get(0).getLocation().getWorld())
					)) {
					//NORMAL EXPLOSION, WHEN PHYSICS DISABLED!
					for(Block b : BlockList) {
						Material type = b.getType();
						if(type != Material.TNT & type != Material.FIRE &  type != Material.DISPENSER && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
					        b.breakNaturally();
							b.setType(Material.AIR);
					        //b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
						}
						if(type.equals(Material.TNT) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							TNTPrimed tnt = (TNTPrimed)b.getLocation().getWorld().spawn(b.getLocation(), TNTPrimed.class);
							b.setType(Material.AIR);
							tnt.setIsIncendiary(false);
							//Random random = new Random();
							tnt.setFuseTicks(10 + getRandomNumber(20));
							tnt.setYield(3.0f);
							tnt.setMetadata("GG_breakNoBlocks", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(getRandomBoolean())));
							
							if(cause != null) {
								tnt.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));
							}
							
							tnt.setMetadata("GG_strength", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(8)));
							tnt.setMetadata("GG_Explosive", new FixedMetadataValue(GunGamePlugin.instance, "true"));
							tnt.setMetadata("GG_NoDamage", new FixedMetadataValue(GunGamePlugin.instance, "false"));
							
							if(getRandomBoolean()) {
								placeFire(1 + getRandomNumber(3), b.getLocation(), getRandomBoolean(), 0);
							}
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	public static void computeBlockDamage_v1_13_up(List<Block> BlockList, UUID cause) {
		
		if(BlockList != null && !BlockList.isEmpty()) {
			if((BlockList.get(0).getLocation().getWorld().hasMetadata("GG_Physics") && 
					BlockList.get(0).getLocation().getWorld().getMetadata("GG_Physics").get(0).asBoolean()) || (
							!GunGamePlugin.instance.griefHelper.isGGWorld(BlockList.get(0).getLocation().getWorld()) &&
							GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.PHYSIC_ENGINE, BlockList.get(0).getLocation().getWorld())
							)) {
				
				List<String> changeList = GunGamePlugin.instance.getConfig().getStringList("Config.BlockChangeList");
				List<Material> blockFrom = new ArrayList<Material>();
				List<String> blockTo = new ArrayList<String>();
				List<Material> blockToMat = new ArrayList<Material>();
				HashMap<Material, Integer> computedList = new HashMap<Material, Integer>();
				
				for(String s : changeList) {
					String[] temp = s.split(";");
					Material mat = Material.valueOf(temp[0]);
					blockFrom.add(mat);
					blockTo.add(temp[1]);
					computedList.put(mat, blockTo.indexOf(temp[1]));
				}
				if(BlockList != null && BlockList.size() > 0) {
					for(Block b : BlockList) {
						List<Block> fallingBlocks = new ArrayList<Block>(BlockList);
						Material type = b.getType();
						//short data = block.getData();
						//Filter if block is a falling one
						if(blockFrom.contains(type)) {
							String toBlock = blockTo.get(computedList.get(type));
							//String[] temp = toBlock.split(":");
							Material mat = Material.valueOf(toBlock);
							blockToMat.add(mat);
							//String dat = temp[1];
							
							if(WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), mat)) {
								b.setType(mat);
							}
							//b.setData(Byte.valueOf(dat));
							
							
							fallingBlocks.remove(b);
						} 
						if(type != Material.TNT & type != Material.FIRE &  type != Material.DISPENSER && fallingBlocks.contains(b) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							float x = -1.0F + (float)(Math.random() * 1.5D);
					        float y = -2.0F + (float)(Math.random() * 2.5D);
					        float z = -1.5F + (float)(Math.random() * 2.0D);
					          
					        FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
					        fallingBlock.setVelocity(new org.bukkit.util.Vector(x, y, z));
					        fallingBlock.setDropItem(false);
					        fallingBlock.setMetadata("GG_GravBlock", new FixedMetadataValue(GunGamePlugin.instance, "true"));
					        if(cause != null) {
					        	fallingBlock.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));

					        }
					        b.setType(Material.AIR);
					        b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
					        //b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
						}
						if(type.equals(Material.TNT) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							TNTPrimed tnt = (TNTPrimed)b.getLocation().getWorld().spawn(b.getLocation(), TNTPrimed.class);
							b.setType(Material.AIR);
							tnt.setIsIncendiary(false);
							//Random random = new Random();
							tnt.setFuseTicks(10 + getRandomNumber(20));
							tnt.setYield(3.0f);
							tnt.setMetadata("GG_breakNoBlocks", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(getRandomBoolean())));
							
							if(cause != null) {
								tnt.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));
							}
							
							tnt.setMetadata("GG_strength", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(8)));
							tnt.setMetadata("GG_Explosive", new FixedMetadataValue(GunGamePlugin.instance, "true"));
							tnt.setMetadata("GG_NoDamage", new FixedMetadataValue(GunGamePlugin.instance, "false"));
							
							if(getRandomBoolean()) {
								placeFire(1 + getRandomNumber(3), b.getLocation(), getRandomBoolean(), 0);
							}
						}
					}
				}	
			} else {
				if((BlockList.get(0).getLocation().getWorld().hasMetadata("GG_ExplosionBreakBlocks") && 
						BlockList.get(0).getLocation().getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()) || (
								!GunGamePlugin.instance.griefHelper.isGGWorld(BlockList.get(0).getLocation().getWorld()) &&
								GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.EXPLOSIONS, BlockList.get(0).getLocation().getWorld())
					)) {
					//NORMAL EXPLOSION, WHEN PHYSICS DISABLED!
					for(Block b : BlockList) {
						Material type = b.getType();
						if(type != Material.TNT & type != Material.FIRE &  type != Material.DISPENSER && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							b.breakNaturally();
					        b.setType(Material.AIR);
					        //b.breakNaturally();
					        //b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
					        //b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 100, 100);
						}
						if(type.equals(Material.TNT) && WaterbodyProtectionUtil.canBlockBeDestroyed(b, b.getWorld(), Material.AIR)) {
							TNTPrimed tnt = (TNTPrimed)b.getLocation().getWorld().spawn(b.getLocation(), TNTPrimed.class);
							b.setType(Material.AIR);
							tnt.setIsIncendiary(false);
							//Random random = new Random();
							tnt.setFuseTicks(10 + getRandomNumber(20));
							tnt.setYield(3.0f);
							tnt.setMetadata("GG_breakNoBlocks", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(getRandomBoolean())));
							
							if(cause != null) {
								tnt.setMetadata("GG_Owner", new FixedMetadataValue(GunGamePlugin.instance, cause.toString()));
							}
							
							tnt.setMetadata("GG_strength", new FixedMetadataValue(GunGamePlugin.instance, String.valueOf(8)));
							tnt.setMetadata("GG_Explosive", new FixedMetadataValue(GunGamePlugin.instance, "true"));
							tnt.setMetadata("GG_NoDamage", new FixedMetadataValue(GunGamePlugin.instance, "false"));
							
							if(getRandomBoolean()) {
								placeFire(1 + getRandomNumber(3), b.getLocation(), getRandomBoolean(), 0);
							}
						}
					}
				}
			}
		}
	}
	
	public static void dealItems(Player p) {
		p.getInventory().clear();
		
		Gun gun1 = GunGamePlugin.instance.weaponManager.getRandomGun();
		while(gun1.hasUsePermission() && !p.hasPermission(gun1.getPermission())) {
			gun1 = GunGamePlugin.instance.weaponManager.getRandomGun();
		}
		Gun gun2 = GunGamePlugin.instance.weaponManager.getRandomGun();
		while(gun2.hasUsePermission() && !p.hasPermission(gun2.getPermission())) {
			gun2 = GunGamePlugin.instance.weaponManager.getRandomGun();
		}
		
		Ammo aGun1 = gun1.getAmmo();
		Ammo aGun2 = gun2.getAmmo();
		
		ItemStack ammo1 = aGun1.getItem().clone();
		ammo1.setAmount(64);
		
		ItemStack ammo2 = aGun2.getItem().clone();
		ammo2.setAmount(64);
		
		Grenade grenade = GunGamePlugin.instance.weaponManager.getRandomGrenade();
		
		ItemStack gren = grenade.getGrenadeItem().clone();
		gren.setAmount(16);
		
		/*if(p.getName().equalsIgnoreCase("gommehd")) {
			p.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
		} else {*/
			p.getInventory().setItem(0, gun1.getItem());
			p.getInventory().setItem(1, gun2.getItem());
			p.getInventory().setItem(2, gren);
		//}
		
		p.getInventory().setItem(9, ammo1);
		p.getInventory().setItem(10, ammo2);
		
		p.getInventory().setItem(5, SuicideArmor.remote());
		p.getInventory().setItem(6, C4.c4Remote());
		if(GunGamePlugin.instance.serverPre113) {
			p.getInventory().setItem(8, Crowbar_pre_1_13.CrowBar());
		} else {
			p.getInventory().setItem(8, Crowbar_v1_13_up.CrowBar());
		}
		p.getInventory().setItem(7, Radar.radar());
		
		ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemMeta meta = helmet.getItemMeta();
		meta.setUnbreakable(true);
		helmet.setItemMeta(meta);
		
		ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemMeta meta2 = chest.getItemMeta();
		meta2.setUnbreakable(true);
		chest.setItemMeta(meta2);
		
		ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemMeta meta3 = leggings.getItemMeta();
		meta3.setUnbreakable(true);
		leggings.setItemMeta(meta3);
		
		ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
		ItemMeta meta4 = boots.getItemMeta();
		meta4.setUnbreakable(true);
		boots.setItemMeta(meta4);
		
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chest);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
		
		p.setLevel(0);
	}
	public static void calcCoins(Player p) {
		try {
			Integer killstreak = GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).getCurrentKillStreak();
			p.setLevel(p.getLevel() + killstreak + coinsPerKill);
			
			Arena arena = GunGamePlugin.instance.arenaManager.getArena(p);
			
			if(arena.getArenaMode().equals(EArenaGameMode.ALL_VS_ALL)) {
				float killsToWin = arena.getKillsToWin();
				float kills = arena.getKills(p);
				
				float percentage = kills / killsToWin;
				
				p.setExp(percentage);
			}
			if(arena.getArenaMode().equals(EArenaGameMode.TEAM_DEATHMATCH)) {
				arena.getTdmMode().getTeam(p).addKill();
			}
			if(arena.getArenaMode().equals(EArenaGameMode.LAST_MAN_STANDING)) {
				p.setExp(1.0f);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public static Boolean isGlass(Material mat) {
		if(GunGamePlugin.instance.serverPre113) {
			if(mat.equals(Material.GLASS) ||
					mat.equals(Material.valueOf("STAINED_GLASS")) ||
					mat.equals(Material.ICE) ||
					mat.equals(Material.FROSTED_ICE) ||
					mat.equals(Material.valueOf("THIN_GLASS")) ||
					mat.equals(Material.valueOf("STAINED_GLASS_PANE"))) {
				return true;
			}
		} else {
			if(mat.equals(Material.GLASS) ||
					mat.equals(Material.GLASS_PANE) ||
					mat.equals(Material.ICE) ||
					mat.equals(Material.FROSTED_ICE) ||
					mat.equals(Material.BLACK_STAINED_GLASS) ||
					mat.equals(Material.BLACK_STAINED_GLASS_PANE) ||
					mat.equals(Material.BLUE_STAINED_GLASS) ||
					mat.equals(Material.BLUE_STAINED_GLASS_PANE) ||
					mat.equals(Material.BROWN_STAINED_GLASS) ||
					mat.equals(Material.BROWN_STAINED_GLASS_PANE) ||
					mat.equals(Material.CYAN_STAINED_GLASS) ||
					mat.equals(Material.CYAN_STAINED_GLASS_PANE) ||
					mat.equals(Material.GRAY_STAINED_GLASS) ||
					mat.equals(Material.GRAY_STAINED_GLASS_PANE) ||
					mat.equals(Material.GREEN_STAINED_GLASS) ||
					mat.equals(Material.GREEN_STAINED_GLASS_PANE) ||
					mat.equals(Material.LIGHT_BLUE_STAINED_GLASS) ||
					mat.equals(Material.LIGHT_BLUE_STAINED_GLASS_PANE) ||
					mat.equals(Material.LIGHT_GRAY_STAINED_GLASS) ||
					mat.equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
					mat.equals(Material.LIME_STAINED_GLASS) ||
					mat.equals(Material.LIME_STAINED_GLASS_PANE) ||
					mat.equals(Material.MAGENTA_STAINED_GLASS) ||
					mat.equals(Material.MAGENTA_STAINED_GLASS_PANE) ||
					mat.equals(Material.ORANGE_STAINED_GLASS) ||
					mat.equals(Material.ORANGE_STAINED_GLASS_PANE) ||
					mat.equals(Material.PINK_STAINED_GLASS) ||
					mat.equals(Material.PINK_STAINED_GLASS_PANE) ||
					mat.equals(Material.PURPLE_STAINED_GLASS) ||
					mat.equals(Material.PURPLE_STAINED_GLASS_PANE) ||
					mat.equals(Material.RED_STAINED_GLASS) ||
					mat.equals(Material.RED_STAINED_GLASS_PANE) ||
					mat.equals(Material.WHITE_STAINED_GLASS) ||
					mat.equals(Material.WHITE_STAINED_GLASS_PANE) ||
					mat.equals(Material.YELLOW_STAINED_GLASS) ||
					mat.equals(Material.YELLOW_STAINED_GLASS_PANE)
					) {
				return true;
			}
		}
		return false;
	}
	public static Boolean isGlassPane(Material mat) {
		if(GunGamePlugin.instance.serverPre113) {
			if(mat.equals(Material.valueOf("THIN_GLASS")) ||
					mat.equals(Material.valueOf("STAINED_GLASS_PANE"))) {
				return true;
			}
		} else {
			if(//mat.equals(Material.GLASS) ||
					mat.equals(Material.GLASS_PANE) ||
					//mat.equals(Material.ICE) ||
					//mat.equals(Material.FROSTED_ICE) ||
					//mat.equals(Material.BLACK_STAINED_GLASS) ||
					mat.equals(Material.BLACK_STAINED_GLASS_PANE) ||
					//mat.equals(Material.BLUE_STAINED_GLASS) ||
					mat.equals(Material.BLUE_STAINED_GLASS_PANE) ||
					//mat.equals(Material.BROWN_STAINED_GLASS) ||
					mat.equals(Material.BROWN_STAINED_GLASS_PANE) ||
					//mat.equals(Material.CYAN_STAINED_GLASS) ||
					mat.equals(Material.CYAN_STAINED_GLASS_PANE) ||
					//mat.equals(Material.GRAY_STAINED_GLASS) ||
					mat.equals(Material.GRAY_STAINED_GLASS_PANE) ||
					//mat.equals(Material.GREEN_STAINED_GLASS) ||
					mat.equals(Material.GREEN_STAINED_GLASS_PANE) ||
					//mat.equals(Material.LIGHT_BLUE_STAINED_GLASS) ||
					mat.equals(Material.LIGHT_BLUE_STAINED_GLASS_PANE) ||
					//mat.equals(Material.LIGHT_GRAY_STAINED_GLASS) ||
					mat.equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
					//mat.equals(Material.LIME_STAINED_GLASS) ||
					mat.equals(Material.LIME_STAINED_GLASS_PANE) ||
					//mat.equals(Material.MAGENTA_STAINED_GLASS) ||
					mat.equals(Material.MAGENTA_STAINED_GLASS_PANE) ||
					//mat.equals(Material.ORANGE_STAINED_GLASS) ||
					mat.equals(Material.ORANGE_STAINED_GLASS_PANE) ||
					//mat.equals(Material.PINK_STAINED_GLASS) ||
					mat.equals(Material.PINK_STAINED_GLASS_PANE) ||
					//mat.equals(Material.PURPLE_STAINED_GLASS) ||
					mat.equals(Material.PURPLE_STAINED_GLASS_PANE) ||
					//mat.equals(Material.RED_STAINED_GLASS) ||
					mat.equals(Material.RED_STAINED_GLASS_PANE) ||
					//mat.equals(Material.WHITE_STAINED_GLASS) ||
					mat.equals(Material.WHITE_STAINED_GLASS_PANE) ||
					//mat.equals(Material.YELLOW_STAINED_GLASS) ||
					mat.equals(Material.YELLOW_STAINED_GLASS_PANE)
					) {
				return true;
			}
		}
		return false;
	}
	public static boolean isInteractable(Block block) {
		if(block != null) {
			Material m = block.getType();
			if(GunGamePlugin.instance.serverPre113) {
				if(isDoorOrTrapDoor(block) ||
						m.equals(Material.valueOf("FLOWER_POT")) ||
						m.equals(Material.valueOf("STONE_BUTTON")) ||
						m.equals(Material.valueOf("WOOD_BUTTON")) ||
						m.equals(Material.valueOf("CHEST")) ||
						m.equals(Material.valueOf("TRAPPED_CHEST")) ||
						m.equals(Material.valueOf("BED_BLOCK")) ||
						m.equals(Material.valueOf("ANVIL")) ||
						m.equals(Material.valueOf("BEACON")) ||
						m.equals(Material.valueOf("ENCHANTMENT_TABLE")) ||
						m.equals(Material.valueOf("WORKBENCH")) ||
						m.equals(Material.valueOf("LEVER")) ||
						m.equals(Material.valueOf("REDSTONE_COMPARATOR_ON")) ||
						m.equals(Material.valueOf("REDSTONE_COMPARATOR_OFF")) ||
						m.equals(Material.valueOf("DIODE_BLOCK_ON")) ||
						m.equals(Material.valueOf("DIODE_BLOCK_OFF")) ||
						m.equals(Material.valueOf("JUKEBOX")) ||
						m.equals(Material.valueOf("NOTE_BLOCK")) ||
						m.equals(Material.valueOf("BREWING_STAND")) ||
						m.equals(Material.valueOf("FURNACE")) ||
						m.equals(Material.valueOf("BURNING_FURNACE")) ||
						m.equals(Material.valueOf("CAULDRON")) ||
						m.equals(Material.valueOf("ENDER_CHEST")) ||
						m.equals(Material.valueOf("ENDER_PORTAL_FRAME"))
						) {
					return true;
				}
			} else {
				if(isDoorOrTrapDoor(block) ||
						m.equals(Material.BEACON) ||
						m.equals(Material.ENDER_CHEST) ||
						m.equals(Material.END_PORTAL_FRAME) ||
						m.equals(Material.FLOWER_POT) ||
						m.equals(Material.CHEST) ||
						m.equals(Material.TRAPPED_CHEST) ||
						m.equals(Material.LEVER) ||
						m.equals(Material.ACACIA_BUTTON) ||
						m.equals(Material.BIRCH_BUTTON) ||
						m.equals(Material.DARK_OAK_BUTTON) ||
						m.equals(Material.JUNGLE_BUTTON) ||
						m.equals(Material.OAK_BUTTON) ||
						m.equals(Material.SPRUCE_BUTTON) ||
						m.equals(Material.STONE_BUTTON) ||
						m.equals(Material.REPEATER) ||
						m.equals(Material.COMPARATOR) ||
						m.equals(Material.JUKEBOX) ||
						m.equals(Material.NOTE_BLOCK) ||
						m.equals(Material.ANVIL) ||
						m.equals(Material.CHIPPED_ANVIL) ||
						m.equals(Material.DAMAGED_ANVIL) ||
						m.equals(Material.FURNACE) ||
						m.equals(Material.ENCHANTING_TABLE) ||
						m.equals(Material.BREWING_STAND) ||
						m.equals(Material.CAULDRON) ||
						m.equals(Material.CRAFTING_TABLE) ||
						m.equals(Material.BLACK_BED) ||
						m.equals(Material.BLUE_BED) ||
						m.equals(Material.BROWN_BED) ||
						m.equals(Material.CYAN_BED) ||
						m.equals(Material.GRAY_BED) ||
						m.equals(Material.GREEN_BED) ||
						m.equals(Material.LIME_BED) ||
						m.equals(Material.MAGENTA_BED) ||
						m.equals(Material.ORANGE_BED) ||
						m.equals(Material.PINK_BED) ||
						m.equals(Material.PURPLE_BED) ||
						m.equals(Material.RED_BED) ||
						m.equals(Material.WHITE_BED) ||
						m.equals(Material.YELLOW_BED) ||
						m.equals(Material.LIGHT_BLUE_BED) ||
						m.equals(Material.LIGHT_GRAY_BED)				
					) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static Boolean isDoorOrTrapDoor(Block block) {
		Material m = block.getType();
		if(GunGamePlugin.instance.serverPre113) {
			if(
					m.equals(Material.valueOf("FENCE_GATE")) ||
					m.equals(Material.valueOf("ACACIA_FENCE_GATE")) ||
					m.equals(Material.valueOf("BIRCH_FENCE_GATE")) ||
					m.equals(Material.valueOf("DARK_OAK_FENCE_GATE")) ||
					m.equals(Material.valueOf("JUNGLE_FENCE_GATE")) ||
					m.equals(Material.valueOf("SPRUCE_FENCE_GATE")) ||
					m.equals(Material.valueOf("WOODEN_DOOR")) ||
					m.equals(Material.valueOf("WOOD_DOOR")) ||
					m.equals(Material.valueOf("ACACIA_DOOR")) ||
					m.equals(Material.valueOf("BIRCH_DOOR")) ||
					m.equals(Material.valueOf("DARK_OAK_DOOR")) ||
					m.equals(Material.valueOf("IRON_DOOR")) ||
					m.equals(Material.valueOf("IRON_DOOR_BLOCK")) ||
					m.equals(Material.valueOf("JUNGLE_DOOR")) ||
					m.equals(Material.valueOf("SPRUCE_DOOR")) ||
					m.equals(Material.valueOf("TRAP_DOOR")) ||
					m.equals(Material.valueOf("IRON_TRAPDOOR"))
				) {
				return true;
			}
		} else {
			org.bukkit.block.data.BlockData data = block.getBlockData();
			if(
					m.equals(Material.ACACIA_FENCE_GATE) ||
					m.equals(Material.BIRCH_FENCE_GATE) ||
					m.equals(Material.DARK_OAK_FENCE_GATE) ||
					m.equals(Material.JUNGLE_FENCE_GATE) ||
					m.equals(Material.OAK_FENCE_GATE) ||
					m.equals(Material.SPRUCE_FENCE_GATE) ||
					m.equals(Material.ACACIA_DOOR) ||
					m.equals(Material.BIRCH_DOOR) ||
					m.equals(Material.SPRUCE_DOOR) ||
					m.equals(Material.OAK_DOOR) ||
					m.equals(Material.DARK_OAK_DOOR) ||
					m.equals(Material.IRON_DOOR) ||
					m.equals(Material.JUNGLE_DOOR) ||
					m.equals(Material.ACACIA_TRAPDOOR)
					|| m.equals(Material.BIRCH_TRAPDOOR)
					|| m.equals(Material.DARK_OAK_TRAPDOOR)
					|| m.equals(Material.IRON_TRAPDOOR)
					|| m.equals(Material.JUNGLE_TRAPDOOR)
					|| m.equals(Material.OAK_TRAPDOOR)
					|| m.equals(Material.SPRUCE_TRAPDOOR)
					||
					data instanceof org.bukkit.block.data.type.Gate || 
					data instanceof org.bukkit.block.data.type.Door 
					) {
				return true;
			}
		}
		return false;
	}
	public static String vecToString(Vector v) {
		String vec = v.getX() +"|" + v.getY() + "|" + v.getZ();
		return vec;
	}
	public static Vector stringToVector(String v) {
		String[] a = v.split("\\|");
		Vector vec = new Vector(Double.valueOf(a[0]), Double.valueOf(a[1]), Double.valueOf(a[2]));
		return vec;
	}

}
