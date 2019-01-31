package de.MrBaumeister98.GunGame.Game.Core.Debugger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public abstract class Debugger {
	
	private static File logFile;
	private static FileConfiguration log;
	public static DebuggerWindowHelperThread window = null;

	public static void logInfo(String message) {
    	Logger.getLogger(JavaPlugin.class.getName()).log(Level.INFO, ChatColor.DARK_GREEN + "[GunGame] " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
    	addToLogFile("[INFO]: " + message);
    	saveLog();
    }
	public static void logWarning(String message) {
		Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "[GunGame] " + ChatColor.translateAlternateColorCodes('&', message));
		addToLogFile("[WARNING]: " + ChatColor.stripColor(message));
		saveLog();
	}
	public static void logError(String message) {
		Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "[GunGame] " + ChatColor.translateAlternateColorCodes('&', message));
		addToLogFile("[ERROR]: " + ChatColor.stripColor(message));
		saveLog();
	}
	public static void logInfoWithColoredText(String message) {
		GunGamePlugin.instance.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[GunGame] " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
		addToLogFile("[INFO]: " + message);
		saveLog();
	}
	public static void initializeAdvancedDebugger(GunGamePlugin plugin) {
		DebuggerWindowHelperThread dwht = new DebuggerWindowHelperThread(plugin);
		dwht.start();
		window = dwht;
	}
	private static void addToLogFile(String toLog) {
		SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
		String time = date.format(new Date());
		
		logFile = new File(FileManager.getLogFolder().getAbsolutePath(), time + ".log");
		log = YamlConfiguration.loadConfiguration(logFile);
		
		SimpleDateFormat SysTime = new SimpleDateFormat("HH:mm:ss");
		String sysTime = SysTime.format(new Date());
		
		List<String> temp = log.getStringList(sysTime);
		temp.add(ChatColor.stripColor(toLog));
		log.set(sysTime, temp);
		
		if(window != null) {
			window.log(ChatColor.stripColor(toLog));
		}
		
		/*try {
			log.save(f);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save " + f.getName() + " to " + f + " !", ex);
		}*/
	}
	public static void saveLog() {
		try {
			log.save(logFile);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.WARNING, "Unable to save " + logFile.getName() + " to " + logFile + " !", ex);
		}
	}
}
