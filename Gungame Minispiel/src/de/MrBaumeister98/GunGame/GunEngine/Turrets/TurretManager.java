package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.tr7zw.nbtapi.NBTItem;

public class TurretManager {
	
	public GunGamePlugin plugin;
	
	public HashMap<UUID, Turret> getTurretById;
	public HashMap<String, TurretConfig> getTurretConfigByName;
	
	public List<TurretConfig> turrets;
	
	public List<TurretData> turretsToSpawn;
	
	public TurretManager(GunGamePlugin plugin) {
		this.turretsToSpawn = new ArrayList<TurretData>();
		this.plugin = plugin;
		this.getTurretById = new HashMap<UUID, Turret>();
		this.getTurretConfigByName = new HashMap<String, TurretConfig>();
		this.turrets = new ArrayList<TurretConfig>();
	}
	public TurretConfig getTurretConfig(String name) {
		if(this.getTurretConfigByName.containsKey(name)) {
			return getTurretConfigByName.get(name);
		}
		return null;
	}
	public TurretConfig getTurretConfig(NBTItem nbti) {
		if(nbti.hasKey("GG_Turret") && nbti.hasKey("GG_Turret_Name")) {
			String key = nbti.getString("GG_Turret_Name");
			return getTurretConfig(key);
		}
		return null;
	}
	public TurretConfig getTurretConfig(ItemStack item) {
		if(ItemUtil.isGGTurret(item)) {
			NBTItem nbti = new NBTItem(item);
			return getTurretConfig(nbti);
		}
		return null;
	}
	public Turret getTurret(UUID id) {
		if(this.getTurretById.containsKey(id)) {
			return this.getTurretById.get(id);
		}
		return null;
	}
	public void removeTurret(Turret turret) {
		if(this.getTurretById.containsValue(turret)) {
			this.getTurretById.remove(turret.turretID, turret);
		}
	}
	public void initialize() {
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Turret Files...");
		File[] files = FileManager.getTurretFolder().listFiles();
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + files.length + ChatColor.YELLOW + " Turret(s), loading...");
		if(files != null && files.length > 0) {
			for(File config : files) {
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(config);
				TurretConfig tc = new TurretConfig(cfg);
				tc.load();
				this.getTurretConfigByName.put(tc.name, tc);
				this.turrets.add(tc);
				
				Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
				Debugger.logInfoWithColoredText(ChatColor.RED +"Added Turret: " + ChatColor.GREEN + tc.name + ChatColor.RED +"!");
			}
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + files.length + ChatColor.GREEN +" Turret(s)!");
	}
	public void saveTurretData() {
		for(Turret turret : this.getTurretById.values()) {
			if(!turret.getWorld().getName().contains("GunGame-")) {
				File dataFile = new File(turret.getWorld().getWorldFolder().getAbsolutePath() + "/data/gungame", "vehicles.yml");
				if(!dataFile.exists()) {
					try {
						dataFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileConfiguration dataFileC = YamlConfiguration.loadConfiguration(dataFile);
				List<String> turrets = dataFileC.getStringList("Turrets");
				if(turrets.isEmpty() || turrets.size() == 0 || !turrets.contains(turret.generateDataSaveString())) {
					turrets.add(turret.generateDataSaveString());
				}
				dataFileC.set("Turrets", turrets);
				try {
					dataFileC.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				turret.remove();
			}
		}
		for(TurretData data : this.turretsToSpawn) {
			if(!data.location.getWorld().getName().contains("GunGame-")) {
				File dataFile = new File(data.location.getWorld().getWorldFolder().getAbsolutePath() + "/data/gungame", "vehicles.yml");
				if(!dataFile.exists()) {
					try {
						dataFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileConfiguration dataFileC = YamlConfiguration.loadConfiguration(dataFile);
				List<String> turrets = dataFileC.getStringList("Turrets");
				if(turrets.isEmpty() || turrets.size() == 0 || !turrets.contains(data.generateDataSaveString())) {
					turrets.add(data.generateDataSaveString());
				}
				dataFileC.set("Turrets", turrets);
				try {
					dataFileC.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void respawnTurrets(World world) {
		File dataFile = new File(world.getWorldFolder().getAbsolutePath() + "/data/gungame", "vehicles.yml");
		if(dataFile.exists()) {
			FileConfiguration dataFileC = YamlConfiguration.loadConfiguration(dataFile);
			List<String> turrets = dataFileC.getStringList("Turrets");
			List<String> iteratingList = new ArrayList<String>(turrets);
			for(String turret : iteratingList) {
				turrets.remove(turret);
				String[] args = turret.split("\\,");
				String tConfig = args[0];
				if(getTurretConfig(tConfig) != null) {
					TurretData td = new TurretData(this, args);
					this.turretsToSpawn.add(td);
				}
			}
			dataFileC.set("Turrets", turrets);
			try {
				dataFileC.save(dataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void respawnTurret(TurretData data) {
		Location loc = data.location;
		Debugger.logInfoWithColoredText(ChatColor.AQUA + "Respawning Turret at: " + ChatColor.LIGHT_PURPLE + Util.locToString(loc) + ChatColor.AQUA + "...");
		for(Entity ent : loc.getWorld().getNearbyEntities(loc, 0.25, 0.25, 0.25)) {
			if(ent.getType().equals(EntityType.ARMOR_STAND) || ent instanceof ArmorStand) {
				ent.remove();
			}
		}
		Turret turret = new Turret(loc, data.startYaw, data.tc);
		turret.setHealth(data.hp);
		turret.setTemperature(data.heat);
		turret.changeYaw(data.angle);
		turret.setMagazine(data.mag);
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Done!");
		if(this.turretsToSpawn.contains(data)) {
			this.turretsToSpawn.remove(data);
		}
	}

}
