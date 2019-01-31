package de.MrBaumeister98.GunGame.Game.Core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager {
		
	private static FileConfiguration lang;
	private static File langFile;
	
	private static FileConfiguration arenaConfig;
	private static File arenaFile;
	
	private static String language = GunGamePlugin.instance.getConfig().getString("Config.Language", "EN");
	

	
	private static boolean doesLanguageExist(String lg) {
		for(File f : GunGamePlugin.instance.getDataFolder().listFiles()) {
			if(f.isFile()) {
				if(f.getName().equalsIgnoreCase("lang_" + lg + ".yml")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void loadLang() {
		if(langFile == null) {
			if(doesLanguageExist(language)) {
				langFile = new File(GunGamePlugin.instance.getDataFolder(), "lang_" + language + ".yml");
			} else {
				langFile = new File(GunGamePlugin.instance.getDataFolder(), "lang_EN.yml");
			}
			GunGamePlugin.instance.saveResource("lang_EN.yml", false);
			GunGamePlugin.instance.saveResource("lang_DE.yml", false);
		}
		//lang = YamlConfiguration.loadConfiguration(langf);
		lang = new YamlConfiguration();
		
		//InputStream defaultLangStream = Main.plugin.getResource("lang.yml");
		//if(defaultLangStream !=null) {
			//YamlConfiguration defaultLang = YamlConfiguration.loadConfiguration(langf);
			//lang.setDefaults(defaultLang);
		//}
		try {
			lang.load(langFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	public static FileConfiguration getLang() {
		if(lang == null) {
			loadLang();
		}
		return lang;
	}
	public static void saveLang() {
		if(lang == null || langFile == null) {
			return;
		}
			{
				try {
					GunGamePlugin.instance.saveResource("lang_EN.yml", false);
					GunGamePlugin.instance.saveResource("lang_DE.yml", false);
					lang.save(langFile);
				} catch(IOException ex) {
					Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save lang.yml to " + langFile + " !", ex);
				}
			}
	}
	
	public static void loadArenaConfig() {
		if(arenaFile == null) {
			arenaFile = new File(GunGamePlugin.instance.getDataFolder(), "arenas.yml");
			GunGamePlugin.instance.saveResource("arenas.yml", false);
		}
		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
		
		InputStream defaultLangStream = GunGamePlugin.instance.getResource("arenas.yml");
		if(defaultLangStream !=null) {
			YamlConfiguration defaultLang = YamlConfiguration.loadConfiguration(arenaFile);
			arenaConfig.setDefaults(defaultLang);
		}
	}
	public static FileConfiguration getArenaConfig() {
		if(arenaConfig == null) {
			loadArenaConfig();
		}
		return arenaConfig;
	}
	public static void saveArenaConfig() {
		if(arenaConfig == null || arenaFile == null) {
			return;
		}
			{
				try {
					GunGamePlugin.instance.saveResource("arenas.yml", false);
					arenaConfig.save(arenaFile);
				} catch(IOException ex) {
					Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save arenas.yml to " + arenaFile + " !", ex);
				}
			}
	}
	
	
	
	
	
	
	
	
	
	

	public static File getBackupFolder() {
		File dataFolder = Bukkit.getPluginManager().getPlugin(GunGamePlugin.instance.getName()).getDataFolder();
		File backupFolder = new File(dataFolder, "ArenaWorldsData");
		if(!backupFolder.isDirectory()) {
			backupFolder.mkdirs();
		}
		return backupFolder;
	}
	public static File getPlayerDataFolder() {
		File playerFolder = Bukkit.getPluginManager().getPlugin(GunGamePlugin.instance.getName()).getDataFolder();
		File playerDataFolder = new File(playerFolder, "PlayerData");
		if(!playerDataFolder.isDirectory()) {
			playerDataFolder.mkdirs();
		}
		return playerDataFolder;
	}
	
	
	
	
	
	private static File getGunModuleFolder() {
		File gunFolder = Bukkit.getPluginManager().getPlugin(GunGamePlugin.instance.getName()).getDataFolder();
		File gunDataFolder = new File(gunFolder, "Weapons");
		if(!gunDataFolder.isDirectory()) {
			gunDataFolder.mkdirs();
		}
		return gunDataFolder;
	}
	private static File getTankModuleFolder() {
		File tankFolder = Bukkit.getPluginManager().getPlugin(GunGamePlugin.instance.getName()).getDataFolder();
		File tankDataFolder = new File(tankFolder, "Tanks");
		if(!tankDataFolder.isDirectory()) {
			tankDataFolder.mkdirs();
		}
		return tankDataFolder;
	}
	public static File getGunFolder() {
		File gunFolder = new File(getGunModuleFolder().getAbsolutePath(), "Guns/"); 
		if(!gunFolder.isDirectory()) {
			gunFolder.mkdirs();
		}
		return gunFolder;
	}
	public static File getAmmoFolder() {
		File ammoFolder = new File(getGunModuleFolder().getAbsolutePath(), "Ammo/");
		if(!ammoFolder.isDirectory()) {
			ammoFolder.mkdirs();
		}
		return ammoFolder;
	}
	public static File getGrenadesFolder() {
		File grenadeFolder = new File(getGunModuleFolder().getAbsolutePath(), "Grenades/");
		if(!grenadeFolder.isDirectory()) {
			grenadeFolder.mkdirs();
		}
		return grenadeFolder;
	}
	public static File getAirstrikeFolder() {
		File grenadeFolder = new File(getGunModuleFolder().getAbsolutePath(), "Airstrikes/");
		if(!grenadeFolder.isDirectory()) {
			grenadeFolder.mkdirs();
		}
		return grenadeFolder;
	}
	public static File getLandmineFolder() {
		File grenadeFolder = new File(getGunModuleFolder().getAbsolutePath(), "Landmines/");
		if(!grenadeFolder.isDirectory()) {
			grenadeFolder.mkdirs();
		}
		return grenadeFolder;
	}
	public static File getSoundSetsFolder() {
		File SoundSetFolder = new File(getGunModuleFolder().getAbsolutePath(), "SoundSets/");
		if(!SoundSetFolder.isDirectory()) {
			SoundSetFolder.mkdirs();
		}
		return SoundSetFolder;
	}
	public static File getTurretFolder() {
		File turretFolder = new File(getGunModuleFolder().getAbsolutePath(), "Turrets/");
		if(!turretFolder.isDirectory()) {
			turretFolder.mkdirs();
		}
		return turretFolder;
	}
	public static File getTankFolder() {
		File tankFolder = new File(getTankModuleFolder().getAbsolutePath(), "Tanks/");
		if(!tankFolder.isDirectory()) {
			tankFolder.mkdirs();
		}
		return tankFolder;
	}
	public static File getTankSoundsetFolder() {
		File tssFolder = new File(getTankModuleFolder().getAbsolutePath(), "SoundSets/");
		if(!tssFolder.isDirectory()) {
			tssFolder.mkdirs();
		}
		return tssFolder;
	}
	
	
	
	
	
	public static File getLogFolder() {
		File logFolder = new File(Bukkit.getPluginManager().getPlugin(GunGamePlugin.instance.getName()).getDataFolder(), "logs");
		if(!logFolder.isDirectory()) {
			logFolder.mkdirs();
		}
		return logFolder;
	}

	
}
