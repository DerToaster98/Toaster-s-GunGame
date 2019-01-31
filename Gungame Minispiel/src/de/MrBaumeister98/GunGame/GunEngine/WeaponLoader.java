package de.MrBaumeister98.GunGame.GunEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.MrBaumeister98.GunGame.Game.Core.FileManager;
//import de.MrBaumeister98.GunGame.Game.Core.Main;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;

public class WeaponLoader {
	
	private WeaponManager manager;
	//private Main plugin;
	
	public WeaponLoader(WeaponManager manager/*, Main plugin*/) {
		this.manager = manager;
		//this.plugin = plugin;
	}
	
	public FileConfiguration getWeapon(String weaponName) {
		File file = new File(FileManager.getGunFolder().getAbsolutePath(), weaponName + ".yml");
		FileConfiguration weaponFile = null;
		try {
			weaponFile = YamlConfiguration.loadConfiguration(file);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return weaponFile;
	}
	public FileConfiguration getGrenade(String grenadeName) {
		File file = new File(FileManager.getGrenadesFolder().getAbsolutePath(), grenadeName + ".yml");
		FileConfiguration grenadeFile = null;
		try {
			grenadeFile = YamlConfiguration.loadConfiguration(file);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return grenadeFile;
	}
	public FileConfiguration getAmmo(String ammoName) {
		File file = new File(FileManager.getAmmoFolder().getAbsolutePath(), ammoName + ".yml");
		FileConfiguration ammoFile = null;
		try {
			ammoFile = YamlConfiguration.loadConfiguration(file);
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
		return ammoFile;
	}
	private void addFilesToList(File folder, List<File> list) {
		for(File f : folder.listFiles()) {
			if(f.isDirectory()) {
				addFilesToList(f, list);
			} else if(f.isFile()) {
				list.add(f);
			}
		}
	}
	public void load(Boolean reload) {
		if(reload) {
			//this.manager.clearLists();  ALREADY DONE IN MANAGER
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "ReLoading Weapons...");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Weapons...");
		}	
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Gun-Sound-Set Files...");
		File[] filesFolder = FileManager.getSoundSetsFolder().listFiles();
		List<File> files = new ArrayList<File>();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Gun-Sound-Set(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadSoundSet(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Gun-Sound-Set(s)!");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Ammo Files...");
		filesFolder = FileManager.getAmmoFolder().listFiles();
		files.clear();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Ammo(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadAmmo(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Ammo(s)!");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Gun Files...");
		filesFolder = FileManager.getGunFolder().listFiles();
		files.clear();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Gun(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadGun(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Gun(s)!");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Grenade Files...");
		filesFolder = FileManager.getGrenadesFolder().listFiles();
		files.clear();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Grenade(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadGrenade(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Grenade(s)!");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Airstrike Files...");
		filesFolder = FileManager.getAirstrikeFolder().listFiles();
		files.clear();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Airstrike(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadAirstrike(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Airstrike(s)!");
		
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Landmine Files...");
		filesFolder = FileManager.getLandmineFolder().listFiles();
		files.clear();
		for(File f : filesFolder) {
			if(f.isDirectory()) {
				addFilesToList(f, files);
			} else if(f.isFile()) {
				files.add(f);
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.size() + ChatColor.YELLOW + " Landmine(s), loading...");
		for(File f : files) {
			FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			
			loadLandmine(weaponConfig);
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.size() + ChatColor.GREEN +" Landmine(s)!");
			
	}
	
	private void loadGun(FileConfiguration weaponFile) {
		this.manager.registerGun(weaponFile);
	}
	private void loadGrenade(FileConfiguration weaponFile) {
		this.manager.registerGrenade(weaponFile);
	}
	private void loadAmmo(FileConfiguration weaponFile) {
		this.manager.registerAmmo(weaponFile);
	}
	private void loadSoundSet(FileConfiguration weaponFile) {
		this.manager.registerSoundSet(weaponFile);
	}
	private void loadAirstrike(FileConfiguration weaponFile) {
		this.manager.registerAirstrike(weaponFile);
	}
	private void loadLandmine(FileConfiguration weaponFile) {
		this.manager.registerLandmine(weaponFile);
	}

}
