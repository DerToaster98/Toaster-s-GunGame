package de.MrBaumeister98.GunGame.Game.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import de.MrBaumeister98.GunGame.Achievements.Achievements.GunGameAchievementUtil;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Listeners.BKCommonLibListener;
import de.MrBaumeister98.GunGame.Game.Listeners.ChunkLoadListener;
import de.MrBaumeister98.GunGame.Game.Listeners.CommandListener;
import de.MrBaumeister98.GunGame.Game.Listeners.GameListener;
import de.MrBaumeister98.GunGame.Game.Listeners.SignListener;
import de.MrBaumeister98.GunGame.Game.Listeners.SpectatorListener;
import de.MrBaumeister98.GunGame.Game.Mechanics.LootChests;
import de.MrBaumeister98.GunGame.Game.Mechanics.MediCake_pre_1_13;
import de.MrBaumeister98.GunGame.Game.Mechanics.MediCake_v1_13_up;
import de.MrBaumeister98.GunGame.Game.Util.JoinGuiHelper;
import de.MrBaumeister98.GunGame.Game.Util.RadarUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.GunEngineCommandListener;
import de.MrBaumeister98.GunGame.GunEngine.GunMenu;
import de.MrBaumeister98.GunGame.GunEngine.WeaponListener;
import de.MrBaumeister98.GunGame.GunEngine.WeaponManager;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.GriefHelper;
import de.MrBaumeister98.GunGame.GunEngine.Shop.ShopGUI;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.PlayerInteractAtTankListener;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankListener;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankManager;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankMoveListener.TankMovementListener_1_12_R1;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankMoveListener.TankMovementListener_1_13_R1;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankMoveListener.TankMovementListener_1_13_R2;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretListener;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretManager;
import de.MrBaumeister98.GunGame.Items.C4;
import de.MrBaumeister98.GunGame.Items.Crowbar_pre_1_13;
import de.MrBaumeister98.GunGame.Items.Crowbar_v1_13_up;
import de.MrBaumeister98.GunGame.Items.FlareGun;
import de.MrBaumeister98.GunGame.Items.GameConfigurator;
import de.MrBaumeister98.GunGame.Items.InfoItem;
import de.MrBaumeister98.GunGame.Items.LeaveLobbyItem;
import de.MrBaumeister98.GunGame.Items.SuicideArmor;
import de.MrBaumeister98.GunGame.Items.TrackPadItem;
import de.MrBaumeister98.GunGame.Items.Voter;

public class GunGamePlugin extends JavaPlugin {
	

	public static GunGamePlugin instance;
	
	public ArenaManager arenaManager;
	public WeaponManager weaponManager;
	public GunGameAchievementUtil achUtil;
	public GunMenu gunShop;
	public TurretManager turretManager;
	public JoinGuiHelper joinGuiManager;
	public GriefHelper griefHelper;
	public ShopGUI weaponShop;
	public TankManager tankManager;
	public ChunkLoadListener chunkloadlistener;
	
	public Boolean serverPre113;
	
	public Boolean TabListAPIloaded;
	
	public String versionMC;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Debugger.logInfoWithColoredText(ChatColor.AQUA + "Checking server version...");
		String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
		String majorVer = split[0]; //For 1.10 will be "1"
		String minorVer = split[1]; //For 1.10 will be "10"
		String minorMinerVer = "0";
		if(split.length > 2) {
			minorMinerVer = split[2];
		}
		if(minorMinerVer != null) {
			versionMC = majorVer + "." + minorVer + "." + minorMinerVer;
		} else {
			versionMC = majorVer + "." + minorVer;
		}
		Debugger.logInfoWithColoredText(ChatColor.AQUA + "Server version: " + net.md_5.bungee.api.ChatColor.GREEN + versionMC);
		if(Integer.valueOf(majorVer) >= 1 && Integer.valueOf(minorVer) >= 13) {
			serverPre113 = false;
			Debugger.logInfoWithColoredText(ChatColor.GREEN + "Server version is 1.13 and up!");
		} else {
			serverPre113 = true;
			Debugger.logInfoWithColoredText(ChatColor.GREEN + "Server version is 1.12.2 and lower!");
		}
		
		this.TabListAPIloaded = false;
		if(Bukkit.getPluginManager().getPlugin("TTA") != null && Bukkit.getPluginManager().isPluginEnabled("TTA")) {
			this.TabListAPIloaded = true;
		}
		
		
		
		
		arenaManager = new ArenaManager(this);
		//vehicleManager = new VehicleManager(this);
		weaponManager = new WeaponManager(this);
		gunShop = new GunMenu(this, this.weaponManager);
		turretManager = new TurretManager(this);
		joinGuiManager = new JoinGuiHelper();
		griefHelper = new GriefHelper(this);
		weaponShop = new ShopGUI(this);
		//if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			tankManager = new TankManager();
		//}
		chunkloadlistener = new ChunkLoadListener();
		
		if(this.getConfig().getBoolean("Config.UseDebuggerWindow")) {
			Debugger.initializeAdvancedDebugger(instance);
		}
		
		Debugger.logInfoWithColoredText(ChatColor.RED + "!BOOTING UP GUNGAME!");
		
		//checkForPlugins();
			
		loadConfigurations();

		FileManager.getBackupFolder();	

		saveConfig();	
		
		Debugger.logInfoWithColoredText(ChatColor.RED + "Initializing Game System...");
		
		try {
			Util.getGlobalLobby();
		} catch(ExceptionInInitializerError e) {
			e.printStackTrace();
			Util.setGlobalLobby(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		Util.loadShopBlocks();
		Util.getAllowedCommands();
		Util.fillMeltList();
		
		//this.vehicleManager.loadFlakNames();
		
		this.weaponManager.initialize();
		this.turretManager.initialize();
		
		if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			Debugger.logInfoWithColoredText(ChatColor.GREEN + "ProtocolLib found!");
			this.tankManager.loadTankConfigs();
		} else {
			Debugger.logInfoWithColoredText(ChatColor.RED + "ProtocolLib not present :(");
			Debugger.logInfoWithColoredText(ChatColor.RED + "Tank System won't be loaded!");
		}
		
		this.weaponManager.initializeShop();
		
		this.weaponManager.setupItemLores();
		
		TrackPadItem.initTrackPadItem();
		
		this.arenaManager.initializeArenas();
		
		this.gunShop.loadPages();
		
		this.achUtil = new GunGameAchievementUtil(this);
		
		registerCommands();
		registerEvents();
		
		Debugger.logInfoWithColoredText(ChatColor.RED + "Initializing Anti-Griefing System for Weapon Engine...");
		for(World world : Bukkit.getWorlds()) {
			this.griefHelper.processWorld(world);
		}

		
		this.weaponShop.setupLists();
		
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Debugger.logInfoWithColoredText(ChatColor.RED + "Loading Tank-Respawn-Data...");
				for(World world : Bukkit.getWorlds()) {
					Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							GunGamePlugin.instance.tankManager.respawnTanks(world);
						}
					});
				}
				Debugger.logInfoWithColoredText(ChatColor.RED + "Loading Turret-Respawn-Data...");
				for(World world : Bukkit.getWorlds()) {
					Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							GunGamePlugin.instance.turretManager.respawnTurrets(world);
						}
					});
				}
			}
		}, 30);
		
		Debugger.saveLog();
	}

	@Override
	public void onDisable() {
		FileManager.saveArenaConfig();
		
		saveConfig();
		
		arenaManager.unloadArenaWorlds();
		Debugger.logInfoWithColoredText(ChatColor.RED + "!SHUTTING DOWN GUNGAME!");
		if(Debugger.window != null) {
			Debugger.window.startstop(false);
		}
		
		this.tankManager.saveTankData();
		this.turretManager.saveTurretData();
		
		Debugger.saveLog();
	}
	
	private void registerCommands() {
		this.getCommand("gg").setExecutor(new CommandListener());
		this.getCommand("gg").setTabCompleter(new CommandListener());
		
		this.getCommand("ge").setExecutor(new GunEngineCommandListener());
		this.getCommand("ge").setTabCompleter(new GunEngineCommandListener());
	}
	
	private void registerEvents() {
		
		registerPacketEvents();
		this.getServer().getPluginManager().registerEvents(new TankListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractAtTankListener(), this);
		
		this.getServer().getPluginManager().registerEvents(new SignListener(), this);
		this.getServer().getPluginManager().registerEvents(new GameListener(), this);	
		
		this.getServer().getPluginManager().registerEvents(new WeaponListener(this.weaponManager), this);
		
		this.getServer().getPluginManager().registerEvents(this.gunShop, this);
		this.getServer().getPluginManager().registerEvents(this.joinGuiManager, this);
		
		this.getServer().getPluginManager().registerEvents(new LeaveLobbyItem(), this);
		this.getServer().getPluginManager().registerEvents(new RadarUtil(this), this);
		this.getServer().getPluginManager().registerEvents(new C4(), this);
		//this.getServer().getPluginManager().registerEvents(new VehicleListeners(), this);
		if(this.serverPre113) {
			this.getServer().getPluginManager().registerEvents(new Crowbar_pre_1_13(), this);
		} else {
			this.getServer().getPluginManager().registerEvents(new Crowbar_v1_13_up(), this);
		}
		this.getServer().getPluginManager().registerEvents(new FlareGun(), this);
		this.getServer().getPluginManager().registerEvents(new SuicideArmor(), this);
		this.getServer().getPluginManager().registerEvents(new Voter(), this);
		this.getServer().getPluginManager().registerEvents(new GameConfigurator(), this);
		this.getServer().getPluginManager().registerEvents(new InfoItem(), this);
		this.getServer().getPluginManager().registerEvents(new LootChests(), this);
		this.getServer().getPluginManager().registerEvents(new TrackPadItem(), this);
		if(this.serverPre113) {
			this.getServer().getPluginManager().registerEvents(new MediCake_pre_1_13(), this);
		} else {
			this.getServer().getPluginManager().registerEvents(new MediCake_v1_13_up(), this);
		}
		this.getServer().getPluginManager().registerEvents(new TurretListener(), this);
		this.getServer().getPluginManager().registerEvents(this.griefHelper, this);
		this.getServer().getPluginManager().registerEvents(new SpectatorListener(this), this);
		this.getServer().getPluginManager().registerEvents(this.weaponShop, this);
		this.getServer().getPluginManager().registerEvents(this.chunkloadlistener, this);
		if(Bukkit.getPluginManager().getPlugin("BKCommonLib") != null && Bukkit.getPluginManager().isPluginEnabled("BKCommonLib")) {
			this.getServer().getPluginManager().registerEvents(new BKCommonLibListener(), this);
		}
	}
	
	private void registerPacketEvents() {
		if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			Debugger.logInfoWithColoredText(ChatColor.AQUA + "ProtocolLib found! Enabling tanks...");
			
			switch(versionMC) {
			default:
				Debugger.logInfoWithColoredText(ChatColor.RED + "Server version not supported! Disabling tanks...");
				break;
			case "1.12.0":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_12_R1(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			case "1.12.1":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_12_R1(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			case "1.12.2":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_12_R1(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			case "1.13.0":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_13_R1(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			case "1.13.1":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_13_R2(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			case "1.13.2":
				com.comphenix.protocol.ProtocolLibrary.getProtocolManager()
				.addPacketListener(new TankMovementListener_1_13_R2(
						instance, 
						com.comphenix.protocol.events.ListenerPriority.NORMAL, 
						new com.comphenix.protocol.PacketType[] {com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE} )
				);
				break;
			}
		} else {
			Debugger.logInfoWithColoredText(ChatColor.AQUA + "ProtocolLib " +  ChatColor.RED + "NOT" + ChatColor.AQUA +" found or ProtocolLib is " +  ChatColor.RED + "NOT" + ChatColor.AQUA + " enabled! Disabling tanks...");
		}
	}
	
	private void loadConfigurations() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		FileManager.getLang().options().copyDefaults(true);
		FileManager.saveLang();
		
		FileManager.getArenaConfig().options().copyDefaults(true);
		FileManager.saveArenaConfig();

	}
	
	
	
	
	
	
	
	
	//WARINING: REMOVE THE DOWNLOAD THING BEFORE POSTING ON SPIGOT.ORG!!!
	/**private void checkForPlugins() {
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Checking for core libraries and needed plugins...");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Looking for ItemNBTAPI...");
		if(Bukkit.getPluginManager().getPlugin("ItemNBTAPI") != null) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "ItemNBTAPI found! Continue Loading...");
		} else {
			Debugger.logWarning("ItemNBTAPI was NOT found! Pausing loading process...");
			Debugger.logInfoWithColoredText(ChatColor.GREEN + "Downloading ItemNBTAPI...");
			
			String downloadURL = "https://api.spiget.org/v2/resources/" + "7939" + "/download";
			File dir = new File(this.getDataFolder().getAbsolutePath());
			String pluginFolder = dir.getParentFile().getAbsolutePath() + "/GunGame_ItemNBTAPI.jar";
			
			try {	
				FileUtils.copyURLToFile(new URL(downloadURL), new File(pluginFolder), 10000, 10000);
				//HttpDownloadUtility.downloadFile(downloadURL, pluginFolder);
				Debugger.logInfoWithColoredText(ChatColor.GREEN + "Downloaded ItemNBTAPI! Now Loading...");
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			try {
				Plugin itemNBTAPI = Bukkit.getPluginManager().loadPlugin(new File(pluginFolder));
				Bukkit.getPluginManager().enablePlugin(itemNBTAPI);
				Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded ItemNBTAPI successfully! Unpausing Loading process...");
			} catch (UnknownDependencyException e) {
				e.printStackTrace();
			} catch (InvalidPluginException e) {
				e.printStackTrace();
			} catch (InvalidDescriptionException e) {
				e.printStackTrace();
			}
		}
			
	}**/
	
}
