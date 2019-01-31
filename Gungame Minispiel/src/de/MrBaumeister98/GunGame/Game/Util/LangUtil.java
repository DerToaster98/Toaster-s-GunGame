package de.MrBaumeister98.GunGame.Game.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.GunEngine.Airstrike;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.GunEngine.Landmine;
import de.MrBaumeister98.GunGame.GunEngine.Enums.LandmineType;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;

public class LangUtil {
	private static String tempPreFix = FileManager.getLang().getString("lang.Game.Prefix");
	public static String prefix = ChatColor.translateAlternateColorCodes('&', tempPreFix);
	public static String prefixErr = ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("lang.Game.Error"));
	private static String tempNoPerms = FileManager.getLang().getString("lang.Errors.noPermission");
	public static String noPermission = prefix + ChatColor.translateAlternateColorCodes('&', tempNoPerms);
	
	/*Placeholders:
	 * %arena% --> Arena
	 * %arenaworld% --> Map
	 * %minp% --> Minimum Players required to start game
	 * %maxp% --> Maximum allowed Players for Arena
	 * %player% --> Current Player
	 * %index% --> Index of something, e.g.: Spawns
	 * %timer% --> Time, e.g.: Remaining Seconds
	 * %command% --> Command prefix
	 * %location% --> Location as String
	 * %sbkills% --> Kills of Player in current game
	 * %sbdeaths% --> Deaths of Player in current game
	 * %leadingplayer% --> Player closest to Victorykills in current game
	 * %wallet% --> Coins of a Player in current game
	 * %cost% --> for shop only! represents cost of something
	 */
	public static List<String> createStringList2 (String pathInFile,
			Arena arena,
			String map,
			Player player,
			Integer time,
			Integer index,
			Integer minP,
			Integer maxP,
			String command,
			Location location,
			Integer currentKills,
			Integer currentDeaths,
			Integer wallet,
			Integer cost,
			Player leadingPlayer,
			Boolean addPrefix,
			Boolean error) {
		return createStringList(pathInFile, arena, map, player, time, index, minP, maxP, command, Util.locToString(location), currentKills, currentDeaths, wallet, cost, leadingPlayer, addPrefix, error);
	}
	public static List<String> createStringList(String pathInFile,
			Arena arena,
			String map,
			Player player,
			Integer time,
			Integer index,
			Integer minP,
			Integer maxP,
			String command,
			String location,
			Integer currentKills,
			Integer currentDeaths,
			Integer wallet,
			Integer cost,
			Player leadingPlayer,
			Boolean addPrefix,
			Boolean error) {
		List<String> inFile = null;
		List<String> retList = null;
		try {
			retList = new ArrayList<String>();
			inFile = FileManager.getLang().getStringList(pathInFile);
			if(inFile != null && !inFile.isEmpty()) {
				for(String s : inFile) {
					retList.add(ChatColor.translateAlternateColorCodes('&', translate(s, arena, map, player, time, index, minP, maxP, command, location, currentKills, currentDeaths, wallet, cost, leadingPlayer, addPrefix, error)));
				}
			} else {
				retList.add("ERROR: Missing entry in config for path: " + pathInFile);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			retList.add("ERROR: Missing entry in config for path: " + pathInFile);
		}
		return retList;
	}
	public static String createString2(String pathInFile,
			Arena arena,
			String map,
			Player player,
			Integer time,
			Integer index,
			Integer minP,
			Integer maxP,
			String command,
			Location location,
			Integer currentKills,
			Integer currentDeaths,
			Integer wallet,
			Integer cost,
			Player leadingPlayer,
			Boolean addPrefix,
			Boolean error) {
		String loc = Util.locToString(location);
		return createString(pathInFile, arena, map, player, time, index, minP, maxP, command, loc, currentKills, currentDeaths, wallet, cost, leadingPlayer, addPrefix, error);
	}
	private static String translate(String toTranslate,
			Arena arena,
			String map,
			Player player,
			Integer time,
			Integer index,
			Integer minP,
			Integer maxP,
			String command,
			String location,
			Integer currentKills,
			Integer currentDeaths,
			Integer wallet,
			Integer cost,
			Player leadingPlayer,
			Boolean addPrefix,
			Boolean error) {
		try {
			if(toTranslate != null) {
				if(arena != null) {
					String aName = arena.getName();
					try {
						toTranslate = toTranslate.replace("%arena%", aName);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(map != null) {
					String mapName = map;
					try {
						toTranslate = toTranslate.replace("%arenaworld%", mapName);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(player != null) {
					String pName = player.getName();
					try {
						toTranslate = toTranslate.replace("%player%", pName);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(time != null) {
					String timeS = time.toString();
					try {
						toTranslate = toTranslate.replace("%timer%", timeS);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(index != null) {
					String indexS = index.toString();
					try {
						toTranslate = toTranslate.replace("%index%", indexS);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(minP != null) {
					String mS = minP.toString();
					try {
						toTranslate = toTranslate.replace("%minp%", mS);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(maxP != null) {
					String mS = maxP.toString();
					try {
						toTranslate = toTranslate.replace("%maxp%", mS);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(command != null) {
					try {
						toTranslate = toTranslate.replace("%command%", command);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(location != null) {
					try {
						toTranslate = toTranslate.replace("%location%", location);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(currentKills != null) {
					String s = currentKills.toString();
					try {
						toTranslate = toTranslate.replace("%sbkills%", s);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(currentDeaths != null) {
					String s = currentDeaths.toString();
					try {
						toTranslate = toTranslate.replace("%sbdeaths%", s);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(wallet != null) {
					String w = wallet.toString();
					try {
						toTranslate = toTranslate.replace("%wallet%", w);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(cost != null) {
					String c = cost.toString();
					try {
						toTranslate = toTranslate.replace("%cost%", c);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(leadingPlayer != null) {
					String pn = leadingPlayer.getName();
					try {
						toTranslate = toTranslate.replace("%leadingplayer%", pn);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				if(addPrefix || error) {
					if(error) {
						toTranslate = prefixErr + toTranslate;
					} else {
						toTranslate = prefix + toTranslate;
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return toTranslate;
	}
	public static String createString(String pathInFile,
			Arena arena,
			String map,
			Player player,
			Integer time,
			Integer index,
			Integer minP,
			Integer maxP,
			String command,
			String location,
			Integer currentKills,
			Integer currentDeaths,
			Integer wallet,
			Integer cost,
			Player leadingPlayer,
			Boolean addPrefix,
			Boolean error) {
		String inFile = null;
		try {
			inFile = FileManager.getLang().getString(pathInFile, "&c&lMissing value in langfile for entry: &r&4" + pathInFile);
			
			if(inFile != null) {
				inFile = translate(inFile, arena, map, player, time, index, minP, maxP, command, location, currentKills, currentDeaths, wallet, cost, leadingPlayer, addPrefix, error);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		try {
			inFile = ChatColor.translateAlternateColorCodes('&', inFile);
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		}
		
		return inFile;
	}
	public static String buildHelpString(String path) {
		String inFile = null;
		try {
			inFile = FileManager.getLang().getString("lang.Help." + path, "&c&lMissing value in langfile for entry: &r&4" + "lang.Help." + path);
			if(inFile != null) {
				inFile = ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', inFile);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return inFile;
	}
	public static String buildGUIString(String GUIName) {
		String inFile = null;
		try {
			inFile = FileManager.getLang().getString("lang.GUI." + GUIName, "&c&lMissing value in langfile for entry: &r&4" + "lang.GUI." + GUIName);
			if(inFile != null) {
				inFile = ChatColor.translateAlternateColorCodes('&', inFile);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return inFile;
	}
	public static String buildItemName(String itemName) {
		String inFile = null;
		try {
			inFile = FileManager.getLang().getString("lang.Items." + itemName + ".Name", "&c&lMissing value in langfile for entry: &r&4" + "lang.Items." + itemName + ".Name");
			if(inFile != null) {
				inFile = ChatColor.translateAlternateColorCodes('&', inFile);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return inFile;
	}
	public static String buildItemName2(String itemName) {
		String inFile = null;
		try {
			inFile = FileManager.getLang().getString("lang.Items." + itemName + ".Name", "&c&lMissing value in langfile for entry: &r&4" + "lang.Items." + itemName + ".Name");
			if(inFile != null) {
				inFile = inFile.replace('&', '§');
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return inFile;
	}
	public static List<String> buildItemLore(String itemName) {
		List<String> inFile = new ArrayList<String>();
		List<String> ret = new ArrayList<String>();
		try {
			inFile = FileManager.getLang().getStringList("lang.Items." + itemName + ".Lore");
			if(inFile != null) {
				for(String s : inFile) {
					ret.add(ChatColor.translateAlternateColorCodes('&', s));
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static String getStringByPath(String path) {
		String temp = FileManager.getLang().getString(path, "&c&lMissing value in langfile for entry: &r&4" + path);
		temp = ChatColor.translateAlternateColorCodes('&', temp);
		return temp;
	}
	public static List<String> getStringListByPath(String path) {
		List<String> temp = FileManager.getLang().getStringList(path);
		List<String> ret = new ArrayList<String>();
		for(String s : temp) {
			ret.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return ret;
	}
	
	public static String getGunMenuString(String path) {
		String path2 = "lang.GUI.GunEngine." + path;
		String temp = ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString(path2, "&c&lMissing value in langfile for entry: &r&4" + path2));
		return temp;
	}
	public static Material getGunMenuItemMaterial(String path, String def) {
		String path2 = "lang.GUI.GunEngine.ItemsMenu." + path + ".Item";
		String temp = FileManager.getLang().getString(path2, def);
		if(FileManager.getLang().getString(path2) == null || FileManager.getLang().getString(path2).equals("")) {
			Debugger.logWarning("&cMissing value in langfile for entry: &r&4" + path2);
		} else {
			Debugger.logInfoWithColoredText("&eConfig value for path &a" + path2 + "&e is &a" + temp);
		}
		Material m;
		try {
			m = Material.getMaterial(temp);
		} catch(NullPointerException ex) {
			Debugger.logWarning("&eSetting in langfile at: &a" + path2 + "&r&e is not a valid Material!");
			Debugger.logError("&cMaterial: &e" + temp + " &cis not valid! Using Material: &a" + def + "&cinstead...");
			m = Material.getMaterial(def);
		}
		if(m == null) {
			Debugger.logWarning("&eSetting in langfile at: &a" + path2 + "&r&e is not a existing Material!");
			Debugger.logError("&cMaterial: &e" + temp + " &cdoesn't exist! Using Material: &a" + def + "&cinstead...");
			m = Material.getMaterial(def);
		}
		return m;
	}
	public static Material getShopGUIItem(String path, String def) {
		String path2 = "lang.GUI.GunEngine.Shop." + path;
		String temp = FileManager.getLang().getString(path2, def);
		if(FileManager.getLang().getString(path2) == null || FileManager.getLang().getString(path2).equals("")) {
			Debugger.logWarning("&cMissing value in langfile for entry: &r&4" + path2);
		} else {
			Debugger.logInfoWithColoredText("&eConfig value for path &a" + path2 + "&e is &a" + temp);
		}
		Material m;
		try {
			m = Material.getMaterial(temp);
		} catch(NullPointerException ex) {
			Debugger.logWarning("&eSetting in langfile at: &a" + path2 + "&r&e is not a valid Material!");
			Debugger.logError("&cMaterial: &e" + temp + " &cis not valid! Using Material: &a" + def + "&cinstead...");
			m = Material.getMaterial(def);
		}
		if(m == null) {
			Debugger.logWarning("&eSetting in langfile at: &a" + path2 + "&r&e is not a existing Material!");
			Debugger.logError("&cMaterial: &e" + temp + " &cdoesn't exist! Using Material: &a" + def + "&cinstead...");
			m = Material.getMaterial(def);
		}
		return m;
	}
	public static List<String> getWeaponItemLore(Gun gun) {
		/**
		 * %name%
		 * %damage%
		 * %magazine%
		 * %shotcount%
		 * %type%
		 * %ammo%
		 * %reloadtime%
		 * %rpm%
		 * %spray%
		 * %weight%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.getGunName());
		String damage = gun.getShotDamage().toString();
		String magazine = gun.getMaxAmmo().toString();
		String projectilespershot = gun.getShotCount().toString();
		String type = gun.getType().toString();
		String ammo = ChatColor.stripColor(gun.getAmmo().getAmmoName());
		String reloadTime = String.valueOf(gun.getReloadDuration());
		String rpm = String.valueOf((long)1200 / gun.getShootingDelay());
		String weight = gun.getWeight().toString();
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		String spray = String.valueOf(gun.getAccuracy());
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getWeaponFile().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Gun")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%magazine%", magazine);
			s = s.replaceAll("%shotcount%", projectilespershot);
			s = s.replaceAll("%type%", type);
			s = s.replaceAll("%ammo%", ammo);
			s = s.replaceAll("%reloadtime%", reloadTime);
			s = s.replaceAll("%rpm%", rpm);
			s = s.replaceAll("%spray%", spray);
			s = s.replaceAll("%weight%", weight);
			s = s.replaceAll("%worth%", worth);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(Grenade gun) {
		/**
		 * %name%
		 * %damage%
		 * %radius%
		 * %type%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.getGrenadeName());
		String damage = String.valueOf(gun.getStrength());
		String type = gun.getGrenadeType().toString();
		String radius = gun.getRadius().toString();
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getWeaponFile().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Grenade")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%type%", type);
			s = s.replaceAll("%worth%", worth);
			s = s.replaceAll("%radius%", radius);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(Ammo gun) {
		/**
		 * %name%
		 * %shotsperitem%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.getAmmoName());
		String shots = gun.getShotCount().toString();
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getWeaponFile().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Ammo")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%worth%", worth);
			s = s.replaceAll("%shotsperitem%", shots);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(Landmine gun) {
		/**
		 * %name%
		 * %radius%
		 * %damage%
		 * %type%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.getName());
		String damage = "";
		if(gun.getType().equals(LandmineType.BEARTRAP)) {
			damage = gun.getBearTrapDamage().toString();
		} else {
			damage = String.valueOf(gun.getStrength());
		}
		String type = gun.getType().toString();
		String radius = "0";
		if(gun.getRadius() != null) {
			radius = gun.getRadius().toString();
		}
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getConfig().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Landmine")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%type%", type);
			s = s.replaceAll("%worth%", worth);
			s = s.replaceAll("%radius%", radius);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(Airstrike gun) {
		/**
		 * %name%
		 * %bomblets%
		 * %damage%
		 * %radius%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.name);
		String damage = String.valueOf(gun.getPower());
		String radius = gun.getDropRadius().toString();
		String bomblets = gun.getBombCount().toString();
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getConfig().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Airstrike")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%bomblets%", bomblets);
			s = s.replaceAll("%worth%", worth);
			s = s.replaceAll("%radius%", radius);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(TurretConfig gun) {
		/**
		 * %name%
		 * %damage%
		 * %magazine%
		 * %health%
		 * %type%
		 * %ammo%
		 * %reloadtime%
		 * %rpm%
		 * %spray%
		 * %worth%
		 */
		String name = ChatColor.stripColor(gun.name);
		String damage = gun.getBulletDamage().toString();
		String magazine = gun.getMagazineSize().toString();
		String hp = gun.getMaxHealth().toString();
		String type = gun.getBulletType().toString();
		String ammo = ChatColor.stripColor(gun.getNeededAmmo().getAmmoName());
		String reloadTime = String.valueOf(gun.getReloadDuration());
		String rpm = String.valueOf((long)1200 / gun.getShootDelay());
		String worth = GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(gun).toString();
		String spray = String.valueOf(gun.getAccuracy());
		
		List<String> desc = new ArrayList<String>();
		for(String s : gun.getConfig().getStringList("Items.Icon.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Turret")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%magazine%", magazine);
			s = s.replaceAll("%health%", hp);
			s = s.replaceAll("%type%", type);
			s = s.replaceAll("%ammo%", ammo);
			s = s.replaceAll("%reloadtime%", reloadTime);
			s = s.replaceAll("%rpm%", rpm);
			s = s.replaceAll("%spray%", spray);
		    s = s.replaceAll("%worth%", worth);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	public static List<String> getWeaponItemLore(TankConfig tac) {
		/**
		 * %name%
		 * %damage%
		 * %magazine%
		 * %health%
		 * %type%
		 * %speed%
		 * %speedreverse%
		 * %turnangle%
		 * %minangle%
		 * %maxangle%
		 * %acceleration%
		 * %reloadtime%
		 * %rpm%
		 * %worth%
		 */
		String name = tac.name;
		String damage = ((Double)tac.getProjectileDamage()).toString();
		String magazine = tac.getMagazineSize().toString();
		String health  = tac.getMaxHealth().toString();
		String type = tac.getProjectileType().toString();
		String speed = ((Double)(tac.getMaxSpeed() * 3.6D)).toString();
		String speedrev = ((Double)(Math.abs(tac.getMaxSpeedReverse()) * 3.6D)).toString();
		String ta = tac.getTurnAnglePerTick().toString();
		String ma = tac.getMinBarrelAngle().toString();
		String maa = tac.getMaxBarrelAngle().toString();
		String acc = tac.getSpeedUpPerTick().toString();
		String rt = ((Long)tac.getReloadDuration()).toString();
		String rpm = ((Long)((long)1200 / tac.getShootDelay())).toString();
		String worth =  GunGamePlugin.instance.weaponManager.getShopHelper().getPrice(tac).toString();
		
		List<String> desc = new ArrayList<String>();
		for(String s : tac.getFile().getStringList("Item.Lore")) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		desc.add("");
		List<String> lore = new ArrayList<String>();
		
		if(desc.size() > 1) {
			for(String s : desc) {
				lore.add(s);
			}
		}
		for(String s : getStringListByPath("lang.Commands.GunEngine.Visuals.WeaponLore.Tank")) {
			s = s.replaceAll("%name%", name);
			s = s.replaceAll("%damage%", damage);
			s = s.replaceAll("%magazine%", magazine);
			s = s.replaceAll("%health%", health);
			s = s.replaceAll("%type%", type);
			s = s.replaceAll("%speed%", speed);
			s = s.replaceAll("%speedreverse%", speedrev);
			s = s.replaceAll("%turnangle%", ta);
			s = s.replaceAll("%minangle%", ma);
			s = s.replaceAll("%maxangle%", maa);
			s = s.replaceAll("%acceleration%", acc);
			s = s.replaceAll("%reloadtime%", rt);
			s = s.replaceAll("%rpm%", rpm);
			s = s.replaceAll("%worth%", worth);
			
			s = ChatColor.translateAlternateColorCodes('&', s);
			
			lore.add(s);
		}
		
		return lore;
	}
	
	public static String buildDeathMessage(String killer) {
		String tmp = getStringByPath("lang.Info.deathMessage");
		tmp = tmp.replaceAll("%killer%", killer);
		
		return ChatColor.translateAlternateColorCodes('&', tmp);
	}
	public static String buildKillMessage(String victim, int kills, int killstreak) {
		String tmp = getStringByPath("lang.Info.killMessage");
		tmp = tmp.replaceAll("%victim%", victim);
		tmp = tmp.replaceAll("%kills%", String.valueOf(kills));
		tmp = tmp.replaceAll("%killstreak%", String.valueOf(killstreak));
		
		return ChatColor.translateAlternateColorCodes('&', tmp);
	}
}
