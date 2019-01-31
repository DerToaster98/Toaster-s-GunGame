package de.MrBaumeister98.GunGame.GunEngine.Tanks;

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
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.tr7zw.itemnbtapi.NBTItem;

public class TankManager {
	
	private HashMap<UUID, Tank> tankByID;
	private List<Tank> activeTanks;
	private List<TankConfig> tankConfigs;
	private List<TankSoundSet> tankSounSets;
	private HashMap<String, TankConfig> tankconfigByName;
	private HashMap<String, TankSoundSet> tanksoundsetByName;
	
	public List<TankData> tanksToSpawn;
	
	public TankManager() {
		this.tanksToSpawn = new ArrayList<TankData>();
		this.tankByID = new HashMap<UUID, Tank>();
		this.activeTanks = new ArrayList<Tank>();
		this.tankConfigs = new ArrayList<TankConfig>();
		this.tankconfigByName = new HashMap<String, TankConfig>();
		this.tanksoundsetByName = new HashMap<String, TankSoundSet>();
		this.tankSounSets = new ArrayList<TankSoundSet>();
	}
	
	public void loadTankConfigs() {
		Debugger.logInfoWithColoredText(ChatColor.RED + "Initializing Tank System...");
		loadTankSoundSets();

		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Tank Files...");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + FileManager.getTankFolder().listFiles().length + ChatColor.YELLOW + " Tank(s), loading...");
		for(File f : FileManager.getTankFolder().listFiles()) {
			FileConfiguration c = YamlConfiguration.loadConfiguration(f);
			Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Tank: " + ChatColor.GREEN + c.getString("Name", "Tank") + ChatColor.YELLOW + "...");
			TankConfig tc = new TankConfig(c);
			this.tankConfigs.add(tc);
			this.tankconfigByName.put(tc.name, tc);
			Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
			Debugger.logInfoWithColoredText(ChatColor.RED +"Added Tank: " + ChatColor.GREEN + tc.name + ChatColor.RED +"!");
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + FileManager.getTankFolder().listFiles().length + ChatColor.GREEN +" Tank(s)!");
	}
	public void saveTankData() {
		for(Tank tank : this.activeTanks) {
			if(!tank.getWorld().getName().contains("GunGame-")) {
				File dataFile = new File(tank.getWorld().getWorldFolder().getAbsolutePath() + "/data/gungame", "vehicles.yml");
				if(!dataFile.exists()) {
					try {
						dataFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileConfiguration dataFileC = YamlConfiguration.loadConfiguration(dataFile);
				List<String> tanks = dataFileC.getStringList("Tanks");
				if(tanks.isEmpty() || tanks.size() == 0 || !tanks.contains(tank.generateDataSaveString())) {
					tanks.add(tank.generateDataSaveString());
				}
				dataFileC.set("Tanks", tanks);
				try {
					dataFileC.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				tank.remove();
			}
		}
		for(TankData data : this.tanksToSpawn) {
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
				List<String> tanks = dataFileC.getStringList("Tanks");
				if(tanks.isEmpty() || tanks.size() == 0 || !tanks.contains(data.generateDataSaveString())) {
					tanks.add(data.generateDataSaveString());
				}
				dataFileC.set("Tanks", tanks);
				try {
					dataFileC.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void respawnTanks(World world) {
		File dataFile = new File(world.getWorldFolder().getAbsolutePath() + "/data/gungame", "vehicles.yml");
		if(dataFile.exists()) {
			FileConfiguration dataFileC = YamlConfiguration.loadConfiguration(dataFile);
			List<String> tanks = dataFileC.getStringList("Tanks");
			List<String> iteratingList = new ArrayList<String>(tanks);
			//Debugger.logInfoWithColoredText(ChatColor.AQUA + "Respawning " + ChatColor.GREEN + iteratingList.size() + ChatColor.AQUA + " Tanks in World: " + ChatColor.GREEN + world.getName() + ChatColor.AQUA + "...");
			for(String tank : iteratingList) {
				tanks.remove(tank);
				String[] args = tank.split("\\,");
				String tConfig = args[0];
				if(getTankConfig(tConfig) != null) {
					TankData td = new TankData(this, args);
					this.tanksToSpawn.add(td);
					/*Location loc = Util.stringToLoc(args[1]);
					for(Entity ent : loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
						if(ent.getType().equals(EntityType.ARMOR_STAND) || ent instanceof ArmorStand) {
							ent.remove();
						}
						if(ent.getType().equals(EntityType.MINECART) || ent instanceof Minecart) {
							ent.remove();
						}
					}
					Float turnAngle = Float.valueOf(args[2]);
					Double hp = Double.valueOf(args[3]);
					Integer mag = Integer.valueOf(args[4]);
					Double turretAngleY = Double.valueOf(args[5]);
					Double barrelAngleX = Double.valueOf(args[6]);
					
					Tank tnk = new Tank(loc, getTankConfig(tConfig), this);
					tnk.getTankMover().setTurnAngle(turnAngle);
					tnk.setHealth(hp);
					tnk.setMagazine(mag);
					tnk.adjustTurret(turretAngleY.floatValue(), barrelAngleX.floatValue());*/
				}
			}
			//Debugger.logInfoWithColoredText(ChatColor.GREEN + "Done!");
			dataFileC.set("Tanks", tanks);
			try {
				dataFileC.save(dataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void respawnTank(TankData data) {
		Location loc = data.location.add(0.0, 1.0, 0.0);
		Debugger.logInfoWithColoredText(ChatColor.AQUA + "Respawning Tank at: " + ChatColor.LIGHT_PURPLE + Util.locToString(loc) + ChatColor.AQUA + "...");
		for(Entity ent : loc.getWorld().getNearbyEntities(loc, 0.25, 0.25, 0.25)) {
			if(ent.getType().equals(EntityType.ARMOR_STAND) || ent instanceof ArmorStand) {
				ent.remove();
			}
		}
		/*Material locMat = Material.valueOf(loc.getBlock().getRelative(BlockFace.DOWN).getType().toString());
		loc.getBlock().getRelative(BlockFace.DOWN).setType(locMat);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				
				Tank tank = new Tank(loc, data.tc, GunGamePlugin.instance.tankManager);
				//tank.getBodyArmorStand().setGravity(false);
				tank.getTankMover().setTurnAngle(data.turnAngle);
				tank.setHealth(data.hp);
				tank.setMagazine(data.mag);
				tank.adjustTurret(data.turretAngleY.floatValue(), data.barrelAngleX.floatValue());
			}
		}, 60);*/
		//tank.getTankMover().setTurnAngle(data.turnAngle);
		//tank.setHealth(data.hp);
		//tank.setMagazine(data.mag);
		//tank.adjustTurret(data.turretAngleY.floatValue(), data.barrelAngleX.floatValue());
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "NOT YET IMPLEMENTED!");
		if(this.tanksToSpawn.contains(data)) {
			this.tanksToSpawn.remove(data);
		}
	}
	private void loadTankSoundSets() {
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Loading Tank-SoundSet Files...");
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Found " + ChatColor.RED + FileManager.getTankSoundsetFolder().listFiles().length + ChatColor.YELLOW + " Tank-SoundSet(s), loading...");
		for(File f : FileManager.getTankSoundsetFolder().listFiles()) {
			FileConfiguration c = YamlConfiguration.loadConfiguration(f.getAbsoluteFile());
			Debugger.logInfoWithColoredText(ChatColor.YELLOW +  "Registering Tank-SoundSet: " + ChatColor.GREEN + c.getString("Name", "TSS") + ChatColor.YELLOW + "...");
			TankSoundSet tss = new TankSoundSet(c);
			this.tankSounSets.add(tss);
			this.tanksoundsetByName.put(tss.name, tss);
			Debugger.logInfoWithColoredText(ChatColor.YELLOW +"Done!");
			Debugger.logInfoWithColoredText(ChatColor.RED +"Added Tank-SoundSet: " + ChatColor.GREEN + tss.name + ChatColor.RED +"!");
		}
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Loaded " + ChatColor.RED + FileManager.getTankSoundsetFolder().listFiles().length + ChatColor.GREEN +" Tank-SoundSet(s)!");
	}
	
	public void addTankEntity(Tank tank) {
		this.activeTanks.add(tank);
		this.tankByID.put(tank.getTankID(), tank);
	}
	public void removeTank(Tank tank) {
		if(this.activeTanks.contains(tank)) {
			this.activeTanks.remove(tank);
		}
		if(this.tankByID.containsValue(tank)) {
			this.tankByID.remove(tank.getTankID(), tank);
		}
	}
	
	public Tank getTankByID(UUID tankID) {
		if(this.tankByID.containsKey(tankID)) {
			return this.tankByID.get(tankID);
		}
		return null;
	}
	public TankConfig getTankConfig(String name) {
		if(this.tankconfigByName.containsKey(name)) {
			return this.tankconfigByName.get(name);
		}
		return null;
	}
	public TankConfig getTankConfig(ItemStack item) {
		if(ItemUtil.isGGTank(item)) {
			NBTItem nbti = new NBTItem(item);
			return getTankConfig(nbti);
		}
		return null;
	}
	public TankConfig getTankConfig(NBTItem nbti) {
		if(nbti.hasKey("GG_Tank") && nbti.hasKey("GG_Tank_Name")) {
			String key = nbti.getString("GG_Tank_Name");
			return getTankConfig(key);
		}
		return null;
	}
	public List<TankConfig> getTankConfigs() {
		return this.tankConfigs;
	}
	public TankSoundSet getTankSoundSet(String name) {
		if(this.tanksoundsetByName.containsKey(name)) {
			return this.tanksoundsetByName.get(name);
		}
		return null;
	}
	
	public Tank isPositionInATankHitbox(Location loc) {
		for(Tank tank : this.activeTanks) {
			if(loc.getWorld().equals(tank.getWorld()) && tank.getHitboxChecker().isLocationInsideHitbox(loc)) {
				return tank;
			}
		}
		return null;
	}

}
