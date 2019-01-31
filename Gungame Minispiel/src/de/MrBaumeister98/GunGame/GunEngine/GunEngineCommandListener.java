package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.TankConfig;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class GunEngineCommandListener implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String cName = cmd.getName();
		if(cName.equalsIgnoreCase("gunengine") | cName.equalsIgnoreCase("gune") | cName.equalsIgnoreCase("gengine") | cName.equalsIgnoreCase("ge")) {
			
			/*Debugger.logInfo("Length: " + args.length);
			Debugger.logInfo("Args: ");
			for(Integer i = 0; i < args.length; i++) {
				Debugger.logInfo("args[" + i + "]:= " + args[i]);
			}*/
			
			Boolean hasPerm = false;
			if(sender instanceof Player && ((Player)sender).hasPermission("gunengine.cmd")) {
				hasPerm = true;
			}
			
			List<String> consoleHelp = new ArrayList<String>(LangUtil.getStringListByPath("lang.Commands.GunEngine.Help.All"));
			List<String> playerHelp = new ArrayList<String>(LangUtil.getStringListByPath("lang.Commands.GunEngine.Help.Player"));
			
			if(args.length > 0) {
				
				switch(args[0]) {
				default:
					if(sender instanceof ConsoleCommandSender || hasPerm == true) {
						if(hasPerm) {
							for(String m : playerHelp) {
								sender.sendMessage(m);
							}
						} else if(sender instanceof ConsoleCommandSender) {
							for(String m : consoleHelp) {
								sender.sendMessage(m);
							}
						}
					}
					
					return true;
				case "help":
					if(sender instanceof ConsoleCommandSender || hasPerm == true) {
						if(hasPerm) {
							for(String m : playerHelp) {
								sender.sendMessage(m);
							}
						} else if(sender instanceof ConsoleCommandSender) {
							for(String m : consoleHelp) {
								sender.sendMessage(m);
							}
						}
					}
					
					return true;
				case "?":
					if(sender instanceof ConsoleCommandSender || hasPerm == true) {
						if(hasPerm) {
							for(String m : playerHelp) {
								sender.sendMessage(m);
							}
						} else if(sender instanceof ConsoleCommandSender) {
							for(String m : consoleHelp) {
								sender.sendMessage(m);
							}
						}
					}
					
					return true;
				case "list":
					if(sender instanceof ConsoleCommandSender || hasPerm == true) {
						String path = "lang.Commands.GunEngine.List.";
						sender.sendMessage(LangUtil.getStringByPath(path + "Heading"));
						
						if(args.length <= 1) {
							sender.sendMessage(LangUtil.getStringByPath(path + "Guns"));
							for(Gun g : GunGamePlugin.instance.weaponManager.guns) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGunName());
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get gun " + "(" + g.getGunName() + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
							sender.sendMessage(LangUtil.getStringByPath(path + "Grenades"));
							for(Grenade g : GunGamePlugin.instance.weaponManager.grenades) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGrenadeName());
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get grenade " +  "(" + g.getGrenadeName() + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
							sender.sendMessage(LangUtil.getStringByPath(path + "Ammo"));
							for(Ammo a : GunGamePlugin.instance.weaponManager.ammos) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + a.getAmmoName());
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get ammo " +  "(" + a.getAmmoName() + ")"));
								
								sender.spigot().sendMessage(component);
							}				

							sender.sendMessage(LangUtil.getStringByPath(path + "Landmines"));
							for(Landmine mine : GunGamePlugin.instance.weaponManager.landmines) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + mine.getName());
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get landmine " +  "(" + mine.getName() + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
							sender.sendMessage(LangUtil.getStringByPath(path + "Airstrikes"));
							for(Airstrike strike : GunGamePlugin.instance.weaponManager.airstrikes) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + strike.name);
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get airstrike " +  "(" + strike.name + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
							sender.sendMessage(LangUtil.getStringByPath(path + "Turrets"));
							for(TurretConfig turret : GunGamePlugin.instance.turretManager.turrets) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + turret.name);
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get turret " +  "(" + turret.name + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
							sender.sendMessage(LangUtil.getStringByPath(path + "Tanks"));
							for(TankConfig tank : GunGamePlugin.instance.tankManager.getTankConfigs()) {
								TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + tank.name);
								component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get tank " +  "(" + tank.name + ")"));
								
								sender.spigot().sendMessage(component);
							}
							
						} else if(args.length >= 2 && args.length == 2) {
							switch(args[1]) {
							
							default:
								sender.sendMessage(LangUtil.getStringByPath(path + "Guns"));
								for(Gun g : GunGamePlugin.instance.weaponManager.guns) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGunName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get gun " +  "(" + g.getGunName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Grenades"));
								for(Grenade g : GunGamePlugin.instance.weaponManager.grenades) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGrenadeName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get grenade " +  "(" + g.getGrenadeName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Ammo"));
								for(Ammo a : GunGamePlugin.instance.weaponManager.ammos) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + a.getAmmoName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get ammo " +  "(" + a.getAmmoName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Landmines"));
								for(Landmine mine : GunGamePlugin.instance.weaponManager.landmines) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + mine.getName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get landmine " +  "(" + mine.getName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Airstrikes"));
								for(Airstrike strike : GunGamePlugin.instance.weaponManager.airstrikes) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + strike.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get airstrike " +  "(" + strike.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Turrets"));
								for(TurretConfig turret : GunGamePlugin.instance.turretManager.turrets) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + turret.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get turret " +  "(" + turret.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								sender.sendMessage(LangUtil.getStringByPath(path + "Tanks"));
								for(TankConfig tank : GunGamePlugin.instance.tankManager.getTankConfigs()) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + tank.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get tank " +  "(" + tank.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								
								return true;
							
							case "guns":
								sender.sendMessage(LangUtil.getStringByPath(path + "Guns"));
								for(Gun g : GunGamePlugin.instance.weaponManager.guns) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGunName());
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGunName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get gun " +  "(" + g.getGunName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
								
							case "grenades":
								sender.sendMessage(LangUtil.getStringByPath(path + "Grenades"));
								for(Grenade g : GunGamePlugin.instance.weaponManager.grenades) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGrenadeName());
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + g.getGrenadeName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get grenade " +  "(" + g.getGrenadeName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
							
							case "ammo":
								sender.sendMessage(LangUtil.getStringByPath(path + "Ammo"));
								for(Ammo a : GunGamePlugin.instance.weaponManager.ammos) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + a.getAmmoName());
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + a.getAmmoName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get ammo " +  "(" + a.getAmmoName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
							
							case "landmines":
								sender.sendMessage(LangUtil.getStringByPath(path + "Landmines"));
								for(Landmine mine : GunGamePlugin.instance.weaponManager.landmines) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + mine.getName());
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + mine.getName());
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get landmine " +  "(" + mine.getName() + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
								
							case "airstrikes":
								sender.sendMessage(LangUtil.getStringByPath(path + "Airstrikes"));
								for(Airstrike strike : GunGamePlugin.instance.weaponManager.airstrikes) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + strike.name);
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + strike.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get airstrike " +  "(" + strike.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
								
							case "turrets":
								sender.sendMessage(LangUtil.getStringByPath(path + "Turrets"));
								for(TurretConfig turret : GunGamePlugin.instance.turretManager.turrets) {
									//sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + turret.name);
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + turret.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get turret " +  "(" + turret.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
								
							case "tanks":
								sender.sendMessage(LangUtil.getStringByPath(path + "Tanks"));
								for(TankConfig tank : GunGamePlugin.instance.tankManager.getTankConfigs()) {
									TextComponent component = new TextComponent(ChatColor.GRAY + "   - " + ChatColor.DARK_GRAY + tank.name);
									component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ge get tank " +  "(" + tank.name + ")"));
									
									sender.spigot().sendMessage(component);
								}
								return true;
							}
						}
					}
					
					return true;
				case "menu":
					if(hasPerm && (sender instanceof Player)) {
						Player p = (Player) sender;
						GunGamePlugin.instance.gunShop.showSelectionMenu(p);
					}
					return true;
				case "griefing":
					//Debugger.logInfo("Sub Command:= griefing");
					if(hasPerm && (sender instanceof Player)) {
						//OPEN MENU
						GunGamePlugin.instance.griefHelper.openGUI((Player)sender);
					} else if(!(sender instanceof Player)) {
						if(args.length >= 2 && (args[1].equalsIgnoreCase("EXPLOSIONS") ||
								args[1].equalsIgnoreCase("PHYSIC_ENGINE") ||
								args[1].equalsIgnoreCase("BULLETS_IGNITE_TNT") ||
								args[1].equalsIgnoreCase("SHOTS_BREAK_GLASS"))) {
							switch(EGriefType.valueOf(args[1].toUpperCase())) {
							case BULLETS_IGNITE_TNT:
								//Debugger.logInfo("Sub Command:= IGNITE TNT");
								if(args.length > 2) {
									String wn = args[2];
									if(isWorldName(wn)) {
										World world = Bukkit.getWorld(wn);
										if(args.length > 3) {
											if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
												Boolean allow = Boolean.valueOf(args[3]);
												GunGamePlugin.instance.griefHelper.setGriefAllowed(EGriefType.valueOf(args[1].toUpperCase()), allow, world);
											}
										}
									} else {
										Debugger.logInfoWithColoredText(ChatColor.RED +"[ERROR]: " + ChatColor.YELLOW + "Unknown World!");
									}
								}
								break;
							case EXPLOSIONS:
								//Debugger.logInfo("Sub Command:= EXPLOSIONS");
								if(args.length >2) {
									String wn = args[2];
									if(isWorldName(wn)) {
										World world = Bukkit.getWorld(wn);
										if(args.length >3) {
											if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
												Boolean allow = Boolean.valueOf(args[3]);
												GunGamePlugin.instance.griefHelper.setGriefAllowed(EGriefType.valueOf(args[1].toUpperCase()), allow, world);
											}
										}
									} else {
										Debugger.logInfoWithColoredText(ChatColor.RED +"[ERROR]: " + ChatColor.YELLOW + "Unknown World!");
									}
								}
								break;
							case PHYSIC_ENGINE:
								//Debugger.logInfo("Sub Command:= PHYSICS");
								if(args.length >2) {
									String wn = args[2];
									if(isWorldName(wn)) {
										World world = Bukkit.getWorld(wn);
										if(args.length >3) {
											if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
												Boolean allow = Boolean.valueOf(args[3]);
												GunGamePlugin.instance.griefHelper.setGriefAllowed(EGriefType.valueOf(args[1].toUpperCase()), allow, world);
											}
										}
									} else {
										Debugger.logInfoWithColoredText(ChatColor.RED +"[ERROR]: " + ChatColor.YELLOW + "Unknown World!");
									}
								}
								break;
							case SHOTS_BREAK_GLASS:
								//Debugger.logInfo("Sub Command:= BREAK GLASS");
								if(args.length >2) {
									String wn = args[2];
									if(isWorldName(wn)) {
										World world = Bukkit.getWorld(wn);
										if(args.length >3) {
											if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
												Boolean allow = Boolean.valueOf(args[3]);
												GunGamePlugin.instance.griefHelper.setGriefAllowed(EGriefType.valueOf(args[1].toUpperCase()), allow, world);
											}
										}
									} else {
										Debugger.logInfoWithColoredText(ChatColor.RED +"[ERROR]: " + ChatColor.YELLOW + "Unknown World!");
									}
								}
								break;
							default:
								break;
							
							}
						}
					} else {
						Debugger.logInfo("No Permission!");
					}
					return true;
				case "get":
					if(hasPerm && (sender instanceof Player)) {
						Player p = (Player) sender;
						if(args.length >= 2) {
							String name = "";
							for(int i = 0; i < args.length; i++) {
								name = name + args[i] + " ";
								//Bukkit.broadcast(name, "*.*");
							}
							switch(args[1]) {
							case "gun":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.weaponManager.getGun(name).getItem());
								}
								return true;
							
							case "grenade":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.weaponManager.getGrenade(name).getGrenadeItem());
								}
								return true;
								
							case "ammo":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.weaponManager.getAmmo(name).getItem());
								}
								return true;
								
							case "landmine":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.weaponManager.getLandmine(name).getItem());
								}
								return true;
								
							case "airstrike":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.weaponManager.getAirstrike(name).getItem());
								}
								return true;
								
							case "turret":
								if(args.length >= 3) {
									//String name = args[2];
									name = name.substring(name.indexOf("(") +1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.turretManager.getTurretConfig(name).getItem());
								}
								return true;
								
							case "tank":
								if(args.length >= 3) {
									name = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
									p.getInventory().addItem(GunGamePlugin.instance.tankManager.getTankConfig(name).getTankItem());
								}
							}
						}
					}
					return true;
					
				}				
	
			} else {
				if(sender instanceof ConsoleCommandSender || hasPerm == true) {
					if(hasPerm) {
						for(String m : playerHelp) {
							sender.sendMessage(m);
						}
					} else if(sender instanceof ConsoleCommandSender) {
						for(String m : consoleHelp) {
							sender.sendMessage(m);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> retList = new ArrayList<String>();
		String cName = cmd.getName();
		if(cName.equalsIgnoreCase("gunengine") | cName.equalsIgnoreCase("gune") | cName.equalsIgnoreCase("gengine") | cName.equalsIgnoreCase("ge")) {
			
			
			
			Boolean hasPerm = false;
			if(sender instanceof Player && ((Player)sender).hasPermission("gunengine.cmd")) {
				hasPerm = true;
			}
			
			if(args.length < 4 && (args.length == 0 || args == null)) {
				retList.clear();
				
				retList.add("list");
				
				retList.add("help");
				if(hasPerm || sender instanceof ConsoleCommandSender) {
					retList.add("get");
					retList.add("griefing");
					retList.add("menu");
				}
				return retList;
			} else if(args.length > 0) {
				
				switch(args[0]) {
				
				default:					
					retList.clear();
					
					retList.add("list");
					retList.add("help");
					if(hasPerm || sender instanceof ConsoleCommandSender) {
						retList.add("menu");
						retList.add("griefing");
						retList.add("get");
					}
					
					return retList;
				case "list":
					retList.clear();
					
					retList.add("guns");
					retList.add("grenades");
					retList.add("ammo");
					retList.add("landmines");
					retList.add("airstrikes");
					retList.add("turrets");
					retList.add("tanks");
					
					return retList;
				case "menu":
					retList.clear();
					
					//retList.add("yourself a life!");
					
					return retList;
				case "griefing":
					retList.clear();
					
					if(sender instanceof ConsoleCommandSender) {
						retList.add(EGriefType.EXPLOSIONS.toString());
						retList.add(EGriefType.BULLETS_IGNITE_TNT.toString());
						retList.add(EGriefType.PHYSIC_ENGINE.toString());
						retList.add(EGriefType.SHOTS_BREAK_GLASS.toString());
						if(args.length > 1) {
							if(args[1].equalsIgnoreCase("EXPLOSIONS") ||
									args[1].equalsIgnoreCase("PHYSIC_ENGINE") ||
									args[1].equalsIgnoreCase("BULLETS_IGNITE_TNT") ||
									args[1].equalsIgnoreCase("SHOTS_BREAK_GLASS")) {
								switch(EGriefType.valueOf(args[1].toUpperCase())) {
								case BULLETS_IGNITE_TNT:
									retList.clear();
									for(World w : Bukkit.getWorlds()) {
										retList.add(w.getName());
									}
									break;
								case EXPLOSIONS:
									retList.clear();
									for(World w : Bukkit.getWorlds()) {
										retList.add(w.getName());
									}
									break;
								case PHYSIC_ENGINE:
									retList.clear();
									for(World w : Bukkit.getWorlds()) {
										retList.add(w.getName());
									}
									break;
								case SHOTS_BREAK_GLASS:
									retList.clear();
									for(World w : Bukkit.getWorlds()) {
										retList.add(w.getName());
									}
									break;
								default:
									break;
								
								}
							} else {
								//retList.clear();
							}
						}
					}
					return retList;
				case "get":
					retList.clear();
					
					retList.add("gun");
					retList.add("grenade");
					retList.add("ammo");
					retList.add("landmine");
					retList.add("airstrike");
					retList.add("turret");
					retList.add("tank");
					
					if(args.length > 1) {
						switch(args[1]) {
						case "gun":
							retList.clear();
							
							for(Gun g : GunGamePlugin.instance.weaponManager.guns) {
								retList.add( "(" + g.getGunName() + ")");
							}
							
							return retList;
						
						case "grenade":
							retList.clear();
							
							for(Grenade g : GunGamePlugin.instance.weaponManager.grenades) {
								retList.add( "(" + g.getGrenadeName() + ")");
							}
							
							return retList;
							
						case "ammo":
							retList.clear();
							
							for(Ammo a : GunGamePlugin.instance.weaponManager.ammos) {
								retList.add( "(" + a.getAmmoName() + ")");
							}
							
							return retList;
							
						case "landmine":
							retList.clear();
							
							for(Landmine lm : GunGamePlugin.instance.weaponManager.landmines) {
								retList.add( "(" + lm.getName() + ")");
							}
								
							return retList;
							
						case "airstrike":
							retList.clear();
							
							for(Airstrike as : GunGamePlugin.instance.weaponManager.airstrikes) {
								retList.add( "(" + as.name + ")");
							}
							
							return retList;
							
						case "turret":
							retList.clear();

							for(TurretConfig tc : GunGamePlugin.instance.turretManager.turrets) {
								retList.add( "(" + tc.name + ")");
							}
							
							return retList;
							
						case "tank":
							retList.clear();
							
							for(TankConfig tc : GunGamePlugin.instance.tankManager.getTankConfigs()) {
								retList.add("(" + tc.name + ")");
							}
							
							return retList;
						}
						
					}
					
					return retList;
				case "help":
					retList.clear();
					
					return retList;
				}
				
			}
			
		}
		return retList;
	}
	
	private Boolean isWorldName(String name) {
		for(World world : Bukkit.getWorlds()) {
			if(world.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

}
